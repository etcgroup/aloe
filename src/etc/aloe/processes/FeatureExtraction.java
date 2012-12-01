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
package etc.aloe.processes;

import etc.aloe.data.ExampleSet;
import etc.aloe.data.FeatureSpecification;

/**
 * FeatureExtraction extracts features from the provided data.
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public interface FeatureExtraction extends Loggable {

    /**
     * Extracts the specified features from the segmented data.
     *
     * Exactly one example will be created in the example set for every segment.
     *
     * @param ExampleSet The basic examples created from segments.
     * @param spec The features to extract.
     * @return A set of examples with features extracted, or null on failure.
     */
    ExampleSet extractFeatures(ExampleSet basicExamples, FeatureSpecification spec);
}
