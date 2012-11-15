/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package etc.aloe.cscw2013;

import etc.aloe.data.ExampleSet;
import etc.aloe.data.Model;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

/**
 *
 * @author michael
 */
public class TrainingImplTest {
    private Instances testInstances;
    private Instances instances;

    public TrainingImplTest() {
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
     * Test of train method, of class TrainingImpl.
     */
    @Test
    public void testTrain() {
        System.out.println("train");

        TrainingImpl instance = new TrainingImpl();
        Model model = instance.train(new ExampleSet(instances));

        //The test here is whether the model works
        Boolean[] expResult = new Boolean[]{true, true, false, false};
        ExampleSet examples = new ExampleSet(testInstances);
        List<Boolean> result = model.getPredictedLabels(examples);
        assertArrayEquals(expResult, result.toArray());
    }
}
