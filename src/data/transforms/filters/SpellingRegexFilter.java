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
public class SpellingRegexFilter extends AbstractRegexFilter {

    private NamedRegex[] regexFeatures = new NamedRegex[]{
        // Capitalization
        new NamedRegex("caps", "[A-Z]{2,}"),
        // Matches many styles of hmm
        new NamedRegex("hmm", "(?<!\\w)(h+|u+|m+)u*m+", Pattern.CASE_INSENSITIVE),
        // Matches lols, hehes, heehees, hahaas, and hohos (plus many others)
        new NamedRegex("laughter", "(?<!\\w)(lol(ol)*|(h+e+){2,}h?|he(h+|e+)|(h+(a|o)+)+h*)(?!\\w)", Pattern.CASE_INSENSITIVE),
        // Matches letters repeated more than twice
        new NamedRegex("repetition", "(\\w)\\1{2,}", Pattern.CASE_INSENSITIVE)
    };

    public SpellingRegexFilter() {
    }

    public SpellingRegexFilter(String attributeName) {
        this.setStringAttributeName(attributeName);
    }

    @Override
    protected NamedRegex[] getRegexFeatures() {
        return regexFeatures;
    }

    @Override
    protected HashMap<String, double[]> getTests() throws Exception {
        HashMap<String, double[]> tests = new HashMap<String, double[]>();

        //Hmm tests
        tests.put("h m u", expect("hmm", 0));
        tests.put("huh", expect("hmm", 0));
        tests.put("hm", expect("hmm", 1));
        tests.put("um", expect("hmm", 1));
        tests.put("mm", expect("hmm", 1));
        tests.put("hmm", expect("hmm", 1));
        tests.put("hmmm", expect("hmm", 1));
        tests.put("umm", expect("hmm", 1));
        tests.put("ummm", expect("hmm", 1));
        tests.put("hum", expect("hmm", 1));
        tests.put("humm", expect("hmm", 1));
        tests.put("hummm", expect("hmm", 1));
        tests.put("hhmmmm", expect("hmm", 1));
        tests.put("hhummm", expect("hmm", 1));

        //Laughter tests
        tests.put("ha", expect("laughter", 1));
        tests.put("haha", expect("laughter", 1));
        tests.put("hah", expect("laughter", 1));
        tests.put("hhhahhha", expect("laughter", 1));
        tests.put("hahah", expect("laughter", 1));
        tests.put("hahahhhh", expect("laughter", 1));
        tests.put("lo", expect("laughter", 0));
        tests.put("lol", expect("laughter", 1));
        tests.put("lololol", expect("laughter", 1));
        tests.put("he", expect("laughter", 0));
        tests.put("hhhhhhhhhh", expect("laughter", 0));
        tests.put("hhhehhhe", expect("laughter", 1));
        tests.put("hehe", expect("laughter", 1));
        tests.put("heh", expect("laughter", 1));
        tests.put("heheh", expect("laughter", 1));
        tests.put("hee", expect("laughter", 1));
        tests.put("hehhhh", expect("laughter", 1));
        tests.put("heehe", expect("laughter", 1));
        tests.put("heeeeeheeeheee", expect("laughter", 1));
        tests.put("hoho", expect("laughter", 1));

        //Caps tests
        tests.put("Happy birthday. How are You?", expect("caps", 0));
        tests.put("HahaHahaHa?", expect("caps", 0, "laughter", 1));
        tests.put("AAAAAAA!", expect("caps", 1));
        tests.put("AAAAAAA AAAAAAA AAAAAAA!", expect("caps", 3));

        return tests;
    }

    public static void main(String[] args) {
        SpellingRegexFilter ext = new SpellingRegexFilter();
        ext.runTests();
    }
}
