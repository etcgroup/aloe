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
package etc.aloe.cscw2013;

import etc.aloe.data.ExampleSet;
import etc.aloe.data.FeatureSpecification;
import etc.aloe.processes.FeatureExtraction;
import etc.aloe.processes.Loggable.Verbosity;
import weka.core.Instances;
import weka.filters.Filter;

/**
 * Applies an existing feature specification to a data set to get a new data set
 * with features extracted.
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class FeatureExtractionImpl implements FeatureExtraction {

    private Verbosity verbosity = Verbosity.Normal;

    @Override
    public ExampleSet extractFeatures(ExampleSet basicExamples, FeatureSpecification spec) {
        ExampleSet examples = basicExamples;

        if (this.verbosity.ordinal() > Verbosity.Quiet.ordinal()) {
            System.out.print("Extracting features for " + examples.size() + " examples... ");
        }

        for (Filter filter : spec.getFilters()) {
            try {
                Instances instances = Filter.useFilter(examples.getInstances(), filter);
                examples = new ExampleSet(instances);
            } catch (Exception e) {
                System.err.println("Unable to apply filter: " + filter.toString());
                System.err.println("\t" + e.getMessage());
                return null;
            }
        }

        if (this.verbosity.ordinal() > Verbosity.Quiet.ordinal()) {
            System.out.println("done.");
        }

        return examples;
    }

    @Override
    public void setVerbosity(Verbosity verbosityLevel) {
        this.verbosity = verbosityLevel;
    }
}
