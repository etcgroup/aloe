package etc.aloe.filters;

import java.util.regex.Pattern;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.stemmers.SnowballStemmer;
import weka.core.stemmers.Stemmer;
import weka.filters.unsupervised.attribute.StringToWordVector;

/**
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class SimpleStringToWordVector extends StringToWordVector {

    String stringAttributeName;

    @Override
    public boolean setInputFormat(Instances instanceInfo) throws Exception {
        if (stringAttributeName == null) {
            throw new IllegalStateException("String attribute name was not set");
        }

        Attribute stringAttr = instanceInfo.attribute(stringAttributeName);
        if (stringAttr == null) {
            throw new IllegalStateException("Attribute " + stringAttributeName + " does not exist");
        }

        this.setAttributeIndicesArray(new int[] {stringAttr.index()});

        return super.setInputFormat(instanceInfo);
    }

    public String getStringAttributeName() {
        return stringAttributeName;
    }

    public void setStringAttributeName(String stringAttributeName) {
        this.stringAttributeName = stringAttributeName;
    }

    public static class NoNonsenseStemmer implements Stemmer {
        private SnowballStemmer snowball;
        private final Pattern nonsensePattern;

        public NoNonsenseStemmer(boolean useSnowball) {
            if (useSnowball) {
                this.snowball = new SnowballStemmer();
            }

            this.nonsensePattern = Pattern.compile("^[\\p{Digit}\\p{Punct}]*$");
        }

        @Override
        public String stem(String word) {

            //is it an unreasonable word?
            if (nonsensePattern.matcher(word).matches()) {
                word = "";
            }

            if (snowball != null) {
                return snowball.stem(word);
            } else {
                return word;
            }
        }

        @Override
        public String getRevision() {
            return "1";
        }

    }

}
