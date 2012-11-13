package etc.aloe.data;

import etc.aloe.processes.Loading;
import etc.aloe.processes.Saving;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.List;

/**
 * MessageSet contains messages.
 */
public class MessageSet implements Loading, Saving {

    private List<Message> messages = new ArrayList<Message>();

    public void add(Message message) {
        messages.add(message);
    }

    public List<Message> getMessages() {
        return messages;
    }

    @Override
    public boolean load(File source) throws FileNotFoundException, InvalidObjectException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean save(File destination) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
