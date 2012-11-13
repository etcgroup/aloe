package etc.aloe.processes;

import etc.aloe.data.FeatureSpecification;
import etc.aloe.data.SegmentSet;

/**
 * FeatureGeneration creates a feature specification from a set of messages.
 */
public interface FeatureGeneration {

    /**
     * Generate a feature specification from some messages.
     *
     * @param segments The segments to use to determine the features.
     * @return A feature specification
     */
    FeatureSpecification generateFeatures(SegmentSet segments);
}
