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
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.RemoveByName;
import weka.filters.unsupervised.attribute.StringToWordVector;

/**
 *
 * @author michael
 */
public class FeatureGenerationImpl implements FeatureGeneration {

    private static final boolean COUNT_REGEX_LENGTHS = true;
    private static final String EMOTICON_FEATURE_PREFIX = "#";
    private static final String BAG_OF_WORDS_FEATURE_PREFIX = "_";
    private final List<String> emoticonDictionary;

    public FeatureGenerationImpl(List<String> emoticonDictionary) {
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
            Enumeration attrs = output.enumerateAttributes();
            while (attrs.hasMoreElements()) {
                Attribute attr = (Attribute) attrs.nextElement();
                System.out.print(attr.name() + ", ");
            }
            System.out.println();
        } catch (Exception e) {
            System.err.println("Error generating features.");
            System.err.println("\t" + e.getMessage());
        }

        return spec;
    }

    private Filter getSpecialWordsFilter(ExampleSet examples) throws Exception {
        SpecialRegexFilter filter = new SpecialRegexFilter(ExampleSet.MESSAGE_ATTR_NAME);

        filter.setInputFormat(examples.getInstances());
        Instances filtered = Filter.useFilter(examples.getInstances(), filter);
        examples.setInstances(filtered);

        return filter;
    }

    private Filter getSpellingFilter(ExampleSet examples) throws Exception {
        SpellingRegexFilter filter = new SpellingRegexFilter(ExampleSet.MESSAGE_ATTR_NAME);
        filter.setCountRegexLengths(COUNT_REGEX_LENGTHS);

        filter.setInputFormat(examples.getInstances());
        Instances filtered = Filter.useFilter(examples.getInstances(), filter);
        examples.setInstances(filtered);

        return filter;
    }

    private Filter getPunctuationFilter(ExampleSet examples) throws Exception {
        PunctuationRegexFilter filter = new PunctuationRegexFilter(ExampleSet.MESSAGE_ATTR_NAME);
        filter.setCountRegexLengths(COUNT_REGEX_LENGTHS);

        filter.setInputFormat(examples.getInstances());
        Instances filtered = Filter.useFilter(examples.getInstances(), filter);
        examples.setInstances(filtered);

        return filter;
    }

    private Filter getPronounsFilter(ExampleSet examples) throws Exception {
        PronounRegexFilter filter = new PronounRegexFilter(ExampleSet.MESSAGE_ATTR_NAME);

        filter.setInputFormat(examples.getInstances());
        Instances filtered = Filter.useFilter(examples.getInstances(), filter);
        examples.setInstances(filtered);

        return filter;
    }

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

    private Filter getBagOfWordsFilter(ExampleSet examples) throws Exception {
        SimpleStringToWordVector filter = new SimpleStringToWordVector();
        filter.setAttributeNamePrefix(BAG_OF_WORDS_FEATURE_PREFIX);
        filter.setStringAttributeName(ExampleSet.MESSAGE_ATTR_NAME);

        //This is stupid because it depends on how much data you use
        //bagger.setMinTermFreq(20);

        filter.setWordsToKeep(600);
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

    private Filter getRemoveIDFilter(ExampleSet examples) throws Exception {
        RemoveByName filter = new RemoveByName();
        filter.setExpression(Pattern.quote(ExampleSet.ID_ATTR_NAME));

        filter.setInputFormat(examples.getInstances());
        Instances filtered = Filter.useFilter(examples.getInstances(), filter);
        examples.setInstances(filtered);

        return filter;
    }
}
