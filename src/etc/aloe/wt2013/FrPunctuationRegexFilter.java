/*
 * This file is part of ALOE.
 *
 * ALOE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * ALOE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with ALOE.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2012 SCCL, University of Washington (http://depts.washington.edu/sccl)
 */
package etc.aloe.wt2013;

/**
 * Filter that searches for occurrences of punctuation characters.
 *
 * @author Michael Brooks <mjbrooks@uw.edu> 
 */
public class FrPunctuationRegexFilter extends FrAbstractRegexFilter {

    private NamedRegex[] regexFeatures = new NamedRegex[]{
        //Ellipses: at least two dots, optionally separated by single whitespace characters
        new NamedRegex("pnct_elipsis", "((\\.\\s?){2,})|((\\,\\s?){2,})"),
        //Question marks: simple
        new NamedRegex("pnct_question", "\\?+"),
        //Exclamation marks: simple
        new NamedRegex("pnct_exclamation", "\\!+"),
        //Matches ?! and !?
        new NamedRegex("pnct_qstn_and_excl", "(\\?\\!|\\!\\?)+"),};

    public FrPunctuationRegexFilter() {
    }

    public FrPunctuationRegexFilter(String attributeName) {
        this.setStringAttributeName(attributeName);
    }

    @Override
    protected NamedRegex[] getRegexFeatures() {
        return regexFeatures;
    }
}
