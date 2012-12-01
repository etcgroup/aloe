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
