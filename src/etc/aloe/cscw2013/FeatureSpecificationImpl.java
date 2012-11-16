package etc.aloe.cscw2013;

import etc.aloe.data.FeatureSpecification;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidObjectException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Implementation of a feature specification. Contains data about bag of words
 * features, emoticon features, and other configuration.
 */
public class FeatureSpecificationImpl extends FeatureSpecification {

    @Override
    public boolean load(InputStream source) throws FileNotFoundException, InvalidObjectException {
        //TODO: fill me in!
        return true;
    }

    @Override
    public boolean save(OutputStream destination) throws IOException {
        PrintStream writer = new PrintStream(destination);
        writer.println("nothing to do here");
        //TODO: fill me in!
        return true;
    }
}
