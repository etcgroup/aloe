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

import com.csvreader.CsvWriter;
import etc.aloe.processes.Saving;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for storing a ROC curve.
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class ROC implements Saving {

    private final List<Double> falsePositiveRates = new ArrayList<Double>();
    private final List<Double> truePositiveRates = new ArrayList<Double>();
    private final List<Double> thresholdValues = new ArrayList<Double>();
    private final String name;

    public ROC(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int size() {
        return this.falsePositiveRates.size();
    }

    public double getFalsePositiveRate(int index) {
        return falsePositiveRates.get(index);
    }

    public double getTruePositiveRate(int index) {
        return truePositiveRates.get(index);
    }

    public double getThresholdValue(int index) {
        return thresholdValues.get(index);
    }

    /**
     * Record a data point on the ROC curve.
     *
     * @param fpRate
     * @param tpRate
     * @param threshold
     */
    public void record(double fpRate, double tpRate, double threshold) {
        this.falsePositiveRates.add(fpRate);
        this.truePositiveRates.add(tpRate);
        this.thresholdValues.add(threshold);
    }

    /**
     * Clear the recorded curves.
     */
    public void clear() {
        this.falsePositiveRates.clear();
        this.truePositiveRates.clear();
        this.thresholdValues.clear();
    }

    /**
     * Generate the ROC curve from the given predictions.
     *
     * @param predictions
     */
    public void calculateCurve(Predictions predictions) {
        if (!Label.isBinary()) {
            throw new IllegalStateException("ROC curves can only be generated for binary classification!");
        }

        clear();
        predictions = predictions.sortByConfidence();

        Label pos = Label.TRUE();
        Label neg = Label.FALSE();

        int truePositives = 0;
        int falsePositives = 0;

        int totalPositives = predictions.getConfusionCount(pos, pos) + predictions.getConfusionCount(pos, neg);
        int totalNegatives = predictions.getConfusionCount(neg, neg) + predictions.getConfusionCount(neg, pos);
        for (int i = 0; i < predictions.size(); i++) {
            Label trueLabel = predictions.getTrueLabel(i);
            Double confidence = predictions.getPredictionConfidence(i);

            if (trueLabel == null) {
                continue;
            } else if (trueLabel == pos) {
                truePositives++;
            } else {
                falsePositives++;
            }

            double tpRate = (double) truePositives / totalPositives;
            double fpRate = (double) falsePositives / totalNegatives;
            record(fpRate, tpRate, confidence);
        }
    }

    @Override
    public boolean save(OutputStream destination) throws IOException {
        CsvWriter out = new CsvWriter(destination, ',', Charset.forName("UTF-8"));

        out.write("Threshold");
        out.write("True Positive Rate");
        out.write("False Positive Rate");
        out.endRecord();

        for (int i = 0; i < size(); i++) {
            double threshold = getThresholdValue(i);
            double fpRate = getFalsePositiveRate(i);
            double tpRate = getTruePositiveRate(i);

            out.write("" + threshold);
            out.write("" + tpRate);
            out.write("" + fpRate);
            out.endRecord();
        }

        out.flush();
        return true;


    }
}
