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

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.Assert;

/**
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class RegexFilterTestUtils {

    private AbstractRegexFilter regexFilter;

    protected RegexFilterTestUtils(AbstractRegexFilter regexFilter) {
        this.regexFilter = regexFilter;
    }

    protected double[] expect(Object... nameValues) {

        AbstractRegexFilter.NamedRegex[] regexFeatures = regexFilter.getRegexFeatures();

        String[] names = new String[nameValues.length / 2];
        double[] values = new double[nameValues.length / 2];

        for (int i = 0; i < nameValues.length / 2; i++) {
            String regexName = (String) nameValues[i * 2];
            double value = 0;
            try {
                value = (Double) nameValues[i * 2 + 1];
            } catch (ClassCastException e) {
                value = (Integer) nameValues[i * 2 + 1];
            }
            names[i] = regexName;
            values[i] = value;
        }

        double[] all = new double[regexFeatures.length];
        for (int i = 0; i < regexFeatures.length; i++) {
            all[i] = 0;

            for (int j = 0; j < names.length; j++) {
                if (regexFeatures[i].getName().equals(names[j])) {
                    all[i] = values[j];
                }
            }
        }

        return all;
    }

    public ArrayList<String> runTest(String stringValue, double[] expectedValues) {
        ArrayList<String> errors = new ArrayList<String>();

        AbstractRegexFilter.NamedRegex[] regexFeatures = regexFilter.getRegexFeatures();

        Assert.assertEquals(expectedValues.length, regexFeatures.length);

        //Test each regex
        for (int i = 0; i < regexFeatures.length; i++) {
            Pattern pattern = regexFeatures[i].getPattern();

            Matcher matches = pattern.matcher(stringValue);
            int count = 0;
            while (matches.find()) {
                count++;
            }

            if (count != expectedValues[i]) {
                System.err.println("Regex " + regexFeatures[i].getName() + " found " + count + ", expected " + expectedValues[i] + ". String: " + stringValue + " Regex: " + regexFeatures[i].getRegex());
            }
            Assert.assertEquals(expectedValues[i], count, 0);

        }

        return errors;
    }
}
