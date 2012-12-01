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
import java.util.List;

/**
 * Procedure for separating data items into training and test sets for cross
 * validation.
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public interface CrossValidationSplit<T extends LabelableItem> {

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
    List<T> getTrainingForFold(List<T> instances, int foldIndex, int numFolds);

    /**
     * Creates the test set for one fold of a cross-validation on the dataset.
     *
     * @param instances the data to split
     * @param foldIndex 0 for the first fold, 1 for the second, ...
     * @param numFolds the number of folds in the cross-validation. Must be
     * greater than 1.
     * @return the test set as a set
     */
    List<T> getTestingForFold(List<T> instances, int foldIndex, int numFolds);
}
