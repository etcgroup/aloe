package etc.aloe.cscw2013;

import etc.aloe.data.FeatureSpecification;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InvalidObjectException;

/**
 * Implementation of a feature specification. Contains data about bag of words
 * features, emoticon features, and other configuration.
 */
public class FeatureSpecificationImpl extends FeatureSpecification {

    @Override
    public boolean load(File source) throws FileNotFoundException, InvalidObjectException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean save(File destination) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
