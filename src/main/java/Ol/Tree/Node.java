package Ol.Tree;

/**
 *
 * The {@code Node} class wraps original tree node
 *
 * @author Oleksii Ivanov
 */
public class Node {
    /**
     * The left collect child
     */
    private int childCountdown = -1;


    /**
     * The source data
     */
    protected Object node;

    /**
     * The index of this Node in parent.children[]
     */
    protected int indexInParent=-1;

    /**
     * The child nodes
     */
    protected Node[] children;


    /**
     *  The Node creator for non package classes
     * @param srcNode       -   source data
     * @param indexInParent -   The index of this Node in parent.children[]
     * @param childCount    -   parent.children[].length
     */
    public static Node createNode(Object srcNode,int indexInParent,int childCount){
        return new Node(srcNode,indexInParent,childCount);
    }

    /**
     *  The constructor
     * @param node          -   source data
     * @param indexInParent -   The index of this Node in parent.children[]
     * @param childCount    -   parent.children[].length
     */
    protected Node(Object node,int indexInParent,int childCount){
        this.node=node;
        this.indexInParent=indexInParent;
        setChieldCount(childCount);
    }


    public int getChildCount() {
        return children.length;
    }

    public Node getChildAt(int index) {
        return children[index];
    }

    public Object getNode() {
        return node;
    }

    /**
     * Sets count of children in this node
     * creates new children[], all data in children[] is loosing
     * @param childCount    -   new count of children in this node (children[].length)
     */
    protected void setChieldCount(int childCount) {
        this.childCountdown = childCount;
        children = new Node[childCount > 0 ? childCount : 0];
    }

    /**
     * Sets child node at postion  <b>childIndex</b>
     * @param child     -   child Node
     * @param childIndex-   postion  of <b>child</b> in <b>children</b>
     */
    protected void setChildAt(Node child, int childIndex) {
        if (children[childIndex]==null){
            child.indexInParent=childIndex;
            children[childIndex]=child;
            childCountdown--;
        }
    }

}
