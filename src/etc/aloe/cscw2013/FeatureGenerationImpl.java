package etc.aloe.cscw2013;

import etc.aloe.data.FeatureSpecification;
import etc.aloe.data.SegmentSet;
import etc.aloe.filters.PronounRegexFilter;
import etc.aloe.processes.FeatureGeneration;

/**
 *
 * @author michael
 */
public class FeatureGenerationImpl implements FeatureGeneration {

    static final String MESSAGE_ATTR_NAME = "message";

    @Override
    public FeatureSpecification generateFeatures(SegmentSet segments) {
        
        FeatureSpecificationImpl spec = new FeatureSpecificationImpl();

        //TODO: fill me in!
        spec.addFilter(new PronounRegexFilter(MESSAGE_ATTR_NAME));

        return spec;
    }

}
