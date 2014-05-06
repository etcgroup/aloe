package etc.aloe.oilspill2010;


import etc.aloe.data.ExampleSet;
import etc.aloe.data.FeatureSpecification;
import etc.aloe.filters.SimpleStringToWordVector;
import etc.aloe.filters.WordFeaturesExtractor;
import etc.aloe.oilspill2010.FeatureGenerationImpl;
import java.util.List;
import java.util.regex.Pattern;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.RemoveByName;
import weka.filters.unsupervised.attribute.StringToWordVector;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author mjbrooks
 */
public class BigramFeatureGenerationImpl extends FeatureGenerationImpl {

    public BigramFeatureGenerationImpl(List<String> emoticonDictionary) {
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
            spec.addFilter(getUnigramBigramFilter(examples));
            spec.addFilter(getParticipantsFilter(examples));
            spec.addFilter(getRemoveIDFilter(examples));
            spec.addFilter(getRemoveMessageFilter(examples));
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
    
    /**
     * Get a bag of words filter based on the provided examples.
     *
     * @param examples
     * @return
     * @throws Exception
     */
    protected Filter getUnigramBigramFilter(ExampleSet examples) throws Exception {
        WordFeaturesExtractor filter = new WordFeaturesExtractor();
        filter.setSelectedAttributeName(ExampleSet.MESSAGE_ATTR_NAME);
        
        filter.setLowerCaseTokens(true);
        //use stemming and remove "nonsense"
        filter.setStemmer(new SimpleStringToWordVector.NoNonsenseStemmer(false));

        filter.setUseBigrams(true);
        
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
    protected Filter getRemoveMessageFilter(ExampleSet examples) throws Exception {
        RemoveByName filter = new RemoveByName();
        filter.setExpression(Pattern.quote(ExampleSet.MESSAGE_ATTR_NAME));

        filter.setInputFormat(examples.getInstances());
        Instances filtered = Filter.useFilter(examples.getInstances(), filter);
        examples.setInstances(filtered);

        return filter;
    }
}
