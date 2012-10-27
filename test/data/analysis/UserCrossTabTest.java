/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data.analysis;

import data.EntitySet;
import data.indexes.CodeNames;
import data.indexes.UserNames;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class UserCrossTabTest {
    
    public UserCrossTabTest() {
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
     * Test of analyze method, of class UserCrossTab.
     */
    @Test
    public void testAnalyze() {
        System.out.println("analyze");
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
        
        UserCrossTab uct = new UserCrossTab();
        
        CrossTab.Result result = (CrossTab.Result)uct.analyze(dataSet);
        System.out.println("UserCrossTab");
        System.out.println(result.getAsString(true));
        
        assertEquals(2, result.getCell(1, 2), 0);
        assertEquals(2, result.getCell(2, 1), 0);
        assertEquals(1, result.getCell(1, 4), 0);
        assertEquals(1, result.getCell(4, 1), 0);
        assertEquals(0, result.getCell(2, 4), 0);
        assertEquals(0, result.getCell(4, 2), 0);
        assertEquals(0, result.getCell(3, 5), 0);
        assertEquals(0, result.getCell(2, 7), 0);
        
        //Number of distinct user-pairs the user occurs in
        //Probably not that useful, but whatever...
        assertEquals(3, result.getTotal(1), 0);
        assertEquals(2, result.getTotal(2), 0);
        assertEquals(0, result.getTotal(3), 0);
        assertEquals(1, result.getTotal(4), 0);
        assertEquals(0, result.getTotal(5), 0);
        
        assertEquals(3, result.getGrandTotal(), 0);
        assertEquals(3, result.getNumObjects());
    }
}
