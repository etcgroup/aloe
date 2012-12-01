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
import etc.aloe.data.Model;
import etc.aloe.processes.Training;
import weka.classifiers.Classifier;
import weka.classifiers.functions.SMO;
import weka.core.Utils;

/**
 * Performs basic training of a linear support vector machine classifier.
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class TrainingImpl implements Training {

    private static final String SMO_OPTIONS = "-C 1.0 -L 0.0010 -P 1.0E-12 -N 0 -V -1 -W 1 -K \"weka.classifiers.functions.supportVector.PolyKernel -C 250007 -E 1.0\"";

    public TrainingImpl() {
    }

    @Override
    public Model train(ExampleSet examples) {
        System.out.println("SMO Options: " + SMO_OPTIONS);
        SMO smo = new SMO();
        try {
            smo.setOptions(Utils.splitOptions(SMO_OPTIONS));
        } catch (Exception ex) {
            System.err.println("Unable to configure SMO.");
            System.err.println("\t" + ex.getMessage());
            return null;
        }

        Classifier classifier = smo;

        try {
            System.out.print("Training SMO on " + examples.size() + " examples... ");
            classifier.buildClassifier(examples.getInstances());
            System.out.println("done.");
        } catch (Exception ex) {
            System.err.println("Unable to train SMO.");
            System.err.println("\t" + ex.getMessage());
        }

        Model model = new Model(classifier);
        return model;
    }
}
