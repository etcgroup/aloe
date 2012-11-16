/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package etc.aloe.data;

import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

/**
 *
 * @author michael
 */
public class ExampleSetTest {

    private Instances instances;
    private int numMissingClass;

    public ExampleSetTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        ArrayList<Attribute> attributes = new ArrayList<Attribute>();

        attributes.add(new Attribute("id"));

        ArrayList<String> classValues = new ArrayList<String>();
        classValues.add("false");
        classValues.add("true");
        attributes.add(new Attribute("class", classValues));

        this.instances = new Instances("TestInstances", attributes, 12);
        this.instances.setClassIndex(1);

        this.instances.add(new DenseInstance(1.0, new double[]{1.0, 1.0}));
        this.instances.add(new DenseInstance(1.0, new double[]{2.0, Utils.missingValue()}));
        this.instances.add(new DenseInstance(1.0, new double[]{3.0, 1.0}));
        this.instances.add(new DenseInstance(1.0, new double[]{4.0, 1.0}));
        this.instances.add(new DenseInstance(1.0, new double[]{5.0, 1.0}));
        this.instances.add(new DenseInstance(1.0, new double[]{6.0, 1.0}));
        this.instances.add(new DenseInstance(1.0, new double[]{7.0, 0.0}));
        this.instances.add(new DenseInstance(1.0, new double[]{8.0, 0.0}));
        this.instances.add(new DenseInstance(1.0, new double[]{9.0, Utils.missingValue()}));
        this.instances.add(new DenseInstance(1.0, new double[]{10.0, 0.0}));
        this.instances.add(new DenseInstance(1.0, new double[]{11.0, 0.0}));
        this.instances.add(new DenseInstance(1.0, new double[]{12.0, Utils.missingValue()}));

        numMissingClass = 3;
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of size method, of class ExampleSet.
     */
    @Test
    public void testSize() {
        System.out.println("size");
        ExampleSet instance = new ExampleSet(this.instances);
        int expResult = this.instances.size();
        int result = instance.size();
        assertEquals(expResult, result);
    }

    /**
     * Test of onlyLabeled method, of class ExampleSet.
     */
    @Test
    public void testOnlyLabeled() {
        System.out.println("onlyLabeled");

        ExampleSet examples = new ExampleSet(this.instances);
        ExampleSet labeled = examples.onlyLabeled();

        assertEquals("labeled set is the right size", instances.size() - numMissingClass, labeled.size());

        int classAttr = instances.classIndex();
        for (int i = 0; i < labeled.size(); i++) {
            Instance instance = labeled.get(i);
            assertEquals(false, instance.isMissing(classAttr));
        }
    }

    /**
     * Test of get method, of class ExampleSet.
     */
    @Test
    public void testGet() {
        System.out.println("get");

        ExampleSet instance = new ExampleSet(this.instances);
        Instance expResult = null;

        assertNotNull(instance.get(0));
        assertNotNull(instance.get(this.instances.size() - 1));

        try {
            instance.get(-1);
            assertFalse("Not successful", true);
        } catch (IndexOutOfBoundsException e) {
            assertTrue("Throws out of bounds exception on negative indices", true);
        }

        try {
            instance.get(this.instances.size());
            assertFalse("Not successful", true);
        } catch (IndexOutOfBoundsException e) {
            assertTrue("Throws out of bounds exception on out of range indices", true);
        }
    }

    /**
     * Test of getInstances method, of class ExampleSet.
     */
    @Test
    public void testGetInstances() {
        System.out.println("getInstances");
        ExampleSet instance = new ExampleSet(instances);
        Instances result = instance.getInstances();
        assertEquals(instances, result);
    }

    /**
     * Test of getTrueLabel method, of class ExampleSet.
     */
    @Test
    public void testGetTrueLabel() {
        System.out.println("getTrueLabel");
        int i = 0;
        ExampleSet instance = new ExampleSet(instances);

        assertEquals(true, instance.getTrueLabel(0));
        assertEquals(false, instance.getTrueLabel(6));
        assertEquals(null, instance.getTrueLabel(11));
    }

    /**
     * Test of getClassLabel method, of class ExampleSet.
     */
    @Test
    public void testGetClassLabel() {
        System.out.println("getClassLabel");

        ExampleSet instance = new ExampleSet(instances);
        
        assertEquals(true, instance.getClassLabel(1.0));
        assertEquals(false, instance.getClassLabel(0.0));
        assertEquals(null, instance.getClassLabel(Utils.missingValue()));
    }
}
