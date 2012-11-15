package etc.aloe.cscw2013;

import etc.aloe.data.ExampleSet;
import etc.aloe.data.Model;
import etc.aloe.processes.Training;
import java.util.logging.Level;
import java.util.logging.Logger;
import weka.classifiers.functions.SMO;
import weka.core.Utils;

/**
 *
 */
public class TrainingImpl implements Training {

    @Override
    public Model train(ExampleSet examples) {
        SMO classifier = new SMO();
        try {
            classifier.setOptions(Utils.splitOptions("-C 1.0 -L 0.0010 -P 1.0E-12 -N 0 -V -1 -W 1 -K \"weka.classifiers.functions.supportVector.PolyKernel -C 250007 -E 1.0\""));
        } catch (Exception ex) {
            System.err.println("Unable to configure SMO.");
            System.err.println("\t" + ex.getMessage());
            return null;
        }
        
        try {
            classifier.buildClassifier(examples.getInstances());
        } catch (Exception ex) {
            System.err.println("Unable to train SMO.");
            System.err.println("\t" + ex.getMessage());
        }

        Model model = new Model(classifier);
        return model;
    }
}
