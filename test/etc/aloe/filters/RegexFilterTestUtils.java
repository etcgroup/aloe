package etc.aloe.filters;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.Assert;

/**
 *
 * @author michael
 */
public class RegexFilterTestUtils {

    private AbstractRegexFilter regexFilter;

    protected RegexFilterTestUtils(AbstractRegexFilter regexFilter) {
        this.regexFilter = regexFilter;
    }

    protected double[] expect(Object... nameValues) {

        AbstractRegexFilter.NamedRegex[] regexFeatures = regexFilter.getRegexFeatures();

        String[] names = new String[nameValues.length / 2];
        double[] values = new double[nameValues.length / 2];

        for (int i = 0; i < nameValues.length / 2; i++) {
            String regexName = (String) nameValues[i * 2];
            double value = 0;
            try {
                value = (Double) nameValues[i * 2 + 1];
            } catch (ClassCastException e) {
                value = (Integer) nameValues[i * 2 + 1];
            }
            names[i] = regexName;
            values[i] = value;
        }

        double[] all = new double[regexFeatures.length];
        for (int i = 0; i < regexFeatures.length; i++) {
            all[i] = 0;

            for (int j = 0; j < names.length; j++) {
                if (regexFeatures[i].getName().equals(names[j])) {
                    all[i] = values[j];
                }
            }
        }

        return all;
    }

    public ArrayList<String> runTest(String stringValue, double[] expectedValues) {
        ArrayList<String> errors = new ArrayList<String>();

        AbstractRegexFilter.NamedRegex[] regexFeatures = regexFilter.getRegexFeatures();

        Assert.assertEquals(expectedValues.length, regexFeatures.length);

        //Test each regex
        for (int i = 0; i < regexFeatures.length; i++) {
            Pattern pattern = regexFeatures[i].getPattern();

            Matcher matches = pattern.matcher(stringValue);
            int count = 0;
            while (matches.find()) {
                count++;
            }

            if (count != expectedValues[i]) {
                System.err.println("Regex " + regexFeatures[i].getName() + " found " + count + ", expected " + expectedValues[i] + ". String: " + stringValue + " Regex: " + regexFeatures[i].getRegex());
            }
            Assert.assertEquals(expectedValues[i], count, 0);

        }

        return errors;
    }
}
