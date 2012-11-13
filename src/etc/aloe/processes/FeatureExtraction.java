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
     * @param segments The messages to extract features for.
     * @param spec The features to extract.
     * @return A set of examples with features extracted.
     */
    ExampleSet extractFeatures(SegmentSet segments, FeatureSpecification spec);
}
