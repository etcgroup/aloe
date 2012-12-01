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
package etc.aloe.filters;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
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
