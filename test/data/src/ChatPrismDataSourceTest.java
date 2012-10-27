/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data.src;

import data.io.ChatPrismDataSource;
import data.EntitySet;
import data.indexes.CodeNames;
import data.indexes.UserNames;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class ChatPrismDataSourceTest {

    static ChatPrismDataSource db;
    
    public ChatPrismDataSourceTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        db = new ChatPrismDataSource();
        db.setVerbose(true);
        
        db.setHost("localhost");
        db.setUsername("root");
        db.setPassword("");
        db.setDatabaseSchema("chatdb");
        
        db.initialize();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        
    }

    @After
    public void tearDown() {
        CodeNames.instance.clear();
        UserNames.instance.clear();
    }

    /**
     * Test of loadIndexes method, of class ChatPrismDataSource.
     */
    @Test
    public void testLoadIndexes() {
        System.out.println("loadIndexes");
        
        db.loadIndexes();

        assertTrue(CodeNames.instance.size() > 0);
        assertTrue(UserNames.instance.size() > 0);
    }

    /**
     * Test of getData method, of class ChatPrismDataSource.
     */
    @Test
    public void testGetData() {
        System.out.println("getData");
        
        db.setCodeSchemaId(2);
        db.setSegmentationId(1);
        EntitySet result = db.getData();
        
        assertTrue(result.size() > 10000);
        
        result.takeStock();
        assertTrue(result.getAllCodeIds().size() > 30);
        assertTrue(result.getAllUserIds().size() > 4);
        assertTrue(result.countAllRatings() > 10000);
        
        System.out.println("Codes: " + result.getAllCodeIds().size());
        System.out.println("Users: " + result.getAllUserIds().size());
        System.out.println("Ratings: " + result.countAllRatings());
    }
    
    /**
     * Test of getData method, of class ChatPrismDataSource.
     * Gets raw data points, not segments.
     */
    @Test
    public void testGetDataPoints() {
        System.out.println("getDataPoints");
        
        db.setCodeSchemaId(2);
        db.setSegmentationId(0);
        db.setMessageFilter("participant_id = 6");
        
        EntitySet result = db.getData();
        
        assertTrue(result.size() > 1000);
        
        result.takeStock();
        assertTrue(result.getAllCodeIds().size() > 30);
        assertTrue(result.getAllUserIds().size() > 4);
        assertTrue(result.countAllRatings() > 1000);
        
        System.out.println("Codes: " + result.getAllCodeIds().size());
        System.out.println("Users: " + result.getAllUserIds().size());
        System.out.println("Ratings: " + result.countAllRatings());
    }
}
