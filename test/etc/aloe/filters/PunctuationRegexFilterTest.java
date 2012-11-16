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
public class PunctuationRegexFilterTest {

    public PunctuationRegexFilterTest() {
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
     * Test of getRegexFeatures method, of class PunctuationRegexFilter.
     */
    @Test
    public void testGetRegexFeatures() {
        System.out.println("getRegexFeatures (questions and exclamations)");
        PunctuationRegexFilter instance = new PunctuationRegexFilter();
        RegexFilterTestUtils util = new RegexFilterTestUtils(instance);
        //Question marks
        util.runTest("Hello???What's going on here?", util.expect("pnct_question", 2));

        util.runTest("Angry! angry angry angry!!!", util.expect("pnct_exclamation", 2));

        //Combined ?! and !/
        util.runTest("Crazy!? Who are you calling crazy?!", new double[]{0, 2, 2, 2});
    }

    /**
     * Test of getRegexFeatures method, of class PunctuationRegexFilter.
     */
    @Test
    public void testGetRegexFeatures_Ellipses() {
        System.out.println("getRegexFeatures (ellipses)");
        PunctuationRegexFilter instance = new PunctuationRegexFilter();
        RegexFilterTestUtils util = new RegexFilterTestUtils(instance);
        //Ellipses
        util.runTest("Hm... that ", util.expect("pnct_elipsis", 1));
        util.runTest("is very interesting.. I ", util.expect("pnct_elipsis", 1));
        util.runTest("wonder...... very . . intere", util.expect("pnct_elipsis", 2));
        util.runTest("sting . . . indeed. ", util.expect("pnct_elipsis", 1));
    }
}
