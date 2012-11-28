package etc.aloe.cscw2013;

import etc.aloe.data.ExampleSet;
import etc.aloe.data.FeatureSpecification;
import etc.aloe.processes.FeatureExtraction;
import etc.aloe.processes.Logging.Verbosity;
import weka.core.Instances;
import weka.filters.Filter;

/**
 *
 */
public class FeatureExtractionImpl implements FeatureExtraction {

    private Verbosity verbosity = Verbosity.Normal;

    @Override
    public ExampleSet extractFeatures(ExampleSet basicExamples, FeatureSpecification spec) {
        ExampleSet examples = basicExamples;

        if (this.verbosity.ordinal() > Verbosity.Quiet.ordinal()) {
            System.out.print("Extracting features for " + examples.size() + " examples... ");
        }

        for (Filter filter : spec.getFilters()) {
            try {
                Instances instances = Filter.useFilter(examples.getInstances(), filter);
                examples = new ExampleSet(instances);
            } catch (Exception e) {
                System.err.println("Unable to apply filter: " + filter.toString());
                System.err.println("\t" + e.getMessage());
                return null;
            }
        }

        if (this.verbosity.ordinal() > Verbosity.Quiet.ordinal()) {
            System.out.println("done.");
        }

        return examples;
    }

    @Override
    public void setVerbosity(Verbosity verbosityLevel) {
        this.verbosity = verbosityLevel;
    }
}
