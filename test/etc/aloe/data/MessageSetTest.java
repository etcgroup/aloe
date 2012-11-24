/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package etc.aloe.data;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
        Message message = new Message(1, new Date(), "Alice", "hello");
        MessageSet messages = new MessageSet();
        messages.add(message);
        assertEquals(1, messages.size());
        assertEquals(message, messages.get(0));
    }

    /**
     * Test of getMessages method, of class MessageSet.
     */
    @Test
    public void testGetMessages() {
        System.out.println("getMessages");
        Message message = new Message(1, new Date(), "Alice", "hello");
        MessageSet messages = new MessageSet();
        assertEquals(0, messages.getMessages().size());

        messages.add(message);

        assertEquals(1, messages.getMessages().size());
        assertEquals(message, messages.getMessages().get(0));
    }

    /**
     * Test of load method, of class MessageSet.
     */
    @Test
    public void testLoad() throws Exception {
        System.out.println("load");
        String exampleData =
                "\"id\",\"time\",\"participant\",\"message\"\n"
                + "1,\"2005-01-04T00:07:47\",\"BERT\",\"15 hrs 59 min to 12deg twilight (at 16:07 UTC)\"\n"
                + "2,\"2005-01-04T00:07:48\",\"Ray\",\"hi bert\"\n"
                + "3,\"2005-01-04T00:07:48\",\"BERT\",\"ray, why did you create me?\"\n"
                + "4,\"2005-01-04T00:07:50\",\"BERT\",\"(sunrise at 16:48 UTC)\"\n"
                + "5,\"2005-01-04T00:07:55\",\"Ray\",\"To make you suffer\"\n";

        InputStream source = new ByteArrayInputStream(exampleData.getBytes());
        MessageSet messages = new MessageSet();
        String dateFormatString = "yyyy-MM-dd'T'HH:mm:ss";
        DateFormat dateFormat = new SimpleDateFormat(dateFormatString);
        messages.setDateFormat(dateFormat);

        assertTrue(messages.load(source));
        assertEquals(5, messages.size());

        assertEquals(1, messages.get(0).getId());
        assertEquals("15 hrs 59 min to 12deg twilight (at 16:07 UTC)", messages.get(0).getMessage());
        assertEquals("2005-01-04T00:07:48", dateFormat.format(messages.get(1).getTimestamp()));
        assertEquals("BERT", messages.get(2).getParticipant());
        assertEquals("To make you suffer", messages.get(4).getMessage());
    }

    /**
     * Test of load method, of class MessageSet, where the data has truth
     * labels.
     */
    @Test
    public void testLoad_withTruth() throws Exception {
        System.out.println("load_withTruth");
        String exampleData =
                "\"id\",\"time\",\"participant\",\"message\",\"truth\"\n"
                + "1,\"2005-01-04T00:07:47\",\"BERT\",\"15 hrs 59 min to 12deg twilight (at 16:07 UTC)\",true\n"
                + "2,\"2005-01-04T00:07:48\",\"Ray\",\"hi bert\",false\n"
                + "3,\"2005-01-04T00:07:48\",\"BERT\",\"ray, why did you create me?\"\n"
                + "4,\"2005-01-04T00:07:50\",\"BERT\",\"(sunrise at 16:48 UTC)\",\n"
                + "5,\"2005-01-04T00:07:55\",\"Ray\",\"To make you suffer\",true\n";

        InputStream source = new ByteArrayInputStream(exampleData.getBytes());
        MessageSet messages = new MessageSet();
        String dateFormatString = "yyyy-MM-dd'T'HH:mm:ss";
        DateFormat dateFormat = new SimpleDateFormat(dateFormatString);
        messages.setDateFormat(dateFormat);

        assertTrue(messages.load(source));
        assertEquals(5, messages.size());

        assertEquals(1, messages.get(0).getId());
        assertEquals("15 hrs 59 min to 12deg twilight (at 16:07 UTC)", messages.get(0).getMessage());
        assertEquals("2005-01-04T00:07:48", dateFormat.format(messages.get(1).getTimestamp()));
        assertEquals("BERT", messages.get(2).getParticipant());
        assertEquals("To make you suffer", messages.get(4).getMessage());

        assertEquals(true, messages.get(0).getTrueLabel());
        assertEquals(false, messages.get(1).getTrueLabel());
        assertEquals(null, messages.get(2).getTrueLabel());
        assertEquals(null, messages.get(3).getTrueLabel());
        assertEquals(true, messages.get(4).getTrueLabel());
    }

    /**
     * Test of save method, of class MessageSet.
     */
    @Test
    public void testSave() throws Exception {
        System.out.println("save");
        OutputStream destination = null;
        MessageSet instance = new MessageSet();
//        boolean expResult = false;
//        boolean result = instance.save(destination);
//        assertEquals(expResult, result);
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
    }

    /**
     * Test of setDateFormat method, of class MessageSet.
     */
    @Test
    public void testSetDateFormat() {
        System.out.println("setDateFormat");
        DateFormat dateFormat = new SimpleDateFormat();
        MessageSet instance = new MessageSet();
        instance.setDateFormat(dateFormat);
        assertEquals(dateFormat, instance.getDateFormat());
    }

    /**
     * Test of get method, of class MessageSet.
     */
    @Test
    public void testGet() {
        System.out.println("get");
        Message message = new Message(1, new Date(), "Alice", "hello");
        MessageSet messages = new MessageSet();
        messages.add(message);
        Message result = messages.get(0);
        assertEquals(message, result);

        try {
            messages.get(-1);
            assertTrue(false);
        } catch (IndexOutOfBoundsException e) {
            assertTrue(true);
        }

        try {
            messages.get(1);
            assertTrue(false);
        } catch (IndexOutOfBoundsException e) {
            assertTrue(true);
        }
    }

    /**
     * Test of size method, of class MessageSet.
     */
    @Test
    public void testSize() {
        System.out.println("size");
        MessageSet instance = new MessageSet();
        assertEquals(0, instance.size());

        instance.add(new Message(1, new Date(), "Alice", "Hello"));

        assertEquals(1, instance.size());
    }
}
