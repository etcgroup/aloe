package etc.aloe.cscw2013;

import etc.aloe.data.ExampleSet;
import etc.aloe.data.FeatureSpecification;
import etc.aloe.data.SegmentSet;
import etc.aloe.processes.FeatureExtraction;
import java.util.ArrayList;
import weka.core.Attribute;
import weka.core.Instances;

/**
 *
 */
public class FeatureExtractionImpl implements FeatureExtraction {

    @Override
    public ExampleSet extractFeatures(SegmentSet segments, FeatureSpecification spec) {
        Instances instances = new Instances("Example Set", new ArrayList<Attribute>(), 0);
        ExampleSet examples = new ExampleSet(instances);
        return examples;
    }
}
