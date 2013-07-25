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

import etc.aloe.data.ExampleSet;
import etc.aloe.data.Label;
import etc.aloe.data.Predictions;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
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
public class WekaModelTest {

    private Instances instances;
    private Instances testInstances;

    public WekaModelTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        Label.startLabelSet();
        Label.FALSE();
        Label.TRUE();
        Label.closeLabelSet();

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

        J48 classifier = new J48();
        classifier.setNumFolds(456);
        WekaModel model = new WekaModel(classifier);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        assertTrue(model.save(out));
        out.close();
        byte[] serializedStr = out.toByteArray();

        //It wrote something
        assertTrue(serializedStr.length > 0);

        //It wrote a J48 classifier
        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(serializedStr));
        try {
            J48 serialized = (J48) in.readObject();
            in.close();
            assertEquals(classifier.getNumFolds(), serialized.getNumFolds());
        } catch (ClassNotFoundException e) {
            assertTrue(e.getMessage(), false);
        } catch (IOException e) {
            assertTrue(e.getMessage(), false);
        }
    }

    /**
     * Test of load method, of class Model.
     */
    @Test
    public void testLoad() throws Exception {
        System.out.println("load");

        J48 serializedClassifier = new J48();
        serializedClassifier.setNumFolds(456);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream serializer = new ObjectOutputStream(out);
        serializer.writeObject(serializedClassifier);
        out.close();
        byte[] serializedStr = out.toByteArray();

        ByteArrayInputStream in = new ByteArrayInputStream(serializedStr);
        WekaModel model = new WekaModel();
        assertTrue(model.load(in));
        in.close();

        Classifier classifier = model.getClassifier();
        assertTrue(classifier instanceof J48);
        J48 j48 = (J48) classifier;
        assertEquals(serializedClassifier.getNumFolds(), j48.getNumFolds());
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

        WekaModel instance = new WekaModel(classifier);

        Label[] expResult = new Label[]{Label.TRUE(), Label.TRUE(), Label.FALSE(), Label.FALSE()};
        Double[] expConfidence = new Double[]{1.0, 1.0, 0.0, 0.0};

        ExampleSet examples = new ExampleSet(testInstances);

        Predictions predictions = instance.getPredictions(examples);

        assertEquals(expResult.length, predictions.size());

        for (int i = 0; i < expResult.length; i++) {
            assertEquals(expResult[i], predictions.getPredictedLabel(i));
            assertEquals(expConfidence[i], predictions.getPredictionConfidence(i));
            assertEquals(testInstances.get(i).classValue(), predictions.getTrueLabel(i).getNumber(), 0.0);
        }
    }
}
