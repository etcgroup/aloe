package etc.aloe.cscw2013;

import etc.aloe.data.EvaluationReport;
import etc.aloe.data.ExampleSet;
import etc.aloe.data.Model;
import etc.aloe.processes.Evaluation;
import java.util.List;

/**
 *
 */
public class EvaluationImpl implements Evaluation {

    @Override
    public EvaluationReport evaluate(Model model, ExampleSet examples) throws IllegalArgumentException {

        EvaluationReport evaluation = new EvaluationReport();

        List<Boolean> predictions = model.getPredictedLabels(examples);

        for (int i = 0; i < examples.size(); i++) {
            evaluation.recordPrediction(predictions.get(i), examples.getTrueLabel(i));
        }

        return evaluation;
    }
}
