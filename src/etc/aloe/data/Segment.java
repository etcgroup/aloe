package etc.aloe.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores a list of LabeledMessages, associated with an unique ID for the
 * segment (auto-increment, zero-index)
 */
public class Segment {

    private static int ID_COUNTER = 0;
    private int id;
    private List<Message> messages;

    public Segment() {
        messages = new ArrayList<Message>();
        id = ID_COUNTER;
        ID_COUNTER++;
    }

    public void add(Message message) {
        messages.add(message);
    }
}
