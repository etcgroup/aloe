/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package etc.aloe.cscw2013;

import etc.aloe.data.Segment;
import etc.aloe.data.SegmentSet;
import java.util.ArrayList;
import java.util.Collections;
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
public class DownsampleBalancingTest {

    private SegmentSet segmentSet;
    private int numTrue;
    private int numFalse;

    public DownsampleBalancingTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        numTrue = 5;
        numFalse = 11;

        List<Segment> segments = new ArrayList<Segment>();
        for (int i = 0; i < numTrue; i++) {
            segments.add(new Segment(true, null));
        }
        for (int i = 0; i < numFalse; i++) {
            segments.add(new Segment(false, null));
        }

        Collections.shuffle(segments);

        segmentSet = new SegmentSet();
        segmentSet.setSegments(segments);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of balance method, of class DownsampleBalancing.
     */
    @Test
    public void testBalance() {
        System.out.println("balance equally");


        DownsampleBalancing instance = new DownsampleBalancing(1, 1);

        SegmentSet result = instance.balance(segmentSet);

        //Should have an equal number of true and false examples
        assertEquals(numTrue, segmentSet.getCountWithTrueLabel(true));
        assertEquals(numFalse, segmentSet.getCountWithTrueLabel(false));
    }

    /**
     * Test of balance method, of class DownsampleBalancing.
     */
    @Test
    public void testBalance_withUnlabeled() {
        System.out.println("balance with unlabeled");


        DownsampleBalancing instance = new DownsampleBalancing(1, 1);

        segmentSet.add(new Segment(null, null));
        try {
            SegmentSet result = instance.balance(segmentSet);
            assertTrue(false);
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    /**
     * Test of balance method, of class DownsampleBalancing.
     */
    @Test
    public void testBalance_againstFalsePositive() {
        System.out.println("balance with high false positive cost");


        DownsampleBalancing instance = new DownsampleBalancing(2, 1);

        SegmentSet result = instance.balance(segmentSet);

        int actualTrue = segmentSet.getCountWithTrueLabel(true);
        int actualFalse = segmentSet.getCountWithTrueLabel(false);

        //Both are more than 0
        assertTrue(actualTrue > 0);
        assertTrue(actualFalse > 0);

        //The ratio of false/true should be 2:1
        assertEquals((double) actualFalse / actualTrue, 2.0, 0.1);
    }

    /**
     * Test of balance method, of class DownsampleBalancing.
     */
    @Test
    public void testBalance_againstFalseNegative() {
        System.out.println("balance with high false negative cost");


        DownsampleBalancing instance = new DownsampleBalancing(1, 2);

        SegmentSet result = instance.balance(segmentSet);

        int actualTrue = segmentSet.getCountWithTrueLabel(true);
        int actualFalse = segmentSet.getCountWithTrueLabel(false);

        //Both are more than 0
        assertTrue(actualTrue > 0);
        assertTrue(actualFalse > 0);

        //The ratio of false/true should be 1:2
        assertEquals((double) actualFalse / actualTrue, 0.5, 0.1);
    }
}
