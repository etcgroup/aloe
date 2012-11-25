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
                + "FN: " + falseNegativeCount;
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
