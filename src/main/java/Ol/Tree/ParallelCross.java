package Ol.Tree;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;


public final class ParallelCross {
    private ParallelCross() {
    }

    public static <T extends ITree, Y extends ITreeNodeConverter, R> R doCross(
            Class<T> treeClass
            , Y treeConverterFunc
            , int parallelism
            , long threadTimeout
            , TimeUnit timeUnit) throws IllegalAccessException, InstantiationException {

        List<Cross> pool = new ArrayList<>();

        if (parallelism <= 0) {
            parallelism = Runtime.getRuntime().availableProcessors() - 1;
        }


        int newNodeCount=0, totalNodeCount = 0;

        ExecutorService executor = Executors.newFixedThreadPool(parallelism);

        Collector collector = new Collector();
        for (int i = 0; i < parallelism; i++) {
            pool.add(Cross.instance(treeClass, collector));
        }

        do {
            List<Future<Integer>> result = null;
            try {
                result = executor.invokeAll(pool);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < result.size(); i++) {
                int currentNodeCount = 0;

                try {

                    currentNodeCount = result.get(i).get(threadTimeout, timeUnit).intValue();

                    /*TODO
                    make it self-healing after thread interruptions
                     */
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }
                newNodeCount += currentNodeCount;

                if (collector.getWorksetSize() > 0) {
                    result.add(executor.submit(Cross.instance(treeClass, collector)));
                }
            }

            totalNodeCount += newNodeCount;
        } while (collector.getWorksetSize() > 0);

        executor.shutdownNow();

        return (R) convertToOriginalTree(collector.getRoot(), treeConverterFunc);
    }

    private static <Y1 extends ITreeNodeConverter, R1> R1 convertToOriginalTree(Node nodeCross, Y1 treeConverterFunc) {
        R1 result = (R1) nodeCross.node;
        for (int i = 0; i < nodeCross.getChildCount(); i++) {
            treeConverterFunc.setChildAt(result, i, convertToOriginalTree(nodeCross.getChildAt(i), treeConverterFunc));
        }
        return result;
    }

}
