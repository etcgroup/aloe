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
import weka.classifiers.bayes.HMM;
import weka.core.SelectedTag;
import weka.core.Attribute;
import weka.core.Utils;

/**
 * Performs basic training of an HMM classifier.
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 * @authort Eli Rose <erose@oberlin.edu>
 */
public class TrainingImplHMM implements Training {

    public TrainingImplHMM() {
    }

    @Override
    public WekaModel train(ExampleSet examples) {
        HMM hmm = new HMM();
        hmm.setCovarianceType(new SelectedTag(2, HMM.TAGS_COVARIANCE_TYPE));
        hmm.setNumStates(5);
        Classifier classifier = hmm;

        try {
            System.out.print("Training HMM on " + examples.size() + " examples... ");

            classifier.buildClassifier(examples.getInstances());
            System.out.println("done.");
        } catch (Exception ex) {
            System.err.println("Unable to train HMM.");
            System.err.println("\t" + ex.getMessage());
            ex.printStackTrace();
        }

        WekaModel model = new WekaModel(classifier);
        return model;
    }
}
