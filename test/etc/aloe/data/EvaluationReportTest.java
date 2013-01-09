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

import java.io.ByteArrayOutputStream;
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
public class EvaluationReportTest {

    public EvaluationReportTest() {
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
     * Test of save method, of class EvaluationReport.
     */
    @Test
    public void testSave() throws Exception {
        System.out.println("save");

        EvaluationReport eval = new EvaluationReport("testing");
        eval.setTrueNegativeCount(2);
        eval.setTruePositiveCount(1);
        eval.setFalseNegativeCount(3);
        eval.setFalsePositiveCount(4);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        assertTrue(eval.save(out));
        out.close();

        String nl = "\n";
        String expectedReport = "TP: 1" + nl
                + "FP: 4" + nl
                + "TN: 2" + nl
                + "FN: 3";

        String report = out.toString();

        assertTrue(report.contains(expectedReport));
    }

    /**
     * Test of addPartial method, of class EvaluationReport.
     */
    @Test
    public void testAddPartial() {
        System.out.println("addPartial");
        EvaluationReport partial = new EvaluationReport("report 1");
        partial.setTrueNegativeCount(1);
        partial.setTruePositiveCount(2);
        partial.setFalseNegativeCount(3);
        partial.setFalsePositiveCount(4);

        EvaluationReport instance = new EvaluationReport("report 2");
        instance.addPartial(partial);

        assertEquals(partial.getTrueNegativeCount(), instance.getTrueNegativeCount());
        assertEquals(partial.getFalseNegativeCount(), instance.getFalseNegativeCount());
        assertEquals(partial.getTruePositiveCount(), instance.getTruePositiveCount());
        assertEquals(partial.getFalsePositiveCount(), instance.getFalsePositiveCount());

        instance.addPartial(partial);

        assertEquals(2 * partial.getTrueNegativeCount(), instance.getTrueNegativeCount());
        assertEquals(2 * partial.getFalseNegativeCount(), instance.getFalseNegativeCount());
        assertEquals(2 * partial.getTruePositiveCount(), instance.getTruePositiveCount());
        assertEquals(2 * partial.getFalsePositiveCount(), instance.getFalsePositiveCount());

    }

    /**
     * Test of recordPrediction method, of class EvaluationReport.
     */
    @Test
    public void testRecordPrediction() {
        System.out.println("recordPrediction");

        Predictions predictions = new Predictions();

        predictions.add(Label.TRUE(), 1.0, Label.TRUE());
        EvaluationReport instance = new EvaluationReport("report 1");
        instance.addPredictions(predictions);
        assertEquals(1, instance.getTruePositiveCount());

        predictions.add(Label.TRUE(), 1.0, Label.FALSE());
        instance = new EvaluationReport("report 1");
        instance.addPredictions(predictions);
        assertEquals(1, instance.getFalsePositiveCount());

        predictions.add(Label.FALSE(), 0.0, Label.FALSE());
        instance = new EvaluationReport("report 1");
        instance.addPredictions(predictions);
        assertEquals(1, instance.getTrueNegativeCount());

        predictions.add(Label.FALSE(), 0.0, Label.TRUE());
        instance = new EvaluationReport("report 1");
        instance.addPredictions(predictions);
        assertEquals(1, instance.getFalseNegativeCount());

    }

    /**
     * Test of getRecall method, of class EvaluationReport.
     */
    @Test
    public void testGetRecall() {
        System.out.println("getRecall");
        EvaluationReport instance = new EvaluationReport("test");
        instance.setTrueNegativeCount(1);
        instance.setTruePositiveCount(2);
        instance.setFalseNegativeCount(3);
        instance.setFalsePositiveCount(4);

        double expResult = (2.0) / (2 + 3);
        double result = instance.getRecall();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of getPrecision method, of class EvaluationReport.
     */
    @Test
    public void testGetPrecision() {
        System.out.println("getPrecision");
        EvaluationReport instance = new EvaluationReport("test");
        instance.setTrueNegativeCount(1);
        instance.setTruePositiveCount(2);
        instance.setFalseNegativeCount(3);
        instance.setFalsePositiveCount(4);

        double expResult = (2.0) / (2 + 4);
        double result = instance.getPrecision();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of getFMeasure method, of class EvaluationReport.
     */
    @Test
    public void testGetFMeasure() {
        System.out.println("getFMeasure");
        EvaluationReport instance = new EvaluationReport("test");
        instance.setTrueNegativeCount(1);
        instance.setTruePositiveCount(2);
        instance.setFalseNegativeCount(3);
        instance.setFalsePositiveCount(4);

        double expResult = (2.0 * instance.getPrecision() * instance.getRecall()) / (instance.getPrecision() + instance.getRecall());
        double result = instance.getFMeasure();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of getPercentCorrect method, of class EvaluationReport.
     */
    @Test
    public void testGetPercentCorrect() {
        System.out.println("getPercentCorrect");
        EvaluationReport instance = new EvaluationReport("test");
        instance.setTrueNegativeCount(1);
        instance.setTruePositiveCount(2);
        instance.setFalseNegativeCount(3);
        instance.setFalsePositiveCount(4);

        double expResult = (1.0 + 2.0) / (10);
        double result = instance.getPercentCorrect();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of getPercentIncorrect method, of class EvaluationReport.
     */
    @Test
    public void testGetPercentIncorrect() {
        System.out.println("getPercentIncorrect");
        EvaluationReport instance = new EvaluationReport("test");
        instance.setTrueNegativeCount(1);
        instance.setTruePositiveCount(2);
        instance.setFalseNegativeCount(3);
        instance.setFalsePositiveCount(4);

        double expResult = 1 - instance.getPercentCorrect();
        double result = instance.getPercentIncorrect();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of getTotalCost method, of class EvaluationReport.
     */
    @Test
    public void testGetTotalCost_equalCost() {
        System.out.println("getTotalCost without equal cost");
        EvaluationReport instance = new EvaluationReport("test");
        instance.setTrueNegativeCount(1);
        instance.setTruePositiveCount(2);
        instance.setFalseNegativeCount(3);
        instance.setFalsePositiveCount(4);

        double expResult = 3 + 4;
        double result = instance.getTotalCost();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of getTotalCost method, of class EvaluationReport.
     */
    @Test
    public void testGetTotalCost_unequalCost() {
        System.out.println("getTotalCost with unequal cost");
        EvaluationReport instance = new EvaluationReport("test", 1, 2);
        instance.setTrueNegativeCount(1);
        instance.setTruePositiveCount(2);
        instance.setFalseNegativeCount(3);
        instance.setFalsePositiveCount(4);

        double expResult = 2 * 3 + 4;
        double result = instance.getTotalCost();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of getAverageCost method, of class EvaluationReport.
     */
    @Test
    public void testGetAverageCost_equalCost() {
        System.out.println("getAverageCost with equal cost");
        EvaluationReport instance = new EvaluationReport("test");
        instance.setTrueNegativeCount(1);
        instance.setTruePositiveCount(2);
        instance.setFalseNegativeCount(3);
        instance.setFalsePositiveCount(4);

        double expResult = instance.getPercentIncorrect();
        double result = instance.getAverageCost();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of getAverageCost method, of class EvaluationReport.
     */
    @Test
    public void testGetAverageCost_unequalCost() {
        System.out.println("getAverageCost with unequal cost");
        EvaluationReport instance = new EvaluationReport("test", 1, 2);
        instance.setTrueNegativeCount(1);
        instance.setTruePositiveCount(2);
        instance.setFalseNegativeCount(3);
        instance.setFalsePositiveCount(4);

        double expResult = instance.getTotalCost() / 10;
        double result = instance.getAverageCost();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of getTotalExamples method, of class EvaluationReport.
     */
    @Test
    public void testGetTotalExamples() {
        System.out.println("getTotalExamples");
        EvaluationReport instance = new EvaluationReport("test");
        instance.setTrueNegativeCount(1);
        instance.setTruePositiveCount(2);
        instance.setFalseNegativeCount(3);
        instance.setFalsePositiveCount(4);

        double expResult = 10;
        double result = instance.getTotalExamples();
        assertEquals(expResult, result, 0.0);
    }
}
