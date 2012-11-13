package etc.aloe.processes;

import etc.aloe.data.ExampleSet;
import etc.aloe.data.Model;

/**
 * Training creates a trained model from labeled examples.
 */
public interface Training {

    /**
     * Given a set of labeled examples, generates a trained model.
     * @param examples
     * @return
     */
    Model train(ExampleSet examples);
}
