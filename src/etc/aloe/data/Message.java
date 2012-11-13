package etc.aloe.data;

import java.text.ParseException;
import java.util.Date;

/**
 * Stores messages with id, participant, timestamp, and (optionally) labels.
 */
public class Message implements Comparable<Message>, LabelableItem {

    public final int id;
    public final Date timestamp;
    public final String participant;
    public final String message;
    public Boolean trueLabel;
    public Boolean predictedLabel = null;

    /**
     * Construct a new un-labeled message.
     *
     * @param id
     * @param timestamp
     * @param participant
     * @param message
     */
    public Message(
            int id,
            Date timestamp,
            String participant,
            String message) {
        this(id, timestamp, participant, message, null);
    }

    /**
     * Construct a new message. Leave trueLabel null if unlabeled.
     *
     * @param id
     * @param timestamp
     * @param participant
     * @param message
     * @param trueLabel
     */
    public Message(
            int id,
            Date timestamp,
            String participant,
            String message,
            Boolean trueLabel) {

        this.id = id;
        this.timestamp = timestamp;
        this.participant = participant;
        this.message = message;
        this.trueLabel = trueLabel;
    }

    public String getMessage() {
        return message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getParticipant() {
        return participant;
    }

    @Override
    public Boolean getTrueLabel() {
        return trueLabel;
    }

    @Override
    public void setTrueLabel(Boolean truth) {
        this.trueLabel = truth;
    }

    @Override
    public boolean hasTrueLabel() {
        return trueLabel != null;
    }

    @Override
    public Boolean getPredictedLabel() {
        return predictedLabel;
    }

    @Override
    public void setPredictedLabel(Boolean prediction) {
        this.predictedLabel = prediction;
    }

    @Override
    public boolean hasPredictedLabel() {
        return predictedLabel != null;
    }

    @Override
    public int compareTo(Message o) {
        return getTimestamp().compareTo(o.getTimestamp());
    }
}
