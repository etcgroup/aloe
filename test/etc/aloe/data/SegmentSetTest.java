/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package etc.aloe.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import weka.core.Instances;

/**
 *
 * @author michael
 */
public class SegmentSetTest {

    public SegmentSetTest() {
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
     * Test of add method, of class SegmentSet.
     */
    @Test
    public void testAdd() {
        System.out.println("add");
        Segment segment = new Segment();
        SegmentSet instance = new SegmentSet();
        instance.add(segment);
        assertEquals(segment, instance.get(0));
    }

    /**
     * Test of size method, of class SegmentSet.
     */
    @Test
    public void testSize() {
        System.out.println("size");
        Segment segment = new Segment();
        SegmentSet instance = new SegmentSet();
        assertEquals(0, instance.size());
        instance.add(segment);
        assertEquals(1, instance.size());
    }

    /**
     * Test of getSegments method, of class SegmentSet.
     */
    @Test
    public void testGetSegments() {
        System.out.println("getSegments");
        Segment segment = new Segment();
        SegmentSet instance = new SegmentSet();
        instance.add(segment);
        List result = instance.getSegments();
        assertEquals(1, result.size());
        assertEquals(segment, result.get(0));
    }

    /**
     * Test of setSegments method, of class SegmentSet.
     */
    @Test
    public void testSetSegments() {
        System.out.println("setSegments");
        List<Segment> segments = new ArrayList<Segment>();
        segments.add(new Segment());
        SegmentSet instance = new SegmentSet();
        instance.setSegments(segments);

        assertEquals(segments, instance.getSegments());
    }

    /**
     * Test ofgetBasicExamples method, of class SegmentSet.
     */
    @Test
    public void testGetBasicExamples() {
        System.out.println("getBasicExamples");
        SegmentSet segments = new SegmentSet();

        Segment seg0 = new Segment();
        seg0.add(new Message(0, new Date(), "Alice", "it's"));
        seg0.add(new Message(1, new Date(), "Bob", "cow"));
        seg0.add(new Message(2, new Date(), "Alice", "time"));
        seg0.setTrueLabel(Boolean.TRUE);
        segments.add(seg0);

        Segment seg1 = new Segment();
        seg1.add(new Message(3, new Date(), "Bob", "noooooooo"));
        seg1.setTrueLabel(Boolean.FALSE);
        segments.add(seg1);

        ExampleSet examples = segments.getBasicExamples();
        assertEquals(segments.size(), examples.size());

        Instances instances = examples.getInstances();
        assertEquals(ExampleSet.ID_ATTR_NAME, instances.attribute(0).name());
        assertEquals(ExampleSet.MESSAGE_ATTR_NAME, instances.attribute(1).name());
        assertEquals(ExampleSet.LABEL_ATTR_NAME, instances.classAttribute().name());

        assertEquals(seg0.getId(), instances.get(0).value(0), 0);
        assertEquals(seg0.concatMessages(), instances.get(0).stringValue(1));
        assertEquals(seg0.getTrueLabel(), examples.getClassLabel(instances.get(0).value(instances.classAttribute())));

        assertEquals(seg1.getId(), instances.get(1).value(0), 0);
        assertEquals(seg1.concatMessages(), instances.get(1).stringValue(1));
        assertEquals(seg1.getTrueLabel(), examples.getClassLabel(instances.get(1).value(instances.classAttribute())));

    }

    /**
     * Test of get method, of class SegmentSet.
     */
    @Test
    public void testGet() {
        System.out.println("get");

        SegmentSet instance = new SegmentSet();
        Segment seg0 = new Segment();
        Segment seg1 = new Segment();
        instance.add(seg0);
        instance.add(seg1);

        assertEquals(seg0, instance.get(0));
        assertEquals(seg1, instance.get(1));

        try {
            instance.get(2);
            assertTrue(false);
        } catch (IndexOutOfBoundsException e) {
            assertTrue(true);
        }
    }

    /**
     * Test of onlyLabeled method, of class SegmentSet.
     */
    @Test
    public void testOnlyLabeled() {
        System.out.println("onlyLabeled");
        SegmentSet instance = new SegmentSet();
        SegmentSet expResult = null;
        SegmentSet result = instance.onlyLabeled();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}
