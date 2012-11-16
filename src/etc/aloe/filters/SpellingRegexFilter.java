package etc.aloe.filters;

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

}
