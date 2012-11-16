package etc.aloe.filters;

import etc.aloe.filters.AbstractRegexFilter.NamedRegex;
import java.util.ArrayList;
import java.util.List;
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

/**
 *
 * @author michael
 */
public class AbstractRegexFilterTest {

    public AbstractRegexFilterTest() {
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
     * Test of getStringAttributeName method, of class AbstractRegexFilter.
     */
    @Test
    public void testGetStringAttributeName() {
        System.out.println("getStringAttributeName");
        AbstractRegexFilter instance = new AbstractRegexFilterImpl();
        String expResult = "my name";
        instance.setStringAttributeName(expResult);
        String result = instance.getStringAttributeName();
        assertEquals(expResult, result);
    }

    /**
     * Test of determineOutputFormat method, of class AbstractRegexFilter.
     */
    @Test
    public void testDetermineOutputFormat() throws Exception {
        System.out.println("determineOutputFormat");

        AbstractRegexFilter instance = new AbstractRegexFilterImpl();
        instance.setStringAttributeName("string_attr");

        ArrayList<Attribute> attrs = new ArrayList<Attribute>();
        attrs.add(new Attribute("string_attr", (List<String>) null));
        attrs.add(new Attribute("another_attr"));
        Instances inputFormat = new Instances("data", attrs, 0);

        instance.setCountRegexLengths(false);
        Instances output = instance.determineOutputFormat(inputFormat);
        assertEquals(3, output.numAttributes());
        assertEquals("regex", output.attribute(2).name());

        instance.setCountRegexLengths(true);
        output = instance.determineOutputFormat(inputFormat);
        assertEquals(4, output.numAttributes());
        assertEquals("regex", output.attribute(2).name());
        assertEquals("regex_L", output.attribute(3).name());

    }

    /**
     * Test of process method, of class AbstractRegexFilter.
     */
    @Test
    public void testProcess() throws Exception {
        System.out.println("process");
        AbstractRegexFilter filter = new AbstractRegexFilterImpl();
        filter.setStringAttributeName("string_attr");

        ArrayList<Attribute> attrs = new ArrayList<Attribute>();
        attrs.add(new Attribute("string_attr", (List<String>) null));
        attrs.add(new Attribute("another_attr"));

        Instances inputFormat = new Instances("data", attrs, 0);
        Attribute stringAttr = inputFormat.attribute("string_attr");
        Attribute anotherAttr = inputFormat.attribute("another_attr");

        List<Instance> instances = new ArrayList<Instance>();
        Instance a = new DenseInstance(2);
        a.setValue(stringAttr, "i have one");
        a.setValue(anotherAttr, 1);
        instances.add(a);

        Instance b = new DenseInstance(2);
        b.setValue(stringAttr, "aaaaa");
        b.setValue(anotherAttr, 5);
        instances.add(b);

        Instance c = new DenseInstance(2);
        c.setValue(stringAttr, "nothing here");
        c.setValue(anotherAttr, 0);
        instances.add(c);

        inputFormat.addAll(instances);
        filter.setInputFormat(inputFormat);
        Instances output = filter.getOutputFormat();

        for (int i = 0; i < instances.size(); i++) {
            Instance original = inputFormat.get(i);
            Instance filtered = filter.process(original);
            filtered.setDataset(output);

            assertEquals(original.stringValue(0), filtered.stringValue(0));
            assertEquals(original.value(1), filtered.value(1), 0);
            assertEquals(original.value(1), filtered.value(2), 0);
        }
    }

    /**
     * Test of globalInfo method, of class AbstractRegexFilter.
     */
    @Test
    public void testGlobalInfo() {
        System.out.println("globalInfo");
        AbstractRegexFilter instance = new AbstractRegexFilterImpl();
        String result = instance.globalInfo();
        assertTrue(result.length() > 0);
    }

    /**
     * Test of toRegex method, of class AbstractRegexFilter.
     */
    @Test
    public void testToRegex_StringArr() {
        System.out.println("toRegex");
        String[] fragments = new String[]{
            "abc",
            "def",
            "ghi"
        };
        AbstractRegexFilter instance = new AbstractRegexFilterImpl();
        String expResult = "\\Qabc\\E|\\Qdef\\E|\\Qghi\\E";
        String result = instance.toRegex(fragments);
        assertEquals(expResult, result);

        fragments = new String[]{
            "abc(d)",
            "e[f*]+"
        };
        expResult = "\\Qabc(d)\\E|\\Qe[f*]+\\E";
        result = instance.toRegex(fragments);
        assertEquals(expResult, result);
    }

    /**
     * Test of toRegex method, of class AbstractRegexFilter.
     */
    @Test
    public void testToRegex_StringArr_boolean() {
        System.out.println("toRegex");
        String[] fragments = new String[]{
            "abc",
            "def",
            "ghi"
        };
        AbstractRegexFilter instance = new AbstractRegexFilterImpl();
        String expResult = "abc|def|ghi";
        String result = instance.toRegex(fragments, false);
        assertEquals(expResult, result);

        fragments = new String[]{
            "abc(d)",
            "e[f*]+"
        };
        expResult = "abc(d)|e[f*]+";
        result = instance.toRegex(fragments, false);
        assertEquals(expResult, result);
    }

    public class AbstractRegexFilterImpl extends AbstractRegexFilter {

        @Override
        public NamedRegex[] getRegexFeatures() {
            return new NamedRegex[]{
                        new NamedRegex("regex", "a")
                    };
        }
    }
}
