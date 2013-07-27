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
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

/**
 * The EvaluationReport contains data about model performance as compared to a
 * source of truth data.
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class EvaluationReport implements Saving {

    private List<ROC> rocs;
    private final String name;
    private double[][] costMatrix;
    private int[][] confusionMatrix;

    /**
     * Construct an equal-cost evaluation report
     *
     * @param name
     */
    public EvaluationReport(String name) {
        this.name = name;
        int labelCount = Label.getLabelCount();
        this.confusionMatrix = new int[labelCount][labelCount];

        this.costMatrix = new double[labelCount][labelCount];
        for (int i = 0; i < labelCount; i++) {
            for (int j = 0; j < labelCount; j++) {
                if (i != j) {
                    costMatrix[i][j] = 1;
                }
            }
        }

        if (Label.getLabelCount() == 2) {
             this.rocs = new ArrayList<ROC>();
        }
    }

    /**
     * Construct a cost-sensitive evaluation report.
     *
     * @param name
     * @param costMatrix
     */
    public EvaluationReport(String name, double[][] costMatrix) {
        this(name);
        if (Label.getLabelCount() != costMatrix.length) {
            throw new IllegalArgumentException("Cost matrix is not the right size!");
        }
        this.costMatrix = costMatrix;
    }

    /**
     * Get the number of examples with a given true label where the prediction was another
     * label (or the same label).
     * @param trueLabel
     * @param predictedLabel
     * @return
     */
    public int getConfusionCount(Label trueLabel, Label predictedLabel) {
        return this.confusionMatrix[predictedLabel.getNumber()][trueLabel.getNumber()];
    }

    /**
     * Set the number of examples with a given true label where the prediction was another
     * label (or the same label). Mostly just for testing.
     * @param trueLabel
     * @param predictedLabel
     * @param count
     */
    public void setConfusionCount(Label trueLabel, Label predictedLabel, int count) {
        this.confusionMatrix[predictedLabel.getNumber()][trueLabel.getNumber()] = count;
    }

    /**
     * Get the total number of examples truly labeled with the given label.
     * @param label
     * @return
     */
    public int getTrueCount(Label label) {
        int labelN = label.getNumber();
        int count = 0;
        for (int j = 0; j < this.confusionMatrix.length; j++) {
            count += this.confusionMatrix[j][labelN];
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
        for (int i = 0; i < this.confusionMatrix[labelN].length; i++) {
            count += this.confusionMatrix[labelN][i];
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
     * Set the number of examples with a positive prediction that was correct.
     * @param count
     */
    public void setTruePositiveCount(int count) {
        if (!Label.isBinary()) {
            throw new IllegalStateException("TP count only available in binary classification");
        }

        this.setConfusionCount(Label.TRUE(), Label.TRUE(), count);
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
     * Set the number of examples with a negative prediction that was correct.
     * @param count
     */
    public void setTrueNegativeCount(int count) {
        if (!Label.isBinary()) {
            throw new IllegalStateException("TN count only available in binary classification");
        }

        this.setConfusionCount(Label.FALSE(), Label.FALSE(), count);
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
     * Set the number of examples with a positive prediction that was incorrect.
     * @param count
     */
    public void setFalsePositiveCount(int count) {
        if (!Label.isBinary()) {
            throw new IllegalStateException("FP count only available in binary classification");
        }

        this.setConfusionCount(Label.FALSE(), Label.TRUE(), count);
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
     * Set the number of examples with a negative prediction that was incorrect.
     * @param count
     */
    public void setFalseNegativeCount(int count) {
        if (!Label.isBinary()) {
            throw new IllegalStateException("FN count only available in binary classification");
        }

        this.setConfusionCount(Label.TRUE(), Label.FALSE(), count);
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
     * Get the weighted average recall over all classes.
     *
     * @return
     */
    public double getAverageRecall() {
        double sum = 0;

        for (int i = 0; i < Label.getLabelCount(); i++) {
            Label label = Label.get(i);
            sum += getRecall(label) * getTrueCount(label);
        }

        return sum / getTotalExamples();
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
     * Get the weighted average precision over all classes.
     *
     * @return
     */
    public double getAveragePrecision() {
        double sum = 0;

        for (int i = 0; i < Label.getLabelCount(); i++) {
            Label label = Label.get(i);
            sum += getPrecision(label) * getTrueCount(label);
        }

        return sum / getTotalExamples();
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
     * Get the weighted average F-measure over all classes.
     *
     * @return
     */
    public double getAverageFMeasure() {
        double sum = 0;

        for (int i = 0; i < Label.getLabelCount(); i++) {
            Label label = Label.get(i);
            sum += getFMeasure(label) * getTrueCount(label);
        }

        return sum / getTotalExamples();
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
        if (!Label.isBinary()) {
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
        int total = 0;
        for (int i = 0; i < confusionMatrix.length; i++) {
            for (int j = 0; j < confusionMatrix[i].length; j++) {
                total += confusionMatrix[i][j];
            }
        }
        return total;
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
        Formatter fmt = new Formatter(str, Locale.US);

        //The label summary
        str.append("== Label Distribution (num instances) ==\n");
        fmt.format("%6s  %10s %10s %10s %n", "", "Label", "True", "Predicted");

        int numLabels = Label.getLabelCount();
        for (int i = 0; i < numLabels; i++) {
            Label label = Label.get(i);
            fmt.format("%6d. %10s %10d %10d %n", label.getNumber(), label.getName(),
                    getTrueCount(label), getPredictedCount(label));
        }

        //Matrix Column Headers
        Object[] labelNames = new Object[numLabels];
        String columnFormatter = "";
        String floatingCellFormatter = "";
        String integerCellFormatter = "";
        for (int i = 0; i < numLabels; i++) {
            labelNames[i] = Label.get(i).getName();
            columnFormatter += "%10s";
            floatingCellFormatter += "%10f";
            integerCellFormatter += "%10d";
            if (i + 1 < numLabels) {
                columnFormatter += " ";
                floatingCellFormatter += " ";
                integerCellFormatter += " ";
            } else {
                columnFormatter += "%n";
                floatingCellFormatter += "%n";
                integerCellFormatter += "%n";
            }
        }

        str.append("\n== Cost Matrix ==\n");
        fmt.format("%12s ", "\u25BE Predicted");
        fmt.format(columnFormatter, labelNames);

        for (int i = 0; i < numLabels; i++) {
            Label trueLabel = Label.get(i);
            fmt.format("%12s ", trueLabel.getName());

            Object[] matrixRow = new Object[costMatrix[i].length];
            for (int j = 0; j < matrixRow.length; j++) {
                matrixRow[j] = costMatrix[i][j];
            }

            fmt.format(floatingCellFormatter, matrixRow);
        }

        str.append("\n== Confusion Matrix (num instances) ==\n");

        fmt.format("%12s ", "\u25BE Predicted");
        fmt.format(columnFormatter, labelNames);

        for (int i = 0; i < numLabels; i++) {
            Label trueLabel = Label.get(i);
            fmt.format("%12s ", trueLabel.getName());

            Object[] matrixRow = new Object[confusionMatrix[i].length];
            for (int j = 0; j < matrixRow.length; j++) {
                matrixRow[j] = confusionMatrix[i][j];
            }

            fmt.format(integerCellFormatter, matrixRow);
        }

        str.append("\n== Performance Statistics ==\n");
        fmt.format("%10s %10s %10s %10s%n", "Label", "Precision", "Recall", "F-Measure");

        for (int i = 0; i < numLabels; i++) {

            Label label = Label.get(i);
            fmt.format("%10s %10f %10f %10f%n", label.getName(),
                    getPrecision(label),
                    getRecall(label),
                    getFMeasure(label));
        }

        fmt.format("%10s %10f %10f %10f%n", "Average",
                getAveragePrecision(),
                getAverageRecall(),
                getAverageFMeasure());

        str.append("\n-- Overall Statistics --\n");
        fmt.format("%11s: %10f%n", "% Correct", getPercentCorrect());
        fmt.format("%11s: %10f%n", "% Incorrect", getPercentIncorrect());
        if (Label.isBinary()) {
            fmt.format("%11s: %10f%n", "Kappa", getCohensKappa());
        }
        fmt.format("%11s: %10f%n", "Total Cost", getTotalCost());
        fmt.format("%11s: %10f%n", "Avg Cost", getAverageCost());

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
