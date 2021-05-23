package Ol.Tree;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;


/**
 * The thread class provides work between {@link Collector} and implementation of {@link ITree}
 * Gets Nodes from {@link ITree} and puts it to {@link Collector}
 */
public class Cross implements Callable<Integer> {
    private static final AtomicInteger count = new AtomicInteger(0);

    private ReentrantLock lock = new ReentrantLock();

    private long id;
    private int collectedNodes;

    private Collector collector;
    private ITree treeObj;

    private Cross(ITree treeObj, Collector collector) {
        id = count.incrementAndGet();
        this.collector = collector;
        this.treeObj = treeObj;
        this.treeObj.setTreeHolder(collector);
    }

    protected static <T extends ITree> Cross instance(Class<T> classParam, Collector collector) throws IllegalAccessException, InstantiationException {
        return new Cross(classParam.newInstance(), collector);
    }

    private void init() {
        synchronized (collector) {
            if (collector.getRoot() == null) {
                List<Node> root = treeObj.getBranchByPath(new int[0]);
                collector.setRoot(root.get(0));
            }
        }
    }

    private int doCross() {
        init();

        int[] currentPath = collector.popMinPath();

        /**
         * if <b>currentPath==null</b> - nothing to do
         */
        if (currentPath == null) {
            return 0;
        }

        do {
            List<Node> branch = treeObj.getBranchByPath(currentPath);
            collector.putBranch(branch);

            currentPath = collector.popPath(currentPath);
            collectedNodes++;
            /**
             * if <b>currentPath==null</b> - nothing to do
             * if self test <b>treeObj.isCanContinue()==false</b> - current thread should be finished
             */
        } while (currentPath != null && treeObj.isCanContinue());

        if (currentPath != null) {
            collector.pushPath(currentPath);
        }

        return collectedNodes;
    }

    @Override
    public Integer call() {

        return doCross();
    }

}
