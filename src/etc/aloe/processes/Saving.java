package etc.aloe.processes;

import java.io.IOException;
import java.io.OutputStream;

/**
 * An object that can be saved to a file.
 */
public interface Saving {

    /**
     * Saves the object to a file.
     *
     * @param source The destiation to save to.
     * @return True if saving was successful
     * @throws IOException
     */
    public boolean save(OutputStream destination) throws IOException;
}
