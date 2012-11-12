package etc.aloe.data;

import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;

/**
 * Stores messages with id, participant, timestamp, and (optional) label. Also
 * maintains a set of all the labels used.
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class LabeledMessage implements Comparable<LabeledMessage> {

    public static final HashSet<String> labels = new HashSet<String>();
    public final int id;
    public final Date timestamp;
    public final String participant;
    public final String message;
    public final String label;

    public LabeledMessage(
            int id,
            Date timestamp,
            String participant,
            String message,
            String label) throws ParseException {
        this.id = id;
        this.timestamp = timestamp;
        this.participant = participant;
        this.message = message;
        this.label = label;
        labels.add(label);
    }

    @Override
    public int compareTo(LabeledMessage o) {
        return timestamp.compareTo(o.timestamp);
    }
}
