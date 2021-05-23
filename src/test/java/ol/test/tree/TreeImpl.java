package ol.test.tree;

import Ol.Tree.ITree;
import Ol.Tree.ITreeHolder;
import Ol.Tree.Node;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class TreeImpl implements ITree {
    public static String yamlFileName;
    ITreeHolder treeHolder;
    int nodesCount = 0;
    private YamlNodeTest rootSrc;

    public TreeImpl() {
        rootSrc = readYaml();
        nodesCount = 0;
    }

    public YamlNodeTest readYaml() {
        YamlNodeTest resultYamlNode = null;
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream(yamlFileName);
            resultYamlNode = mapper.readValue(is, YamlNodeTest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultYamlNode;
    }

    @Override
    public List<Node> getBranchByPath(int[] path) {
        if (path.length == 0) {
            List<Node> result = new ArrayList<>();
            result.add(Node.createNode(rootSrc, -1, rootSrc.itemChildren.size()));
            return result;
        }
        List<Node> currentPath = null;

        YamlNodeTest currentSrc = rootSrc;
        if (treeHolder != null) {
            /**
             * Optimization by using already collected source tree nodes
             *
             * In that case source tree node can be used without parent nodes
             */
            currentPath = treeHolder.getAvailableBranch(path);
            if (currentPath.size() > 0) {
                currentSrc = (YamlNodeTest) currentPath.get(currentPath.size() - 1).getNode();
            }
        } else {
            currentPath = new ArrayList<>();
        }


        for (int i = currentPath.size(); i < path.length; i++) {
            currentSrc = currentSrc.itemChildren.get(path[i]);
            if (i >= currentPath.size()) {
                nodesCount++;
                currentPath.add(Node.createNode(currentSrc, path[i], currentSrc.itemChildren.size()));
            }
        }

        return currentPath;
    }


    @Override
    public boolean isCanContinue() {
        return nodesCount < 50;
        //return true;
    }

    @Override
    public void setTreeHolder(ITreeHolder treeHolder) {
        this.treeHolder = treeHolder;
    }

}
