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
import data.transforms.filters.SimpleStringToWordVector.NoNonsenseStemmer;
import data.transforms.filters.*;
import java.io.File;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import weka.core.SelectedTag;
import weka.core.stemmers.SnowballStemmer;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

/**
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class ParameterizedFeatureConfig implements FeatureConfig {

    EnumSet<FeatureSet> featureSets = EnumSet.noneOf(FeatureSet.class);
    EnumSet<EmoticonSettings> emoticonSettings = EnumSet.noneOf(EmoticonSettings.class);
    EnumSet<BagOfWordsSettings> bagOfWordsSettings = EnumSet.noneOf(BagOfWordsSettings.class);
    private final String name;
    private boolean countRegexLengths = false;

    @Override
    public String[] describeFeatures() {
        FeatureSet[] featureSetValues = FeatureSet.values();
        EmoticonSettings[] emoticonSettingsValues = EmoticonSettings.values();
        BagOfWordsSettings[] bagOfWordsSettingsValues = BagOfWordsSettings.values();
        
        //1 for regex lengths switch
        int totalFeatureValues = 1 + featureSetValues.length + emoticonSettingsValues.length + bagOfWordsSettingsValues.length;
        String[] featureDesc = new String[totalFeatureValues];
        int featureDescIter = 0;
        
        for (int f = 0; f < featureSetValues.length; f++, featureDescIter++) {
            featureDesc[featureDescIter] = Boolean.toString(featureSets.contains(featureSetValues[f]));
        }
        
        for (int f = 0; f < emoticonSettingsValues.length; f++, featureDescIter++) {
            featureDesc[featureDescIter] = Boolean.toString(emoticonSettings.contains(emoticonSettingsValues[f]));
        }
        
        for (int f = 0; f < bagOfWordsSettingsValues.length; f++, featureDescIter++) {
            featureDesc[featureDescIter] = Boolean.toString(bagOfWordsSettings.contains(bagOfWordsSettingsValues[f]));
        }
        
        featureDesc[featureDescIter++] = Boolean.toString(countRegexLengths);
        
        return featureDesc;
    }
    
    @Override
    public String[] describeFeaturesHeader() {
        FeatureSet[] featureSetValues = FeatureSet.values();
        EmoticonSettings[] emoticonSettingsValues = EmoticonSettings.values();
        BagOfWordsSettings[] bagOfWordsSettingsValues = BagOfWordsSettings.values();
        
        //1 for regex lengths
        int totalFeatureValues = 1 + featureSetValues.length + emoticonSettingsValues.length + bagOfWordsSettingsValues.length;
        String[] featureDesc = new String[totalFeatureValues];
        int featureDescIter = 0;
        
        for (int f = 0; f < featureSetValues.length; f++, featureDescIter++) {
            featureDesc[featureDescIter] = featureSetValues[f].toString();
        }
        
        for (int f = 0; f < emoticonSettingsValues.length; f++, featureDescIter++) {
            featureDesc[featureDescIter] = "E_" + emoticonSettingsValues[f].toString();
        }
        
        for (int f = 0; f < bagOfWordsSettingsValues.length; f++, featureDescIter++) {
            featureDesc[featureDescIter] = "W_" + bagOfWordsSettingsValues[f].toString();
        }
        
        featureDesc[featureDescIter++] = "RegexLengths";
        
        return featureDesc;
    }

    public ParameterizedFeatureConfig(String name) {
        this.name = name;
    }

    public void countRegexLengths(boolean doCount) {
        this.countRegexLengths = doCount;
    }
    
    

    public enum FeatureSet {

        Pronouns,
        Punctuations,
        SpecialStrings,
        SpellingWeirdness,
        EmoticonDictionary,
        BagOfWords
    }

    public enum EmoticonSettings {

        LargeMinFrequency,
        GlobalBasis,
        WordCounts,
        TFIDFTransform
    }

    public enum BagOfWordsSettings {

        LargeMinFrequency,
        GlobalBasis,
        WordCounts,
        TFIDFTransform,
        LowerCase,
        Stemming,
        Stopwords,
        ExcludeNonsense,
        ImposeHardLimit
    }

    public EnumSet<FeatureSet> getFeatureSets() {
        return featureSets;
    }

    public void setFeatureSets(EnumSet<FeatureSet> featureSets) {
        this.featureSets = featureSets;
    }

    public void addFeatureSet(FeatureSet featureSet) {
        featureSets.add(featureSet);
    }

    public EnumSet<BagOfWordsSettings> getBagOfWordsSettings() {
        return bagOfWordsSettings;
    }

    public void setBagOfWordsSettings(EnumSet<BagOfWordsSettings> bagOfWordsSettings) {
        this.bagOfWordsSettings = bagOfWordsSettings;
    }

    public EnumSet<EmoticonSettings> getEmoticonSettings() {
        return emoticonSettings;
    }

    public void setEmoticonSettings(EnumSet<EmoticonSettings> emoticonSettings) {
        this.emoticonSettings = emoticonSettings;
    }

    @Override
    public List<AttributeProducer> getPrimaryAttributeProducers() {
        List<AttributeProducer> producers = new ArrayList<AttributeProducer>();
        producers.add(new BasicFeaturesProducer());
        return producers;
    }

    @Override
    public List<DataSetTransform> getPreSplitTransforms() {
        FilterDataSetTransform transform = new FilterDataSetTransform();
        transform.setInputPartition(DataSet.Partition.Full);
        transform.setOutputPartitions(DataSet.Partition.Full);

        if (featureSets.contains(FeatureSet.Pronouns)) {
            transform.addFilter(new PronounRegexFilter("message"));
        }
        if (featureSets.contains(FeatureSet.Punctuations)) {
            AbstractRegexFilter filter = new PunctuationRegexFilter("message");
            filter.countRegexLengths(countRegexLengths);
            transform.addFilter(filter);
        }
        if (featureSets.contains(FeatureSet.SpecialStrings)) {
            transform.addFilter(new SpecialRegexFilter("message"));
        }
        if (featureSets.contains(FeatureSet.SpellingWeirdness)) {
            AbstractRegexFilter filter = new SpellingRegexFilter("message");
            filter.countRegexLengths(countRegexLengths);
            transform.addFilter(filter);
        }

        List<DataSetTransform> transforms = new ArrayList<DataSetTransform>();
        transforms.add(transform);
        return transforms;
    }

    @Override
    public List<Filter> getFinalFilters() {
        List<Filter> filters = new ArrayList<Filter>();
        if (featureSets.contains(FeatureSet.EmoticonDictionary)) {
            Filter emoticons = getEmoticonsFilter();
            filters.add(emoticons);
        }
        if (featureSets.contains(FeatureSet.BagOfWords)) {
            Filter bagger = getBagOfWordsFilter();
            filters.add(bagger);
        }
        return filters;
    }

    private Filter getEmoticonsFilter() {
        StringToDictionaryVector emoticons = new StringToDictionaryVector();
        emoticons.setAttributeNamePrefix("#");
        emoticons.setDictionaryFile(new File("emoticons.txt"));
        emoticons.setStringAttribute("message");
        emoticons.setWordsToKeep(10000);//don't want this to limit things
        
        if (emoticonSettings.contains(EmoticonSettings.LargeMinFrequency)) {
            emoticons.setMinTermFreq(10);
        } else {
            emoticons.setMinTermFreq(5);
        }

        if (emoticonSettings.contains(EmoticonSettings.GlobalBasis)) {
            emoticons.setDoNotOperateOnPerClassBasis(true);
        } else {
            emoticons.setDoNotOperateOnPerClassBasis(false);
        }

        if (emoticonSettings.contains(EmoticonSettings.TFIDFTransform)) {
            emoticons.setTFTransform(true);
            emoticons.setIDFTransform(true);
            emoticons.setNormalizeDocLength(new SelectedTag(StringToWordVector.FILTER_NORMALIZE_ALL, StringToWordVector.TAGS_FILTER));
        }

        if (emoticonSettings.contains(EmoticonSettings.WordCounts)) {
            emoticons.setOutputWordCounts(true);
        }

        return emoticons;
    }

    private Filter getBagOfWordsFilter() {
        SimpleStringToWordVector bagger = new SimpleStringToWordVector();
        bagger.setAttributeNamePrefix("_");
        bagger.setStringAttributeName("message");
        bagger.setWordsToKeep(10000);//don't want this to limit things
        
        if (bagOfWordsSettings.contains(BagOfWordsSettings.LargeMinFrequency)) {
            bagger.setMinTermFreq(20);
        } else {
//            bagger.setMinTermFreq(5);
        }

        if (bagOfWordsSettings.contains(BagOfWordsSettings.ImposeHardLimit)) {
            bagger.setWordsToKeep(600);
        }
        
        if (bagOfWordsSettings.contains(BagOfWordsSettings.GlobalBasis)) {
//            bagger.setDoNotOperateOnPerClassBasis(true);
        } else {
            bagger.setDoNotOperateOnPerClassBasis(false);
        }

        if (bagOfWordsSettings.contains(BagOfWordsSettings.LowerCase)) {
            bagger.setLowerCaseTokens(true);
        } else {
            bagger.setLowerCaseTokens(false);
        }

        if (bagOfWordsSettings.contains(BagOfWordsSettings.Stemming)) {
            if (bagOfWordsSettings.contains(BagOfWordsSettings.ExcludeNonsense)) {
                bagger.setStemmer(new NoNonsenseStemmer(true));
            } else {
                bagger.setStemmer(new SnowballStemmer());
            }
        } else {
            if (bagOfWordsSettings.contains(BagOfWordsSettings.ExcludeNonsense)) {
                bagger.setStemmer(new NoNonsenseStemmer(false));
            }
        }

        if (bagOfWordsSettings.contains(BagOfWordsSettings.Stopwords)) {
            bagger.setUseStoplist(true);
        }

        if (bagOfWordsSettings.contains(BagOfWordsSettings.TFIDFTransform)) {
            bagger.setTFTransform(true);
            bagger.setIDFTransform(true);
            bagger.setNormalizeDocLength(new SelectedTag(StringToWordVector.FILTER_NORMALIZE_ALL, StringToWordVector.TAGS_FILTER));
        }

        if (bagOfWordsSettings.contains(BagOfWordsSettings.WordCounts)) {
            bagger.setOutputWordCounts(true);
        }

        return bagger;
    }

    @Override
    public String getName() {
        return name;
    }
    
    
}
