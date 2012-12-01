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
package etc.aloe.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import weka.filters.Filter;
import weka.filters.unsupervised.instance.Randomize;

/**
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class FeatureSpecificationTest {

    public FeatureSpecificationTest() {
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
     * Test of load method, of class FeatureSpecification.
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
        FeatureSpecification instance = new FeatureSpecification();
        assertTrue(instance.load(input));
        input.close();

        List<Filter> filters = instance.getFilters();
        assertEquals(1, filters.size());
        assertTrue(filters.get(0) instanceof Randomize);
        Randomize serialized = (Randomize) filters.get(0);
        assertEquals(filter.getRandomSeed(), serialized.getRandomSeed());
    }

    /**
     * Test of save method, of class FeatureSpecification.
     */
    @Test
    public void testSave() throws Exception {
        System.out.println("save");;

        Randomize filter = new Randomize();
        filter.setRandomSeed(455);

        FeatureSpecification instance = new FeatureSpecification();
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
     * Test of addFilter method, of class FeatureSpecification.
     */
    @Test
    public void testAddFilter() {
        System.out.println("addFilter");

        Randomize filter = new Randomize();

        FeatureSpecification instance = new FeatureSpecification();
        instance.addFilter(filter);

        assertEquals(filter, instance.getFilters().get(0));
    }
}
