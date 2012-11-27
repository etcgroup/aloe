package etc.aloe.cscw2013;

import etc.aloe.data.ExampleSet;
import etc.aloe.data.Model;
import etc.aloe.processes.Training;
import weka.classifiers.Classifier;
import weka.classifiers.CostMatrix;
import weka.classifiers.functions.SMO;
import weka.classifiers.meta.CostSensitiveClassifier;
import weka.core.Utils;

/**
 *
 */
public class CostTrainingImpl implements Training {

    private static final String SMO_OPTIONS = "-C 1.0 -L 0.0010 -P 1.0E-12 -N 0 -V -1 -W 1 -K \"weka.classifiers.functions.supportVector.PolyKernel -C 250007 -E 1.0\"";
    private double falsePositiveCost = 1;
    private double falseNegativeCost = 1;
    private boolean useReweighting = false;

    public CostTrainingImpl(double falsePositiveCost, double falseNegativeCost, boolean useReweighting) {
        this.falsePositiveCost = falsePositiveCost;
        this.falseNegativeCost = falseNegativeCost;
    }

    @Override
    public Model train(ExampleSet examples) {
        System.out.println("SMO Options: " + SMO_OPTIONS);
        SMO smo = new SMO();
        try {
            smo.setOptions(Utils.splitOptions(SMO_OPTIONS));
        } catch (Exception ex) {
            System.err.println("Unable to configure SMO.");
            System.err.println("\t" + ex.getMessage());
            return null;
        }

        CostSensitiveClassifier cost = new CostSensitiveClassifier();
        cost.setClassifier(smo);
        CostMatrix matrix = new CostMatrix(2);
        matrix.setElement(0, 0, 0);
        matrix.setElement(0, 1, falsePositiveCost);
        matrix.setElement(1, 0, falseNegativeCost);
        matrix.setElement(1, 1, 0);
        cost.setCostMatrix(matrix);
        Classifier classifier = cost;

        System.out.print("Wrapping SMO in CostSensitiveClassifier " + matrix.toMatlab());

        if (useReweighting) {
            cost.setMinimizeExpectedCost(false);
            System.out.println(" using re-weighting.");
        } else {
            cost.setMinimizeExpectedCost(true);
            System.out.println(" using min-cost criterion.");
        }

        try {
            System.out.print("Training SMO on " + examples.size() + " examples... ");
            classifier.buildClassifier(examples.getInstances());
            System.out.println("done.");
        } catch (Exception ex) {
            System.err.println("Unable to train SMO.");
            System.err.println("\t" + ex.getMessage());
        }
        Model model = new Model(classifier);
        return model;
    }
}
