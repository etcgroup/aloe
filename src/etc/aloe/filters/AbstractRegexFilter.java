package etc.aloe.filters;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import weka.core.Capabilities.Capability;
import weka.core.*;
import weka.filters.SimpleStreamFilter;
import weka.filters.UnsupervisedFilter;

/**
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public abstract class AbstractRegexFilter extends SimpleStreamFilter
        implements UnsupervisedFilter {

    private boolean countRegexLengths = false;

    /**
     * Set to true to add features for the length of the regex match.
     * Defaults to false.
     * @param countRegexLengths
     */
    public void setCountRegexLengths(boolean countRegexLengths) {
        this.countRegexLengths = countRegexLengths;
    }

    protected static class NamedRegex implements Serializable {

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
    private String stringAttributeName;
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

    /**
     * Combines an array of string fragments into a regex-compatible string
     * using the alternative symbol: "|" All fragments are escaped.
     *
     * @param fragments
     * @return
     */
    protected String toRegex(String[] fragments) {
        return toRegex(fragments, true);
    }

    /**
     * Combines an array of string fragments into a regex-compatible string
     * using the alternative symbol: "|" If escape is true, escapes all special
     * characters in the fragments.
     *
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
}
