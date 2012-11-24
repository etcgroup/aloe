/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package etc.aloe.cscw2013;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import weka.classifiers.trees.J48;
import weka.filters.Filter;
import weka.filters.unsupervised.instance.Randomize;

/**
 *
 * @author michael
 */
public class FeatureSpecificationImplTest {

    public FeatureSpecificationImplTest() {
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
     * Test of load method, of class FeatureSpecificationImpl.
     */
    @Test
    public void testLoad() throws Exception {
        System.out.println("load");

        Randomize filter = new Randomize();
        filter.setRandomSeed(455);

        ArrayList<Filter> filtersList = new ArrayList<Filter>();
        filtersList.add(filter);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream objOut = new ObjectOutputStream(out);
        objOut.writeObject(filtersList);
        out.close();
        byte[] serializedBytes = out.toByteArray();

        ByteArrayInputStream input = new ByteArrayInputStream(serializedBytes);
        FeatureSpecificationImpl instance = new FeatureSpecificationImpl();
        assertTrue(instance.load(input));
        input.close();

        List<Filter> filters = instance.getFilters();
        assertEquals(1, filters.size());
        assertTrue(filters.get(0) instanceof Randomize);
        Randomize serialized = (Randomize) filters.get(0);
        assertEquals(filter.getRandomSeed(), serialized.getRandomSeed());
    }

    /**
     * Test of save method, of class FeatureSpecificationImpl.
     */
    @Test
    public void testSave() throws Exception {
        System.out.println("save");;

        Randomize filter = new Randomize();
        filter.setRandomSeed(455);

        FeatureSpecificationImpl instance = new FeatureSpecificationImpl();
        instance.addFilter(filter);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        assertTrue(instance.save(out));
        out.close();
        byte[] bytes = out.toByteArray();

        //It wrote something
        assertTrue(bytes.length > 0);

        //It wrote the correct thing
        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes));
        try {
            List<Filter> serialized = (List<Filter>) in.readObject();
            in.close();
            assertEquals(1, serialized.size());
            assertTrue(serialized.get(0) instanceof Randomize);
            Randomize randomize = (Randomize) serialized.get(0);
            assertEquals(filter.getRandomSeed(), randomize.getRandomSeed());
        } catch (ClassNotFoundException e) {
            assertTrue(e.getMessage(), false);
        } catch (IOException e) {
            assertTrue(e.getMessage(), false);
        }
    }

    /**
     * Test of addFilter method, of class FeatureSpecificationImpl.
     */
    @Test
    public void testAddFilter() {
        System.out.println("addFilter");

        Randomize filter = new Randomize();

        FeatureSpecificationImpl instance = new FeatureSpecificationImpl();
        instance.addFilter(filter);

        assertEquals(filter, instance.getFilters().get(0));
    }
}
