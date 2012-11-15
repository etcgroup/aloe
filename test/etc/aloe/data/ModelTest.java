/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package etc.aloe.data;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.Utils;

/**
 *
 * @author michael
 */
public class ModelTest {

    private Instances instances;
    private Instances testInstances;

    public ModelTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        ArrayList<Attribute> attributes = new ArrayList<Attribute>();

        attributes.add(new Attribute("id"));

        ArrayList<String> classValues = new ArrayList<String>();
        classValues.add("false");
        classValues.add("true");
        attributes.add(new Attribute("class", classValues));

        this.instances = new Instances("TrainInstances", attributes, 12);
        this.instances.setClassIndex(1);

        this.instances.add(new DenseInstance(1.0, new double[]{1.0, 1.0}));
        this.instances.add(new DenseInstance(1.0, new double[]{2.0, 1.0}));
        this.instances.add(new DenseInstance(1.0, new double[]{3.0, 1.0}));
        this.instances.add(new DenseInstance(1.0, new double[]{4.0, 1.0}));
        this.instances.add(new DenseInstance(1.0, new double[]{5.0, 1.0}));
        this.instances.add(new DenseInstance(1.0, new double[]{6.0, 1.0}));
        this.instances.add(new DenseInstance(1.0, new double[]{7.0, 0.0}));
        this.instances.add(new DenseInstance(1.0, new double[]{8.0, 0.0}));
        this.instances.add(new DenseInstance(1.0, new double[]{9.0, 0.0}));
        this.instances.add(new DenseInstance(1.0, new double[]{10.0, 0.0}));
        this.instances.add(new DenseInstance(1.0, new double[]{11.0, 0.0}));
        this.instances.add(new DenseInstance(1.0, new double[]{12.0, 0.0}));

        this.testInstances = new Instances("TestInstances", attributes, 4);
        this.testInstances.setClassIndex(1);

        this.testInstances.add(new DenseInstance(1.0, new double[]{1.0, 1.0}));
        this.testInstances.add(new DenseInstance(1.0, new double[]{6.0, 1.0}));
        this.testInstances.add(new DenseInstance(1.0, new double[]{7.0, 0.0}));
        this.testInstances.add(new DenseInstance(1.0, new double[]{12.0, 0.0}));
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of save method, of class Model.
     */
    @Test
    public void testSave() throws Exception {
        System.out.println("save");
        File destination = null;
        Model instance = new Model();
        boolean expResult = false;
//        boolean result = instance.save(destination);
//        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of load method, of class Model.
     */
    @Test
    public void testLoad() throws Exception {
        System.out.println("load");
        File source = null;
        Model instance = new Model();
        boolean expResult = false;
//        boolean result = instance.load(source);
//        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getPredictedLabels method, of class Model.
     */
    @Test
    public void testGetPredictedLabels() {
        System.out.println("getPredictedLabels");

        Classifier classifier = new J48();
        try {
            classifier.buildClassifier(instances);
        } catch (Exception e) {
            assertTrue("Classifier could not be trained", false);
        }

        Model instance = new Model(classifier);

        Boolean[] expResult = new Boolean[]{true, true, false, false};
        ExampleSet examples = new ExampleSet(testInstances);

        List<Boolean> result = instance.getPredictedLabels(examples);
        assertArrayEquals(expResult, result.toArray());
    }
}
