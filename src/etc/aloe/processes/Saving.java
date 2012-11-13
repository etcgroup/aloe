package etc.aloe.processes;

import java.io.File;
import java.io.IOException;

/**
 * An object that can be saved to a file.
 */
public interface Saving {

    /**
     * Saves the object to a file.
     *
     * @param source The file to save.
     * @return True if saving was successful
     * @throws IOException
     */
    public boolean save(File destination) throws IOException;
}
