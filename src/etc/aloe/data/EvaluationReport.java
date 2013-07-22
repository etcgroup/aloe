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

import etc.aloe.processes.Saving;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * The EvaluationReport contains data about model performance as compared to a
 * source of truth data.
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class EvaluationReport implements Saving {

    private List<ROC> rocs = new ArrayList<ROC>();
    private final String name;
    private double[][] costMatrix;
    private int[][] confusionMatrix;
    private int totalExamples = 0;

    /**
     * Construct an equal-cost evaluation report
     *
     * @param name
     */
    public EvaluationReport(String name) {
        this.name = name;
        this.confusionMatrix = new int[Label.getLabelCount()][Label.getLabelCount()];
    }

    /**
     * Construct a cost-sensitive evaluation report.
     *
     * @param name
     * @param costMatrix
     */
    public EvaluationReport(String name, double[][] costMatrix) {
        if (Label.getLabelCount() != costMatrix.length) {
            throw new IllegalArgumentException("Cost matrix is not the right size!");
        }
        this.name = name;
        this.costMatrix = costMatrix;
        this.confusionMatrix = new int[Label.getLabelCount()][Label.getLabelCount()];
    }

    /**
     * Get the number of examples with a given true label where the prediction was another
     * label (or the same label).
     * @param trueLabel
     * @param predictedLabel
     * @return
     */
    public int getConfusionCount(Label trueLabel, Label predictedLabel) {
        return this.confusionMatrix[trueLabel.getNumber()][predictedLabel.getNumber()];
    }

    /**
     * Get the total number of examples truly labeled with the given label.
     * @param label
     * @return
     */
    public int getTrueCount(Label label) {
        int labelN = label.getNumber();
        int count = 0;
        for (int j = 0; j < this.confusionMatrix[labelN].length; j++) {
            count += this.confusionMatrix[labelN][j];
        }
        return count;
    }

    /**
     * Get the total number of examples predicted to have the given label.
     * @param label
     * @return
     */
    public int getPredictedCount(Label label) {
        int labelN = label.getNumber();
        int count = 0;
        for (int i = 0; i < this.confusionMatrix.length; i++) {
            count += this.confusionMatrix[i][labelN];
        }
        return count;
    }

    /**
     * Get the number of examples with a positive prediction that was correct.
     *
     * @return
     */
    public int getTruePositiveCount() {
        if (!Label.isBinary()) {
            throw new IllegalStateException("TP count only available in binary classification");
        }

        return this.getConfusionCount(Label.TRUE(), Label.TRUE());
    }

    /**
     * Get the number of examples with a negative prediction that was correct.
     *
     * @return
     */
    public int getTrueNegativeCount() {
        if (!Label.isBinary()) {
            throw new IllegalStateException("TN count only available in binary classification");
        }

        return this.getConfusionCount(Label.FALSE(), Label.FALSE());
    }

    /**
     * Get the number of examples with a positive prediction that was incorrect.
     *
     * @return
     */
    public int getFalsePositiveCount() {
        if (!Label.isBinary()) {
            throw new IllegalStateException("FP count only available in binary classification");
        }

        return this.getConfusionCount(Label.FALSE(), Label.TRUE());
    }

    /**
     * Get the number of examples with a negative prediction that was incorrect.
     *
     * @return
     */
    public int getFalseNegativeCount() {
        if (!Label.isBinary()) {
            throw new IllegalStateException("FN count only available in binary classification");
        }

        return this.getConfusionCount(Label.TRUE(), Label.FALSE());
    }

    /**
     * Recall = (correctly classified positives) / (total actual positives)
     *
     * @return
     */
    public double getRecall(Label label) {
        int correctlyPredicted = getConfusionCount(label, label);
        int allTrueLabeled = getTrueCount(label);

        return (double) correctlyPredicted / allTrueLabeled;
    }

    /**
     * Precision = (correctly classified positives) / (total predicted as
     * positive)
     *
     * @return
     */
    public double getPrecision(Label label) {
        int correctlyPredicted = getConfusionCount(label, label);
        int allPredictedLabeled = getPredictedCount(label);

        return (double) correctlyPredicted / allPredictedLabeled;
    }

    /**
     * FMeasure = (2 * recall * precision) / (recall + precision)
     *
     * @return
     */
    public double getFMeasure(Label label) {
        double precision = getPrecision(label);
        double recall = getRecall(label);
        if ((precision + recall) == 0) {
            return 0;
        }
        return 2 * precision * recall / (precision + recall);
    }

    /**
     * PercentCorrect = (TP + TN) / (TP + TN + FP + FN)
     *
     * @return
     */
    public double getPercentCorrect() {
        int correct = 0;
        for (int i = 0; i < this.confusionMatrix.length; i++) {
            Label label = Label.get(i);
            correct += getConfusionCount(label, label);
        }
        return (double) correct / getTotalExamples();
    }

    /**
     * PercentIncorrect = (FP + FN) / (TP + TN + FP + FN)
     *
     * @return
     */
    public double getPercentIncorrect() {
        return 1 - getPercentCorrect();
    }

    /**
     * Get Cohen's kappa, the probability of agreement with the truth data,
     * corrected by the probability of random agreement.
     *
     * See http://en.wikipedia.org/wiki/Cohen's_kappa
     *
     * @return
     */
    public double getCohensKappa() {
        if (Label.isBinary()) {
            throw new IllegalStateException("Cohens kappa currently only available for binary classification");
        }

        double probabilityAgreement = getPercentCorrect();

        double numPositiveInTruth = getTrueCount(Label.TRUE());
        double numPositiveInPrediction = getPredictedCount(Label.TRUE());
        double numNegativeInTruth = getTrueCount(Label.FALSE());
        double numNegativeInPrediction = getPredictedCount(Label.FALSE());

        double probabilityRandomPositiveAgreement =
                (numPositiveInTruth / getTotalExamples())
                * (numPositiveInPrediction / getTotalExamples());
        double probabilityRandomNegativeAgreement =
                (numNegativeInTruth / getTotalExamples())
                * (numNegativeInPrediction / getTotalExamples());

        double probabilityRandomAgreement =
                probabilityRandomPositiveAgreement
                + probabilityRandomNegativeAgreement;

        double kappa = (probabilityAgreement - probabilityRandomAgreement)
                / (1 - probabilityRandomAgreement);

        return kappa;
    }

    /**
     * Gets the total cost of all misclassified examples.
     *
     * @return
     */
    public double getTotalCost() {
        double cost = 0;
        for (int i = 0; i < confusionMatrix.length; i++) {
            for (int j = 0; j < confusionMatrix[i].length; j++) {
                cost += costMatrix[i][j] * confusionMatrix[i][j];
            }
        }
        return cost;
    }

    /**
     * Gets the cost of all misclassified examples divided by the total number
     * of examples.
     *
     * @return
     */
    public double getAverageCost() {
        return getTotalCost() / getTotalExamples();
    }

    /**
     * Get the total number of examples classified.
     *
     * @return
     */
    public int getTotalExamples() {
        return totalExamples;
    }

    @Override
    public boolean save(OutputStream destination) throws IOException {
        PrintStream out = new PrintStream(destination);
        out.print(this.toString());
        out.flush();
        return true;
    }

    /**
     * Get the evaluation report as a string.
     *
     * @return
     */
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();

        //The label summary
        str.append("== Label Distribution ==\n")
                .append("  \tLabel \tTrue \tPredicted\n");
        int numLabels = Label.getLabelCount();
        for (int i = 0; i < numLabels; i++) {
            Label label = Label.get(i);
            str.append(label.getNumber()).append(".\t").append(label.getName())
                    .append(": \t").append(getTrueCount(label))
                    .append(" \t").append(getPredictedCount(label))
                    .append("\n");
        }

        str.append("\n== Cost Matrix ==\n")
                .append("true label\n");

        for (int i = 0; i < numLabels; i++) {
            Label trueLabel = Label.get(i);
            str.append(trueLabel.getName()).append(" \t");

            for (int j = 0; j < numLabels; j++) {
                str.append(costMatrix[i][j]).append(" \t");
            }

            str.append("\n");
        }

        str.append("\n== Confusion Matrix ==\n")
                .append("true label\n");

        for (int i = 0; i < numLabels; i++) {
            Label trueLabel = Label.get(i);
            str.append(trueLabel.getName()).append(" \t");

            for (int j = 0; j < numLabels; j++) {
                str.append(confusionMatrix[i][j]).append(" \t");
            }

            str.append("\n");
        }

        str.append("\n== Performance Statistics ==\n")
                .append("Label \tPrecision \tRecall \tF-Measure\n");

        for (int i = 0; i < numLabels; i++) {
            Label label = Label.get(i);
            str.append(label.getName()).append(" \t")
                    .append(getPrecision(label)).append(" \t")
                    .append(getRecall(label)).append(" \t")
                    .append(getFMeasure(label)).append("\n");
        }

        str.append("-- Overall Statistics --\n");
        str.append("% Correct: ").append(getPercentCorrect()).append("\n");
        str.append("% Incorrect: ").append(getPercentIncorrect()).append("\n");
        if (Label.isBinary()) {
            str.append("Kappa: ").append(getCohensKappa()).append("\n");
        }
        str.append("Total Cost: ").append(getTotalCost()).append("\n");
        str.append("Avg Cost: ").append(getAverageCost()).append("\n");

        return str.toString();
    }

    /**
     * Add a partial evaluation report to this report. Modifies the current
     * report.
     *
     * @param report
     */
    public void addPartial(EvaluationReport report) {
        for (int i = 0; i < report.confusionMatrix.length; i++) {
            for (int j = 0; j < report.confusionMatrix[i].length; j++) {
                this.confusionMatrix[i][j] += report.confusionMatrix[i][j];
            }
        }

        if (this.rocs != null) {
            this.rocs.addAll(report.getROCs());
        }

        this.totalExamples += report.totalExamples;
    }

    /**
     * Evaluate the given predictions.
     *
     * @param predictions
     */
    public void addPredictions(Predictions predictions) {
        if (this.rocs != null) {
            ROC roc = new ROC(this.getName());
            roc.calculateCurve(predictions);
            this.rocs.add(roc);
        }

        this.confusionMatrix = predictions.getConfusionMatrix();
        this.totalExamples += predictions.size();
    }

    public String getName() {
        return name;
    }

    /**
     * Get a list of named ROC curves included in this report.
     *
     * @return
     */
    public List<ROC> getROCs() {
        return rocs;
    }
}
