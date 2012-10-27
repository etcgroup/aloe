/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data.analysis;

import data.EntitySet;
import data.analysis.UserCodeCounts.CountType;
import data.indexes.CodeNames;
import data.indexes.UserNames;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class UserCodeCountsTest {

    public UserCodeCountsTest() {
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
     * Test of getCountType method, of class UserCodeCounts.
     */
    @Test
    public void testGetCountType() {
        System.out.println("getCountType");
        UserCodeCounts instance = new UserCodeCounts();
        CountType expResult = UserCodeCounts.CountType.Ratings;
        CountType result = instance.getCountType();
        assertEquals(expResult, result);
    }

    /**
     * Test of setCountType method, of class UserCodeCounts.
     */
    @Test
    public void testSetCountType() {
        System.out.println("setCountType");
        CountType countType = UserCodeCounts.CountType.UserNormed;
        UserCodeCounts instance = new UserCodeCounts();
        instance.setCountType(countType);
        CountType result = instance.getCountType();
        assertEquals(countType, result);
    }

    /**
     * Test of analyze method, of class UserCodeCounts. Ratings count type
     */
    @Test
    public void testAnalyzeRatings() {
        System.out.println("analyze Ratings");
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

        UserCodeCounts counts = new UserCodeCounts();
        counts.setCountType(CountType.Ratings);
        UserCodeCounts.Result result = (UserCodeCounts.Result) counts.analyze(dataSet);

        System.out.println("UserCodeCounts");
        System.out.println(result.getAsString(true));

        assertEquals(1, result.getUserCodeEntities(1, 2), 0);
        assertEquals(3, result.getUserCodeEntities(1, 1), 0);
        assertEquals(1, result.getUserCodeEntities(1, 3), 0);
        assertEquals(0, result.getUserCodeEntities(1, 5), 0);
        assertEquals(1, result.getUserCodeEntities(2, 2), 0);
        assertEquals(1, result.getUserCodeEntities(2, 5), 0);
        assertEquals(0, result.getUserCodeEntities(6, 1), 0);
        assertEquals(0, result.getUserCodeEntities(2, 7), 0);

        assertEquals(4, result.getTotalCodeEntities(1), 0);
        assertEquals(3, result.getTotalCodeEntities(2), 0);
        assertEquals(3, result.getTotalCodeEntities(3), 0);
        assertEquals(0, result.getTotalCodeEntities(4), 0);
        assertEquals(1, result.getTotalCodeEntities(5), 0);

        assertEquals(5, result.getTotalUserEntities(1), 0);
        assertEquals(4, result.getTotalUserEntities(2), 0);
        assertEquals(0, result.getTotalUserEntities(3), 0);
        assertEquals(2, result.getTotalUserEntities(4), 0);
        assertEquals(0, result.getTotalUserEntities(5), 0);
    }

    /**
     * Test of analyze method, of class UserCodeCounts. UserNormed count type
     */
    @Test
    public void testAnalyzeUserNormed() {
        System.out.println("analyze UserNormed");
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
                new int[]{4, 3},
                new int[]{4, 1}
            },
            new int[][]{
                new int[]{2, 2},
                new int[]{1, 1},
                new int[]{2, 5},
                new int[]{2, 5}
            }
        };
        EntitySet dataSet = EntitySet.constructTestData(testData);

        UserCodeCounts counts = new UserCodeCounts();
        counts.setCountType(CountType.UserNormed);
        UserCodeCounts.Result result = (UserCodeCounts.Result) counts.analyze(dataSet);

        System.out.println("UserCodeCounts");
        System.out.println(result.getAsString(true));

        assertEquals(0.5, result.getUserCodeEntities(1, 2), 0);
        assertEquals(2, result.getUserCodeEntities(1, 1), 0);
        assertEquals(0.5, result.getUserCodeEntities(1, 3), 0);
        assertEquals(0, result.getUserCodeEntities(1, 5), 0);
        assertEquals(0.5, result.getUserCodeEntities(2, 2), 0);
        assertEquals(0.5, result.getUserCodeEntities(2, 5), 0);
        assertEquals(1.0 / 3.0, result.getUserCodeEntities(4, 3), 0);
        assertEquals(0, result.getUserCodeEntities(6, 1), 0);
        assertEquals(0, result.getUserCodeEntities(2, 7), 0);

        assertEquals(3 + 1.0 / 3.0, result.getTotalCodeEntities(1), 0);
        assertEquals(1 + 1.0 / 3.0, result.getTotalCodeEntities(2), 0);
        assertEquals(1.5 + 1.0 / 3.0, result.getTotalCodeEntities(3), 0);
        assertEquals(0, result.getTotalCodeEntities(4), 0);
        assertEquals(0.5, result.getTotalCodeEntities(5), 0);

        assertEquals(3, result.getTotalUserEntities(1), 0);
        assertEquals(3, result.getTotalUserEntities(2), 0);
        assertEquals(0, result.getTotalUserEntities(3), 0);
        assertEquals(1, result.getTotalUserEntities(4), 0);
        assertEquals(0, result.getTotalUserEntities(5), 0);
    }
    
    /**
     * Test of analyze method, of class UserCodeCounts. EntityNormed count type
     */
    @Test
    public void testAnalyzeEntityNormed() {
        System.out.println("analyze EntityNormed");
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
                new int[]{4, 3},
                new int[]{4, 1}
            },
            new int[][]{
                new int[]{2, 2},
                new int[]{1, 1},
                new int[]{2, 5},
                new int[]{2, 5}
            }
        };
        EntitySet dataSet = EntitySet.constructTestData(testData);

        UserCodeCounts counts = new UserCodeCounts();
        counts.setCountType(CountType.EntityNormed);
        UserCodeCounts.Result result = (UserCodeCounts.Result) counts.analyze(dataSet);

        System.out.println("UserCodeCounts");
        System.out.println(result.getAsString(true));

        double delta = 0.0000001;
        
        assertEquals(1.0 / 3.0, result.getUserCodeEntities(1, 2), delta);
        assertEquals(2.0 / 3.0 + 1.0 / 5.0, result.getUserCodeEntities(1, 1), delta);
        assertEquals(1.0 / 5.0, result.getUserCodeEntities(1, 3), delta);
        assertEquals(0, result.getUserCodeEntities(1, 5), delta);
        assertEquals(1.0 / 3.0, result.getUserCodeEntities(2, 2), delta);
        assertEquals(1.0 / 3.0, result.getUserCodeEntities(2, 5), delta);
        assertEquals(1.0 / 5.0, result.getUserCodeEntities(4, 3), delta);
        assertEquals(0, result.getUserCodeEntities(6, 1), delta);
        assertEquals(0, result.getUserCodeEntities(2, 7), delta);

        assertEquals(1 + 2.0 / 5.0, result.getTotalCodeEntities(1), delta);
        assertEquals(2.0 / 3.0 + 1.0 / 5.0, result.getTotalCodeEntities(2), delta);
        assertEquals(1 + 2.0 / 5.0, result.getTotalCodeEntities(3), delta);
        assertEquals(0, result.getTotalCodeEntities(4), delta);
        assertEquals(1.0 / 3.0, result.getTotalCodeEntities(5), delta);

        assertEquals(1 + 2.0 / 5.0, result.getTotalUserEntities(1), delta);
        assertEquals(2, result.getTotalUserEntities(2), delta);
        assertEquals(0, result.getTotalUserEntities(3), delta);
        assertEquals(3.0 / 5.0, result.getTotalUserEntities(4), delta);
        assertEquals(0, result.getTotalUserEntities(5), delta);
    }
}
