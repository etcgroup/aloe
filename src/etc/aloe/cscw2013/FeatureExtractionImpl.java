package etc.aloe.cscw2013;

import etc.aloe.data.ExampleSet;
import etc.aloe.data.FeatureSpecification;
import etc.aloe.data.SegmentSet;
import etc.aloe.processes.FeatureExtraction;
import weka.core.Instances;
import weka.filters.Filter;

/**
 *
 */
public class FeatureExtractionImpl implements FeatureExtraction {

    @Override
    public ExampleSet extractFeatures(ExampleSet basicExamples, FeatureSpecification spec) {
        ExampleSet examples = basicExamples;

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

        return examples;
    }
}
