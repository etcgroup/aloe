/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package etc.aloe.cscw2013;

import etc.aloe.data.ExampleSet;
import etc.aloe.data.MessageSet;
import etc.aloe.data.Model;
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
public class PredictionImplTest {

    public PredictionImplTest() {
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
     * Test of predict method, of class PredictionImpl.
     */
    @Test
    public void testPredict() {
        System.out.println("predict");
        ExampleSet examples = null;
        Model model = null;
        MessageSet rawMessages = null;
        PredictionImpl instance = new PredictionImpl();
        MessageSet expResult = null;
//        MessageSet result = instance.predict(examples, model, rawMessages);
//        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}
