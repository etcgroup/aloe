package etc.aloe.data;

import java.util.Date;

/**
 * Stores messages with id, participant, timestamp, and (optionally) labels.
 */
public class Message implements Comparable<Message>, LabelableItem {

    private final int id;
    private final Date timestamp;
    private final String participant;
    private final String message;
    private Boolean trueLabel;
    private Boolean predictedLabel = null;
    private int segmentId = -1;

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
        this(id, timestamp, participant, message, null, null, -1);
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
        this(id, timestamp, participant, message, trueLabel, null, -1);
    }

    /**
     * Construct a new message. Leave trueLabel and predictedLabel null if
     * unlabeled.
     *
     * @param id
     * @param timestamp
     * @param participant
     * @param message
     * @param trueLabel
     * @param predictedLabel
     */
    public Message(
            int id,
            Date timestamp,
            String participant,
            String message,
            Boolean trueLabel,
            Boolean predictedLabel) {
        this(id, timestamp, participant, message, trueLabel, predictedLabel, -1);
    }

    /**
     * Construct a new message. Leave trueLabel and predictedLabel null if
     * unlabeled. Leave segmentId -1 if not segmented.
     *
     * @param id
     * @param timestamp
     * @param participant
     * @param message
     * @param trueLabel
     * @param predictedLabel
     * @param segmentId
     */
    public Message(
            int id,
            Date timestamp,
            String participant,
            String message,
            Boolean trueLabel,
            Boolean predictedLabel,
            int segmentId) {

        this.id = id;
        this.timestamp = timestamp;
        this.participant = participant;
        this.message = message;
        this.trueLabel = trueLabel;
        this.predictedLabel = predictedLabel;
        this.segmentId = segmentId;
    }

    public int getId() {
        return id;
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

    public int getSegmentId() {
        return segmentId;
    }

    public boolean hasSegmentId() {
        return segmentId != -1;
    }

    public void setSegmentId(int segId) {
        this.segmentId = segId;
    }
}
