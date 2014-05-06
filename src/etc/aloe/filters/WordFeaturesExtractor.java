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
package etc.aloe.filters;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import weka.core.Attribute;
import weka.core.Capabilities;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;
import weka.core.Stopwords;
import weka.core.Utils;
import weka.core.stemmers.NullStemmer;
import weka.core.stemmers.Stemmer;
import weka.core.tokenizers.Tokenizer;
import weka.core.tokenizers.WordTokenizer;
import weka.filters.SimpleStreamFilter;

/**
 * A feature extractor that selects plausible unigrams from the training data to
 * use as features.
 *
 * If configured to use bigrams, it can also locate possible useful bigrams in
 * the training data.
 *
 * The algorithm is based on Tan et al. 2002:
 * http://dx.doi.org/10.1016/S0306-4573(01)00045-0
 *
 * @author mjbrooks
 */
public class WordFeaturesExtractor extends SimpleStreamFilter {

    private String selectedAttributeName = null;

    private boolean lowerCaseTokens = false;
    private boolean useBigrams = true;
    private File stopList = null;
    private Stemmer stemmer = new NullStemmer();
    private Tokenizer tokenizer = new WordTokenizer();

    //Default thresholds from Tan et al.
    //The minium document frequency to include a unigram
    private double unigramMinimumDocumentFrequency = 0.0001;
    //The minimum per-class frequency to include a bigram
    private double bigramMinimumPerClassFrequency = 0.001;
    //The minimum global term frequency to include a bigram
    private int bigramMinimumGlobalCount = 3;

    //Bigrams must have infogain higher than this bottom percentile of unigrams
    private double unigramInformationGainPosition = 0.01;
    //Calculated from the above
    private double bigramMinimumInformationGain;

    private Stopwords stopwords;
    private List<String> unigrams = new ArrayList<String>();
    private List<Bigram> bigrams = new ArrayList<Bigram>();
    private int selectedAttributeIndex;

    @Override
    public String globalInfo() {
        return "Extracts unigrams and optionally bigrams";
    }

    public boolean isUseBigrams() {
        return useBigrams;
    }

    public void setUseBigrams(boolean useBigrams) {
        this.useBigrams = useBigrams;
    }

    public boolean isLowerCaseTokens() {
        return lowerCaseTokens;
    }

    public void setLowerCaseTokens(boolean lowerCaseTokens) {
        this.lowerCaseTokens = lowerCaseTokens;
    }

    public File getStopList() {
        return stopList;
    }

    public void setStopList(File stopList) {
        this.stopList = stopList;
    }

    public Stemmer getStemmer() {
        return stemmer;
    }

    public void setStemmer(Stemmer stemmer) {
        this.stemmer = stemmer;
    }

    public Tokenizer getTokenizer() {
        return tokenizer;
    }

