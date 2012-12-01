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
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.stemmers.SnowballStemmer;
import weka.core.stemmers.Stemmer;
import weka.filters.unsupervised.attribute.StringToWordVector;

/**
 * Extension of Weka's StringToWordVector filter that allows a string attribute
 * to be specified by name, and uses a special term filter (NoNonsenseStemmer).
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class SimpleStringToWordVector extends StringToWordVector {

    String stringAttributeName;

    @Override
    public boolean setInputFormat(Instances instanceInfo) throws Exception {
        if (stringAttributeName == null) {
            throw new IllegalStateException("String attribute name was not set");
        }

        Attribute stringAttr = instanceInfo.attribute(stringAttributeName);
        if (stringAttr == null) {
            throw new IllegalStateException("Attribute " + stringAttributeName + " does not exist");
        }

        this.setAttributeIndicesArray(new int[]{stringAttr.index()});

        return super.setInputFormat(instanceInfo);
    }

    public String getStringAttributeName() {
        return stringAttributeName;
    }

    public void setStringAttributeName(String stringAttributeName) {
        this.stringAttributeName = stringAttributeName;
    }

    public static class NoNonsenseStemmer implements Stemmer {

        private SnowballStemmer snowball;
        private final Pattern nonsensePattern;

        public NoNonsenseStemmer(boolean useSnowball) {
            if (useSnowball) {
                this.snowball = new SnowballStemmer();
            }

            this.nonsensePattern = Pattern.compile("^[\\p{Digit}\\p{Punct}]*$");
        }

        @Override
        public String stem(String word) {

            //is it an unreasonable word?
            if (nonsensePattern.matcher(word).matches()) {
                word = "";
            }

            if (snowball != null) {
                return snowball.stem(word);
            } else {
                return word;
            }
        }

        @Override
        public String getRevision() {
            return "1";
        }
    }
}
