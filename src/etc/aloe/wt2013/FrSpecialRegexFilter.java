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
 * @author Sayer Rippey <srippey@oberlin.edu>
 * original code by Michael Brooks <mjbrooks@uw.edu>
 */
public class FrSpecialRegexFilter extends FrAbstractRegexFilter {

    private final String[] contractedNegationForms = new String[]{ 
        "n'?\\s*importe",
        "n'?\\s*est?",
        "n'?\\s*ai",
        "n'?\\s*as?",
        "n'?\\s*y"
        
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
        // French negation: non, ne, n', pas, and words that replace pas
        // http://frencha.bout.com/od/grammar/a/negation.htm
        new NamedRegex("negation", "(?<!\\w)(n+o+n*|ne|nan|p+a+s+|n'?|jamais|nul|aucun|rien|personne" + toRegex(contractedNegationForms, false) + ")(?!\\w)", Pattern.CASE_INSENSITIVE),
        // Matches acceptance terms
        new NamedRegex("accept", "(?<!\\w)(d'?ac+|d'?\\s?accord|ok|okay)(?!\\w)", Pattern.CASE_INSENSITIVE),
        // Matches agreement terms
        new NamedRegex("agree", "(?<!\\w)(d'?ac+|d'?\\s?accord|ok|okay|oui|yep|h?m?ouais?)(?!\\w)", Pattern.CASE_INSENSITIVE),
        // Matches many swearwords (including the English ones)
        // Plus signs and asterixes to allow for repeated letters/typos
        new NamedRegex("swear", "(?<!\\w)("
        + "((?=\\p{Punct}*[@#$%^&*]\\p{Punct}*[@#$%^&*])([\\p{Punct}&&[^.]]{4,}))"
        + "|m+e+r+d+e*s?(e+u+x+|e+u+s+e+)?"
        + "|p+u+t+(e+|a+i+n+)" 
        + "|c+o+n+(nerie?s?|(ne)?s?|n?arde?s?|n?asses?)?"
        + "|f+o+i+r+e*(s+|o+n+s+|e+z+|\u00E9+s*|a+n+t+|e+r+)?" 
        + "|c+u+l+"
        + "|b+r+o+n+x+"
        + "|s+a+l+o+p+(a+r+d+|e+)?"
        + "|c+h+i+o+t+te+"
        + "|c+r+a+i+n+t"
        + "|c+a+s+se-*\\s*to+i+"
        + "|c+a+s+se+z+-*\\s*v+o+u+s+"
        + "|b+o+r+d+e+l+s*"
        + "|m+i+n+c+e+"
        + "|m+o+c+h+e+"
        + "|c+r+a+p+(p?e+d+|s+|p?i+n+g+|p?y+)?"
        + "|s+h+i+t+(s+|t?i+n+g+|t?y+)?"
        + "|(g+o+d?)?d+a+m+(n+|mi+t+)?"
        + "|(m+o+t+h+e+r+)?f+u+c+k+(e+d+|i+n+g+|e+r+)?"
        + "|a+s+s+(h+o+l+e+)?"
        + "|s+u+c+k+(y+|s+|e+d+)?"
        + ")(?!\\w)", Pattern.CASE_INSENSITIVE),
        //Matches known named
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
