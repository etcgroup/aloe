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

import etc.aloe.TestLabelable;
import etc.aloe.processes.CrossValidationPrep;
import java.util.ArrayList;
import java.util.Arrays;
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
public class CrossValidationPrepTest {

    private List<TestLabelable> originalItems;

    public CrossValidationPrepTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        TestLabelable[] itemsArray = new TestLabelable[]{
            new TestLabelable("one", Label.TRUE(), Label.TRUE()),
            new TestLabelable("two", Label.FALSE(), Label.TRUE()),
            new TestLabelable("three", Label.FALSE(), Label.TRUE()),
            new TestLabelable("four", Label.TRUE(), Label.TRUE()),
            new TestLabelable("five", Label.FALSE(), Label.TRUE()),
            new TestLabelable("six", Label.TRUE(), Label.TRUE()),
            new TestLabelable("seven", Label.TRUE(), Label.TRUE()),
            new TestLabelable("eight", Label.TRUE(), Label.TRUE()),
            new TestLabelable("nine", Label.FALSE(), Label.TRUE()),
            new TestLabelable("ten", Label.FALSE(), Label.TRUE()),
            new TestLabelable("eleven", Label.FALSE(), Label.TRUE()),
            new TestLabelable("twelve", Label.TRUE(), Label.TRUE())
        };

        this.originalItems = Arrays.asList(itemsArray);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of randomize method, of class CrossValidationPrep.
     */
    @Test
    public void testRandomize() {
        System.out.println("randomize");

        //Repeat the test 5 times
        for (int i = 0; i < 5; i++) {

            List<TestLabelable> randomizedItems = new ArrayList<TestLabelable>(originalItems);

            CrossValidationPrep<TestLabelable> instance = new CrossValidationPrep<TestLabelable>();
            instance.randomize(randomizedItems);

            assertEquals("size unchanged", originalItems.size(), randomizedItems.size());

            int numNotEqual = 0;
            for (int j = 0; j < originalItems.size(); j++) {
                if (originalItems.get(j) != randomizedItems.get(j)) {
                    numNotEqual++;
                }
            }
            assertTrue("at least one item was moved", numNotEqual > 0);
        }
    }

    /**
     * Test of swap method, of class CrossValidationPrep.
     */
    @Test
    public void testSwap() {
        System.out.println("swap");

        List<TestLabelable> items = new ArrayList<TestLabelable>(originalItems);

        CrossValidationPrep instance = new CrossValidationPrep();
        instance.swap(items, 0, items.size() - 1);

        assertEquals("size unchanged", originalItems.size(), items.size());
        assertEquals("first moved to last", originalItems.get(0), items.get(items.size() - 1));
        assertEquals("last moved to first", originalItems.get(originalItems.size() - 1), items.get(0));
    }

    /**
     * Test of stratify method, of class CrossValidationPrep.
     */
    @Test
    public void testStratify() {
        System.out.println("stratify");

        List<TestLabelable> items = new ArrayList<TestLabelable>(originalItems);

        int numStrats = 3;
        CrossValidationPrep instance = new CrossValidationPrep();
        List<TestLabelable> stratified = instance.stratify(items, numStrats);

        assertEquals("size unchanged", originalItems.size(), stratified.size());

        int stratSize = originalItems.size() / numStrats;
        for (int i = stratSize; i < originalItems.size(); i++) {
            TestLabelable item = stratified.get(i);
            TestLabelable itemFromFirstStrat = stratified.get(i % stratSize);
            assertTrue("matching strats " + i, item.getTrueLabel() == itemFromFirstStrat.getTrueLabel());
        }
    }
}
