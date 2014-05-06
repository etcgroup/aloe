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
import java.util.Collection;
import java.util.List;

/**
 * The EvaluationReport contains data about model performance as compared to a
 * source of truth data.
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class EvaluationReport implements Saving {

    private int truePositiveCount;
    private int trueNegativeCount;
    private int falsePositiveCount;
    private int falseNegativeCount;
    private double falsePositiveCost = 1;
    private double falseNegativeCost = 1;
    private List<ROC> rocs = new ArrayList<ROC>();
    private List<SegmentSet> testSets = new ArrayList<SegmentSet>();
    private List<String> testSetNames = new ArrayList<String>();
    
    private final String name;
    
    /**
     * Construct an equal-cost evaluation report
     */
    public EvaluationReport(String name) {
        this.name = name;
    }

    /**
     * Construct a cost-sensitive evaluation report.
     *
     * @param falsePositiveCost
     * @param falseNegativeCost
     */
    public EvaluationReport(String name, double falsePositiveCost, double falseNegativeCost) {
        this.name = name;
        this.falsePositiveCost = falsePositiveCost;
        this.falseNegativeCost = falseNegativeCost;
    }

    /**
     * Get the number of examples with a positive prediction that was correct.
     *
     * @return
     */
    public int getTruePositiveCount() {
        return truePositiveCount;
    }

    /**
     * Get the number of examples with a negative prediction that was correct.
     *
     * @return
     */
    public int getTrueNegativeCount() {
        return trueNegativeCount;
    }

    /**
     * Get the number of examples with a positive prediction that was incorrect.
     *
     * @return
     */
    public int getFalsePositiveCount() {
        return falsePositiveCount;
    }

    /**
     * Get the number of examples with a negative prediction that was incorrect.
     *
     * @return
     */
    public int getFalseNegativeCount() {
        return falseNegativeCount;
    }

    /**
     * Set the number of examples with a positive prediction that was correct.
     *
     * @param truePositiveCount
     */
    public void setTruePositiveCount(int truePositiveCount) {
        this.truePositiveCount = truePositiveCount;
    }

    /**
     * Set the number of examples with a negative prediction that was correct.
     *
     * @param trueNegativeCount
     */
    public void setTrueNegativeCount(int trueNegativeCount) {
        this.trueNegativeCount = trueNegativeCount;
    }

    /**
     * Set the number of examples with a positive prediction that was incorrect.
     *
     * @param falsePositiveCount
     */
    public void setFalsePositiveCount(int falsePositiveCount) {
        this.falsePositiveCount = falsePositiveCount;
    }

    /**
     * Set the number of examples with a negative prediction that was incorrect.
     *
     * @param falseNegativeCount
     */
    public void setFalseNegativeCount(int falseNegativeCount) {
        this.falseNegativeCount = falseNegativeCount;
    }

    /**
     * Recall = (correctly classified positives) / (total actual positives)
     *
     * @return
     */
    public double getRecall() {
        return (double) truePositiveCount / (truePositiveCount + falseNegativeCount);
    }

    /**
     * Precision = (correctly classified positives) / (total predicted as
     * positive)
     *
     * @return
     */
    public double getPrecision() {
        return (double) truePositiveCount / (truePositiveCount + falsePositiveCount);
    }

    /**
     * FMeasure = (2 * recall * precision) / (recall + precision)
     *
     * @return
     */
    public double getFMeasure() {
        double precision = getPrecision();
        double recall = getRecall();
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
        return (double) (truePositiveCount + trueNegativeCount)
                / getTotalExamples();
    }

    /**
     * PercentIncorrect = (FP + FN) / (TP + TN + FP + FN)
     *
     * @return
     */
    public double getPercentIncorrect() {
        return (double) (falsePositiveCount + falseNegativeCount)
                / getTotalExamples();
    }

    /**
     * Get the number of positive examples in the truth data.
     *
     * @return
     */
    public int getNumTrulyPositive() {
        return getTruePositiveCount() + getFalseNegativeCount();
    }

    /**
     * Get the number of negative examples in the truth data.
     *
     * @return
     */
    public int getNumTrulyNegative() {
        return getTrueNegativeCount() + getFalsePositiveCount();
    }

    /**
     * Get the number of examples predicted to be positive.
     *
     * @return
     */
    public int getNumPredictedPositive() {
        return getTruePositiveCount() + getFalsePositiveCount();
    }

    /**
     * Get the number of examples predicted to be negative.
     *
     * @return
     */
    public int getNumPredictedNegative() {
        return getTrueNegativeCount() + getFalseNegativeCount();
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
        double probabilityAgreement = getPercentCorrect();

        double numPositiveInTruth = getNumTrulyPositive();
        double numPositiveInPrediction = getNumPredictedPositive();
        double numNegativeInTruth = getNumTrulyNegative();
        double numNegativeInPrediction = getNumPredictedNegative();

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
        return falsePositiveCount * falsePositiveCost + falseNegativeCount * falseNegativeCost;
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
        return truePositiveCount + trueNegativeCount + falsePositiveCount + falseNegativeCount;
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
        return "Examples: " + getTotalExamples() + "\n"
                + "Positive: " + getNumTrulyPositive() + "\n"
                + "Negative: " + getNumTrulyNegative() + "\n"
                + "FP Cost: " + falsePositiveCost + "\n"
                + "FN Cost: " + falseNegativeCost + "\n"
                + "------------------\n"
                + "TP: " + truePositiveCount + "\n"
                + "FP: " + falsePositiveCount + "\n"
                + "TN: " + trueNegativeCount + "\n"
                + "FN: " + falseNegativeCount + "\n"
                + "------------------\n"
                + "Positive Predictions: " + getNumPredictedPositive() + "\n"
                + "Negative Predictions: " + getNumPredictedNegative() + "\n"
                + "------------------\n"
                + "Precision: " + getPrecision() + "\n"
                + "Recall: " + getRecall() + "\n"
                + "FMeasure: " + getFMeasure() + "\n"
                + "------------------\n"
                + "% Correct: " + getPercentCorrect() + "\n"
                + "% Incorrect: " + getPercentIncorrect() + "\n"
                + "Kappa: " + getCohensKappa() + "\n"
                + "------------------\n"
                + "Total Cost: " + getTotalCost() + "\n"
                + "Avg Cost: " + getAverageCost();
    }

    /**
     * Add a partial evaluation report to this report. Modifies the current
     * report.
     *
     * @param report
     */
    public void addPartial(EvaluationReport report) {
        truePositiveCount += report.truePositiveCount;
        trueNegativeCount += report.trueNegativeCount;
        falsePositiveCount += report.falsePositiveCount;
        falseNegativeCount += report.falseNegativeCount;

        this.rocs.addAll(report.getROCs());
        this.testSets.addAll(report.getTestSets());
        this.testSetNames.addAll(report.getTestSetNames());
    }
    
    /**
     * Add some test data with labels to the report, for later export.
     * 
     * @param testingSegments 
     */
    public void addLabeledTestData(SegmentSet testingSegments) {
        this.testSets.add(testingSegments);
        this.testSetNames.add(this.getName());
    }

    /**
     * Evaluate the given predictions.
     *
     * @param predictions
     */
    public void addPredictions(Predictions predictions) {
        ROC roc = new ROC(this.getName());
        roc.calculateCurve(predictions);
        this.rocs.add(roc);

        this.setTruePositiveCount(predictions.getTruePositiveCount());
        this.setFalsePositiveCount(predictions.getFalsePositiveCount());
        this.setTrueNegativeCount(predictions.getTrueNegativeCount());
        this.setFalseNegativeCount(predictions.getFalseNegativeCount());
    }

    public String getName() {
        return name;
    }

    /**
     * Get the labeled test sets from cross validation.
     * @return 
     */
    public List<SegmentSet> getTestSets() {
        return testSets;
    }
    
    /**
     * Get a list of named ROC curves included in this report.
     *
     * @return
     */
    public List<ROC> getROCs() {
        return rocs;
    }

    public List<String> getTestSetNames() {
        return this.testSetNames;
    }
}
