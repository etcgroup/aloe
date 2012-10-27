/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data.transforms.filters;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import weka.core.*;
import weka.filters.Filter;
import weka.filters.SimpleBatchFilter;

/**
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class StringToDictionaryVector extends SimpleBatchFilter {

    private int stringAttributeIndex = -1;
    private String stringAttribute;
    private File dictionaryFile;
    /**
     * Contains the number of documents (instances) in the input format from
     * which the dictionary is created. It is used in IDF transform.
     */
    private int m_NumInstances = -1;
    /**
     * whether to operate on a per-class basis.
     */
    private boolean m_doNotOperateOnPerClassBasis = false;
    /**
     * The default number of words (per class if there is a class attribute
     * assigned) to attempt to keep.
     */
    private int m_WordsToKeep = 1000;
    /**
     * the minimum (per-class) word frequency.
     */
    private int m_minTermFreq = 1;
    /**
     * Contains the number of documents (instances) a particular word appears
     * in. The counts are stored with the same indexing as m_selectedTerms.
     */
    private int[] m_DocsCounts;
    /**
     * A String prefix for the attribute names.
     */
    private String m_Prefix = "";
    /**
     * The set of terms that occurred frequently enough to be included as
     * attributes.
     */
    private ArrayList<String> m_selectedTerms;
    /**
     * The trie containing the selected terms for matching.
     */
    private Trie m_selectedTermsTrie;
    /**
     * Maps the terms to indices in m_selectedTerms
     */
    private HashMap<String, Integer> m_selectedTermIndices;
    /**
     * True if word frequencies should be transformed into log(1+fi) where fi is
     * the frequency of word i.
     */
    private boolean m_TFTransform;
    /**
     * True if word frequencies should be transformed into
     * fij*log(numOfDocs/numOfDocsWithWordi).
     */
    private boolean m_IDFTransform;
    /**
     * True if output instances should contain word frequency rather than
     * boolean 0 or 1.
     */
    private boolean m_OutputCounts = false;
    /**
     * The normalization to apply.
     */
    protected int m_filterType = FILTER_NONE;
    /**
     * normalization: No normalization.
     */
    public static final int FILTER_NONE = 0;
    /**
     * normalization: Normalize all data.
     */
    public static final int FILTER_NORMALIZE_ALL = 1;
    /**
     * normalization: Normalize test data only.
     */
    public static final int FILTER_NORMALIZE_TEST_ONLY = 2;
    /**
     * Specifies whether document's (instance's) word frequencies are to be
     * normalized. The are normalized to average length of documents specified
     * as input format.
     */
    public static final Tag[] TAGS_FILTER = {
        new Tag(FILTER_NONE, "No normalization"),
        new Tag(FILTER_NORMALIZE_ALL, "Normalize all data"),
        new Tag(FILTER_NORMALIZE_TEST_ONLY, "Normalize test data only")
    };
    /**
     * Contains the average length of documents (among the first batch of
     * instances aka training data). This is used in length normalization of
     * documents which will be normalized to average document length.
     */
    private double m_AvgDocLength = -1;

    /**
     * Gets whether if the word frequencies for a document (instance) should be
     * normalized or not.
     *
     * @return true if word frequencies are to be normalized.
     */
    public SelectedTag getNormalizeDocLength() {

        return new SelectedTag(m_filterType, TAGS_FILTER);
    }

    /**
     * Sets whether if the word frequencies for a document (instance) should be
     * normalized or not.
     *
     * @param newType the new type.
     */
    public void setNormalizeDocLength(SelectedTag newType) {

        if (newType.getTags() == TAGS_FILTER) {
            m_filterType = newType.getSelectedTag().getID();
        }
    }

    /**
     * Gets whether if the word frequencies should be transformed into
     * log(1+fij) where fij is the frequency of word i in document(instance) j.
     *
     * @return true if word frequencies are to be transformed.
     */
    public boolean getTFTransform() {
        return this.m_TFTransform;
    }

    /**
     * Sets whether if the word frequencies should be transformed into
     * log(1+fij) where fij is the frequency of word i in document(instance) j.
     *
     * @param TFTransform true if word frequencies are to be transformed.
     */
    public void setTFTransform(boolean TFTransform) {
        this.m_TFTransform = TFTransform;
    }

    /**
     * Sets whether if the word frequencies in a document should be transformed
     * into: <br> fij*log(num of Docs/num of Docs with word i) <br> where fij is
     * the frequency of word i in document(instance) j.
     *
     * @return true if the word frequencies are to be transformed.
     */
    public boolean getIDFTransform() {
        return this.m_IDFTransform;
    }

    /**
     * Sets whether if the word frequencies in a document should be transformed
     * into: <br> fij*log(num of Docs/num of Docs with word i) <br> where fij is
     * the frequency of word i in document(instance) j.
     *
     * @param IDFTransform true if the word frequecies are to be transformed
     */
    public void setIDFTransform(boolean IDFTransform) {
        this.m_IDFTransform = IDFTransform;
    }

    /**
     * Gets whether output instances contain 0 or 1 indicating word presence, or
     * word counts.
     *
     * @return true if word counts should be output.
     */
    public boolean getOutputWordCounts() {
        return m_OutputCounts;
    }

    /**
     * Sets whether output instances contain 0 or 1 indicating word presence, or
     * word counts.
     *
     * @param outputWordCounts true if word counts should be output.
     */
    public void setOutputWordCounts(boolean outputWordCounts) {
        m_OutputCounts = outputWordCounts;
    }

    /**
     * Get the attribute name prefix.
     *
     * @return The current attribute name prefix.
     */
    public String getAttributeNamePrefix() {
        return m_Prefix;
    }

    /**
     * Set the attribute name prefix.
     *
     * @param newPrefix String to use as the attribute name prefix.
     */
    public void setAttributeNamePrefix(String newPrefix) {
        m_Prefix = newPrefix;
    }

    /**
     * Get the MinTermFreq value.
     *
     * @return the MinTermFreq value.
     */
    public int getMinTermFreq() {
        return m_minTermFreq;
    }

    /**
     * Set the MinTermFreq value.
     *
     * @param newMinTermFreq The new MinTermFreq value.
     */
    public void setMinTermFreq(int newMinTermFreq) {
        this.m_minTermFreq = newMinTermFreq;
    }

    /**
     * Gets the number of words (per class if there is a class attribute
     * assigned) to attempt to keep.
     *
     * @return the target number of words in the output vector (per class if
     * assigned).
     */
    public int getWordsToKeep() {
        return m_WordsToKeep;
    }

    /**
     * Sets the number of words (per class if there is a class attribute
     * assigned) to attempt to keep.
     *
     * @param newWordsToKeep the target number of words in the output vector
     * (per class if assigned).
     */
    public void setWordsToKeep(int newWordsToKeep) {
        m_WordsToKeep = newWordsToKeep;
    }

    public String getStringAttribute() {
        return stringAttribute;
    }

    public void setStringAttribute(String name) {
        stringAttribute = name;
    }

    public File getDictionaryFile() {
        return dictionaryFile;
    }

    public void setDictionaryFile(File file) {
        this.dictionaryFile = file;
    }

    /**
     * Get the DoNotOperateOnPerClassBasis value.
     *
     * @return the DoNotOperateOnPerClassBasis value.
     */
    public boolean getDoNotOperateOnPerClassBasis() {
        return m_doNotOperateOnPerClassBasis;
    }

    /**
     * Set the DoNotOperateOnPerClassBasis value.
     *
     * @param newDoNotOperateOnPerClassBasis The new DoNotOperateOnPerClassBasis
     * value.
     */
    public void setDoNotOperateOnPerClassBasis(boolean newDoNotOperateOnPerClassBasis) {
        this.m_doNotOperateOnPerClassBasis = newDoNotOperateOnPerClassBasis;
    }

    @Override
    public Capabilities getCapabilities() {
        Capabilities result = super.getCapabilities();
        result.enableAllAttributes();
        result.enableAllClasses();
        result.enable(Capabilities.Capability.NO_CLASS);  //// filter doesn't need class to be set//
        return result;
    }

    @Override
    public String globalInfo() {
        return "Creates a bag of words for a given string attribute. The values in the bag are selected from the provided dictionary.";
    }

    @Override
    protected Instances determineOutputFormat(Instances inputFormat) throws Exception {
        if (getStringAttribute() == null) {
            throw new IllegalStateException("String attribute name not set");
        }

        stringAttributeIndex = inputFormat.attribute(getStringAttribute()).index();
        
        inputFormat = getInputFormat();
        //This generates m_selectedTerms and m_DocsCounts
        int[] docsCountsByTermIdx = determineDictionary(inputFormat);

        //Initialize the output format to be just like the input
        Instances outputFormat = new Instances(inputFormat, 0);

        //Set up the map from attr index to document frequency
        m_DocsCounts = new int[m_selectedTerms.size()];
        //And add the new attributes
        for (int i = 0; i < m_selectedTerms.size(); i++) {
            int attrIdx = outputFormat.numAttributes();
            int docsCount = docsCountsByTermIdx[i];
            m_DocsCounts[i] = docsCount;

            outputFormat.insertAttributeAt(new Attribute(m_Prefix + m_selectedTerms.get(i)), attrIdx);
        }

        return outputFormat;
    }

    private class Count {

        public int count = 0;
        public int docCount = 0;

        public Count(int count) {
            this.count = count;
        }
    }

    /**
     * sorts an array.
     *
     * @param array the array to sort
     */
    private static void sortArray(int[] array) {

        int i, j, h, N = array.length - 1;

        for (h = 1; h <= N / 9; h = 3 * h + 1);

        for (; h > 0; h /= 3) {
            for (i = h + 1; i <= N; i++) {
                int v = array[i];
                j = i;
                while (j > h && array[j - h] > v) {
                    array[j] = array[j - h];
                    j -= h;
                }
                array[j] = v;
            }
        }
    }

    private int[] determineDictionary(Instances instances) {
        if (stringAttributeIndex < 0) {
            throw new IllegalStateException("String attribute index not valid");
        }
        
        // Operate on a per-class basis if class attribute is set
        int classInd = instances.classIndex();
        int values = 1;
        if (!m_doNotOperateOnPerClassBasis && (classInd != -1)) {
            values = instances.attribute(classInd).numValues();
        }

        //Read in the dictionary file
        HashSet<String> termSet = new HashSet<String>();
        try {
            Scanner dict = new Scanner(getDictionaryFile());
            while (dict.hasNextLine()) {
                String line = dict.nextLine();
                if (!line.startsWith("### ")) {
                    line = line.trim();
                    if (!line.isEmpty()) {
                        termSet.add(line);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("File " + getDictionaryFile() + " could not be opened.");
            System.exit(1);
        }

        ArrayList<String> termList = new ArrayList<String>(termSet);

        HashMap<String, Integer> termIndices = new HashMap<String, Integer>();
        for (int i = 0; i < termList.size(); i++) {
            termIndices.put(termList.get(i), i);
        }

        //Create the trie for matching terms
        Trie termTrie = new Trie(termList);

        //Initialize the dictionary/count map
        ArrayList<HashMap<Integer, Count>> termCounts = new ArrayList<HashMap<Integer, Count>>();
        for (int z = 0; z < values; z++) {
            termCounts.add(new HashMap<Integer, Count>());
        }

        //Go through all the instances and count the emoticons
        for (int i = 0; i < instances.numInstances(); i++) {
            Instance instance = instances.instance(i);
            int vInd = 0;
            if (!m_doNotOperateOnPerClassBasis && (classInd != -1)) {
                vInd = (int) instance.classValue();
            }

            //Get the string attribute to examine
            String stringValue = instance.stringValue(stringAttributeIndex);

            HashMap<Integer, Count> termCountsForClass = termCounts.get(vInd);

            HashMap<String, Integer> termMatches = termTrie.countNonoverlappingMatches(stringValue);
            for (Map.Entry<String, Integer> entry : termMatches.entrySet()) {
                String term = entry.getKey();
                int termIdx = termIndices.get(term);

                int matches = entry.getValue();

                Count count = termCountsForClass.get(termIdx);
                if (count == null) {
                    count = new Count(0);
                    termCountsForClass.put(termIdx, count);
                }

                if (matches > 0) {
                    count.docCount += 1;
                    count.count += matches;
                }
            }
        }

        // Figure out the minimum required word frequency
        int prune[] = new int[values];
        for (int z = 0; z < values; z++) {
            HashMap<Integer, Count> termCountsForClass = termCounts.get(z);

            int array[] = new int[termCountsForClass.size()];
            int pos = 0;
            for (Map.Entry<Integer, Count> entry : termCountsForClass.entrySet()) {
                array[pos] = entry.getValue().count;
                pos++;
            }

            // sort the array
            sortArray(array);

            if (array.length < m_WordsToKeep) {
                // if there aren't enough words, set the threshold to
                // minFreq
                prune[z] = m_minTermFreq;
            } else {
                // otherwise set it to be at least minFreq
                prune[z] = Math.max(m_minTermFreq, array[array.length - m_WordsToKeep]);
            }
        }

        // Add the word vector attributes (eliminating duplicates
        // that occur in multiple classes)
        HashSet<String> selectedTerms = new HashSet<String>();
        for (int z = 0; z < values; z++) {
            HashMap<Integer, Count> termCountsForClass = termCounts.get(z);

            for (Map.Entry<Integer, Count> entry : termCountsForClass.entrySet()) {
                int termIndex = entry.getKey();
                String term = termList.get(termIndex);
                Count count = entry.getValue();
                if (count.count >= prune[z]) {
                    selectedTerms.add(term);
                }
            }
        }

        //Save the selected terms as a list
        this.m_selectedTerms = new ArrayList<String>(selectedTerms);
        this.m_selectedTermsTrie = new Trie(this.m_selectedTerms);
        this.m_NumInstances = instances.size();

        //Construct the selected terms to index map
        this.m_selectedTermIndices = new HashMap<String, Integer>();
        for (int i = 0; i < m_selectedTerms.size(); i++) {
            m_selectedTermIndices.put(m_selectedTerms.get(i), i);
        }

        // Compute document frequencies, organized by selected term index (not original term index)
        int[] docsCounts = new int[m_selectedTerms.size()];
        for (int i = 0; i < m_selectedTerms.size(); i++) {
            String term = m_selectedTerms.get(i);
            int termIndex = termIndices.get(term);
            int docsCount = 0;
            for (int z = 0; z < values; z++) {
                HashMap<Integer, Count> termCountsForClass = termCounts.get(z);

                Count count = termCountsForClass.get(termIndex);
                if (count != null) {
                    docsCount += count.docCount;
                }
            }
            docsCounts[i] = docsCount;
        }
        return docsCounts;
    }

    /**
     * Converts the instance w/o normalization.
     *
     * @param instance the instance to convert
     *
     * @param ArrayList<Instance> the list of instances
     * @return the document length
     */
    private double convertInstancewoDocNorm(Instance instance, ArrayList<Instance> converted) {
        if (stringAttributeIndex < 0) {
            throw new IllegalStateException("String attribute index not valid");
        }

        int numOldValues = instance.numAttributes();
        double[] newValues = new double[numOldValues + m_selectedTerms.size()];

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

        String stringValue = instance.stringValue(stringAttributeIndex);
        double docLength = 0;

        HashMap<String, Integer> termMatches = m_selectedTermsTrie.countNonoverlappingMatches(stringValue);
        for (Map.Entry<String, Integer> entry : termMatches.entrySet()) {
            String term = entry.getKey();
            int termIdx = m_selectedTermIndices.get(term);
            double matches = entry.getValue();
            if (!m_OutputCounts && matches > 0) {
                matches = 1;
            }

            if (matches > 0) {
                if (m_TFTransform == true) {
                    matches = Math.log(matches + 1);
                }

                if (m_IDFTransform == true) {
                    matches = matches * Math.log(m_NumInstances / (double) m_DocsCounts[termIdx]);
                }

                newValues[numOldValues + termIdx] = matches;
                docLength += matches * matches;
            }
        }

        Instance result = new SparseInstance(instance.weight(), newValues);
        converted.add(result);

        return Math.sqrt(docLength);
    }

    /**
     * Normalizes given instance to average doc length (only the newly
     * constructed attributes).
     *
     * @param inst	the instance to normalize
     * @param double the document length
     * @throws Exception if avg. doc length not set
     */
    private void normalizeInstance(Instance inst, double docLength)
            throws Exception {

        if (docLength == 0) {
            return;
        }

        int numOldValues = getInputFormat().numAttributes();

        if (m_AvgDocLength < 0) {
            throw new Exception("Average document length not set.");
        }

        // Normalize document vector
        for (int j = numOldValues; j < inst.numAttributes(); j++) {
            double val = inst.value(j) * m_AvgDocLength / docLength;
            inst.setValue(j, val);
        }
    }

    @Override
    protected Instances process(Instances instances) throws Exception {
        Instances result = new Instances(getOutputFormat(), 0);

        // Convert all instances w/o normalization
        ArrayList<Instance> converted = new ArrayList<Instance>();
        ArrayList<Double> docLengths = new ArrayList<Double>();
        if (!isFirstBatchDone()) {
            m_AvgDocLength = 0;
        }
        for (int i = 0; i < instances.size(); i++) {
            double docLength = convertInstancewoDocNorm(instances.instance(i), converted);

            // Need to compute average document length if necessary
            if (m_filterType != FILTER_NONE) {
                if (!isFirstBatchDone()) {
                    m_AvgDocLength += docLength;
                }
                docLengths.add(docLength);
            }
        }
        if (m_filterType != FILTER_NONE) {

            if (!isFirstBatchDone()) {
                m_AvgDocLength /= instances.size();
            }

            // Perform normalization if necessary.
            if (isFirstBatchDone() || (!isFirstBatchDone() && m_filterType == FILTER_NORMALIZE_ALL)) {
                for (int i = 0; i < converted.size(); i++) {
                    normalizeInstance(converted.get(i), docLengths.get(i));
                }
            }
        }
        // Push all instances into the output queue
        for (int i = 0; i < converted.size(); i++) {
            result.add(converted.get(i));
        }

        return result;
    }

    private static class Trie {

        private static class TrieNode {

            boolean exists = false;
            HashMap<Character, TrieNode> branches = new HashMap<Character, Trie.TrieNode>();
        }
        TrieNode root = new TrieNode();

        public Trie() {
        }

        public Trie(Collection<String> tokens) {
            for (String token : tokens) {
                this.add(token);
            }
        }

        public void add(String term) {
            TrieNode currentNode = this.root;
            for (int i = 0; i < term.length(); i++) {
                char c = term.charAt(i);
                TrieNode next = currentNode.branches.get(c);
                if (next == null) {
                    next = new TrieNode();
                    currentNode.branches.put(c, next);
                }
                currentNode = next;
            }
            currentNode.exists = true;
        }

        public boolean contains(String term) {
            TrieNode currentNode = this.root;
            for (int i = 0; i < term.length(); i++) {
                char c = term.charAt(i);
                TrieNode next = currentNode.branches.get(c);
                if (next == null) {
                    return false;
                }
                currentNode = next;
            }
            return currentNode.exists;
        }

        /**
         * Finds the longest substring in the Trie that matches haystack
         * starting from the given index.
         *
         * @param haystack
         * @return
         */
        public String getLongestMatch(String haystack, int startingFrom) {
            TrieNode currentNode = this.root;
            String longestMatch = null;
            String workingString = "";
            for (int i = startingFrom; i < haystack.length(); i++) {
                char c = haystack.charAt(i);
                TrieNode next = currentNode.branches.get(c);
                if (next == null) {
                    return longestMatch;
                }
                currentNode = next;
                //Build up the next match
                workingString += c;

                if (currentNode.exists) {
                    //This is the best match so far
                    longestMatch = workingString;
                }
            }
            return longestMatch;
        }

        /**
         * Matches all Trie terms against the haystack, not overlapping. Returns
         * the matched words and the number of times they occurred.
         */
        public HashMap<String, Integer> countNonoverlappingMatches(String haystack) {
            HashMap<String, Integer> matchCounts = new HashMap<String, Integer>();

            //Go through the string greedily
            for (int i = 0; i < haystack.length();) {
                String longestMatch = getLongestMatch(haystack, i);
                if (longestMatch != null) {
                    if (!matchCounts.containsKey(longestMatch)) {
                        matchCounts.put(longestMatch, 1);
                    } else {
                        int count = matchCounts.get(longestMatch);
                        matchCounts.put(longestMatch, count + 1);
                    }

                    //Skip ahead by length
                    i += longestMatch.length();
                } else {
                    i++;
                }
            }

            return matchCounts;
        }
    }

    public static void main(String[] args) {

        //Create a test dataset
        ArrayList<Attribute> attributes = new ArrayList<Attribute>();
        attributes.add(new Attribute("message", (ArrayList<String>) null));
        attributes.add(new Attribute("id"));
        {
            ArrayList<String> classValues = new ArrayList<String>();
            classValues.add("0");
            classValues.add("1");
            attributes.add(new Attribute("class", classValues));
        }

        Instances instances = new Instances("test", attributes, 0);
        instances.setClassIndex(2);

        String[] messages = new String[]{
            "No emoticons here",
            "I have a smiley :)",
            "Two smileys and a frownie :) :) :(",
            "Several emoticons :( :-( :) :-) ;-) 8-) :-/ :-P"
        };

        for (int i = 0; i < messages.length; i++) {
            Instance instance = new DenseInstance(instances.numAttributes());
            instance.setValue(instances.attribute(0), messages[i]);
            instance.setValue(instances.attribute(1), i);
            instance.setValue(instances.attribute(2), Integer.toString(i % 2));
            instances.add(instance);
        }

        System.out.println("Before filter:");
        for (int i = 0; i < instances.size(); i++) {
            System.out.println(instances.instance(i).toString());
        }

        try {
            String dictionaryName = "emoticons.txt";
            StringToDictionaryVector filter = new StringToDictionaryVector();
            filter.setDictionaryFile(new File(dictionaryName));
            filter.setMinTermFreq(1);
            filter.setTFTransform(true);
            filter.setIDFTransform(true);
            filter.setNormalizeDocLength(new SelectedTag(FILTER_NORMALIZE_TEST_ONLY, TAGS_FILTER));
            filter.setOutputWordCounts(true);
            filter.setStringAttribute("message");

            filter.setInputFormat(instances);
            Instances trans1 = Filter.useFilter(instances, filter);
            Instances trans2 = Filter.useFilter(instances, filter);

            System.out.println("\nFirst application:");
            System.out.println(trans1.toString());

            System.out.println("\nSecond application:");
            System.out.println(trans2.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
