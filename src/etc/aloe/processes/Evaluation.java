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

import etc.aloe.data.EvaluationReport;
import etc.aloe.data.ExampleSet;
import java.util.List;

/**
 * Evaluation measures the performance of a model over some labeled example
 * data.
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public interface Evaluation {

    /**
     * Evaluates the performance of the model over the given labeled examples.
     *
     * The model features must match the example features.
     *
     * @param predictions The predictions to evaluate.
     * @param examples The examples to use for evaluation.
     * @return The EvaluationReport.
     * @throws IllegalArgumentException If the model features do not match the
     * examples features.
     */
    EvaluationReport evaluate(List<Boolean> predictions, ExampleSet examples) throws IllegalArgumentException;
}
