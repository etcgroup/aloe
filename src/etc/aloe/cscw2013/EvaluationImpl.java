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

import etc.aloe.data.EvaluationReport;
import etc.aloe.data.ExampleSet;
import etc.aloe.processes.Evaluation;
import java.util.List;

/**
 * Evaluates a set of labeled examples given some predicted labels.
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class EvaluationImpl implements Evaluation {

    private double falseNegativeCost = 1;
    private double falsePositiveCost = 1;

    /**
     * Construct an evaluation with equal costs (1) for each kind of error.
     */
    public EvaluationImpl() {
    }

    /**
     * Construct a cost-sensitive evaluation.
     *
     * @param falsePositiveCost
     * @param falseNegativeCost
     */
    public EvaluationImpl(double falsePositiveCost, double falseNegativeCost) {
        this.falsePositiveCost = falsePositiveCost;
        this.falseNegativeCost = falseNegativeCost;
    }

    @Override
    public EvaluationReport evaluate(List<Boolean> predictions, ExampleSet examples) throws IllegalArgumentException {

        EvaluationReport evaluation = new EvaluationReport(falsePositiveCost, falseNegativeCost);

        for (int i = 0; i < examples.size(); i++) {
            Boolean trueLabel = examples.getTrueLabel(i);
            if (trueLabel != null) {
                evaluation.recordPrediction(predictions.get(i), trueLabel);
            }
        }

        return evaluation;
    }
}
