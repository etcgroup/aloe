/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package code;

import java.io.File;
import weka.core.SelectedTag;
import weka.core.stemmers.SnowballStemmer;
import weka.core.tokenizers.WordTokenizer;
import weka.filters.unsupervised.attribute.StringToWordVector;

/**
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class BagOfWordsConfig {
    boolean doStemming;
    boolean removeStopwords;
    int minimumTermFrequency;
    boolean useTermCounts;
    boolean useTfIdfTransform;
    boolean normalizeByLength;
    private String stoplist;

    public BagOfWordsConfig(boolean doStemming, boolean removeStopwords, int minimumTermFrequency, boolean useTermCounts, boolean useTfIdfTransform, boolean normalizeByLength) {
        this.doStemming = doStemming;
        this.removeStopwords = removeStopwords;
        this.minimumTermFrequency = minimumTermFrequency;
        this.useTermCounts = useTermCounts;
        this.useTfIdfTransform = useTfIdfTransform;
        this.normalizeByLength = normalizeByLength;
    }
    
    public void setStoplist(String filename) {
        this.stoplist = filename;
    }
    
    @Override
    public String toString() {
        return (doStemming ? "stemmed" : "unstemmed") + "." +
                (removeStopwords ? "stopped" : "unstopped") + "." +
                ("min" + minimumTermFrequency) + "." +
                (useTermCounts ? "counts" : "binary") + "." +
                (useTfIdfTransform ? "tfidf" : "notransform") + "." +
                (normalizeByLength ? "norm" : "nonorm");
    }

    void configure(StringToWordVector bagger) {
        bagger.setWordsToKeep(10000);
        bagger.setTokenizer(new WordTokenizer());

        if (doStemming) {
            bagger.setStemmer(new SnowballStemmer());
        }

        bagger.setUseStoplist(removeStopwords);
        if (removeStopwords && stoplist != null) {
            bagger.setStopwords(new File(stoplist));
        }
            
        bagger.setMinTermFreq(minimumTermFrequency);

        bagger.setOutputWordCounts(useTermCounts);
        bagger.setIDFTransform(useTfIdfTransform);
        bagger.setNormalizeDocLength(new SelectedTag(StringToWordVector.FILTER_NONE, StringToWordVector.TAGS_FILTER));
    }
}
