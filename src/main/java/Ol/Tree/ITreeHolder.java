package Ol.Tree;

import java.util.List;

/**
 * The interface for ability to get already crossed part of branch
 * It may be use in {@link ITree} instances for boost by minimization node creation
 */
public interface ITreeHolder {
    /**
     * Get already crossed part of branch
     *
     * @param path - array of indexes
     *            path[i] - the Index of child in Node on <tt>i</tt>-th level
     *            path[0] - the Index of root first child (root.children[path[0]])
     * @return already crossed part of branch
     */
    public List<Node> getAvailableBranch(int[] path);
}
