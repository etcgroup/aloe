/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package etc.aloe.cscw2013;

import etc.aloe.data.LabelableItem;
import java.util.ArrayList;
import java.util.Arrays;
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
public class CrossValidationPrepImplTest {

    private List<TestLabelable> originalItems;

    public CrossValidationPrepImplTest() {
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
     * Test of randomize method, of class CrossValidationPrepImpl.
     */
    @Test
    public void testRandomize() {
        System.out.println("randomize");

        //Repeat the test 5 times
        for (int i = 0; i < 5; i++) {

            List<TestLabelable> randomizedItems = new ArrayList<TestLabelable>(originalItems);

            CrossValidationPrepImpl<TestLabelable> instance = new CrossValidationPrepImpl<TestLabelable>();
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
     * Test of swap method, of class CrossValidationPrepImpl.
     */
    @Test
    public void testSwap() {
        System.out.println("swap");

        List<TestLabelable> items = new ArrayList<TestLabelable>(originalItems);

        CrossValidationPrepImpl instance = new CrossValidationPrepImpl();
        instance.swap(items, 0, items.size() - 1);

        assertEquals("size unchanged", originalItems.size(), items.size());
        assertEquals("first moved to last", originalItems.get(0), items.get(items.size() - 1));
        assertEquals("last moved to first", originalItems.get(originalItems.size() - 1), items.get(0));
    }

    /**
     * Test of stratify method, of class CrossValidationPrepImpl.
     */
    @Test
    public void testStratify() {
        System.out.println("stratify");

        List<TestLabelable> items = new ArrayList<TestLabelable>(originalItems);

        int numStrats = 3;
        CrossValidationPrepImpl instance = new CrossValidationPrepImpl();
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
