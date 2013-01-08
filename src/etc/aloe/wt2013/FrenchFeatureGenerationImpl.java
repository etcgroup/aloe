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
package etc.aloe.wt2013;

import etc.aloe.data.ExampleSet;
import etc.aloe.data.FeatureSpecification;
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
 * Features include words, emoticons, pronouns, punctuation, and other strings.
 * @author Sayer Rippey <srippey@oberlin.edu>
 * original code by Michael Brooks <mjbrooks@uw.edu>
 */

public class FrenchFeatureGenerationImpl implements FeatureGeneration {

    private static final boolean COUNT_REGEX_LENGTHS = true;
    private static final String EMOTICON_FEATURE_PREFIX = "#";
    private static final String BAG_OF_WORDS_FEATURE_PREFIX = "_";
    private final List<String> emoticonDictionary;

    /**
     * Construct a new FeatureGeneration implementation.
     *
     * @param emoticonDictionary The list of emoticons to look for in the
     * messages.
     */
    public FrenchFeatureGenerationImpl(List<String> emoticonDictionary) {
        this.emoticonDictionary = emoticonDictionary;
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
            Filter finalFilter = getRemoveIDFilter(examples);
            spec.addFilter(finalFilter);

            Instances output = finalFilter.getOutputFormat();
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
    private Filter getSpecialWordsFilter(ExampleSet examples) throws Exception {
        FrSpecialRegexFilter filter = new FrSpecialRegexFilter(ExampleSet.MESSAGE_ATTR_NAME);

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
    private Filter getSpellingFilter(ExampleSet examples) throws Exception {
        FrSpellingRegexFilter filter = new FrSpellingRegexFilter(ExampleSet.MESSAGE_ATTR_NAME);
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
    private Filter getPunctuationFilter(ExampleSet examples) throws Exception {
        FrPunctuationRegexFilter filter = new FrPunctuationRegexFilter(ExampleSet.MESSAGE_ATTR_NAME);
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
    private Filter getPronounsFilter(ExampleSet examples) throws Exception {
        FrPronounRegexFilter filter = new FrPronounRegexFilter(ExampleSet.MESSAGE_ATTR_NAME);

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
    private Filter getEmoticonsFilter(ExampleSet examples) throws Exception {
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
    private Filter getBagOfWordsFilter(ExampleSet examples) throws Exception {
        FrSimpleStringToWordVector filter = new FrSimpleStringToWordVector();
        filter.setAttributeNamePrefix(BAG_OF_WORDS_FEATURE_PREFIX);
        filter.setStringAttributeName(ExampleSet.MESSAGE_ATTR_NAME);

        //This is stupid because it depends on how much data you use
        //bagger.setMinTermFreq(20);

        filter.setWordsToKeep(600);
        filter.setLowerCaseTokens(true);

        //use stemming and remove "nonsense"
        filter.setStemmer(new FrSimpleStringToWordVector.NoNonsenseStemmer(true));

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
    private Filter getRemoveIDFilter(ExampleSet examples) throws Exception {
        RemoveByName filter = new RemoveByName();
        filter.setExpression(Pattern.quote(ExampleSet.ID_ATTR_NAME));

        filter.setInputFormat(examples.getInstances());
        Instances filtered = Filter.useFilter(examples.getInstances(), filter);
        examples.setInstances(filtered);

        return filter;
    }
    
}
