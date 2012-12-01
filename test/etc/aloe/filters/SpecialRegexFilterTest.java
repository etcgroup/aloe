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
public class SpecialRegexFilterTest {

    public SpecialRegexFilterTest() {
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
     * Test of getRegexFeatures method, of class SpecialRegexFilter.
     */
    @Test
    public void testGetRegexFeatures() {
        System.out.println("getRegexFeatures");
        SpecialRegexFilter instance = new SpecialRegexFilter();
        RegexFilterTestUtils util = new RegexFilterTestUtils(instance);

        //Negation tests
        util.runTest("don't doesn't won't can't", util.expect("negation", 4));
        util.runTest("dont doesnt wont cant", util.expect("negation", 4));
        util.runTest("current ontario fumigant", util.expect("negation", 0));
        util.runTest("no not cannot", util.expect("negation", 3));
        util.runTest("piano notorious pinot", util.expect("negation", 0));

        //Swearing tests
        util.runTest("crap crappy crapy", util.expect("swear", 3));
        util.runTest("!@)$&*(^@#$)^&", util.expect("swear", 1));
        util.runTest("!@#%", util.expect("swear", 1));
        util.runTest("(@%*&", util.expect("swear", 1));
        util.runTest("!@#*@$@", util.expect("swear", 1));
        //Without enough magic characters, ignored
        util.runTest("!!!! .... !@!!", util.expect("swear", 0));
        util.runTest("#### ****", util.expect("swear", 2));
        //Periods disrupt swearing
        util.runTest("#.#.#.# *.*.*.*.", util.expect("swear", 0));

        //Names tests
        util.runTest("Ray gary", util.expect("names", 2));
        util.runTest("fooray", util.expect("names", 0));
        util.runTest("rayfoo", util.expect("names", 1));
    }
}
