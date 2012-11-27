/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package etc.aloe.data;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author michael
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

        EvaluationReport eval = new EvaluationReport();
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
        EvaluationReport partial = new EvaluationReport();
        partial.setTrueNegativeCount(1);
        partial.setTruePositiveCount(2);
        partial.setFalseNegativeCount(3);
        partial.setFalsePositiveCount(4);

        EvaluationReport instance = new EvaluationReport();
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

        EvaluationReport instance = new EvaluationReport();
        instance.recordPrediction(true, true);
        assertEquals(1, instance.getTruePositiveCount());

        instance.recordPrediction(true, false);
        assertEquals(1, instance.getFalsePositiveCount());

        instance.recordPrediction(false, false);
        assertEquals(1, instance.getTrueNegativeCount());

        instance.recordPrediction(false, true);
        assertEquals(1, instance.getFalseNegativeCount());

    }

    /**
     * Test of getRecall method, of class EvaluationReport.
     */
    @Test
    public void testGetRecall() {
        System.out.println("getRecall");
        EvaluationReport instance = new EvaluationReport();
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
        EvaluationReport instance = new EvaluationReport();
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
        EvaluationReport instance = new EvaluationReport();
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
        EvaluationReport instance = new EvaluationReport();
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
        EvaluationReport instance = new EvaluationReport();
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
        EvaluationReport instance = new EvaluationReport();
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
        EvaluationReport instance = new EvaluationReport(1, 2);
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
        EvaluationReport instance = new EvaluationReport();
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
        EvaluationReport instance = new EvaluationReport(1, 2);
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
        EvaluationReport instance = new EvaluationReport();
        instance.setTrueNegativeCount(1);
        instance.setTruePositiveCount(2);
        instance.setFalseNegativeCount(3);
        instance.setFalsePositiveCount(4);

        double expResult = 10;
        double result = instance.getTotalExamples();
        assertEquals(expResult, result, 0.0);
    }
}
