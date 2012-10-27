/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data.analysis;

import data.EntitySet;
import data.MultiRatedEntity;
import data.analysis.CodeCrossTab.UserMode;
import data.indexes.CodeNames;
import data.indexes.UserNames;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class CodeCrossTabTest {
    
    public CodeCrossTabTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        CodeNames.instance.put(1, "C_One");
        CodeNames.instance.put(2, "C_Two");
        CodeNames.instance.put(3, "C_Three");
        CodeNames.instance.put(4, "C_Four");
        CodeNames.instance.put(5, "C_Five");
        
        UserNames.instance.put(1, "U_One");
        UserNames.instance.put(2, "U_Two");
        UserNames.instance.put(3, "U_Three");
        UserNames.instance.put(4, "U_Four");
        UserNames.instance.put(5, "U_Five");
        
        

        
        
        
        
        
//        
//        UserCodeCounts ucc = new UserCodeCounts();
//        UserCodeCounts.Result userCodeResult = (UserCodeCounts.Result)ucc.analyze(testDataSet);
//        System.out.println("User-Code Counts (Rating Count)");
//        System.out.println(userCodeResult.getAsString(true));
//        
//        ucc.setCountType(UserCodeCounts.CountType.EntityAverage);
//        userCodeResult = (UserCodeCounts.Result)ucc.analyze(testDataSet);
//        System.out.println("User-Code Counts (EntityAverage)");
//        System.out.println(userCodeResult.getAsString(true));
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        UserNames.instance.clear();
        CodeNames.instance.clear();
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getUserMode method, of class CodeCrossTab.
     */
    @Test
    public void testGetUserMode() {
        System.out.println("getUserMode");
        CodeCrossTab instance = new CodeCrossTab();
        UserMode expResult = CodeCrossTab.UserMode.UserAgnostic;
        UserMode result = instance.getUserMode();
        assertEquals(expResult, result);
    }

    /**
     * Test of setUserMode method, of class CodeCrossTab.
     */
    @Test
    public void testSetUserMode() {
        System.out.println("setUserMode");
        UserMode userMode = CodeCrossTab.UserMode.BetweenUsers;
        CodeCrossTab instance = new CodeCrossTab();
        instance.setUserMode(userMode);
        assertEquals(userMode, instance.getUserMode());
    }

    /**
     * Test of analyze method, of class CodeCrossTab.
     * In UserAgnostic mode
     */
    @Test
    public void testAnalyzeUserAgnostic() {
        System.out.println("analyze UserAgnostic");
        
        int[][][] testData = new int[][][]{
            new int[][]{
                new int[]{1, 1}, //User 1 says 1
                new int[]{1, 2}, //User 1 says 2
                new int[]{2, 1} //User 2 says 1
            },
            new int[][]{
                new int[]{2, 3} //User 2 says 3
            },
            new int[][]{
                new int[]{1, 1},
                new int[]{1, 3},
                new int[]{4, 2},
                new int[]{4, 3}
            },
            new int[][]{
                new int[]{2, 2},
                new int[]{1, 1},
                new int[]{2, 5},
                new int[]{2, 5}
            }
        };
        EntitySet dataSet = EntitySet.constructTestData(testData);
        
        CodeCrossTab cct = new CodeCrossTab();
        cct.setUserMode(UserMode.UserAgnostic);
        
        CrossTab.Result result = (CrossTab.Result)cct.analyze(dataSet);
        System.out.println("CodeCrossTab");
        System.out.println(result.getAsString(true));
        
        assertEquals(0, result.getCell(1, 1), 0);
        assertEquals(3, result.getCell(1, 2), 0);
        assertEquals(3, result.getCell(2, 1), 0);
        assertEquals(1, result.getCell(1, 3), 0);
        assertEquals(1, result.getCell(5, 2), 0);
        assertEquals(0, result.getCell(5, 3), 0);
        assertEquals(0, result.getCell(3, 5), 0);
        assertEquals(0, result.getCell(2, 7), 0);
        
        //Number of distinct pairs the code occurs in
        //Probably not that useful, but whatever...
        assertEquals(5, result.getTotal(1), 0);
        assertEquals(5, result.getTotal(2), 0);
        assertEquals(2, result.getTotal(3), 0);
        assertEquals(2, result.getTotal(5), 0);
        assertEquals(0, result.getTotal(4), 0);
        
        assertEquals(7, result.getGrandTotal(), 0);
        assertEquals(4, result.getNumObjects());
    }
    
    /**
     * Test of analyze method, of class CodeCrossTab.
     * In WithinUsers mode
     */
    @Test
    public void testAnalyzeWithinUsers() {
        System.out.println("analyze WithinUsers");
        
        int[][][] testData = new int[][][]{
            new int[][]{
                new int[]{1, 1}, //User 1 says 1
                new int[]{1, 2}, //User 1 says 2
                new int[]{2, 1} //User 2 says 1
            },
            new int[][]{
                new int[]{2, 3} //User 2 says 3
            },
            new int[][]{
                new int[]{1, 1},
                new int[]{1, 3},
                new int[]{4, 2},
                new int[]{4, 3}
            },
            new int[][]{
                new int[]{2, 2},
                new int[]{1, 1},
                new int[]{2, 5},
                new int[]{2, 5}
            }
        };
        EntitySet dataSet = EntitySet.constructTestData(testData);
        
        CodeCrossTab cct = new CodeCrossTab();
        cct.setUserMode(UserMode.WithinUsers);
        
        CrossTab.Result result = (CrossTab.Result)cct.analyze(dataSet);
        System.out.println("CodeCrossTab");
        System.out.println(result.getAsString(true));
        
        assertEquals(1, result.getCell(1, 2), 0);
        assertEquals(1, result.getCell(2, 1), 0);
        assertEquals(1, result.getCell(1, 3), 0);
        assertEquals(1, result.getCell(5, 2), 0);
        assertEquals(0, result.getCell(5, 1), 0);
        assertEquals(0, result.getCell(1, 5), 0);
        assertEquals(0, result.getCell(2, 7), 0);
        
        //Number of distinct pairs the code occurs in
        //Probably not that useful, but whatever...
        assertEquals(2, result.getTotal(1), 0);
        assertEquals(3, result.getTotal(2), 0);
        assertEquals(2, result.getTotal(3), 0);
        assertEquals(1, result.getTotal(5), 0);
        assertEquals(0, result.getTotal(4), 0);
        
        assertEquals(4, result.getGrandTotal(), 0);
        assertEquals(4, result.getNumObjects());
    }
    
    /**
     * Test of analyze method, of class CodeCrossTab.
     * In BetweenUsers mode
     */
    @Test
    public void testAnalyzeBetweenUsers() {
        System.out.println("analyze BetweenUsers");
        
        int[][][] testData = new int[][][]{
            new int[][]{
                new int[]{1, 1}, //User 1 says 1
                new int[]{1, 2}, //User 1 says 2
                new int[]{2, 1} //User 2 says 1
            },
            new int[][]{
                new int[]{2, 3} //User 2 says 3
            },
            new int[][]{
                new int[]{1, 1},
                new int[]{1, 3},
                new int[]{4, 2},
                new int[]{4, 3}
            },
            new int[][]{
                new int[]{2, 2},
                new int[]{1, 1},
                new int[]{2, 5},
                new int[]{2, 5}
            }
        };
        EntitySet dataSet = EntitySet.constructTestData(testData);
        
        CodeCrossTab cct = new CodeCrossTab();
        cct.setUserMode(UserMode.BetweenUsers);
        
        CrossTab.Result result = (CrossTab.Result)cct.analyze(dataSet);
        System.out.println("CodeCrossTab");
        System.out.println(result.getAsString(true));
        
        assertEquals(3, result.getCell(1, 2), 0);
        assertEquals(3, result.getCell(2, 1), 0);
        assertEquals(1, result.getCell(1, 3), 0);
        assertEquals(0, result.getCell(5, 2), 0);
        assertEquals(1, result.getCell(5, 1), 0);
        assertEquals(1, result.getCell(1, 5), 0);
        assertEquals(0, result.getCell(2, 7), 0);
        
        //Number of distinct pairs the code occurs in
        //Probably not that useful, but whatever...
        assertEquals(6, result.getTotal(1), 0);
        assertEquals(4, result.getTotal(2), 0);
        assertEquals(3, result.getTotal(3), 0);
        assertEquals(1, result.getTotal(5), 0);
        assertEquals(0, result.getTotal(4), 0);
        
        assertEquals(8, result.getGrandTotal(), 0);
        assertEquals(4, result.getNumObjects());
    }
    
    /**
     * Test of analyze method, of class CodeCrossTab.
     * In AllPairs mode
     */
    @Test
    public void testAnalyzeAllPairs() {
        System.out.println("analyze AllPairs");
        
        int[][][] testData = new int[][][]{
            new int[][]{
                new int[]{1, 1}, //User 1 says 1
                new int[]{1, 2}, //User 1 says 2
                new int[]{2, 1} //User 2 says 1
            },
            new int[][]{
                new int[]{2, 3} //User 2 says 3
            },
            new int[][]{
                new int[]{1, 1},
                new int[]{1, 3},
                new int[]{4, 2},
                new int[]{4, 3}
            },
            new int[][]{
                new int[]{2, 2},
                new int[]{1, 1},
                new int[]{2, 5},
                new int[]{2, 5}
            }
        };
        EntitySet dataSet = EntitySet.constructTestData(testData);
        
        CodeCrossTab cct = new CodeCrossTab();
        cct.setUserMode(UserMode.AllPairs);
        
        CrossTab.Result result = (CrossTab.Result)cct.analyze(dataSet);
        System.out.println("CodeCrossTab");
        System.out.println(result.getAsString(true));
        
        assertEquals(1, result.getCell(1, 1), 0);
        assertEquals(4, result.getCell(1, 2), 0);
        assertEquals(4, result.getCell(2, 1), 0);
        assertEquals(2, result.getCell(1, 3), 0);
        assertEquals(1, result.getCell(5, 2), 0);
        assertEquals(1, result.getCell(5, 1), 0);
        assertEquals(1, result.getCell(1, 5), 0);
        assertEquals(0, result.getCell(2, 7), 0);
        
        //Number of distinct pairs the code occurs in
        //Probably not that useful, but whatever...
        assertEquals(8, result.getTotal(1), 0);
        assertEquals(7, result.getTotal(2), 0);
        assertEquals(5, result.getTotal(3), 0);
        assertEquals(2, result.getTotal(5), 0);
        assertEquals(0, result.getTotal(4), 0);
        
        assertEquals(12, result.getGrandTotal(), 0);
        assertEquals(4, result.getNumObjects());
    }
}
