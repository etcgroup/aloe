package etc.aloe.filters;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author michael
 */
public class PronounRegexFilterTest {

    public PronounRegexFilterTest() {
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
     * Test of getRegexFeatures method, of class PronounRegexFilter.
     */
    @Test
    public void testGetRegexFeatures() {
        System.out.println("getRegexFeatures");


        PronounRegexFilter instance = new PronounRegexFilter();
        RegexFilterTestUtils util = new RegexFilterTestUtils(instance);

        //Each feature is in the ballpark
        util.runTest("I you he we yourselves they who", new double[]{1, 1, 1, 1, 1, 1, 1});

        //All options for one feature are recognized
        util.runTest("me i my mine myself", util.expect("prn_first_sng", 5));

        //Simple token recognition works
        util.runTest("westuff stuffwe we'stuff stuff'we we", util.expect("prn_first_pl", 3));
    }
}
