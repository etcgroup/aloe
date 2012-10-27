/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data.transforms.filters;

import java.util.HashMap;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class EmoticonRegexFilter extends AbstractRegexFilter {

    private NamedRegex[] regexFeatures = new NamedRegex[]{
        //Happy smilies:
        new NamedRegex("emtcn_smiley", "[:\\=8\\(][:\\)-D\\=][\\)D\\=:8]?"),
        //Frowny smilies:
        new NamedRegex("emtcn_frownie", "([>:\\)]?[:\\-\\)][c<\\[\\(\\{:])"),
        //Amused smilies:
        new NamedRegex("emtcn_amuse", "([x;\\*>\\(]?[;\\-\\*\\^\\(][;\\)\\]D])|([:xX>d]?[:\\-\\=xX][bpP:])"),
        //Worry smilies:
        new NamedRegex("emtcn_worry", "([>:\\/\\\\\\\\]?[:\\-\\=\\\\\\\\\\/][\\|\\.sS\\\\\\\\\\/\\=])|([\\|\\\\\\\\\\/sS][\\-:][\\/\\\\\\\\:]?)")
    };

    @Override
    protected NamedRegex[] getRegexFeatures() {
        //This class is not yet well tested or checked. I mean just look at those regexes.
        throw new NotImplementedException();
        //return regexFeatures;
    }

    @Override
    protected HashMap<String, double[]> getTests() throws Exception {
        HashMap<String, double[]> tests = new HashMap<String, double[]>();

        //The basics
        tests.put(":) :-) (: (-: :D :-D", expect("emtcn_smiley", 4));
        tests.put(":( :-( ): )-:", expect("emtcn_frownie", 4));
        tests.put(":P :-P ;) ;-)", expect("emtcn_amuse", 4));
        tests.put(":\\ :-o", expect("emtcn_worry", 1));

        return tests;
    }

    public static void main(String[] args) {
        EmoticonRegexFilter ext = new EmoticonRegexFilter();
        ext.runTests();
    }
}
