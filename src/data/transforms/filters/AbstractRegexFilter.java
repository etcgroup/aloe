/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data.transforms.filters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import weka.core.Capabilities.Capability;
import weka.core.*;
import weka.filters.MultiFilter;
import weka.filters.SimpleStreamFilter;
import weka.filters.UnsupervisedFilter;

/**
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public abstract class AbstractRegexFilter extends SimpleStreamFilter
        implements UnsupervisedFilter {
    private boolean countRegexLengths = false;

    public void countRegexLengths(boolean countRegexLengths) {
        this.countRegexLengths = countRegexLengths;
    }

    protected class NamedRegex {

        private final String name;
        private final String regex;
        private final Pattern pattern;

        public NamedRegex(String name, String regex) {
            this(name, regex, 0);
        }

        public NamedRegex(String name, String regex, int flags) {
            this.name = name;
            this.regex = regex;
            this.pattern = Pattern.compile(regex, flags);
        }

        public String getName() {
            return name;
        }

        public String getRegex() {
            return regex;
        }

        public Pattern getPattern() {
            return pattern;
        }
    }
    protected String stringAttributeName;
    private int stringAttributeIndex = -1;

    protected abstract NamedRegex[] getRegexFeatures();

    public String getStringAttributeName() {
        return stringAttributeName;
    }

    public void setStringAttributeName(String stringAttributeName) {
        this.stringAttributeName = stringAttributeName;
    }
    
    

    @Override
    public Capabilities getCapabilities() {
        Capabilities result = super.getCapabilities();
        result.enableAllAttributes();
        result.enableAllClasses();
        result.enable(Capability.NO_CLASS);  //// filter doesn't need class to be set//
        return result;
    }

    @Override
    protected Instances determineOutputFormat(Instances inputFormat) throws Exception {
        if (stringAttributeName == null) {
            throw new IllegalStateException("String attribute name not set");
        }
        
        Instances outputFormat = new Instances(inputFormat, 0);

        Attribute stringAttr = inputFormat.attribute(stringAttributeName);
        stringAttributeIndex = stringAttr.index();
        
        //Add the new columns. There is one for each regex feature.
        NamedRegex[] regexFeatures = getRegexFeatures();
        for (int i = 0; i < regexFeatures.length; i++) {
            String name = regexFeatures[i].getName();
            Attribute attr = new Attribute(name);
            outputFormat.insertAttributeAt(attr, outputFormat.numAttributes());
            
            if (countRegexLengths) {
                name = name + "_L";
                attr = new Attribute(name);
                outputFormat.insertAttributeAt(attr, outputFormat.numAttributes());
            }
            
        }

        return outputFormat;
    }

    @Override
    protected Instance process(Instance instance) throws Exception {
        if (stringAttributeIndex < 0) {
            throw new IllegalStateException("String attribute not set");
        }

        String stringValue = instance.stringValue(stringAttributeIndex);
        NamedRegex[] regexFeatures = getRegexFeatures();

        
        int numOldValues = instance.numAttributes();
        int numNewFeatures = regexFeatures.length;
        if (countRegexLengths) {
            numNewFeatures = regexFeatures.length * 2;
        }
        double[] newValues = new double[numOldValues + numNewFeatures];

        // Copy all attributes from input to output
        for (int i = 0; i < getInputFormat().numAttributes(); i++) {
            if (getInputFormat().attribute(i).type() != Attribute.STRING) {
                // Add simple nominal and numeric attributes directly
                if (instance.value(i) != 0.0) {
                    newValues[i] = instance.value(i);
                }
            } else {
                if (instance.isMissing(i)) {
                    newValues[i] = Utils.missingValue();
                } else {

                    // If this is a string attribute, we have to first add
                    // this value to the range of possible values, then add
                    // its new internal index.
                    if (outputFormatPeek().attribute(i).numValues() == 0) {
                        // Note that the first string value in a
                        // SparseInstance doesn't get printed.
                        outputFormatPeek().attribute(i).addStringValue("Hack to defeat SparseInstance bug");
                    }
                    int newIndex = outputFormatPeek().attribute(i).addStringValue(instance.stringValue(i));
                    newValues[i] = newIndex;
                }
            }
        }
        
        for (int i = 0; i < regexFeatures.length; i++) {
            Pattern pattern = regexFeatures[i].getPattern();

            Matcher matches = pattern.matcher(stringValue);
            int count = 0;
            int maxLength = 0;
            while (matches.find()) {
                count++;
                int len = matches.group().length();
                if (len > maxLength) {
                    maxLength = len;
                }
            }

            int index = numOldValues + i;
            if (countRegexLengths) {
                index = numOldValues + 2 * i;
            }
            newValues[index] = count;
            
            if (countRegexLengths) {
                newValues[numOldValues + 2 * i + 1] = maxLength;
            }
        }

        Instance result = new SparseInstance(instance.weight(), newValues);
        return result;
    }

    @Override
    public String globalInfo() {
        return "Generates a set of attributes from a string attribute. Each new attribute is defined by a regular expression.";
    }

    protected abstract HashMap<String, double[]> getTests() throws Exception;

    protected final boolean runTests() {
        HashMap<String, double[]> tests = null;
        try {
            tests = getTests();
        } catch (Exception e) {
            System.err.println("Test initialization failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
        int failedTests = 0;

        for (Map.Entry<String, double[]> testItem : tests.entrySet()) {
            ArrayList<String> errors = runTest(testItem.getKey(), testItem.getValue());
            if (!errors.isEmpty()) {
                System.err.println("Test failed: " + testItem.getKey());
                for (String message : errors) {
                    System.err.println("  " + message);
                }
                failedTests++;
            }
        }

        if (failedTests > 0) {
            System.err.println("FAILED " + failedTests + " OUT OF " + tests.size() + " TESTS!");
            return false;
        } else {
            System.err.println("All " + tests.size() + " tests passed.");
            return true;
        }
    }

    private ArrayList<String> runTest(String stringValue, double[] expectedValues) {
        ArrayList<String> errors = new ArrayList<String>();

        NamedRegex[] regexFeatures = getRegexFeatures();

        if (expectedValues.length != regexFeatures.length) {
            errors.add("Test should have " + regexFeatures.length + " expected values.");
            return errors;
        }

        //Test each regex
        for (int i = 0; i < regexFeatures.length; i++) {
            Pattern pattern = regexFeatures[i].getPattern();

            Matcher matches = pattern.matcher(stringValue);
            int count = 0;
            while (matches.find()) {
                count++;
            }

            if (count != expectedValues[i]) {
                errors.add("Regex " + regexFeatures[i].getName() + " found " + count + ", expected " + expectedValues[i] + ".");
            }
        }

        return errors;
    }

    /**
     * Combines an array of string fragments into a regex-compatible string
     * using the alternative symbol: "|"
     * All fragments are escaped.
     * @param fragments
     * @return 
     */
    protected String toRegex(String[] fragments) {
        return toRegex(fragments, true);
    }
    
    /**
     * Combines an array of string fragments into a regex-compatible string
     * using the alternative symbol: "|"
     * If escape is true, escapes all special characters in the fragments.
     * @param fragments
     * @param escape
     * @return
     */
    protected String toRegex(String[] fragments, boolean escape) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < fragments.length; i++) {
            String fragment = fragments[i];
            if (escape) {
                fragment = Pattern.quote(fragment);
            }
            
            if (builder.length() > 0) {
                builder.append("|");
            }
            builder.append(fragment);
        }
        return builder.toString();
    }

    protected double[] expect(Object... nameValues) throws Exception {
        if (nameValues.length % 2 != 0) {
            throw new Exception("expect() expects string/double pairs");
        }

        NamedRegex[] regexFeatures = getRegexFeatures();

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
                if (regexFeatures[i].name.equals(names[j])) {
                    all[i] = values[j];
                }
            }
        }

        return all;
    }
}
