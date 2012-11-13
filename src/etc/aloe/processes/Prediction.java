package etc.aloe.processes;

import etc.aloe.data.ExampleSet;
import etc.aloe.data.MessageSet;
import etc.aloe.data.Model;

/**
 * Prediction generates labeled messages given un-classified example data.
 */
public interface Prediction {

    /**
     * Generates a MessageSet with predicted labels based on the provided
     * examples and model.
     *
     * The predictions are superimposed over the raw messages to produce the
     * output.
     *
     * @param examples Segmented, feature-extracted examples.
     * @param model The trained model.
     * @param rawMessages The unsegmented messages.
     * @return
     */
    MessageSet predict(ExampleSet examples, Model model, MessageSet rawMessages);
}
