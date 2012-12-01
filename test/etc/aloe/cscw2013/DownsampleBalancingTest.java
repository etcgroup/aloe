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

import etc.aloe.data.Segment;
import etc.aloe.data.SegmentSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
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
public class DownsampleBalancingTest {

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
    }

    private SegmentSet generateTestSegments(int numPositive, int numNegative) {
        List<Segment> segments = new ArrayList<Segment>();
        for (int i = 0; i < numPositive; i++) {
            segments.add(new Segment(true, null));
        }
        for (int i = 0; i < numNegative; i++) {
            segments.add(new Segment(false, null));
        }

        Collections.shuffle(segments);

        SegmentSet segmentSet = new SegmentSet();
        segmentSet.setSegments(segments);
        return segmentSet;
    }

    private List<SegmentSet> generateTestSegments(int numToGenerate) {
        Random random = new Random(24344);

        List<SegmentSet> segmentSets = new ArrayList<SegmentSet>();

        //Add some noop sets first
        segmentSets.add(generateTestSegments(50, 50));
        segmentSets.add(generateTestSegments(50, 100));
        segmentSets.add(generateTestSegments(100, 50));

        for (int i = 3; i < numToGenerate; i++) {
            int numPositive = random.nextInt(200) + 10;
            int numNegative = random.nextInt(200) + 10;

            segmentSets.add(generateTestSegments(numPositive, numNegative));
        }

        return segmentSets;
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

        List<SegmentSet> segmentSets = generateTestSegments(10);
        for (SegmentSet segmentSet : segmentSets) {
            DownsampleBalancing instance = new DownsampleBalancing(1, 1);

            SegmentSet result = instance.balance(segmentSet);

            int actualTrue = result.getCountWithTrueLabel(true);
            int actualFalse = result.getCountWithTrueLabel(false);

            //Both are more than 0
            assertTrue(actualTrue > 0);
            assertTrue(actualFalse > 0);

            //Both are the same or reduced
            assertTrue(actualTrue <= segmentSet.getCountWithTrueLabel(true));
            assertTrue(actualFalse <= segmentSet.getCountWithTrueLabel(false));

            //Should have an equal number of true and false examples
            assertEquals(actualTrue, actualFalse);
        }
    }

    /**
     * Test of balance method, of class DownsampleBalancing.
     */
    @Test
    public void testBalance_withUnlabeled() {
        System.out.println("balance with unlabeled");

        SegmentSet segmentSet = generateTestSegments(20, 110);
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

        List<SegmentSet> segmentSets = generateTestSegments(10);
        for (SegmentSet segmentSet : segmentSets) {
            DownsampleBalancing instance = new DownsampleBalancing(2, 1);

            SegmentSet result = instance.balance(segmentSet);

            int actualTrue = result.getCountWithTrueLabel(true);
            int actualFalse = result.getCountWithTrueLabel(false);

            //Both are more than 0
            assertTrue(actualTrue > 0);
            assertTrue(actualFalse > 0);

            //Both are the same or reduced
            assertTrue(actualTrue <= segmentSet.getCountWithTrueLabel(true));
            assertTrue(actualFalse <= segmentSet.getCountWithTrueLabel(false));

            //The ratio of false/true should be 2:1
            assertEquals(2.0, (double) actualFalse / actualTrue, 0.1);
        }
    }

    /**
     * Test of balance method, of class DownsampleBalancing.
     */
    @Test
    public void testBalance_againstFalseNegative() {
        System.out.println("balance with high false negative cost");

        List<SegmentSet> segmentSets = generateTestSegments(10);
        for (SegmentSet segmentSet : segmentSets) {
            DownsampleBalancing instance = new DownsampleBalancing(1, 2);

            SegmentSet result = instance.balance(segmentSet);

            int actualTrue = result.getCountWithTrueLabel(true);
            int actualFalse = result.getCountWithTrueLabel(false);

            //Both are more than 0
            assertTrue(actualTrue > 0);
            assertTrue(actualFalse > 0);

            //Both are the same or reduced
            assertTrue(actualTrue <= segmentSet.getCountWithTrueLabel(true));
            assertTrue(actualFalse <= segmentSet.getCountWithTrueLabel(false));

            //The ratio of false/true should be 1:2
            assertEquals(0.5, (double) actualFalse / actualTrue, 0.1);
        }
    }
}
