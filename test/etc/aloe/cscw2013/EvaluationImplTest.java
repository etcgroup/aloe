/*
 * This file is part of ALOE.
 *
 * ALOE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * ALOE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with ALOE.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2012 SCCL, University of Washington (http://depts.washington.edu/sccl)
 */
package etc.aloe.cscw2013;

import etc.aloe.data.EvaluationReport;
import etc.aloe.data.ExampleSet;
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
 * @author Michael Brooks <mjbrooks@uw.edu>
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

        WekaModel model = new WekaModel(classifier);

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
