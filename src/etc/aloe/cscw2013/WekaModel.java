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
import etc.aloe.data.Predictions;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import weka.classifiers.Classifier;

/**
 * Model implementation that uses Weka classifiers.
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class WekaModel implements Model {

    private Classifier classifier;

    public WekaModel() {
    }

    public WekaModel(Classifier classifier) {
        this.classifier = classifier;
    }

    @Override
    public boolean save(OutputStream destination) throws IOException {
        ObjectOutputStream out = new ObjectOutputStream(destination);
        out.writeObject(classifier);
        return true;
    }

    @Override
    public boolean load(InputStream source) throws InvalidObjectException {
        try {
            ObjectInputStream in = new ObjectInputStream(source);
            this.classifier = (Classifier) in.readObject();
            return true;
        } catch (IOException e) {
            throw new InvalidObjectException(e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new InvalidObjectException(e.getMessage());
        }
    }

    /**
     * Attempt to label each example in the example set according to the model.
     *
     * Returns the list of generated labels. Order corresponds to the order of
     * examples.
     *
     * @param examples
     * @return
     */
    @Override
    public Predictions getPredictions(ExampleSet examples) {
        Predictions predictions = new Predictions();

        for (int i = 0; i < examples.size(); i++) {
            try {
                Boolean trueLabel = examples.getTrueLabel(i);

                double classValue = classifier.classifyInstance(examples.get(i));
                Boolean predictedLabel = examples.getClassLabel(classValue);

                double[] distribution = classifier.distributionForInstance(examples.get(i));
                Double confidence = examples.getConfidence(distribution, classValue);
                predictions.add(predictedLabel, confidence, trueLabel);

            } catch (Exception ex) {
                System.err.println("Classification error on instance " + i);
            }
        }

        return predictions;
    }

    /**
     * Get the weka classifier.
     *
     * @return
     */
    public Classifier getClassifier() {
        return classifier;
    }
}
