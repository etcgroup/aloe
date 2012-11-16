package etc.aloe.data;

import etc.aloe.processes.Loading;
import etc.aloe.processes.Saving;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidObjectException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import weka.classifiers.Classifier;
import weka.core.Attribute;

/**
 * A Model has the ability to learn from examples and label unlabeled examples.
 */
public class Model implements Saving, Loading {

    Classifier classifier;

    public Model() {

    }

    public Model(Classifier classifier) {
        this.classifier = classifier;
    }

    @Override
    public boolean save(OutputStream destination) throws IOException {
        PrintStream writer = new PrintStream(destination);
        writer.println("nothing to do here");
        //TODO: fill me in!
        return true;
    }

    @Override
    public boolean load(InputStream source) throws FileNotFoundException, InvalidObjectException {
        //TODO: fill me in!
        return true;
    }

    /**
     * Attempt to label each example in the example set according to the model.
     *
     * Returns the list of generated labels. Order corresponds to the order of
     * examples.
     *
     * @param examples
     * @return
     */
    public List<Boolean> getPredictedLabels(ExampleSet examples) {
        List<Boolean> results = new ArrayList<Boolean>();

        Attribute classAttr = examples.getInstances().classAttribute();

        for (int i = 0; i < examples.size(); i++) {
            try {
                double classValue = classifier.classifyInstance(examples.get(i));
                String classValueStr = classAttr.value((int) classValue);
                results.add(Boolean.parseBoolean(classValueStr));
            } catch (Exception ex) {
                System.err.println("Classification error on instance " + i);
            }
        }

        return results;
    }
}
