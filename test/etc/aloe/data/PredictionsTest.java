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
package etc.aloe.data;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class PredictionsTest {

    public PredictionsTest() {
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
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getPredictedLabel method, of class Predictions.
     */
    @Test
    public void testGetPredictedLabel() {
        System.out.println("getPredictedLabel");

        Predictions instance = new Predictions();
        instance.add(Label.FALSE(), Double.NaN);
        instance.add(Label.TRUE(), Double.NaN);
        instance.add(Label.FALSE(), Double.NaN);

        assertEquals(Label.FALSE(), instance.getPredictedLabel(0));
        assertEquals(Label.TRUE(), instance.getPredictedLabel(1));
        assertEquals(Label.FALSE(), instance.getPredictedLabel(2));
    }

    /**
     * Test of getPredictionConfidence method, of class Predictions.
     */
    @Test
    public void testGetPredictionConfidence() {
        System.out.println("getPredictionConfidence");

        Predictions instance = new Predictions();
        instance.add(Label.FALSE(), 0.0);
        instance.add(Label.FALSE(), 0.5);
        instance.add(Label.FALSE(), 1.0);

        assertEquals(0.0, instance.getPredictionConfidence(0), 0);
        assertEquals(0.5, instance.getPredictionConfidence(1), 0);
        assertEquals(1.0, instance.getPredictionConfidence(2), 0);
    }

    /**
     * Test of getTrueLabel method, of class Predictions.
     */
    @Test
    public void testGetTrueLabel() {
        System.out.println("getTrueLabel");

        Predictions instance = new Predictions();
        instance.add(Label.FALSE(), Double.NaN, Label.TRUE());
        instance.add(Label.TRUE(), Double.NaN);
        instance.add(Label.FALSE(), Double.NaN, Label.FALSE());

        assertEquals(Label.TRUE(), instance.getTrueLabel(0));
        assertEquals(null, instance.getTrueLabel(1));
        assertEquals(Label.FALSE(), instance.getTrueLabel(2));
    }

    /**
     * Test of size method, of class Predictions.
     */
    @Test
    public void testSize() {
        System.out.println("size");

        Predictions instance = new Predictions();

        assertEquals(0, instance.size());

        instance.add(Label.FALSE(), Double.NaN, Label.TRUE());
        instance.add(Label.TRUE(), Double.NaN);
        instance.add(Label.FALSE(), Double.NaN, Label.FALSE());

        assertEquals(3, instance.size());
    }

    /**
     * Test of getTruePositiveCount method, of class Predictions.
     */
    @Test
    public void testGetTruePositiveCount() {
        System.out.println("getTruePositiveCount");

        Predictions instance = new Predictions();
        assertEquals(0, instance.getTruePositiveCount());

        instance.add(Label.TRUE(), Double.NaN, Label.TRUE());
        assertEquals(1, instance.getTruePositiveCount());

        instance.add(Label.FALSE(), Double.NaN, Label.FALSE());
        assertEquals(1, instance.getTruePositiveCount());

        instance.add(Label.TRUE(), Double.NaN, Label.TRUE());
        assertEquals(2, instance.getTruePositiveCount());
    }

    /**
     * Test of getTrueNegativeCount method, of class Predictions.
     */
    @Test
    public void testGetTrueNegativeCount() {
        System.out.println("getTrueNegativeCount");

        Predictions instance = new Predictions();
        assertEquals(0, instance.getTrueNegativeCount());

        instance.add(Label.FALSE(), Double.NaN, Label.FALSE());
        assertEquals(1, instance.getTrueNegativeCount());

        instance.add(Label.TRUE(), Double.NaN, Label.FALSE());
        assertEquals(1, instance.getTrueNegativeCount());

        instance.add(Label.FALSE(), Double.NaN, Label.FALSE());
        assertEquals(2, instance.getTrueNegativeCount());
    }

    /**
     * Test of getFalsePositiveCount method, of class Predictions.
     */
    @Test
    public void testGetFalsePositiveCount() {
        System.out.println("getFalsePositiveCount");

        Predictions instance = new Predictions();
        assertEquals(0, instance.getFalsePositiveCount());

        instance.add(Label.TRUE(), Double.NaN, Label.TRUE());
        assertEquals(0, instance.getFalsePositiveCount());

        instance.add(Label.TRUE(), Double.NaN, Label.FALSE());
        assertEquals(1, instance.getFalsePositiveCount());

        instance.add(Label.TRUE(), Double.NaN, Label.FALSE());
        assertEquals(2, instance.getFalsePositiveCount());
    }

    /**
     * Test of getFalseNegativeCount method, of class Predictions.
     */
    @Test
    public void testGetFalseNegativeCount() {
        System.out.println("getFalseNegativeCount");

        Predictions instance = new Predictions();
        assertEquals(0, instance.getFalseNegativeCount());

        instance.add(Label.FALSE(), Double.NaN, Label.TRUE());
        assertEquals(1, instance.getFalseNegativeCount());

        instance.add(Label.FALSE(), Double.NaN, Label.TRUE());
        assertEquals(2, instance.getFalseNegativeCount());

        instance.add(Label.TRUE(), Double.NaN, Label.TRUE());
        assertEquals(2, instance.getFalseNegativeCount());
    }

    /**
     * Test of sortByConfidence method, of class Predictions.
     */
    @Test
    public void testSortByConfidence() {
        System.out.println("sortByConfidence");

        Predictions instance = new Predictions();
        instance.add(Label.FALSE(), 0.5, Label.FALSE());
        instance.add(Label.FALSE(), 0.4, Label.TRUE());
        instance.add(Label.TRUE(), 0.8, Label.FALSE());
        instance.add(Label.TRUE(), 0.6, Label.TRUE());

        Predictions result = instance.sortByConfidence();

        assertEquals(0.4, result.getPredictionConfidence(0), 0.0);
        assertEquals(0.5, result.getPredictionConfidence(1), 0.0);
        assertEquals(0.6, result.getPredictionConfidence(2), 0.0);
        assertEquals(0.8, result.getPredictionConfidence(3), 0.0);

        assertEquals(Label.FALSE(), result.getPredictedLabel(0));
        assertEquals(Label.FALSE(), result.getPredictedLabel(1));
        assertEquals(Label.TRUE(), result.getPredictedLabel(2));
        assertEquals(Label.TRUE(), result.getPredictedLabel(3));

        assertEquals(Label.TRUE(), result.getTrueLabel(0));
        assertEquals(Label.FALSE(), result.getTrueLabel(1));
        assertEquals(Label.TRUE(), result.getTrueLabel(2));
        assertEquals(Label.FALSE(), result.getTrueLabel(3));
    }
}
