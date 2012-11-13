package etc.aloe.data;

import etc.aloe.processes.Loading;
import etc.aloe.processes.Saving;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.util.List;

/**
 * A Model has the ability to learn from examples and label unlabeled examples.
 */
public class Model implements Saving, Loading {

    @Override
    public boolean save(File destination) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean load(File source) throws FileNotFoundException, InvalidObjectException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Attempt to label each example in the example set according to the model.
     *
     * Returns the list of generated labels. Order corresponds to the order of examples.
     *
     * @param examples
     * @return
     */
    public List<Boolean> getPredictedLabels(ExampleSet examples) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
