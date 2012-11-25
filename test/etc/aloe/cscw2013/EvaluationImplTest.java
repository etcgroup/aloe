/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package etc.aloe.cscw2013;

import etc.aloe.data.EvaluationReport;
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
import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

/**
 *
 * @author michael
 */
public class EvaluationImplTest {

    private Instances instances;
    private Instances testInstances;

    public EvaluationImplTest() {
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

        this.testInstances.add(new DenseInstance(1.0, new double[]{1.0, 1.0})); //TP
        this.testInstances.add(new DenseInstance(1.0, new double[]{3.0, 1.0})); //TP
        this.testInstances.add(new DenseInstance(1.0, new double[]{5.0, 0.0})); //FP
        this.testInstances.add(new DenseInstance(1.0, new double[]{7.0, 0.0})); //TN
        this.testInstances.add(new DenseInstance(1.0, new double[]{8.0, 0.0})); //TN
        this.testInstances.add(new DenseInstance(1.0, new double[]{9.0, 0.0})); //TN
        this.testInstances.add(new DenseInstance(1.0, new double[]{10.0, 1.0})); //FN
        this.testInstances.add(new DenseInstance(1.0, new double[]{11.0, 1.0})); //FN
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of evaluate method, of class EvaluationImpl.
     */
    @Test
    public void testEvaluate() {
        System.out.println("evaluate");
        Classifier classifier = new J48();
        try {
            classifier.buildClassifier(instances);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            assertTrue(false);
        }

        Model model = new Model(classifier);

        ExampleSet examples = new ExampleSet(testInstances);
        EvaluationImpl instance = new EvaluationImpl();
        List<Boolean> predictedLabels = model.getPredictedLabels(examples);
        EvaluationReport result = instance.evaluate(predictedLabels, examples);

        assertEquals(2, result.getTruePositiveCount());
        assertEquals(1, result.getFalsePositiveCount());
        assertEquals(3, result.getTrueNegativeCount());
        assertEquals(2, result.getFalseNegativeCount());

    }
}
