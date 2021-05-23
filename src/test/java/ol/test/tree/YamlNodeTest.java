package ol.test.tree;

import java.util.ArrayList;
import java.util.List;

public class YamlNodeTest {
    public String name;
    public int index;
    public List<YamlNodeTest> itemChildren;

    private static int nodesCount;

    public YamlNodeTest(){
        itemChildren=new ArrayList<YamlNodeTest>();
    }
    public YamlNodeTest(YamlNodeTest src){
        this.name =src.name;
        this.index =src.index;
        itemChildren=new ArrayList<YamlNodeTest>();
    }
    public void setDefName(){
        name="Node "+index;
    }

    @Override
    public boolean equals(Object node){
        return name.equals(((YamlNodeTest)node).name)
                && index==index;
    }

    public static YamlNodeTest createRandomTree(int treeLevelsCount){
        YamlNodeTest root=new YamlNodeTest();
        root.index=nodesCount++;
        root.setDefName();
        if (treeLevelsCount > 0) {
            int childCount = (int) Math.round(Math.random() * 10);
            for (int i = 0; i < childCount; i++) {
                root.itemChildren.add(createRandomTree(treeLevelsCount - 1));
            }
        }

        return root;
    }

}
