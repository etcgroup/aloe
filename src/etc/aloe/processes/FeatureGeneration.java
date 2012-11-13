package etc.aloe.processes;

import etc.aloe.data.FeatureSpecification;
import etc.aloe.data.MessageSet;

/**
 * FeatureGeneration creates a feature specification from a set of messages.
 */
public interface FeatureGeneration {

    /**
     * Generate a feature specification from some messages.
     *
     * @param messages The messages to use to determine the features.
     * @return A feature specification
     */
    FeatureSpecification generateFeatures(MessageSet messages);
}
