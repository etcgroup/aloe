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
package etc.aloe.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A class for storing prediction data based on indexed instances.
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class Predictions {

    private List<Prediction> predictions = new ArrayList<Prediction>();
    private int truePositiveCount = 0;
    private int trueNegativeCount = 0;
    private int falsePositiveCount = 0;
    private int falseNegativeCount = 0;

    public Predictions() {
    }

    /**
     * Make a copy of the specified report.
     *
     * @param parent
     * @param predictions
     */
    private Predictions(Predictions parent) {
        this.predictions = parent.predictions;
        this.truePositiveCount = parent.truePositiveCount;
        this.trueNegativeCount = parent.trueNegativeCount;
        this.falsePositiveCount = parent.falsePositiveCount;
        this.falseNegativeCount = parent.falseNegativeCount;
    }

    public Label getPredictedLabel(int index) {
        return predictions.get(index).getPredictedLabel();
    }

    public Double getPredictionConfidence(int index) {
        return predictions.get(index).getConfidence();
    }

    public Label getTrueLabel(int index) {
        return predictions.get(index).getTrueLabel();
    }

    public int size() {
        return predictions.size();
    }

    public int getTruePositiveCount() {
        return truePositiveCount;
    }

    public int getTrueNegativeCount() {
        return trueNegativeCount;
    }

    public int getFalsePositiveCount() {
        return falsePositiveCount;
    }

    public int getFalseNegativeCount() {
        return falseNegativeCount;
    }

    /**
     * Add a predicted data point.
     *
     * @param predictedLabel
     * @param confidence
     */
    public void add(Label predictedLabel, Double confidence) {
        this.add(predictedLabel, confidence, null);
    }

    /**
     * Add a predicted data point with a known true value.
     *
     * @param predictedLabel
     * @param confidence
     * @param trueLabel
     */

    public void add(Label predictedLabel, Double confidence, Label trueLabel) {
        this.predictions.add(new Prediction(predictedLabel, trueLabel, confidence));

        if (trueLabel != null) {
            if (predictedLabel == true) {
                if (predictedLabel == trueLabel) {
                    truePositiveCount++;
                } else {
                    falsePositiveCount++;
                }
            } else {
                if (predictedLabel == trueLabel) {
                    trueNegativeCount++;
                } else {
                    falseNegativeCount++;
                }
            }
        }
    }

    /**
     * Creates a copy of these predictions, sorted by confidence (ascending).
     *
     * @return
     */
    Predictions sortByConfidence() {
        ArrayList<Prediction> sortedPredictions = new ArrayList<Prediction>(predictions);
        Collections.sort(sortedPredictions, new Comparator<Prediction>() {
            @Override
            public int compare(Prediction o1, Prediction o2) {
                return o1.getConfidence().compareTo(o2.getConfidence());
            }
        });

        Predictions copy = new Predictions(this);
        copy.predictions = sortedPredictions;

        return copy;
    }

    /**
     * Class for storing an individual prediction.
     */
    private class Prediction {

        private final Label predictedLabel;
        private final Label trueLabel;
        private final Double confidence;

        public Prediction(Label predictedLabel, Label trueLabel, Double confidence) {
            this.predictedLabel = predictedLabel;
            this.trueLabel = trueLabel;
            this.confidence = confidence;
        }

        public Label getPredictedLabel() {
            return predictedLabel;
        }

        public Label getTrueLabel() {
            return trueLabel;
        }

        public Double getConfidence() {
            return confidence;
        }
    }
}
