package etc.aloe.cscw2013;

import etc.aloe.data.ExampleSet;
import etc.aloe.data.FeatureSpecification;
import etc.aloe.filters.PronounRegexFilter;
import etc.aloe.processes.FeatureGeneration;

/**
 *
 * @author michael
 */
public class FeatureGenerationImpl implements FeatureGeneration {

    @Override
    public FeatureSpecification generateFeatures(ExampleSet basicExamples) {

        FeatureSpecification spec = new FeatureSpecification();

        //TODO: fill me in!
        spec.addFilter(new PronounRegexFilter(ExampleSet.MESSAGE_ATTR_NAME));

        return spec;
    }

}
