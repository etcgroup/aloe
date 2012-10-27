/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cmd.config;

import data.DataSet;
import data.processing.attrs.AttributeProducer;
import data.processing.attrs.BasicFeaturesProducer;
import data.transforms.DataSetTransform;
import data.transforms.FilterDataSetTransform;
import data.transforms.filters.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import weka.filters.Filter;

/**
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class DefaultFeatureConfig implements FeatureConfig {

    @Override
    public List<AttributeProducer> getPrimaryAttributeProducers() {
        List<AttributeProducer> producers = new ArrayList<AttributeProducer>();
        producers.add(new BasicFeaturesProducer());

        return producers;
    }
    
    @Override
    public String getName() {
        return "default";
    }

    @Override
    public List<DataSetTransform> getPreSplitTransforms() {
        FilterDataSetTransform transform = new FilterDataSetTransform();
        transform.setInputPartition(DataSet.Partition.Full);
        transform.setOutputPartitions(DataSet.Partition.Full);
        transform.addFilter(new PronounRegexFilter("message"));
        transform.addFilter(new PunctuationRegexFilter("message"));
        transform.addFilter(new SpecialRegexFilter("message"));
        transform.addFilter(new SpellingRegexFilter("message"));

        List<DataSetTransform> transforms = new ArrayList<DataSetTransform>();
        transforms.add(transform);
        return transforms;
    }

    @Override
    public List<Filter> getFinalFilters() {
        List<Filter> filters = new ArrayList<Filter>();
        Filter emoticons = getEmoticonsFilter();
        Filter bagger = getBagOfWordsFilter();
        filters.add(emoticons);
        filters.add(bagger);
        return filters;
    }

    private Filter getEmoticonsFilter() {
        StringToDictionaryVector emoticons = new StringToDictionaryVector();
        emoticons.setAttributeNamePrefix("#");
        emoticons.setDictionaryFile(new File("emoticons.txt"));
        emoticons.setMinTermFreq(5);
        emoticons.setOutputWordCounts(true);
        emoticons.setStringAttribute("message");
        return emoticons;
    }

    private Filter getBagOfWordsFilter() {
        SimpleStringToWordVector bagger = new SimpleStringToWordVector();
        bagger.setAttributeNamePrefix("_");
        bagger.setMinTermFreq(5);
        bagger.setOutputWordCounts(true);
        bagger.setStringAttributeName("message");
        return bagger;
    }

    @Override
    public String[] describeFeatures() {
        return new String[]{"Default"};
    }

    @Override
    public String[] describeFeaturesHeader() {
        return new String[]{"Default"};
    }
}
