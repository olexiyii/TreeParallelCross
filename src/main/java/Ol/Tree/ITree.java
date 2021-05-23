package Ol.Tree;

import java.util.List;

/**
 *
 * That interface should be implemented to use parallel tree crossing
 *
 * Each implements is selftesting about is it can continue by <tt>isCanContinue</tt> method
 * when <tt>isCanContinue() == false</tt> this object will be deleted
 *
 * Assumptions about <tt>not change values</tt> while tree in crossing:
 * - the order of nodes
 * - the count of nodes
 *
 * Assumptions about access to tree:
 *  - several processes can read any node at the same time
 */
public interface ITree {
    /**
     * To create <tt>Node</tt>s should use <tt>Node.createNode(Object srcNode,int indexInParent,int childCount)</tt>
     * @param path - array of indexes
     *      path[i] - the Index of child in Node on <tt>i</tt>-th level
     *      path[0] - the Index of root first child (root.children[path[0]])
     *      root = getBranchByPath(new int[0])
     *
     * @return list of <tt>Node</tt> by path
     *      list.get(0) = root.children[path[0]]
     *      list.get(1) = root.children[path[0]].children[path[1]]
     *      ...
    */
    public List<Node> getBranchByPath(int[] path);


    /**
     *  The test if current tree object can continue to work
     * @return <tt>true</tt> - can
     *         <tt>false</tt> - not can
     */
    public boolean isCanContinue();

    /**
     * Sets reference to {@link ITreeHolder} for boost by minimization node creation
     *
     * The optional than in why realization can be void
     *
     * @param treeHolder
     */
    public void setTreeHolder(ITreeHolder treeHolder);
}
