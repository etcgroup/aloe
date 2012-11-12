package etc.aloe.data;

import java.util.ArrayList;

/**
 * Stores a list of LabeledMessages, associated with an unique ID for the
 * segment (auto-increment, zero-index)
 * @author Katie Kuksenok
 */
public class Segment {
    private static int ID_COUNTER = 0;
    private int id;
    private ArrayList<LabeledMessage> messages;
    public Segment(){
        messages = new ArrayList<LabeledMessage>();
        id = ID_COUNTER;
        ID_COUNTER++;
    }

    public void add(LabeledMessage message){
        messages.add(message);
    }

}
