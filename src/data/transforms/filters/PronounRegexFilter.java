/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data.transforms.filters;

import java.util.HashMap;
import java.util.regex.Pattern;

/**
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class PronounRegexFilter extends AbstractRegexFilter {

    private NamedRegex[] regexFeatures = new NamedRegex[]{
        // first person singular pronouns
        new NamedRegex("prn_first_sng", "(?<!\\w)(me|i|my|mine|myself)(?!\\w)", Pattern.CASE_INSENSITIVE),
        // second person singular pronouns
        new NamedRegex("prn_second_sng", "(?<!\\w)(you|your|yours|yourself|y\\'?all)(?!\\w)", Pattern.CASE_INSENSITIVE),
        // third person singular pronouns
        new NamedRegex("prn_third_sng", "(?<!\\w)(he|him|his|himself|she|her|hers|herself|it|itself|its|one|oneself)(?!\\w)", Pattern.CASE_INSENSITIVE),
        // first person plural pronouns
        new NamedRegex("prn_first_pl", "(?<!\\w)(we|us|ourself|ourselves|our|ours)(?!\\w)", Pattern.CASE_INSENSITIVE),
        // second person plural pronouns
        new NamedRegex("prn_second_pl", "(?<!\\w)(you all|yourselves)(?!\\w)", Pattern.CASE_INSENSITIVE),
        // third person plural pronouns
        new NamedRegex("prn_third_pl", "(?<!\\w)(they|them|themself|themselves|theirself|theirselves|theirs|their)(?!\\w)", Pattern.CASE_INSENSITIVE),
        // interrogative pronouns
        new NamedRegex("prn_interrogative", "(?<!\\w)(who(m|se|\\'s)?)(?!\\w)", Pattern.CASE_INSENSITIVE)
    };

    public PronounRegexFilter() {
    }

    public PronounRegexFilter(String attributeName) {
        this.setStringAttributeName(attributeName);
    }

    @Override
    protected NamedRegex[] getRegexFeatures() {
        return regexFeatures;
    }

    @Override
    protected HashMap<String, double[]> getTests() throws Exception {
        HashMap<String, double[]> tests = new HashMap<String, double[]>();

        //Each feature is in the ballpark
        tests.put("I you he we yourselves they who", new double[]{1, 1, 1, 1, 1, 1, 1});

        //All options for one feature are recognized
        tests.put("me i my mine myself", expect("prn_first_sng", 5));

        //Simple token recognition works
        tests.put("westuff stuffwe we'stuff stuff'we we", expect("prn_first_pl", 3));

        return tests;
    }

    public static void main(String[] args) {
        PronounRegexFilter ext = new PronounRegexFilter();
        ext.runTests();
    }
}
