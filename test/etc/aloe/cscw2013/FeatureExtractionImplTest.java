/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package etc.aloe.cscw2013;

import etc.aloe.data.ExampleSet;
import etc.aloe.data.FeatureSpecification;
import etc.aloe.data.SegmentSet;
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
public class FeatureExtractionImplTest {

    public FeatureExtractionImplTest() {
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
     * Test of extractFeatures method, of class FeatureExtractionImpl.
     */
    @Test
    public void testExtractFeatures() {
        System.out.println("extractFeatures");
        SegmentSet segments = null;
        FeatureSpecification spec = null;
        FeatureExtractionImpl instance = new FeatureExtractionImpl();
        ExampleSet expResult = null;
//        ExampleSet result = instance.extractFeatures(segments, spec);
//        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}
