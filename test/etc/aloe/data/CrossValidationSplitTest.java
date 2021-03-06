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
import etc.aloe.processes.CrossValidationSplit;
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
public class CrossValidationSplitTest {

    private List<TestLabelable> originalItems;

    public CrossValidationSplitTest() {
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
            new TestLabelable("one", true, true),
            new TestLabelable("two", false, true),
            new TestLabelable("three", false, true),
            new TestLabelable("four", true, true),
            new TestLabelable("five", false, true),
            new TestLabelable("six", true, true),
            new TestLabelable("seven", true, true),
            new TestLabelable("eight", true, true),
            new TestLabelable("nine", false, true),
            new TestLabelable("ten", false, true),
            new TestLabelable("eleven", false, true),
            new TestLabelable("twelve", true, true)
        };

        this.originalItems = Arrays.asList(itemsArray);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getTrainingForFold method, of class CrossValidationSplit.
     * Tests the middle fold.
     */
    @Test
    public void testGetTrainingForMiddleFold() {
        System.out.println("getTrainingForFold (middle)");

        int numFolds = 3;
        int foldIndex = 1;

        List<TestLabelable> expectedTraining = new ArrayList<TestLabelable>();
        expectedTraining.addAll(originalItems.subList(0, 4));
        expectedTraining.addAll(originalItems.subList(8, 12));

        CrossValidationSplit instance = new CrossValidationSplit();
        List<TestLabelable> training = instance.getTrainingForFold(originalItems, foldIndex, numFolds);

        assertArrayEquals("Correct training set for middle fold", expectedTraining.toArray(), training.toArray());
    }

    /**
     * Test of getTestingForFold method, of class CrossValidationSplit.
     * Tests the middle fold.
     */
    @Test
    public void testGetTestingForMiddleFold() {
        System.out.println("getTestingForFold (middle)");
        int numFolds = 3;
        int foldIndex = 1;

        List<TestLabelable> expectedTesting = new ArrayList<TestLabelable>();
        expectedTesting.addAll(originalItems.subList(4, 8));

        CrossValidationSplit instance = new CrossValidationSplit();

        List<TestLabelable> testing = instance.getTestingForFold(originalItems, foldIndex, numFolds);

        assertArrayEquals("Correct test set for middle fold", expectedTesting.toArray(), testing.toArray());
    }

    /**
     * Test of getTrainingForFold method, of class CrossValidationSplit.
     * Tests the first fold.
     */
    @Test
    public void testGetTrainingForFirstFold() {
        System.out.println("getTrainingForFold (first)");

        int numFolds = 3;
        int foldIndex = 0;

        List<TestLabelable> expectedTraining = new ArrayList<TestLabelable>();
        expectedTraining.addAll(originalItems.subList(4, 12));

        CrossValidationSplit instance = new CrossValidationSplit();
        List<TestLabelable> training = instance.getTrainingForFold(originalItems, foldIndex, numFolds);

        assertArrayEquals("Correct training set for first fold", expectedTraining.toArray(), training.toArray());
    }

    /**
     * Test of getTestingForFold method, of class CrossValidationSplit.
     * Tests the first fold.
     */
    @Test
    public void testGetTestingForFirstFold() {
        System.out.println("getTestingForFold (first)");

        int numFolds = 3;
        int foldIndex = 0;

        List<TestLabelable> expectedTesting = new ArrayList<TestLabelable>();
        expectedTesting.addAll(originalItems.subList(0, 4));

        CrossValidationSplit instance = new CrossValidationSplit();

        List<TestLabelable> testing = instance.getTestingForFold(originalItems, foldIndex, numFolds);

        assertArrayEquals("Correct test set for first fold", expectedTesting.toArray(), testing.toArray());
    }

    /**
     * Test of getTrainingForFold method, of class CrossValidationSplit.
     * Tests the last fold.
     */
    @Test
    public void testGetTrainingForLastFold() {
        System.out.println("getTrainingForFold (last)");

        int numFolds = 3;
        int foldIndex = 2;

        List<TestLabelable> expectedTraining = new ArrayList<TestLabelable>();
        expectedTraining.addAll(originalItems.subList(0, 8));

        CrossValidationSplit instance = new CrossValidationSplit();
        List<TestLabelable> training = instance.getTrainingForFold(originalItems, foldIndex, numFolds);

        assertArrayEquals("Correct training set for last fold", expectedTraining.toArray(), training.toArray());
    }

    /**
     * Test of getTestingForFold method, of class CrossValidationSplit.
     * Tests the middle fold.
     */
    @Test
    public void testGetTestingForLastFold() {
        System.out.println("getTestingForFold (last)");

        int numFolds = 3;
        int foldIndex = 2;

        List<TestLabelable> expectedTesting = new ArrayList<TestLabelable>();
        expectedTesting.addAll(originalItems.subList(8, 12));

        CrossValidationSplit instance = new CrossValidationSplit();

        List<TestLabelable> testing = instance.getTestingForFold(originalItems, foldIndex, numFolds);

        assertArrayEquals("Correct test set for last fold", expectedTesting.toArray(), testing.toArray());
    }
}
