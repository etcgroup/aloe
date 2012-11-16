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
public class SpellingRegexFilterTest {

    public SpellingRegexFilterTest() {
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
     * Test of getRegexFeatures method, of class SpellingRegexFilter.
     */
    @Test
    public void testGetRegexFeatures() {
        System.out.println("getRegexFeatures");
        SpellingRegexFilter instance = new SpellingRegexFilter();
        RegexFilterTestUtils util = new RegexFilterTestUtils(instance);

        //Hmm tests
        util.runTest("h m u", util.expect("hmm", 0));
        util.runTest("huh", util.expect("hmm", 0));
        util.runTest("hm", util.expect("hmm", 1));
        util.runTest("um", util.expect("hmm", 1));
        util.runTest("mm", util.expect("hmm", 1));
        util.runTest("hmm", util.expect("hmm", 1));
        util.runTest("hmmm", util.expect("hmm", 1, "repetition", 1));
        util.runTest("umm", util.expect("hmm", 1));
        util.runTest("ummm", util.expect("hmm", 1, "repetition", 1));
        util.runTest("hum", util.expect("hmm", 1));
        util.runTest("humm", util.expect("hmm", 1));
        util.runTest("hummm", util.expect("hmm", 1, "repetition", 1));
        util.runTest("hhmmmm", util.expect("hmm", 1, "repetition", 1));
        util.runTest("hhummm", util.expect("hmm", 1, "repetition", 1));

        //Laughter tests
        util.runTest("ha", util.expect("laughter", 1));
        util.runTest("haha", util.expect("laughter", 1));
        util.runTest("hah", util.expect("laughter", 1));
        util.runTest("hhhahhha", util.expect("laughter", 1, "repetition", 2));
        util.runTest("hahah", util.expect("laughter", 1));
        util.runTest("hahahhhh", util.expect("laughter", 1, "repetition", 1));
        util.runTest("lo", util.expect("laughter", 0));
        util.runTest("lol", util.expect("laughter", 1));
        util.runTest("lololol", util.expect("laughter", 1));
        util.runTest("he", util.expect("laughter", 0));
        util.runTest("hhhhhhhhhh", util.expect("laughter", 0, "repetition", 1));
        util.runTest("hhhehhhe", util.expect("laughter", 1, "repetition", 2));
        util.runTest("hehe", util.expect("laughter", 1));
        util.runTest("heh", util.expect("laughter", 1));
        util.runTest("heheh", util.expect("laughter", 1));
        util.runTest("hee", util.expect("laughter", 1));
        util.runTest("hehhhh", util.expect("laughter", 1, "repetition", 1));
        util.runTest("heehe", util.expect("laughter", 1));
        util.runTest("heeeeeheeeheee", util.expect("laughter", 1, "repetition", 3));
        util.runTest("hoho", util.expect("laughter", 1));

        //Caps tests
        util.runTest("Happy birthday. How are You?", util.expect("caps", 0));
        util.runTest("HahaHahaHa?", util.expect("caps", 0, "laughter", 1));
        util.runTest("AAAAAAA!", util.expect("caps", 1, "repetition", 1));
        util.runTest("AAAAAAA AAAAAAA AAAAAAA!", util.expect("caps", 3, "repetition", 3));
    }
}
