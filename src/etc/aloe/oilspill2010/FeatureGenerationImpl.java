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
package etc.aloe.oilspill2010;

import etc.aloe.data.ExampleSet;
import etc.aloe.data.FeatureSpecification;
import etc.aloe.filters.SimpleStringToWordVector;
import etc.aloe.filters.SimpleStringToWordVector.NoNonsenseStemmer;
import java.util.List;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.attributeSelection.Ranker;
import weka.attributeSelection.ReliefFAttributeEval;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.PrincipalComponents;
import weka.filters.unsupervised.attribute.StringToWordVector;
import weka.filters.unsupervised.instance.SparseToNonSparse;

/**
 * Generates a set of filters that extract the desired features from message
 * texts.
 *
 * Features include words, emoticons, pronouns, punctuations, and other strings.
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class FeatureGenerationImpl extends etc.aloe.cscw2013.FeatureGenerationImpl {

    private static final String PARTICIPANT_FEATURE_PREFIX = ".";

    /**
     * Construct a new FeatureGeneration implementation.
     *
     * @param emoticonDictionary The list of emoticons to look for in the
     * messages.
     */
    public FeatureGenerationImpl(List<String> emoticonDictionary) {
        super(emoticonDictionary);
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
            spec.addFilter(getParticipantsFilter(examples));
            spec.addFilter(getRemoveIDFilter(examples));
            //spec.addFilter(getSparseToNonsparseFilter(examples));
            //spec.addFilter(getFeatureSelectionFilter(examples));
            
            Instances output = spec.getOutputFormat();
            int numAttrs = output.numAttributes();
            System.out.println("generated " + (numAttrs - 1) + " features.");
        } catch (Exception e) {
            System.err.println("Error generating features.");
            System.err.println("\t" + e.getMessage());
        }

        return spec;
    }
    
    protected Filter getSparseToNonsparseFilter(ExampleSet examples) throws Exception {
        SparseToNonSparse filter = new SparseToNonSparse();
        
        filter.setInputFormat(examples.getInstances());
        Instances filtered = Filter.useFilter(examples.getInstances(), filter);
        examples.setInstances(filtered);
        
        return filter;
    }
    
    protected Filter getFeatureSelectionFilter(ExampleSet examples) throws Exception {
        
        AttributeSelection filter = new AttributeSelection();  // package weka.filters.supervised.attribute!
        //CfsSubsetEval eval = new CfsSubsetEval();
        
        //CorrelationAttributeEval eval = new CorrelationAttributeEval();
        //InfoGainAttributeEval eval = new InfoGainAttributeEval();
        
        ReliefFAttributeEval eval = new ReliefFAttributeEval();
        
        //GreedyStepwise search = new GreedyStepwise();
        //search.setNumToSelect(980);
        //search.setSearchBackwards(true);
        
        Ranker search = new Ranker();
        search.setNumToSelect(980);
        
        filter.setEvaluator(eval);
        filter.setSearch(search);
        
        filter.setInputFormat(examples.getInstances());
        Instances filtered = Filter.useFilter(examples.getInstances(), filter);
        examples.setInstances(filtered);
        
        return filter;
    }
    
    protected Filter getFeatureReductionFilter(ExampleSet examples) throws Exception {
        PrincipalComponents filter = new PrincipalComponents();
        filter.setMaximumAttributes(10);
        
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
    @Override
    protected Filter getBagOfWordsFilter(ExampleSet examples) throws Exception {
        SimpleStringToWordVector filter = new SimpleStringToWordVector();
        filter.setAttributeNamePrefix(BAG_OF_WORDS_FEATURE_PREFIX);
        filter.setStringAttributeName(ExampleSet.MESSAGE_ATTR_NAME);

        //This is stupid because it depends on how much data you use
        //bagger.setMinTermFreq(20);
        
        filter.setDoNotOperateOnPerClassBasis(true);
        filter.setWordsToKeep(3000);
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

        //This is stupid because it depends on how much data you use
        //bagger.setMinTermFreq(20);
        
        filter.setDoNotOperateOnPerClassBasis(true);
        filter.setWordsToKeep(20);
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
