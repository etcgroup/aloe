/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data.analysis;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({data.analysis.CodeCrossTabTest.class, data.analysis.UserCrossTabTest.class, data.analysis.UserCodeCountsTest.class})
public class AnalysisSuite {

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }
    
}
