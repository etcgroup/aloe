package etc.aloe.processes;

import etc.aloe.data.ExampleSet;
import etc.aloe.data.FeatureSpecification;

/**
 * FeatureGeneration creates a feature specification from a set of messages.
 */
public interface FeatureGeneration {

    /**
     * Generate a feature specification from some messages.
     *
     * @param basicExamples The base examples to use to determine the features.
     * @return A feature specification
     */
    FeatureSpecification generateFeatures(ExampleSet basicExamples);
}
