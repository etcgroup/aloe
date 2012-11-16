package etc.aloe.cscw2013;

import etc.aloe.data.ExampleSet;
import etc.aloe.data.FeatureSpecification;
import etc.aloe.data.SegmentSet;
import etc.aloe.processes.FeatureExtraction;

/**
 *
 */
public class FeatureExtractionImpl implements FeatureExtraction {

    @Override
    public ExampleSet extractFeatures(SegmentSet segments, FeatureSpecification spec) {
        //TODO: Fill me in!
        return segments.getBasicExamples();
    }
}
