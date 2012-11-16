package etc.aloe.filters;

import java.util.regex.Pattern;

/**
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class SpecialRegexFilter extends AbstractRegexFilter {

    private final String[] contractedNegationForms = new String[]{
        "aren'?t",
        "can'?t",
        "couldn'?t",
        "daren'?t",
        "didn'?t",
        "doesn'?t",
        "don'?t",
        "hasn'?t",
        "haven'?t",
        "hadn'?t",
        "isn'?t",
        "mayn'?t",
        "mightn'?t",
        "mustn'?t",
        "needn'?t",
        "oughtn'?t",
        "shan'?t",
        "shouldn'?t",
        "wasn'?t",
        "weren'?t",
        "won'?t",
        "wouldn'?t"
    };
    private final String[] namesList = new String[]{
        "Ray",
        "Gary",
        "Pascal",
        "Paul",
        "Derek",
        "Ben",
        "Stef",
        "Rene",
        "Gabriel",
        "Maurice",
        "Emile",
        "Matt",
        "Sam",
        "Kevin",
        "Rick",
        "Naomi",
        "Christophe",
        "Dennis",
        "Rob"
    };
    private NamedRegex[] regexFeatures = new NamedRegex[]{
        // Negation: no, not, cannot, can't/won't/whatever, cant/wont/whatever
        // See http://www.englishclub.com/vocabulary/contractions-negative.htm
        new NamedRegex("negation", "(?<!\\w)(not?|cannot|" + toRegex(contractedNegationForms, false) + ")(?!\\w)", Pattern.CASE_INSENSITIVE),
        // Matches many swearwords
        new NamedRegex("swear", "(?<!\\w)("
            + "((?=\\p{Punct}*[@#$%^&*]\\p{Punct}*[@#$%^&*])([\\p{Punct}&&[^.]]{4,}))"
            + "|crap(p?ed|s|p?ing|p?y)?"
            + "|shit(s|t?ing|t?y)?"
            + "|(god?)?dam(n|mit)?"
            + "|(mother)?fuck(ed|ing|er)?"
            + "|ass(hole)?"
            + "|suck(y|s|ed)?"
            + ")(?!\\w)", Pattern.CASE_INSENSITIVE),
        // Matches lols, hehes, heehees, hahaas, and hohos (plus many others)
        new NamedRegex("names", "(?<!\\w)(" + toRegex(namesList) + ")", Pattern.CASE_INSENSITIVE)
    };

    public SpecialRegexFilter() {
    }

    public SpecialRegexFilter(String attributeName) {
        this.setStringAttributeName(attributeName);
    }

    @Override
    protected NamedRegex[] getRegexFeatures() {
        return regexFeatures;
    }
}
