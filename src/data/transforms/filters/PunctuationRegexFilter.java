/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data.transforms.filters;

import java.util.HashMap;

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

    @Override
    protected HashMap<String, double[]> getTests() throws Exception {
        HashMap<String, double[]> tests = new HashMap<String, double[]>();

        //Question marks
        tests.put("Hello???What's going on here?", expect("pnct_question", 4));

        tests.put("Angry! angry angry angry!!!", expect("pnct_exclamation", 4));
        
        //Combined ?! and !/
        tests.put("Crazy!? Who are you calling crazy?!", new double[]{0, 2, 2, 2});

        //Ellipses
        tests.put("Hm... that ", expect("pnct_elipsis", 1));
        tests.put("is very interesting.. I ", expect("pnct_elipsis", 1));
        tests.put("wonder...... very . . intere", expect("pnct_elipsis", 2));
        tests.put("sting . . . indeed. ", expect("pnct_elipsis", 1));

        return tests;
    }

    public static void main(String[] args) {
        PunctuationRegexFilter ext = new PunctuationRegexFilter();
        ext.runTests();
    }
}
