/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package etc.aloe.cscw2013;

import etc.aloe.data.Message;
import etc.aloe.data.Segment;
import java.util.Date;
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
public class ResolutionImplTest {

    public ResolutionImplTest() {
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
     * Test of resolveLabel method, of class ResolutionImpl.
     */
    @Test
    public void testResolveLabel_AllNegative() {
        System.out.println("resolveLabel_AllNegative");
        Segment segment = new Segment();
        segment.add(new Message(1, new Date(), "Alice", "Hello", Boolean.FALSE));
        segment.add(new Message(2, new Date(), "Bob", "Hello", Boolean.FALSE));
        segment.add(new Message(3, new Date(), "Alice", "How are you", Boolean.FALSE));
        segment.add(new Message(4, new Date(), "Bob", "Very well thanks", Boolean.FALSE));
        segment.add(new Message(5, new Date(), "Alice", "Yay", Boolean.FALSE));

        ResolutionImpl instance = new ResolutionImpl();
        Boolean expResult = Boolean.FALSE;
        Boolean result = instance.resolveLabel(segment);
        assertEquals(expResult, result);
    }

    /**
     * Test of resolveLabel method, of class ResolutionImpl.
     */
    @Test
    public void testResolveLabel_AllPositive() {
        System.out.println("resolveLabel_AllPositive");
        Segment segment = new Segment();
        segment.add(new Message(1, new Date(), "Alice", "Hello", Boolean.TRUE));
        segment.add(new Message(2, new Date(), "Bob", "Hello", Boolean.TRUE));
        segment.add(new Message(3, new Date(), "Alice", "How are you", Boolean.TRUE));
        segment.add(new Message(4, new Date(), "Bob", "Very well thanks", Boolean.TRUE));
        segment.add(new Message(5, new Date(), "Alice", "Yay", Boolean.TRUE));

        ResolutionImpl instance = new ResolutionImpl();
        Boolean expResult = Boolean.TRUE;
        Boolean result = instance.resolveLabel(segment);
        assertEquals(expResult, result);
    }

    /**
     * Test of resolveLabel method, of class ResolutionImpl.
     */
    @Test
    public void testResolveLabel_OnePositive() {
        System.out.println("resolveLabel_OnePositive");
        Segment segment = new Segment();
        segment.add(new Message(1, new Date(), "Alice", "Hello", Boolean.FALSE));
        segment.add(new Message(2, new Date(), "Bob", "Hello", Boolean.TRUE));
        segment.add(new Message(3, new Date(), "Alice", "How are you", Boolean.FALSE));
        segment.add(new Message(4, new Date(), "Bob", "Very well thanks", Boolean.FALSE));
        segment.add(new Message(5, new Date(), "Alice", "Yay", Boolean.FALSE));

        ResolutionImpl instance = new ResolutionImpl();
        Boolean expResult = Boolean.TRUE;
        Boolean result = instance.resolveLabel(segment);
        assertEquals(expResult, result);
    }

    /**
     * Test of resolveLabel method, of class ResolutionImpl.
     */
    @Test
    public void testResolveLabel_OneNegative() {
        System.out.println("resolveLabel_OneNegative");
        Segment segment = new Segment();
        segment.add(new Message(1, new Date(), "Alice", "Hello", Boolean.TRUE));
        segment.add(new Message(2, new Date(), "Bob", "Hello", Boolean.TRUE));
        segment.add(new Message(3, new Date(), "Alice", "How are you", Boolean.TRUE));
        segment.add(new Message(4, new Date(), "Bob", "Very well thanks", Boolean.FALSE));
        segment.add(new Message(5, new Date(), "Alice", "Yay", Boolean.TRUE));

        ResolutionImpl instance = new ResolutionImpl();
        Boolean expResult = Boolean.TRUE;
        Boolean result = instance.resolveLabel(segment);
        assertEquals(expResult, result);
    }

    /**
     * Test of resolveLabel method, of class ResolutionImpl.
     */
    @Test
    public void testResolveLabel_Unlabeled() {
        System.out.println("resolveLabel_Unlabeled");
        Segment segment = new Segment();
        segment.add(new Message(1, new Date(), "Alice", "Hello", null));
        segment.add(new Message(2, new Date(), "Bob", "Hello", null));
        segment.add(new Message(3, new Date(), "Alice", "How are you", null));
        segment.add(new Message(4, new Date(), "Bob", "Very well thanks", null));
        segment.add(new Message(5, new Date(), "Alice", "Yay", null));

        ResolutionImpl instance = new ResolutionImpl();
        Boolean expResult = null;
        Boolean result = instance.resolveLabel(segment);
        assertEquals(expResult, result);
    }
}
