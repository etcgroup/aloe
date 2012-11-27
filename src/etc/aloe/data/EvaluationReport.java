package etc.aloe.data;

import etc.aloe.processes.Saving;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * The EvaluationReport contains data about model performance as compared to a
 * source of truth data.
 */
public class EvaluationReport implements Saving {

    double falsePositiveCost = 1;
    double falseNegativeCost = 1;

    /**
     * Construct an equal-cost evaluation report
     */
    public EvaluationReport() {
    }

    /**
     * Construct a cost-sensitive evaluation report.
     *
     * @param falsePositiveCost
     * @param falseNegativeCost
     */
    public EvaluationReport(double falsePositiveCost, double falseNegativeCost) {
        this.falsePositiveCost = falsePositiveCost;
        this.falseNegativeCost = falseNegativeCost;
    }
    /**
     * The number of examples with a positive prediction that was correct.
     */
    private int truePositiveCount;
    /**
     * The number of examples with a negative prediction that was correct.
     */
    private int trueNegativeCount;
    /**
     * The number of examples with a positive prediction that was incorrect.
     */
    private int falsePositiveCount;
    /**
     * The number of examples with a negative prediction that was incorrect.
     */
    private int falseNegativeCount;

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

    public void setTruePositiveCount(int truePositiveCount) {
        this.truePositiveCount = truePositiveCount;
    }

    public void setTrueNegativeCount(int trueNegativeCount) {
        this.trueNegativeCount = trueNegativeCount;
    }

    public void setFalsePositiveCount(int falsePositiveCount) {
        this.falsePositiveCount = falsePositiveCount;
    }

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

    @Override
    public String toString() {
        return "TP: " + truePositiveCount + "\n"
                + "FP: " + falsePositiveCount + "\n"
                + "TN: " + trueNegativeCount + "\n"
                + "FN: " + falseNegativeCount + "\n"
                + "Precision: " + getPrecision() + "\n"
                + "Recall: " + getRecall() + "\n"
                + "FMeasure: " + getFMeasure() + "\n"
                + "% Correct: " + getPercentCorrect() + "\n"
                + "% Incorrect: " + getPercentIncorrect() + "\n"
                + "Total Cost: " + getTotalCost() + "\n"
                + "Avg Cost: " + getAverageCost();
    }

    public void addPartial(EvaluationReport report) {
        truePositiveCount += report.truePositiveCount;
        trueNegativeCount += report.trueNegativeCount;
        falsePositiveCount += report.falsePositiveCount;
        falseNegativeCount += report.falseNegativeCount;
    }

    public void recordPrediction(Boolean predictedLabel, Boolean actualLabel) {
        if (predictedLabel == true) {
            if (predictedLabel == actualLabel) {
                truePositiveCount++;
            } else {
                falsePositiveCount++;
            }
        } else {
            if (predictedLabel == actualLabel) {
                trueNegativeCount++;
            } else {
                falseNegativeCount++;
            }
        }
    }
}
