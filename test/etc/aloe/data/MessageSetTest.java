/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package etc.aloe.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
    public void testLoad_withLabels() throws Exception {
        System.out.println("load_withLabels");
        String exampleData =
                "\"id\",\"time\",\"participant\",\"message\",truth,predicted,segment\n"
                + "1,\"2005-01-04T00:07:47\",\"BERT\",\"15 hrs 59 min to 12deg twilight (at 16:07 UTC)\",true,false,3\n"
                + "2,\"2005-01-04T00:07:48\",\"Ray\",\"hi bert\",false,true,5\n"
                + "3,\"2005-01-04T00:07:48\",\"BERT\",\"ray, why did you create me?\",,,\n"
                + "4,\"2005-01-04T00:07:50\",\"BERT\",\"(sunrise at 16:48 UTC)\",,false,\n"
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
        assertEquals(false, messages.get(0).getPredictedLabel());
        assertEquals(3, messages.get(0).getSegmentId());

        assertEquals(false, messages.get(1).getTrueLabel());
        assertEquals(true, messages.get(1).getPredictedLabel());
        assertEquals(5, messages.get(1).getSegmentId());

        assertEquals(null, messages.get(2).getTrueLabel());
        assertEquals(null, messages.get(2).getPredictedLabel());
        assertEquals(-1, messages.get(2).getSegmentId());

        assertEquals(null, messages.get(3).getTrueLabel());
        assertEquals(false, messages.get(3).getPredictedLabel());
        assertEquals(-1, messages.get(3).getSegmentId());

        assertEquals(true, messages.get(4).getTrueLabel());
        assertEquals(null, messages.get(4).getPredictedLabel());
        assertEquals(-1, messages.get(4).getSegmentId());
    }

    /**
     * Test of save method, of class MessageSet.
     */
    @Test
    public void testSave() throws Exception {
        System.out.println("save");

        String dateFormatString = "yyyy-MM-dd'T'HH:mm:ss";
        DateFormat dateFormat = new SimpleDateFormat(dateFormatString);

        MessageSet messages = new MessageSet();
        messages.setDateFormat(dateFormat);
        messages.add(new Message(1, new Date(), "Alice", "hello", true, true));
        messages.add(new Message(2, new Date(), "Bob", "goodbye", false, false, 2));
        messages.add(new Message(3, new Date(), "Alice", "cow", null, true));
        messages.add(new Message(4, new Date(), "Bob", "time", false, null, 4));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        messages.save(out);
        out.close();

        byte[] bytes = out.toByteArray();
        assertTrue(bytes.length > 0);

        String foo = new String(bytes);
        System.err.println(foo);
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        MessageSet readMessages = new MessageSet();
        readMessages.setDateFormat(dateFormat);
        readMessages.load(in);
        in.close();

        assertEquals(messages.size(), readMessages.size());

        for (int i = 0; i < messages.size(); i++) {
            assertEquals(messages.get(i).getId(), readMessages.get(i).getId());
            assertEquals(messages.get(i).getParticipant(), readMessages.get(i).getParticipant());
            assertEquals(messages.get(i).getMessage(), readMessages.get(i).getMessage());
            assertEquals(messages.get(i).getTimestamp().toString(), readMessages.get(i).getTimestamp().toString());
            assertEquals(messages.get(i).getTrueLabel(), readMessages.get(i).getTrueLabel());
            assertEquals(messages.get(i).getPredictedLabel(), readMessages.get(i).getPredictedLabel());
            assertEquals(messages.get(i).getSegmentId(), readMessages.get(i).getSegmentId());
        }
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
