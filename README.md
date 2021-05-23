# TreeParallelCross
Tree parallel cross utility
The utility package can be most helpful in case Source tree is slow system with redusing performance along using.

The "test" folder contains example

For using should implements "ITree" and initializing FunctionalInterface "ITreeNodeConverter"

Call ParallelCross.doCross:
"public static <T extends ITree, Y extends ITreeNodeConverter, R> R doCross(
            Class<T> treeClass
            , Y treeConverterFunc
            , int parallelism
            , long threadTimeout
            , TimeUnit timeUnit) throws IllegalAccessException, InstantiationException {
" 

like in "ParallelCrossTest":

YamlNodeTest rootResult = ParallelCross.<TreeImpl, ITreeNodeConverter, YamlNodeTest>doCross(
                    TreeImpl.class
                    , (ITreeNodeConverter<YamlNodeTest>) (parentNodeSrc, index, childNodeSrc) -> {
                        parentNodeSrc.itemChildren.set(index, childNodeSrc);
                    }
                    , -1, 5, TimeUnit.MINUTES);
                    
YamlNodeTest - the class of source tree node
TreeImpl - implements "ITree"
ITreeNodeConverter- (ITreeNodeConverter<YamlNodeTest>) (parentNodeSrc, index, childNodeSrc) -> {
                        parentNodeSrc.itemChildren.set(index, childNodeSrc);
                    }
parallelism - if (parallelism <= 0) {
            parallelism = Runtime.getRuntime().availableProcessors() - 1;
        }
threadTimeout,timeUnit - time limit for each thread, in current version interrupted threads became a looses of data :(
