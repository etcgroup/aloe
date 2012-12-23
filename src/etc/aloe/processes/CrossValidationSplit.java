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

import etc.aloe.data.LabelableItem;
import java.util.ArrayList;
import java.util.List;

/**
 * Procedure for separating data items into training and test sets for cross
 * validation.
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class CrossValidationSplit<T extends LabelableItem> {

    /**
     * Creates the training set for one fold of a cross-validation on the
     * dataset.
     *
     * @param instances the data to split
     * @param foldIndex 0 for the first fold, 1 for the second, ...
     * @param numFolds the number of folds in the cross-validation. Must be
     * greater than 1.
     * @return the training set
     */
    public List<T> getTrainingForFold(List<T> instances, int foldIndex, int numFolds) {

        int numInstForFold, first, offset;

        if (numFolds < 2) {
            throw new IllegalArgumentException("Number of folds must be at least 2!");
        }
        if (numFolds > instances.size()) {
            throw new IllegalArgumentException("Can't have more folds than instances!");
        }


        numInstForFold = instances.size() / numFolds;
        if (foldIndex < instances.size() % numFolds) {
            numInstForFold++;
            offset = foldIndex;
        } else {
            offset = instances.size() % numFolds;
        }

        List<T> train = new ArrayList<T>(instances.size() - numInstForFold);

        first = foldIndex * (instances.size() / numFolds) + offset;

        for (int i = 0; i < first; i++) {
            train.add(instances.get(i));
        }
        for (int i = first + numInstForFold; i < instances.size(); i++) {
            train.add(instances.get(i));
        }

        return train;
    }

    /**
     * Creates the test set for one fold of a cross-validation on the dataset.
     *
     * @param instances the data to split
     * @param foldIndex 0 for the first fold, 1 for the second, ...
     * @param numFolds the number of folds in the cross-validation. Must be
     * greater than 1.
     * @return the test set as a set
     */
    public List<T> getTestingForFold(List<T> instances, int foldIndex, int numFolds) {
        int numInstForFold, first, offset;

        if (numFolds < 2) {
            throw new IllegalArgumentException("Number of folds must be at least 2!");
        }
        if (numFolds > instances.size()) {
            throw new IllegalArgumentException("Can't have more folds than instances!");
        }


        numInstForFold = instances.size() / numFolds;
        if (foldIndex < instances.size() % numFolds) {
            numInstForFold++;
            offset = foldIndex;
        } else {
            offset = instances.size() % numFolds;
        }

        List<T> test = new ArrayList<T>(numInstForFold);

        first = foldIndex * (instances.size() / numFolds) + offset;

        for (int i = first; i < first + numInstForFold; i++) {
            test.add(instances.get(i));
        }

        return test;
    }
}
