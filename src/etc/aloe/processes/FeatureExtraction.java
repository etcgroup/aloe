package etc.aloe.processes;

import etc.aloe.data.ExampleSet;
import etc.aloe.data.FeatureSpecification;
import etc.aloe.data.SegmentSet;

/**
 * FeatureExtraction extracts features from the provided data.
 */
public interface FeatureExtraction {

    /**
     * Extracts the specified features from the segmented data.
     *
     * Exactly one example will be created in the example set for every segment.
     *
     * @param ExampleSet The basic examples created from segments.
     * @param spec The features to extract.
     * @return A set of examples with features extracted, or null on failure.
     */
    ExampleSet extractFeatures(ExampleSet basicExamples, FeatureSpecification spec);
}
