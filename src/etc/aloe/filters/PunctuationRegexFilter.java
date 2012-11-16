package etc.aloe.filters;

/**
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class PunctuationRegexFilter extends AbstractRegexFilter {

    private NamedRegex[] regexFeatures = new NamedRegex[]{
        //Ellipses: at least two dots, optionally separated by single whitespace characters
        new NamedRegex("pnct_elipsis", "(\\.\\s?){2,}"),
        //Question marks: simple
        new NamedRegex("pnct_question", "\\?+"),
        //Exclamation marks: simple
        new NamedRegex("pnct_exclamation", "\\!+"),
        //Matches ?! and !?
        new NamedRegex("pnct_qstn_and_excl", "(\\?\\!|\\!\\?)+"),
    };

    public PunctuationRegexFilter() {
    }

    public PunctuationRegexFilter(String attributeName) {
        this.setStringAttributeName(attributeName);
    }

    @Override
    protected NamedRegex[] getRegexFeatures() {
        return regexFeatures;
    }

}
