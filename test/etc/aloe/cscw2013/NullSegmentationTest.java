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
package etc.aloe.cscw2013;

import etc.aloe.data.Message;
import etc.aloe.data.MessageSet;
import etc.aloe.data.Segment;
import etc.aloe.data.SegmentSet;
import java.util.Date;
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
public class NullSegmentationTest {

    public NullSegmentationTest() {
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
     * Test of segment method, of class NullSegmentation.
     */
    @Test
    public void testSegment() {
        System.out.println("segment");
        MessageSet messages = new MessageSet();

        long now = new Date().getTime();
        long second = 1 * 1000;
        long minute = 60 * 1000;

        messages.add(new Message(0, new Date(now), "Alice", "it's"));
        messages.add(new Message(1, new Date(now + second), "Bob", "cow"));
        messages.add(new Message(2, new Date(now + 2 * second), "Alice", "time"));
        messages.add(new Message(3, new Date(now + minute), "Bob", "noooooooo"));
        messages.add(new Message(4, new Date(now + minute + second), "Bob", "once"));
        messages.add(new Message(5, new Date(now + 2 * minute), "Alice", "upon"));
        messages.add(new Message(6, new Date(now + 3 * minute), "Bob", "a"));
        messages.add(new Message(7, new Date(now + 3 * minute + second), "Alice", "time"));

        NullSegmentation instance = new NullSegmentation();
        SegmentSet segments = instance.segment(messages);

        //Expecting the same number of segments as messages
        assertEquals(messages.size(), segments.size());

        for (int i = 0; i < messages.size(); i++) {
            Message message = messages.get(i);
            Segment segment = segments.get(i);

            assertEquals(1, segment.getMessages().size());
            assertEquals(message, segment.getMessages().get(0));
        }
    }

}
