package etc.aloe.processes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InvalidObjectException;

/**
 * An object that can be loaded from a file.
 */
public interface Loading {

    /**
     * Loads the object from a file.
     *
     * @param source The file to load.
     * @return True if loading was successful
     * @throws FileNotFoundException
     * @throws InvalidObjectException
     */
    public boolean load(File source) throws FileNotFoundException, InvalidObjectException;
}
