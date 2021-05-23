package Ol.Tree;

@FunctionalInterface
public interface ITreeNodeConverter<T> {
    /**
     * The method sets child node in original tree
     * The method used to convert Node-type tree to original type tree
     * @param parentNodeSrc
     * @param index
     * @param childNodeSrc
     */
    public  void setChildAt(T parentNodeSrc,int index, T childNodeSrc);

}
