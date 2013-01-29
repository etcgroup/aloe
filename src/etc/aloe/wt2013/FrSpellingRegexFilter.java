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

import java.util.regex.Pattern;

/**
 * Filter that looks for occurrences of certain words and spelling variations.
 *
 * @author Sayer Rippey <srippey@oberlin.edu>
 * original code by Michael Brooks <mjbrooks@uw.edu>
 */
public class FrSpellingRegexFilter extends FrAbstractRegexFilter {

    private NamedRegex[] regexFeatures = new NamedRegex[]{
        // Capitalization
        new NamedRegex("caps", "[A-Z]{2,}"),
        // Matches many styles of hmm
        new NamedRegex("hmm", "(?<!\\w)(h+|u+|m+)u*m+|(h+|e+|u+)", Pattern.CASE_INSENSITIVE),
        // Matches lols, hehes, heehees, hahaas, and hohos (plus many others)
        new NamedRegex("laughter", "(?<!\\w)(lol(ol)*|mdr|(h+e+){2,}h?|he(h+|e+)|(h+(a|o)+)+h*)(?!\\w)", Pattern.CASE_INSENSITIVE),
        // Matches letters repeated more than twice
        new NamedRegex("repetition", "(\\w)\\1{2,}", Pattern.CASE_INSENSITIVE)
    };

    public FrSpellingRegexFilter() {
    }

    public FrSpellingRegexFilter(String attributeName) {
        this.setStringAttributeName(attributeName);
    }

    @Override 
   protected NamedRegex[] getRegexFeatures() {
        return regexFeatures;
    }
}
