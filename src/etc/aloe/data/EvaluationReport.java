package etc.aloe.data;

import etc.aloe.processes.Saving;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * The EvaluationReport contains data about model performance as compared to a
 * source of truth data.
 */
public class EvaluationReport implements Saving {

    int truePositiveCount;
    int trueNegativeCount;
    int falsePositiveCount;
    int falseNegativeCount;

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
}
