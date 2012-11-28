package etc.aloe.data;

import etc.aloe.processes.Loading;
import etc.aloe.processes.Saving;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import weka.classifiers.Classifier;

/**
 * A Model has the ability to learn from examples and label unlabeled examples.
 */
public class Model implements Saving, Loading {

    private Classifier classifier;

    public Model() {
    }

    public Model(Classifier classifier) {
        this.classifier = classifier;
    }

    @Override
    public boolean save(OutputStream destination) throws IOException {
        ObjectOutputStream out = new ObjectOutputStream(destination);
        out.writeObject(classifier);
        return true;
    }

    @Override
    public boolean load(InputStream source) throws InvalidObjectException {
        try {
            ObjectInputStream in = new ObjectInputStream(source);
            this.classifier = (Classifier) in.readObject();
            return true;
        } catch (IOException e) {
            throw new InvalidObjectException(e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new InvalidObjectException(e.getMessage());
        }
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

        for (int i = 0; i < examples.size(); i++) {
            try {
                double classValue = classifier.classifyInstance(examples.get(i));
                Boolean label = examples.getClassLabel(classValue);
                results.add(label);
            } catch (Exception ex) {
                System.err.println("Classification error on instance " + i);
            }
        }

        return results;
    }

    Classifier getClassifier() {
        return classifier;
    }
}
