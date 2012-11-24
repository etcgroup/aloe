package etc.aloe.processes;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InvalidObjectException;

/**
 * An object that can be loaded from a file.
 */
public interface Loading {

    /**
     * Loads the object from a file.
     *
     * @param source The source to load from.
     * @return True if loading was successful
     * @throws FileNotFoundException
     * @throws InvalidObjectException
     */
    public boolean load(InputStream source) throws InvalidObjectException;
}
