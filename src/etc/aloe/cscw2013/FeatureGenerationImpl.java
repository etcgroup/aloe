/*
 * This file is part of ALOE.
 *
 * ALOE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * ALOE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with ALOE.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2012 SCCL, University of Washington (http://depts.washington.edu/sccl)
 */
package etc.aloe.cscw2013;

import etc.aloe.data.ExampleSet;
import etc.aloe.data.FeatureSpecification;
import etc.aloe.filters.PronounRegexFilter;
import etc.aloe.filters.PunctuationRegexFilter;
import etc.aloe.filters.SimpleStringToWordVector;
import etc.aloe.filters.SimpleStringToWordVector.NoNonsenseStemmer;
import etc.aloe.filters.SpecialRegexFilter;
import etc.aloe.filters.SpellingRegexFilter;
import etc.aloe.filters.StringToDictionaryVector;
import etc.aloe.processes.FeatureGeneration;
import java.util.List;
import java.util.regex.Pattern;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.RemoveByName;
import weka.filters.unsupervised.attribute.StringToWordVector;

/**
 * Generates a set of filters that extract the desired features from message
 * texts.
 *
 * Features include words, emoticons, pronouns, punctuations, and other strings.
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class FeatureGenerationImpl implements FeatureGeneration {

    protected static final boolean COUNT_REGEX_LENGTHS = true;
    protected static final String EMOTICON_FEATURE_PREFIX = "#";
    protected static final String BAG_OF_WORDS_FEATURE_PREFIX = "_";
    protected static final String PARTICIPANT_FEATURE_PREFIX = ".";
    protected final List<String> emoticonDictionary;

    protected int participantFeatures = 0;

    
    /**
     * Construct a new FeatureGeneration implementation.
     *
     * @param emoticonDictionary The list of emoticons to look for in the
     * messages.
     */
    public FeatureGenerationImpl(List<String> emoticonDictionary) {
        this.emoticonDictionary = emoticonDictionary;
    }

    public int getParticipantFeatureCount() {
        return participantFeatures;
    }

    /**
     * Set whether the number of different participants to convert into unigram features.
     * If set to 0 (the default), no participant features will be used.
     * 
     * @param participantFeatures 
     */
    public void setParticipantFeatureCount(int participantFeatures) {
        this.participantFeatures = participantFeatures;
    }
    
    @Override
    public FeatureSpecification generateFeatures(ExampleSet basicExamples) {

        ExampleSet examples = basicExamples.copy();
        FeatureSpecification spec = new FeatureSpecification();

        System.out.print("Configuring features over " + examples.size() + " examples... ");

        try {
            spec.addFilter(getPronounsFilter(examples));
            spec.addFilter(getPunctuationFilter(examples));
            spec.addFilter(getSpecialWordsFilter(examples));
            spec.addFilter(getSpellingFilter(examples));

            spec.addFilter(getEmoticonsFilter(examples));
            spec.addFilter(getBagOfWordsFilter(examples));
            spec.addFilter(getRemoveIDFilter(examples));
            
            if (this.getParticipantFeatureCount() > 0) {
                spec.addFilter(getParticipantsFilter(examples));
            } else {
                spec.addFilter(getRemoveParticipantFilter(examples));
            }
            
            Instances output = spec.getOutputFormat();
            int numAttrs = output.numAttributes();
            System.out.println("generated " + (numAttrs - 1) + " features.");
        } catch (Exception e) {
            System.err.println("Error generating features.");
            System.err.println("\t" + e.getMessage());
        }

        return spec;
    }

    /**
     * Configure the special words filter with the provided data..
     *
     * @param examples
     * @return
     * @throws Exception
     */
    protected Filter getSpecialWordsFilter(ExampleSet examples) throws Exception {
        SpecialRegexFilter filter = new SpecialRegexFilter(ExampleSet.MESSAGE_ATTR_NAME);

        filter.setInputFormat(examples.getInstances());
        Instances filtered = Filter.useFilter(examples.getInstances(), filter);
        examples.setInstances(filtered);

        return filter;
    }

    /**
     * Configure the spelling filter to work with the provided data.
     *
     * @param examples
     * @return
     * @throws Exception
     */
    protected Filter getSpellingFilter(ExampleSet examples) throws Exception {
        SpellingRegexFilter filter = new SpellingRegexFilter(ExampleSet.MESSAGE_ATTR_NAME);
        filter.setCountRegexLengths(COUNT_REGEX_LENGTHS);

        filter.setInputFormat(examples.getInstances());
        Instances filtered = Filter.useFilter(examples.getInstances(), filter);
        examples.setInstances(filtered);

        return filter;
    }

    /**
     * Configure the punctuation filter to work with the provided data.
     *
     * @param examples
     * @return
     * @throws Exception
     */
    protected Filter getPunctuationFilter(ExampleSet examples) throws Exception {
        PunctuationRegexFilter filter = new PunctuationRegexFilter(ExampleSet.MESSAGE_ATTR_NAME);
        filter.setCountRegexLengths(COUNT_REGEX_LENGTHS);

        filter.setInputFormat(examples.getInstances());
        Instances filtered = Filter.useFilter(examples.getInstances(), filter);
        examples.setInstances(filtered);

        return filter;
    }

    /**
     * Configure the pronouns filter to work with the provided data.
     *
     * @param examples
     * @return
     * @throws Exception
     */
    protected Filter getPronounsFilter(ExampleSet examples) throws Exception {
        PronounRegexFilter filter = new PronounRegexFilter(ExampleSet.MESSAGE_ATTR_NAME);

        filter.setInputFormat(examples.getInstances());
        Instances filtered = Filter.useFilter(examples.getInstances(), filter);
        examples.setInstances(filtered);

        return filter;
    }

    /**
     * Configure the emoticons filter to work with the provided examples.
     *
     * @param examples
     * @return
     * @throws Exception
     */
    protected Filter getEmoticonsFilter(ExampleSet examples) throws Exception {
        StringToDictionaryVector filter = new StringToDictionaryVector();
        filter.setAttributeNamePrefix(EMOTICON_FEATURE_PREFIX);
        filter.setTermList(emoticonDictionary);
        filter.setStringAttribute(ExampleSet.MESSAGE_ATTR_NAME);
        filter.setWordsToKeep(100);

        //filter.setMinTermFreq(10);
        filter.setDoNotOperateOnPerClassBasis(true);
        filter.setOutputWordCounts(true);

        filter.setInputFormat(examples.getInstances());
        Instances filtered = Filter.useFilter(examples.getInstances(), filter);
        examples.setInstances(filtered);

        return filter;
    }

    /**
     * Get a bag of words filter based on the provided examples.
     *
     * @param examples
     * @return
     * @throws Exception
     */
    protected Filter getBagOfWordsFilter(ExampleSet examples) throws Exception {
        SimpleStringToWordVector filter = new SimpleStringToWordVector();
        filter.setAttributeNamePrefix(BAG_OF_WORDS_FEATURE_PREFIX);
        filter.setStringAttributeName(ExampleSet.MESSAGE_ATTR_NAME);

        //This is stupid because it depends on how much data you use
        //bagger.setMinTermFreq(20);
        
        filter.setDoNotOperateOnPerClassBasis(true);
        filter.setWordsToKeep(800);
        filter.setLowerCaseTokens(true);

        //use stemming and remove "nonsense"
        filter.setStemmer(new NoNonsenseStemmer(true));

        filter.setTFTransform(true);
        filter.setIDFTransform(true);
        filter.setNormalizeDocLength(new SelectedTag(StringToWordVector.FILTER_NORMALIZE_ALL, StringToWordVector.TAGS_FILTER));

        filter.setOutputWordCounts(true);

        filter.setInputFormat(examples.getInstances());
        Instances filtered = Filter.useFilter(examples.getInstances(), filter);
        examples.setInstances(filtered);

        return filter;
    }
    
    /**
     * Get a filter that removes the id attribute from the data set, necessary
     * before training.
     *
     * @param examples
     * @return
     * @throws Exception
     */
    protected Filter getRemoveIDFilter(ExampleSet examples) throws Exception {
        RemoveByName filter = new RemoveByName();
        filter.setExpression(Pattern.quote(ExampleSet.ID_ATTR_NAME));

        filter.setInputFormat(examples.getInstances());
        Instances filtered = Filter.useFilter(examples.getInstances(), filter);
        examples.setInstances(filtered);

        return filter;
    }
    
    /**
     * Get a filter that removes the id attribute from the data set, necessary
     * before training.
     *
     * @param examples
     * @return
     * @throws Exception
     */
    protected Filter getRemoveParticipantFilter(ExampleSet examples) throws Exception {
        RemoveByName filter = new RemoveByName();
        filter.setExpression(Pattern.quote(ExampleSet.PARTICIPANT_ATTR_NAME));

        filter.setInputFormat(examples.getInstances());
        Instances filtered = Filter.useFilter(examples.getInstances(), filter);
        examples.setInstances(filtered);

        return filter;
    }
    
    /**
     * Get a bag of words filter for participants based on the provided examples.
     *
     * @param examples
     * @return
     * @throws Exception
     */
    protected Filter getParticipantsFilter(ExampleSet examples) throws Exception {
        SimpleStringToWordVector filter = new SimpleStringToWordVector();
        filter.setAttributeNamePrefix(PARTICIPANT_FEATURE_PREFIX);
        filter.setStringAttributeName(ExampleSet.PARTICIPANT_ATTR_NAME);

        filter.setDoNotOperateOnPerClassBasis(true);
        filter.setWordsToKeep(getParticipantFeatureCount());
        filter.setLowerCaseTokens(true);

        //use stemming and remove "nonsense"
        filter.setStemmer(null);
        
        filter.setOutputWordCounts(false);

        filter.setInputFormat(examples.getInstances());
        Instances filtered = Filter.useFilter(examples.getInstances(), filter);
        examples.setInstances(filtered);

        return filter;
    }
}
