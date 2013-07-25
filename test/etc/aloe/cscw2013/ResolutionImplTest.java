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

import etc.aloe.data.Label;
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
 * @author Michael Brooks <mjbrooks@uw.edu>
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
        Label.startLabelSet();
        Label.FALSE();
        Label.TRUE();
        Label.closeLabelSet();
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
        segment.add(new Message(1, new Date(), "Alice", "Hello", Label.FALSE()));
        segment.add(new Message(2, new Date(), "Bob", "Hello", Label.FALSE()));
        segment.add(new Message(3, new Date(), "Alice", "How are you", Label.FALSE()));
        segment.add(new Message(4, new Date(), "Bob", "Very well thanks", Label.FALSE()));
        segment.add(new Message(5, new Date(), "Alice", "Yay", Label.FALSE()));

        ResolutionImpl instance = new ResolutionImpl();
        Label expResult = Label.FALSE();
        Label result = instance.resolveLabel(segment);
        assertEquals(expResult, result);
    }

    /**
     * Test of resolveLabel method, of class ResolutionImpl.
     */
    @Test
    public void testResolveLabel_AllPositive() {
        System.out.println("resolveLabel_AllPositive");
        Segment segment = new Segment();
        segment.add(new Message(1, new Date(), "Alice", "Hello", Label.TRUE()));
        segment.add(new Message(2, new Date(), "Bob", "Hello", Label.TRUE()));
        segment.add(new Message(3, new Date(), "Alice", "How are you", Label.TRUE()));
        segment.add(new Message(4, new Date(), "Bob", "Very well thanks", Label.TRUE()));
        segment.add(new Message(5, new Date(), "Alice", "Yay", Label.TRUE()));

        ResolutionImpl instance = new ResolutionImpl();
        Label expResult = Label.TRUE();
        Label result = instance.resolveLabel(segment);
        assertEquals(expResult, result);
    }

    /**
     * Test of resolveLabel method, of class ResolutionImpl.
     */
    @Test
    public void testResolveLabel_OnePositive() {
        System.out.println("resolveLabel_OnePositive");
        Segment segment = new Segment();
        segment.add(new Message(1, new Date(), "Alice", "Hello", Label.FALSE()));
        segment.add(new Message(2, new Date(), "Bob", "Hello", Label.TRUE()));
        segment.add(new Message(3, new Date(), "Alice", "How are you", Label.FALSE()));
        segment.add(new Message(4, new Date(), "Bob", "Very well thanks", Label.FALSE()));
        segment.add(new Message(5, new Date(), "Alice", "Yay", Label.FALSE()));

        ResolutionImpl instance = new ResolutionImpl();
        Label expResult = Label.TRUE();
        Label result = instance.resolveLabel(segment);
        assertEquals(expResult, result);
    }

    /**
     * Test of resolveLabel method, of class ResolutionImpl.
     */
    @Test
    public void testResolveLabel_OneNegative() {
        System.out.println("resolveLabel_OneNegative");
        Segment segment = new Segment();
        segment.add(new Message(1, new Date(), "Alice", "Hello", Label.TRUE()));
        segment.add(new Message(2, new Date(), "Bob", "Hello", Label.TRUE()));
        segment.add(new Message(3, new Date(), "Alice", "How are you", Label.TRUE()));
        segment.add(new Message(4, new Date(), "Bob", "Very well thanks", Label.FALSE()));
        segment.add(new Message(5, new Date(), "Alice", "Yay", Label.TRUE()));

        ResolutionImpl instance = new ResolutionImpl();
        Label expResult = Label.TRUE();
        Label result = instance.resolveLabel(segment);
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
        Label expResult = null;
        Label result = instance.resolveLabel(segment);
        assertEquals(expResult, result);
    }
}
