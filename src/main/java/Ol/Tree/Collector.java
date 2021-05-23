package Ol.Tree;

import java.util.*;

/**
 *  The <b>Collector</b> class holds tree data:
 *  <li>root</li>
 *  <li>pathes witch should be crossed</li>
 *
 *  Only one instance of <b>Collector</b> used for one tree <b>ParallelCross</b>
 *  Each {@link Cross} thread has link to {@link Collector} to call Path for crossing and push crossed branch
 *
 * @see {@link ParallelCross}, {@link Cross}
 *
 */
class Collector implements ITreeHolder{
    /**
     * The <b>ROOT</b> of crossing tree
     */
    volatile Node root;

    /**
     * The set of {@link TreePath} witch should be crossed
     */
    Set<TreePath> treePathSet;

    protected Collector() {
        createPathesForWork();
    }

    private void createPathesForWork() {
        treePathSet = Collections.synchronizedSet(new HashSet<>());
    }

    protected int getWorksetSize() {
        return treePathSet.size();
    }

    protected Node getRoot() {
        return root;
    }

    /**
     * Set <b>root</b>, create <b>pathSet</b>, add <b>path</b>s of <b>root.children</b> to <b>pathSet</b>
     * @param root
     */
    protected void setRoot(Node root) {
        synchronized (this) {
            this.root = root;
            synchronized (treePathSet) {
                createPathesForWork();
                for (int i = 0; i < root.children.length; i++) {
                    int[] path = new int[1];
                    path[0] = i;
                    pushPath(path);
                }
            }
        }
    }

    /**
     * Get already crossed part of branch
     *
     * @param path - array of indexes
     *            path[i] - the Index of child in Node on <b>i</b>-th level
     *            path[0] - the Index of root first child (root.children[path[0]])
     * @return already crossed part of branch
     */
    @Override
    public List<Node> getAvailableBranch(int[] path) {
        Node currentSrc = getRoot();
        List<Node> result = new ArrayList<>();

        for (int i = 0; i < path.length; i++) {

            Node nodeToAdd = currentSrc.children[path[i]];
            if (nodeToAdd == null) {
                break;
            }
            result.add(nodeToAdd);
            currentSrc = nodeToAdd;
        }
        return result;
    }

    /**
     * Get size of closest longest part of path based on <b>arg1</b>
     *  cmpArray({1,2,3} , {1,2,4})      =   2
     *  cmpArray({1,2,3} , {1,2,3,4})    =   4
     *  cmpArray({1,2,3} , {1,2,3,4,6})  =   5
     *  cmpArray({1,2,3} , {2,2,3,4})    =   0
     *
     * @param arg1  -   base sequence
     * @param arg2  -   variant sequence
     * @return size of equal start sequence
     */
    private int cmpArray(int[] arg1, int[] arg2) {
        int i = 0;
        for (; i < Math.min(arg1.length, arg2.length); i++) {
            if (arg1[i] != arg2[i]) {
                break;
            }
        }
        return i == arg1.length ? arg2.length : i;
    }

    /**
     * @see <b>int cmpArray(int[] arg1, int[] arg2)</b>
     */
    private int cmpArray(TreePath arg1, TreePath arg2) {
        return cmpArray(arg1.path, arg2.path);
    }

    /**
     * Gets <b>path</b> closest to param <b>path</b> and remove found <b>path</b> from <b>pathSet</b>
     * @param path
     * @return <b>path</b> closest to param <b>path</b>
     */
    protected int[] popPath(int[] path) {
        if (treePathSet.size() == 0) {
            return null;
        }
        TreePath treePathParam = new TreePath(path);

        TreePath resultTreePath = new TreePath(new int[0]);
        int[] result = null;
        synchronized (treePathSet) {
            for (TreePath currentTreePath : treePathSet) {
                if (resultTreePath.path.length < cmpArray(treePathParam, currentTreePath)) {
                    resultTreePath = currentTreePath;
                }
            }
            if (resultTreePath.path.length == 0) {
                resultTreePath = treePathSet.stream().findFirst().get();
            }

            result = resultTreePath.path;

            treePathSet.remove(resultTreePath);
        }

        return result;
    }

    /**
     *  Get min lenght <b>path</b> (closest to <b>root</b>) from <b>pathSet</b>
     *  and remove founded <b>path</b> from <b>pathSet</b>
     *
     * @return <b>path</b> closest to <b>root</b>
     */
    protected int[] popMinPath() {
        int[] result = null;
        synchronized (treePathSet) {
            try {
                TreePath minTreePath = treePathSet
                        .stream()
                        .min((p1, p2) -> Integer.compare(p1.length(), p2.length()))
                        .get();

                result = minTreePath.path;
                treePathSet.remove(minTreePath);
            } catch (Exception exception) {

            }
        }
        return result;
    }

    /**
     * Add <b>path</b> to <b>pathSet</b>
     * @param path
     */
    protected void pushPath(int[] path) {
        treePathSet.add(new TreePath(path));
    }

    /**
     * Put brunch to tree
     * if Node of branch present - continue
     * @param branch
     * @return count of new added nodes
     */
    protected int putBranch(List<Node> branch) {

        if (branch == null || branch.size() == 0) {
            return 0;
        }

        int addCount = 0;

        Node parentNode = root;

        List<Integer> currentPath = new ArrayList<>();

        int i = 0;

        while (i < branch.size()) {
            Node adden = branch.get(i);

            currentPath.add(adden.indexInParent);

            Node avaible = parentNode.children[adden.indexInParent];
            /**
             * add Node if absent
             */
            if (avaible == null) {
                parentNode.setChildAt(adden, adden.indexInParent);

                addCount++;

                int[] tmpPath = currentPath.stream().mapToInt(Integer::intValue).toArray();
                treePathSet.remove(new TreePath(tmpPath));
            }
            /**
             * test each child Node
             * if absent    - add TreePath
             * if present   - remove TreePath
             */
            for (int j = 0; j < adden.children.length; j++) {
                currentPath.add(j);
                if(avaible==null) {
                    if (adden.children[j] == null) {

                        int[] tmparr = currentPath.stream().mapToInt(Integer::intValue).toArray();
                        pushPath(tmparr);
                    }
                }else if (adden.children[j] != null && avaible.children[j] == null) {
                    avaible.children[j] = adden.children[j];

                    int[] tmpPath = currentPath.stream().mapToInt(Integer::intValue).toArray();
                    treePathSet.remove(new TreePath(tmpPath));
                }
                currentPath.remove(currentPath.size() - 1);
            }
            parentNode = parentNode.children[adden.indexInParent];
            i++;
        }
        return addCount;
    }

    /**
     * The box class for <b>int[] path</b> to helps using it in collections
     */
    protected class TreePath {
        int[] path;

        TreePath(int[] path) {
            this.path = path;
        }

        public boolean equals(Object obj) {
            return Arrays.equals(path, ((TreePath) obj).path);
        }

        public int hashCode() {
            return Arrays.hashCode(path);
        }

        public int length() {
            return path.length;
        }

    }


}
