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
package etc.aloe.wt2013;

import etc.aloe.cscw2013.*;
import etc.aloe.data.ExampleSet;
import etc.aloe.processes.Training;
import weka.classifiers.Classifier;
import weka.classifiers.trees.DecisionStump;
import weka.core.Utils;

/**
 * Performs basic training of a linear support vector machine classifier.
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class TrainingImplDecisionStump implements Training {
    public TrainingImplDecisionStump() {
    }

    @Override
    public WekaModel train(ExampleSet examples) {
        DecisionStump stump = new DecisionStump();

        Classifier classifier = stump;

        try {
            System.out.print("Training decision tree on " + examples.size() + " examples... ");
            classifier.buildClassifier(examples.getInstances());
            System.out.println("done.");
        } catch (Exception ex) {
            System.err.println("Unable to train decision tree.");
            System.err.println("\t" + ex.getMessage());
        }

        WekaModel model = new WekaModel(classifier);
        return model;
    }
}
