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

import java.util.Date;
import java.util.List;
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
public class SegmentTest {

    public SegmentTest() {
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
     * Test of getId method, of class Segment.
     */
    @Test
    public void testGetId() {
        System.out.println("getId");
        Segment seg0 = new Segment();
        Segment seg1 = new Segment();
        Segment seg2 = new Segment();

        assertEquals(seg0.getId() + 1, seg1.getId());
        assertEquals(seg1.getId() + 1, seg2.getId());
    }

    /**
     * Test of concatMessages method, of class Segment.
     */
    @Test
    public void testConcatMessages() {
        System.out.println("concatMessages");
        Segment segment = new Segment();
        segment.add(new Message(0, new Date(), "Alice", "it's"));
        segment.add(new Message(1, new Date(), "Bob", "cow"));
        segment.add(new Message(2, new Date(), "Alice", "time"));

        String expResult = "it's cow time";
        String result = segment.concatMessages();
        assertEquals(expResult, result);
    }

    /**
     * Test of add method, of class Segment.
     */
    @Test
    public void testAdd() {
        System.out.println("add");
        Message message = new Message(0, new Date(), "Alice", "hello");
        Segment instance = new Segment();

        instance.add(message);
        List<Message> messages = instance.getMessages();
        assertEquals(1, messages.size());
        assertEquals(message, messages.get(0));
        assertEquals(instance.getId(), messages.get(0).getSegmentId());
    }

    /**
     * Test of getMessages method, of class Segment.
     */
    @Test
    public void testGetMessages() {
        System.out.println("getMessages");
        Segment instance = new Segment();
        List result = instance.getMessages();
        assertEquals(0, result.size());
    }

    /**
     * Test of hasTrueLabel method, of class Segment.
     */
    @Test
    public void testHasTrueLabel() {
        System.out.println("hasTrueLabel");
        Segment segment = new Segment();

        assertEquals(false, segment.hasTrueLabel());
        segment.setTrueLabel(Label.TRUE());
        assertEquals(true, segment.hasTrueLabel());
        segment.setTrueLabel(Label.FALSE());
        assertEquals(true, segment.hasTrueLabel());
        segment.setTrueLabel(null);
        assertEquals(false, segment.hasTrueLabel());
    }

    /**
     * Test of hasPredictedLabel method, of class Segment.
     */
    @Test
    public void testHasPredictedLabel() {
        System.out.println("hasPredictedLabel");
        Segment segment = new Segment();

        assertEquals(false, segment.hasPredictedLabel());
        segment.setPredictedLabel(Label.TRUE());
        assertEquals(true, segment.hasPredictedLabel());
        segment.setPredictedLabel(Label.FALSE());
        assertEquals(true, segment.hasPredictedLabel());
        segment.setPredictedLabel(null);
        assertEquals(false, segment.hasPredictedLabel());
    }
}
