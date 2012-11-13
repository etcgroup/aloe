package etc.aloe.processes;

import etc.aloe.data.EvaluationReport;
import etc.aloe.data.ExampleSet;
import etc.aloe.data.Model;

/**
 * Evaluation measures the performance of a model over some labeled example
 * data.
 */
public interface Evaluation {

    /**
     * Evaluates the performance of the model over the given labeled examples.
     *
     * The model features must match the example features.
     *
     * @param model The model to evaluate.
     * @param examples The examples to use for evaluation.
     * @return The EvaluationReport.
     * @throws IllegalArgumentException If the model features do not match the
     * examples features.
     */
    EvaluationReport evaluate(Model model, ExampleSet examples) throws IllegalArgumentException;
}
