/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package etc.aloe.data;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.List;
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
public class MessageSetTest {

    public MessageSetTest() {
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
     * Test of add method, of class MessageSet.
     */
    @Test
    public void testAdd() {
        System.out.println("add");
        Message message = null;
        MessageSet instance = new MessageSet();
        instance.add(message);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getMessages method, of class MessageSet.
     */
    @Test
    public void testGetMessages() {
        System.out.println("getMessages");
        MessageSet instance = new MessageSet();
        List expResult = null;
        List result = instance.getMessages();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of load method, of class MessageSet.
     */
    @Test
    public void testLoad() throws Exception {
        System.out.println("load");
        InputStream source = null;
        MessageSet instance = new MessageSet();
        boolean expResult = false;
        boolean result = instance.load(source);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of save method, of class MessageSet.
     */
    @Test
    public void testSave() throws Exception {
        System.out.println("save");
        OutputStream destination = null;
        MessageSet instance = new MessageSet();
        boolean expResult = false;
        boolean result = instance.save(destination);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDateFormat method, of class MessageSet.
     */
    @Test
    public void testGetDateFormat() {
        System.out.println("getDateFormat");
        MessageSet instance = new MessageSet();
        DateFormat expResult = null;
        DateFormat result = instance.getDateFormat();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setDateFormat method, of class MessageSet.
     */
    @Test
    public void testSetDateFormat() {
        System.out.println("setDateFormat");
        DateFormat dateFormat = null;
        MessageSet instance = new MessageSet();
        instance.setDateFormat(dateFormat);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of get method, of class MessageSet.
     */
    @Test
    public void testGet() {
        System.out.println("get");
        int i = 0;
        MessageSet instance = new MessageSet();
        Message expResult = null;
        Message result = instance.get(i);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of size method, of class MessageSet.
     */
    @Test
    public void testSize() {
        System.out.println("size");
        MessageSet instance = new MessageSet();
        int expResult = 0;
        int result = instance.size();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}
