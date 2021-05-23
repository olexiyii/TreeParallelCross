# TreeParallelCross
<p>Tree parallel cross utility</p>
<p>The utility package can be most helpful in case Source tree is slow system with redusing performance along using.</p>

<p>The <b>test</b> folder contains example</p>

<p>For using should implements <b>ITree</b> and initializing FunctionalInterface <b>ITreeNodeConverter</b></p>

<p>Call ParallelCross.doCross:</p>
<p>
<code>public static <T extends ITree, Y extends ITreeNodeConverter, R> R doCross(
            Class<T> treeClass
            , Y treeConverterFunc
            , int parallelism
            , long threadTimeout
            , TimeUnit timeUnit) throws IllegalAccessException, InstantiationException </code>
</p>
<p>like in "ParallelCrossTest":</p>
<p><code>
YamlNodeTest rootResult = ParallelCross.<TreeImpl, ITreeNodeConverter, YamlNodeTest>doCross(
                    TreeImpl.class
                    , (ITreeNodeConverter<YamlNodeTest>) (parentNodeSrc, index, childNodeSrc) -> {
                        parentNodeSrc.itemChildren.set(index, childNodeSrc);
                    }
                    , -1, 5, TimeUnit.MINUTES);
                    </code>
            </p>
<li>
YamlNodeTest - the class of source tree node</li>
<li>TreeImpl - implements "ITree"/li>
<li>ITreeNodeConverter- (ITreeNodeConverter<YamlNodeTest>) (parentNodeSrc, index, childNodeSrc) -> {
                        parentNodeSrc.itemChildren.set(index, childNodeSrc);
                    }/li>
<li>parallelism - if (parallelism <= 0) {
            parallelism = Runtime.getRuntime().availableProcessors() - 1;
        }/li>
<li>threadTimeout,timeUnit - time limit for each thread/li>

            <p>in current version interrupted threads became a looses of data :(</p>
