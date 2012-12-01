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
 * CrossValidationPrep prepares a set of labeled entities for cross validation.
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public interface CrossValidationPrep<T extends LabelableItem> {

    /**
     * Randomizes the instances in place.
     *
     * @param instances
     */
    void randomize(List<T> instances);

    /**
     * Stratifies the instances and returns a new list.
     *
     * @param instances
     * @param numFolds
     * @return
     */
    List<T> stratify(List<T> instances, int numFolds);
}