    public void setTokenizer(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    public double getUnigramMinimumDocumentFrequency() {
        return unigramMinimumDocumentFrequency;
    }

    public void setUnigramMinimumDocumentFrequency(double unigramMinimumDocumentFrequency) {
        this.unigramMinimumDocumentFrequency = unigramMinimumDocumentFrequency;
    }

    public double getBigramMinimumClassFrequency() {
        return bigramMinimumPerClassFrequency;
    }

    public void setBigramMinimumClassFrequency(double bigramMinimumPerClassFrequency) {
        this.bigramMinimumPerClassFrequency = bigramMinimumPerClassFrequency;
    }

    public int getBigramMinimumGlobalCount() {
        return bigramMinimumGlobalCount;
    }

    public void setBigramMinimumGlobalCount(int bigramMinimumGlobalCount) {
        this.bigramMinimumGlobalCount = bigramMinimumGlobalCount;
    }

    public String getSelectedAttributeName() {
        return selectedAttributeName;
    }

    public void setSelectedAttributeName(String selectedAttributeName) {
        this.selectedAttributeName = selectedAttributeName;
    }

    private Stopwords prepareStopwords() {
        // initialize stopwords
        Stopwords stopwords = new Stopwords();
        if (getStopList() != null) {
            try {
                if (getStopList().exists() && !getStopList().isDirectory()) {
                    stopwords.read(getStopList());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return stopwords;
    }

    @Override
    public Capabilities getCapabilities() {
        Capabilities result = super.getCapabilities();
        result.disableAll();

        // attributes
        result.enableAllAttributes();
        result.enable(Capabilities.Capability.MISSING_VALUES);

        // class
        result.enableAllClasses();
        result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
        result.enable(Capabilities.Capability.NO_CLASS);

        return result;
    }

    @Override
    public boolean setInputFormat(Instances instanceInfo) throws Exception {
        if (selectedAttributeName == null) {
            throw new IllegalStateException("String attribute name was not set");
        }

        Attribute stringAttr = instanceInfo.attribute(selectedAttributeName);
        if (stringAttr == null) {
            throw new IllegalStateException("Attribute " + selectedAttributeName + " does not exist");
        }

        return super.setInputFormat(instanceInfo); //To change body of generated methods, choose Tools | Templates.
    }

    protected List<String> tokenizeDocument(Instance instance) {
        List<String> words = new ArrayList<String>();

        // Use tokenizer
        tokenizer.tokenize(instance.stringValue(selectedAttributeIndex));

        // Iterate through tokens, perform stemming, and remove stopwords
        // (if required)
        while (tokenizer.hasMoreElements()) {
            String word = ((String) tokenizer.nextElement()).intern();

            if (this.lowerCaseTokens == true) {
                word = word.toLowerCase();
            }

            word = stemmer.stem(word);

            if (stopwords.is(word)) {
                continue;
            }

            if (word.length() == 0) {
                continue;
            }

            words.add(word);
        }

        return words;
    }

    protected List<List<String>> tokenizeDocuments(Instances instances) {
        //Convert all instances into term lists
        List<List<String>> documents = new ArrayList<List<String>>();

        for (int i = 0; i < instances.size(); i++) {
            Instance instance = instances.get(i);

            if (instance.isMissing(selectedAttributeIndex) == false) {
                List<String> words = tokenizeDocument(instance);
                documents.add(words);
            }
        }

        return documents;
    }

    private IndexMap<String> indexTerms(List<List<String>> documents) {
        IndexMap<String> termIndices = new IndexMap<String>();

        for (int d = 0; d < documents.size(); d++) {

            List<String> doc = documents.get(d);

            //Map<String, Integer> termFrequencies = new HashMap<String, Integer>();
//            for (int w = 0; w < doc.size(); w++) {
//                String word = doc.get(w);
//                
//                int count = 0;
//                if (termFrequencies.containsKey(word)) {
//                    count = termFrequencies.get(word);
//                }
//                termFrequencies.put(word, count++);
//            }
            for (String word : doc) {
                termIndices.recordIndex(word, d);
            }
        }

        return termIndices;
    }

    protected IndexMap<Bigram> indexBigrams(List<List<String>> documents) {
        IndexMap<Bigram> bigramIndices = new IndexMap<Bigram>();

        Set<String> unigramSet = new HashSet<String>(this.unigrams);

        //Get the document frequency of all possible bigrams
        for (int d = 0; d < documents.size(); d++) {
            String prevWord = null;
            boolean prevInUnigrams = false;

            List<String> doc = documents.get(d);
            for (int w = 0; w < doc.size(); w++) {
                String word = doc.get(w);
                boolean inUnigrams = unigramSet.contains(word);

                if (prevWord != null && (prevInUnigrams || inUnigrams)) {
                    //Add (prev, current)
                    bigramIndices.recordIndex(new Bigram(prevWord, word), d);
                }

                prevWord = word;
                prevInUnigrams = inUnigrams;
            }
        }

        return bigramIndices;
    }

    protected void determineUnigrams(List<List<String>> documents, ClassData classData) {

        //Map from terms to documents where the terms are located
        IndexMap<String> termIndices = indexTerms(documents);

        //Filter the unigrams using the document frequency threshold
        int minDocCount = (int) Math.ceil(this.unigramMinimumDocumentFrequency * documents.size());
        termIndices = termIndices.filterAboveThreshold(minDocCount);

        if (useBigrams) {
            //Calculate the information gain of all unigrams
            Map<String, Double> infoGainMap = termIndices.calculateInfoGain(classData);

            //Sort this by value and find the info gain of the bottom portion
            List<Double> infogains = new ArrayList<Double>(infoGainMap.values());
            Collections.sort(infogains);
            int infogainPercentileIndex = infogains.size() - (int) Math.ceil(this.unigramInformationGainPosition * infogains.size()) - 1;
            this.bigramMinimumInformationGain = infogains.get(infogainPercentileIndex);
        }

        //Only save the unigrams
        this.unigrams = new ArrayList<String>(termIndices.keySet());
    }

    protected void determineBigrams(List<List<String>> documents, ClassData classData) {
        IndexMap<Bigram> bigramIndex = indexBigrams(documents);

        //Filter down to those above the frequency threshold
        bigramIndex = bigramIndex.filterAboveThreshold(bigramMinimumGlobalCount, this.bigramMinimumPerClassFrequency, classData);

        //Filter down to those with the minimum information gain
        bigrams = new ArrayList<Bigram>();
        Map<Bigram, Double> infoGains = bigramIndex.calculateInfoGain(classData);
        for (Map.Entry<Bigram, Set<Integer>> bigramIndices : bigramIndex.entrySet()) {
            if (infoGains.get(bigramIndices.getKey()) >= this.bigramMinimumInformationGain) {
                bigrams.add(bigramIndices.getKey());
            }
        }
    }

    @Override
    protected Instances determineOutputFormat(Instances inputFormat) throws Exception {
        if (this.selectedAttributeName == null) {
            throw new IllegalStateException("String attribute name not set");
        }

        //Lookup the selected attribute
        Attribute stringAttr = inputFormat.attribute(selectedAttributeName);
        selectedAttributeIndex = stringAttr.index();

        //Read the stopwords
        stopwords = this.prepareStopwords();

        //Tokenize all documents
        List<List<String>> documents = tokenizeDocuments(inputFormat);

        //Wrap the instances in something more convenient
        ClassData instances = new ClassData(inputFormat);

        //First determine the list of viable unigrams
        determineUnigrams(documents, instances);

        //Find all bigrams including one of the unigrams, filtered
        if (useBigrams) {
            determineBigrams(documents, instances);
        }

        return generateOutputFormat(inputFormat);
    }

    @Override
    protected Instance process(Instance instance) throws Exception {
        if (selectedAttributeIndex < 0) {
            throw new IllegalStateException("String attribute not set");
        }

        int numOldValues = instance.numAttributes();
        int numNewFeatures = unigrams.size() + bigrams.size();
        double[] newValues = new double[numOldValues + numNewFeatures];

        // Copy all attributes from input to output
        for (int i = 0; i < getInputFormat().numAttributes(); i++) {
            if (getInputFormat().attribute(i).type() != Attribute.STRING) {
                // Add simple nominal and numeric attributes directly
                if (instance.value(i) != 0.0) {
                    newValues[i] = instance.value(i);
                }
            } else {
                if (instance.isMissing(i)) {
                    newValues[i] = Utils.missingValue();
                } else {

                    // If this is a string attribute, we have to first add
                    // this value to the range of possible values, then add
                    // its new internal index.
                    if (outputFormatPeek().attribute(i).numValues() == 0) {
                        // Note that the first string value in a
                        // SparseInstance doesn't get printed.
                        outputFormatPeek().attribute(i).addStringValue("Hack to defeat SparseInstance bug");
                    }
                    int newIndex = outputFormatPeek().attribute(i).addStringValue(instance.stringValue(i));
                    newValues[i] = newIndex;
                }
            }
        }

        String stringValue = instance.stringValue(selectedAttributeIndex);
        if (instance.isMissing(selectedAttributeIndex) == false) {

            List<String> words = tokenizeDocument(instance);
            Set<String> wordSet = new HashSet<String>(words);

            for (int i = 0; i < unigrams.size(); i++) {
                String unigram = unigrams.get(i);
                int count = 0;
                if (wordSet.contains(unigram)) {
                    //Count the times the word is in the document
                    for (int w = 0; w < words.size(); w++) {
                        if (words.get(w).equals(unigram)) {
                            count += 1;
                        }
                    }
                }

                int featureIndex = numOldValues + i;
                newValues[featureIndex] = count;
            }

            for (int i = 0; i < bigrams.size(); i++) {
                Bigram bigram = bigrams.get(i);
                int count = bigram.getTimesInDocument(words);
                int featureIndex = numOldValues + unigrams.size() + i;
                newValues[featureIndex] = count;
            }
        }

        Instance result = new SparseInstance(instance.weight(), newValues);
        return result;
    }

    private Instances generateOutputFormat(Instances inputFormat) {
        Instances outputFormat = new Instances(inputFormat, 0);

        //Add the new columns. There is one for each unigram and each bigram.
        for (int i = 0; i < unigrams.size(); i++) {
            String name = "uni_" + unigrams.get(i);
            Attribute attr = new Attribute(name);
            outputFormat.insertAttributeAt(attr, outputFormat.numAttributes());
        }

        for (int i = 0; i < bigrams.size(); i++) {
            String name = "bi_" + bigrams.get(i);
            Attribute attr = new Attribute(name);
            outputFormat.insertAttributeAt(attr, outputFormat.numAttributes());
        }

        return outputFormat;
    }

    private final class ClassData {

        //Label data from weka Instances
        final Instances instances;

        final int numberInstances;
        final int numberClassValues;
        final int[] countByClassValue;
        final Integer[] instanceClasses;
        final double entropy;

        public ClassData(Instances instances) {
            this.instances = instances;

            this.numberInstances = instances.size();
            this.numberClassValues = instances.numClasses();
            this.instanceClasses = new Integer[numberInstances];
            this.countByClassValue = new int[numberClassValues];

            for (int i = 0; i < numberInstances; i++) {
                Instance inst = instances.get(i);
                if (inst.classIsMissing()) {
                    instanceClasses[i] = null;
                } else {
                    instanceClasses[i] = (int) inst.classValue();

                    //Record how many instances have each class value
                    countByClassValue[instanceClasses[i]] += 1;
                }
            }

            this.entropy = calculateEntropy();
        }

        private double calculateEntropy() {
            double entropy = 0;
            for (int v = 0; v < countByClassValue.length; v++) {
                double proportion = (double) countByClassValue[v] / numberInstances;
                entropy -= Math.log(proportion) * proportion / Math.log(2);
            }

            return entropy;
        }
    }

    private final class Bigram {

        final String first;
        final String second;

        public Bigram(String first, String second) {
            this.first = first;
            this.second = second;
        }

        public int getTimesInDocument(List<String> document) {
            int count = 0;
            //Returns the number of times the bigram matches the string of terms
            for (int i = 1; i < document.size(); i++) {
                if (document.get(i).equals(second) && document.get(i - 1).equals(first)) {
                    count += 1;
                }
            }
            return count;
        }

        @Override
        public String toString() {
            return "(" + first + "," + second + ")";
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 97 * hash + (this.first != null ? this.first.hashCode() : 0);
            hash = 97 * hash + (this.second != null ? this.second.hashCode() : 0);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Bigram other = (Bigram) obj;
            if ((this.first == null) ? (other.first != null) : !this.first.equals(other.first)) {
                return false;
            }
            if ((this.second == null) ? (other.second != null) : !this.second.equals(other.second)) {
                return false;
            }
            return true;
        }
    }

    private class FrequencyMap<K> extends HashMap<K, Integer> {

        public int increment(K key) {
            int count = 0;
            if (this.containsKey(key)) {
                count = this.get(key);
            }
            this.put(key, count++);
            return count;
        }

        private FrequencyMap<K> filterAboveThreshold(int minCount) {
            FrequencyMap<K> filtered = new FrequencyMap<K>();
            for (Map.Entry<K, Integer> termFreq : this.entrySet()) {
                if (termFreq.getValue() >= minCount) {
                    filtered.put(termFreq.getKey(), termFreq.getValue());
                }
            }
            return filtered;
        }

    }

    private class IndexMap<K> extends HashMap<K, Set<Integer>> {

        public Set<Integer> recordIndex(K key, int index) {

            Set<Integer> indices = null;

            if (this.containsKey(key)) {
                indices = this.get(key);
            } else {
                indices = new HashSet<Integer>();
                this.put(key, indices);
            }

            indices.add(index);

            return indices;
        }

        private IndexMap<K> filterAboveThreshold(int minCount) {
            IndexMap<K> filtered = new IndexMap<K>();
            for (Map.Entry<K, Set<Integer>> termIndices : this.entrySet()) {
                Set<Integer> docsWithTerm = termIndices.getValue();
                boolean tossItOut = false;

                if (docsWithTerm.size() < minCount) {
                    tossItOut = true;
                }

                if (!tossItOut) {
                    filtered.put(termIndices.getKey(), docsWithTerm);
                }
            }
            return filtered;
        }
        
        private IndexMap<K> filterAboveThreshold(int minCount, double minFrequencyPerClass, ClassData classData) {
            
            int[] minCountForClass = new int[classData.numberClassValues];
            for (int v = 0; v < classData.numberClassValues; v++){
                int totalWithClass = classData.countByClassValue[v];
                minCountForClass[v] = (int)Math.ceil(totalWithClass * minFrequencyPerClass);
            }
            
            IndexMap<K> filtered = new IndexMap<K>();
            for (Map.Entry<K, Set<Integer>> termIndices : this.entrySet()) {
                Set<Integer> docsWithTerm = termIndices.getValue();
                boolean tossItOut = false;

                if (docsWithTerm.size() < minCount) {
                    tossItOut = true;
                } else {

                    int[] classCountsWithTerm = new int[classData.numberClassValues];

                    //Count the class values in the set with the term (probably small)
                    for (int docIndex : docsWithTerm) {
                        classCountsWithTerm[classData.instanceClasses[docIndex]] += 1;
                    }

                    for (int v = 0; v < classData.numberClassValues; v++) {
                        if (classCountsWithTerm[v] < minCountForClass[v]) {
                            tossItOut = true;
                            break;
                        }
                    }
                }

                if (!tossItOut) {
                    filtered.put(termIndices.getKey(), docsWithTerm);
                }
            }
            return filtered;
        }

        private Map<K, Double> calculateInfoGain(ClassData classData) {
            //Calculate the infogain of every key in this index given the class labels
            //Should run in O(terms * class_values) time (essentially # of terms)
            Map<K, Double> infogain = new HashMap<K, Double>();

            for (Map.Entry<K, Set<Integer>> termIndices : this.entrySet()) {
                Set<Integer> docsWithTerm = termIndices.getValue();

                //The number of documents with this term
                int numWithTerm = docsWithTerm.size();
                //The number of documents without this term
                int numWithoutTerm = classData.numberInstances - numWithTerm;

                int[] classCountsWithTerm = new int[classData.numberClassValues];

                //Count the class values in the set with the term (probably small)
                for (int docIndex : docsWithTerm) {
                    classCountsWithTerm[classData.instanceClasses[docIndex]] += 1;
                }

                double entropyWithTerm = 0;
                double entropyWithoutTerm = 0;

                //Deduce how many of each class remain in the outside set
                //And accumulate the entropy
                for (int v = 0; v < classData.numberClassValues; v++) {
                    int classCountWithoutTerm = classData.countByClassValue[v] - classCountsWithTerm[v];

                    double proportionWith = (double) classCountsWithTerm[v] / numWithTerm;
                    double proportionWithout = (double) classCountWithoutTerm / numWithoutTerm;
                    if (proportionWith > 0) {
                        entropyWithTerm -= Math.log(proportionWith) * proportionWith / Math.log(2);
                    }
                    if (proportionWithout > 0) {
                        entropyWithoutTerm -= Math.log(proportionWithout) * proportionWithout / Math.log(2);
                    }
                }

                //Calculate the information gain
                double infoGain = classData.entropy
                        - numWithTerm * entropyWithTerm / classData.numberInstances
                        - numWithoutTerm * entropyWithoutTerm / classData.numberInstances;

                infogain.put(termIndices.getKey(), infoGain);
            }

            return infogain;
        }
    }
}
