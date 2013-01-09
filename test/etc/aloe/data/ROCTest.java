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
public class ROCTest {

    public ROCTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getName method, of class ROC.
     */
    @Test
    public void testGetName() {
        System.out.println("getName");

        String name = "name";
        ROC instance = new ROC(name);

        String result = instance.getName();
        assertEquals(name, result);
    }

    /**
     * Test of size method, of class ROC.
     */
    @Test
    public void testSize() {
        System.out.println("size");

        ROC instance = new ROC("test");
        assertEquals(0, instance.size());
        instance.record(1, 1, 1);
        assertEquals(1, instance.size());

        instance.record(1, 1, 1);
        assertEquals(2, instance.size());
    }

    /**
     * Test of getFalsePositiveRate method, of class ROC.
     */
    @Test
    public void testGetFalsePositiveRate() {
        System.out.println("getFalsePositiveRate");

        ROC instance = new ROC("test");
        assertEquals(0, instance.size());
        instance.record(0, 0, 0);
        instance.record(0.2, 0, 0);
        instance.record(0.9, 0, 0);

        assertEquals(0, instance.getFalsePositiveRate(0), 0);
        assertEquals(0.2, instance.getFalsePositiveRate(1), 0);
        assertEquals(0.9, instance.getFalsePositiveRate(2), 0);
    }

    /**
     * Test of getTruePositiveRate method, of class ROC.
     */
    @Test
    public void testGetTruePositiveRate() {
        System.out.println("getTruePositiveRate");

        ROC instance = new ROC("test");
        assertEquals(0, instance.size());
        instance.record(0, 0, 0);
        instance.record(0, 0.2, 0);
        instance.record(0, 0.9, 0);

        assertEquals(0, instance.getTruePositiveRate(0), 0);
        assertEquals(0.2, instance.getTruePositiveRate(1), 0);
        assertEquals(0.9, instance.getTruePositiveRate(2), 0);
    }

    /**
     * Test of getThresholdValue method, of class ROC.
     */
    @Test
    public void testGetThresholdValue() {
        System.out.println("getThresholdValue");

        ROC instance = new ROC("test");
        assertEquals(0, instance.size());
        instance.record(0, 0, 0);
        instance.record(0, 0, 0.2);
        instance.record(0, 0, 0.9);

        assertEquals(0, instance.getThresholdValue(0), 0);
        assertEquals(0.2, instance.getThresholdValue(1), 0);
        assertEquals(0.9, instance.getThresholdValue(2), 0);
    }

    /**
     * Test of calculateCurve method, of class ROC.
     */
    @Test
    public void testCalculateCurve() {
        System.out.println("calculateCurve");

        Predictions predictions = new Predictions();
        predictions.add(Label.FALSE(), 0.1, Label.TRUE());
        predictions.add(Label.TRUE(), 0.9, Label.TRUE());
        predictions.add(Label.TRUE(), 0.7, Label.TRUE());
        predictions.add(Label.FALSE(), 0.12, Label.FALSE());
        predictions.add(Label.FALSE(), 0.4, Label.FALSE());
        predictions.add(Label.TRUE(), 0.5, Label.FALSE());

        ROC instance = new ROC("test");
        instance.calculateCurve(predictions);

        assertEquals(0.1, instance.getThresholdValue(0), 0);
        assertEquals(0.12, instance.getThresholdValue(1), 0);
        assertEquals(0.4, instance.getThresholdValue(2), 0);
        assertEquals(0.5, instance.getThresholdValue(3), 0);
        assertEquals(0.7, instance.getThresholdValue(4), 0);
        assertEquals(0.9, instance.getThresholdValue(5), 0);

        assertEquals(2.0 / 3.0, instance.getFalsePositiveRate(2), 0);
        assertEquals(2.0 / 3.0, instance.getTruePositiveRate(4), 0);
    }

    /**
     * Test of clear method, of class ROC.
     */
    @Test
    public void testClear() {
        System.out.println("clear");

        ROC instance = new ROC("test");
        assertEquals(0, instance.size());
        instance.record(1, 1, 1);
        assertEquals(1, instance.size());

        instance.record(1, 1, 1);
        assertEquals(2, instance.size());

        instance.clear();
        assertEquals(0, instance.size());
    }
}
