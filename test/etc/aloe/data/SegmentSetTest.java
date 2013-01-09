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
 * @author Michael Brooks <mjbrooks@uw.edu>
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
        seg0.setTrueLabel(Label.TRUE());
        segments.add(seg0);

        Segment seg1 = new Segment();
        seg1.add(new Message(3, new Date(), "Bob", "noooooooo"));
        seg1.setTrueLabel(Label.FALSE());
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
        SegmentSet segments = new SegmentSet();

        Segment seg0 = new Segment(Label.TRUE(), null);
        Segment seg1 = new Segment(null, null);
        Segment seg2 = new Segment(Label.FALSE(), null);

        segments.add(seg0);
        segments.add(seg1);
        segments.add(seg2);

        SegmentSet result = segments.onlyLabeled();
        assertEquals(2, result.size());
        assertEquals(seg0, result.get(0));
        assertEquals(seg2, result.get(1));
    }

    /**
     * Test of getCountWithTrueLabel method, of class SegmentSet.
     */
    @Test
    public void testGetCountWithTrueLabel() {
        System.out.println("getCountWithTrueLabel");

        SegmentSet segments = new SegmentSet();
        segments.add(new Segment(Label.TRUE(), null));
        segments.add(new Segment(Label.TRUE(), null));
        segments.add(new Segment(Label.FALSE(), null));
        segments.add(new Segment(Label.FALSE(), null));
        segments.add(new Segment(Label.FALSE(), null));
        segments.add(new Segment(null, null));

        assertEquals(2, segments.getCountWithTrueLabel(Label.TRUE()));
        assertEquals(3, segments.getCountWithTrueLabel(Label.FALSE()));
        assertEquals(1, segments.getCountWithTrueLabel(null));
    }
}
