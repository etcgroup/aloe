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
import java.util.ArrayList;
import java.util.Arrays;
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
public class LabelMappingImplTest {

    private MessageSet rawMessages;
    private SegmentSet segments;
    private List<Boolean> predictedLabels;
    private List<Boolean> messageLabels;

    public LabelMappingImplTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        this.rawMessages = new MessageSet();
        rawMessages.add(new Message(1, new Date(), "Alice", "Hello1"));
        rawMessages.add(new Message(2, new Date(), "Bob", "Hello2"));
        rawMessages.add(new Message(3, new Date(), "Alice", "Hello3"));
        rawMessages.add(new Message(4, new Date(), "Bob", "Hello4"));
        rawMessages.add(new Message(5, new Date(), "Alice", "Hello5"));
        rawMessages.add(new Message(6, new Date(), "Bob", "Hello6"));
        rawMessages.add(new Message(7, new Date(), "Alice", "Hello7"));
        rawMessages.add(new Message(8, new Date(), "Alice", "Hello8"));
        rawMessages.add(new Message(9, new Date(), "Alice", "Hello9"));
        rawMessages.add(new Message(10, new Date(), "Alice", "Hello10"));

        this.segments = new SegmentSet();
        Segment seg = new Segment();
        seg.add(rawMessages.get(0));
        this.segments.add(seg);

        seg = new Segment();
        seg.add(rawMessages.get(1));
        seg.add(rawMessages.get(2));
        this.segments.add(seg);

        seg = new Segment();
        seg.add(rawMessages.get(3));
        seg.add(rawMessages.get(4));
        seg.add(rawMessages.get(5));
        this.segments.add(seg);

        seg = new Segment();
        seg.add(rawMessages.get(6));
        seg.add(rawMessages.get(7));
        seg.add(rawMessages.get(8));
        seg.add(rawMessages.get(9));
        this.segments.add(seg);

        this.predictedLabels = new ArrayList<Boolean>();
        this.predictedLabels.add(Boolean.TRUE);
        this.predictedLabels.add(Boolean.FALSE);
        this.predictedLabels.add(Boolean.TRUE);
        this.predictedLabels.add(Boolean.FALSE);

        this.messageLabels = Arrays.asList(new Boolean[] {
            true,
            false, false,
            true, true, true,
            false, false, false, false
        });

    }

    @After
        public void tearDown() {
    }

    /**
     * Test of map method, of class LabelMappingImpl.
     */
    @Test
        public void testMap() {
        System.out.println("predict");
        LabelMappingImpl instance = new LabelMappingImpl();
        instance.map(predictedLabels, this.segments);

        for (int m = 0; m < rawMessages.size(); m++) {
            Message message = rawMessages.get(m);
            Boolean expected = messageLabels.get(m);
            Boolean actual = message.getPredictedLabel();

            assertTrue(expected == actual);
        }

    }
}
