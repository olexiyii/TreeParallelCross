package ol.test.tree;

import Ol.Tree.ITreeNodeConverter;
import Ol.Tree.ParallelCross;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ParallelCrossTest {
    @Before
    public void initialise() {

        TreeImpl.yamlFileName = "YamlNodeTest.yaml";
    }

    //@Test
    public void testMethodCreate() {
        YamlNodeTest root = YamlNodeTest.createRandomTree(5);
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

        try {
            mapper.writeValue(Paths.get("YamlNodeTest.yaml").toFile(), root);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testMethod() {
        long start = System.currentTimeMillis();

        YamlNodeTest rootResult = null;
        try {
            rootResult = ParallelCross.<TreeImpl, ITreeNodeConverter, YamlNodeTest>doCross(
                    TreeImpl.class
                    , (ITreeNodeConverter<YamlNodeTest>) (parentNodeSrc, index, childNodeSrc) -> {
                        parentNodeSrc.itemChildren.set(index, childNodeSrc);
                    }
                    , -1, 5, TimeUnit.MINUTES);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        Path resultPath = Paths.get("yamlResult.yaml");
        try {
            mapper.writeValue(resultPath.toFile(), rootResult);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream(TreeImpl.yamlFileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            List<String> linesSrc = reader.lines().collect(Collectors.toList());
            List<String> linesResult = Files.readAllLines(resultPath);

            Assert.assertTrue(Arrays.deepEquals(linesSrc.toArray(), linesResult.toArray()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("total time:" + ((System.currentTimeMillis() - start) / 1000));

    }

}
