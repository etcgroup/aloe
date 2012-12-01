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
package etc.aloe.filters;

import java.util.regex.Pattern;

/**
 * A filter that searches for occurrences of known pronouns, in several
 * categories.
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class PronounRegexFilter extends AbstractRegexFilter {

    private NamedRegex[] regexFeatures = new NamedRegex[]{
        // first person singular pronouns
        new NamedRegex("prn_first_sng", "(?<!\\w)(me|i|my|mine|myself)(?!\\w)", Pattern.CASE_INSENSITIVE),
        // second person singular pronouns
        new NamedRegex("prn_second_sng", "(?<!\\w)(you|your|yours|yourself|y\\'?all)(?!\\w)", Pattern.CASE_INSENSITIVE),
        // third person singular pronouns
        new NamedRegex("prn_third_sng", "(?<!\\w)(he|him|his|himself|she|her|hers|herself|it|itself|its|one|oneself)(?!\\w)", Pattern.CASE_INSENSITIVE),
        // first person plural pronouns
        new NamedRegex("prn_first_pl", "(?<!\\w)(we|us|ourself|ourselves|our|ours)(?!\\w)", Pattern.CASE_INSENSITIVE),
        // second person plural pronouns
        new NamedRegex("prn_second_pl", "(?<!\\w)(you all|yourselves)(?!\\w)", Pattern.CASE_INSENSITIVE),
        // third person plural pronouns
        new NamedRegex("prn_third_pl", "(?<!\\w)(they|them|themself|themselves|theirself|theirselves|theirs|their)(?!\\w)", Pattern.CASE_INSENSITIVE),
        // interrogative pronouns
        new NamedRegex("prn_interrogative", "(?<!\\w)(who(m|se|\\'s)?)(?!\\w)", Pattern.CASE_INSENSITIVE)
    };

    public PronounRegexFilter() {
    }

    public PronounRegexFilter(String attributeName) {
        this.setStringAttributeName(attributeName);
    }

    @Override
    protected NamedRegex[] getRegexFeatures() {
        return regexFeatures;
    }
}
