package etc.aloe.cscw2013;

import etc.aloe.data.EvaluationReport;
import etc.aloe.data.ExampleSet;
import etc.aloe.processes.Evaluation;
import java.util.List;

/**
 *
 */
public class EvaluationImpl implements Evaluation {

    private double falseNegativeCost = 1;
    private double falsePositiveCost = 1;

    public EvaluationImpl() {
    }

    public EvaluationImpl(double falsePositiveCost, double falseNegativeCost) {
        this.falsePositiveCost = falsePositiveCost;
        this.falseNegativeCost = falseNegativeCost;
    }

    @Override
    public EvaluationReport evaluate(List<Boolean> predictions, ExampleSet examples) throws IllegalArgumentException {

        EvaluationReport evaluation = new EvaluationReport(falsePositiveCost, falseNegativeCost);

        for (int i = 0; i < examples.size(); i++) {
            Boolean trueLabel = examples.getTrueLabel(i);
            if (trueLabel != null) {
                evaluation.recordPrediction(predictions.get(i), trueLabel);
            }
        }

        return evaluation;
    }
}
