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
    int truePositiveCount;
    /**
     * The number of examples with a negative prediction that was correct.
     */
    int trueNegativeCount;
    /**
     * The number of examples with a positive prediction that was incorrect.
     */
    int falsePositiveCount;
    /**
     * The number of examples with a negative prediction that was incorrect.
     */
    int falseNegativeCount;

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

    

    @Override
    public boolean save(OutputStream destination) throws IOException {
        PrintStream out = new PrintStream(destination);
        //TODO: implement me better!
        out.println("TP: " + truePositiveCount);
        out.println("FP: " + falsePositiveCount);
        out.println("TN: " + trueNegativeCount);
        out.println("FN: " + falseNegativeCount);
        return true;
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
