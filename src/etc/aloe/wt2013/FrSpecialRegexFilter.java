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
 * Filter that searches for occurrences of special strings (negations, names,
 * and swearing)
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class FrSpecialRegexFilter extends FrAbstractRegexFilter {

    private final String[] contractedNegationForms = new String[]{
        "n'?importe",
        "n'?est?",
        "n'?ai",
        "n'?as?",
        "n'?y"
        
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
        new NamedRegex("negation", "(?<!\\w)(non|ne|pas|n'?|jamais|nul|aucun|rien|personne" + toRegex(contractedNegationForms, false) + ")(?!\\w)", Pattern.CASE_INSENSITIVE),
        // Matches many swearwords
        new NamedRegex("swear", "(?<!\\w)("
        + "((?=\\p{Punct}*[@#$%^&*]\\p{Punct}*[@#$%^&*])([\\p{Punct}&&[^.]]{4,}))"
        + "|merde?s?"
        + "|put(e|ain)?"
        + "|fou(s|tre|tu)"
        + "|con(nerie?s?|(ne)?s?|n?arde?s?|n?asses?)?"
        + "|foire?(s|ons|ez|\u00E9s?|ant|er)?"
        + "|cul"
        + "|salop(ard|e)?"
        + "|craint"
        + "|casse-toi|cassez-vous"
        + "|bordels?"
        + "|mince"
        + ")(?!\\w)", Pattern.CASE_INSENSITIVE),
        // Matches lols, hehes, heehees, hahaas, and hohos (plus many others)
        new NamedRegex("names", "(?<!\\w)(" + toRegex(namesList) + ")", Pattern.CASE_INSENSITIVE)
    };

    public FrSpecialRegexFilter() {
    }

    public FrSpecialRegexFilter(String attributeName) {
        this.setStringAttributeName(attributeName);
    }

    @Override
    protected NamedRegex[] getRegexFeatures() {
        return regexFeatures;
    }
}
