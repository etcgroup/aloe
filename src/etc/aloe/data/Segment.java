package etc.aloe.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Stores a list of LabeledMessages, associated with an unique ID for the
 * segment (auto-increment, zero-index)
 */
public class Segment implements LabelableItem {

    private static int ID_COUNTER = 0;
    private int id;
    private List<Message> messages;
    private Boolean trueLabel;
    private Boolean predictedLabel;

    public Segment() {
        messages = new ArrayList<Message>();
        id = ID_COUNTER;
        ID_COUNTER++;
    }

    /**
     * Convenience method, mostly for testing.
     * @param trueLabel
     * @param predictedLabel
     */
    public Segment(Boolean trueLabel, Boolean predictedLabel) {
        this();
        this.trueLabel = trueLabel;
        this.predictedLabel = predictedLabel;
    }

    public int getId() {
        return id;
    }

    public String concatMessages() {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Message item : getMessages()) {
            if (first) {
                first = false;
            } else {
                sb.append(" ");
            }
            sb.append(item.getMessage());
        }
        return sb.toString();
    }

    public void add(Message message) {
        messages.add(message);
        message.setSegmentId(this.id);
    }

    public List<Message> getMessages() {
        return messages;
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

    public double getDurationInSeconds() {
        if (messages.isEmpty()) {
            return 0;
        }

        Date start = messages.get(0).getTimestamp();
        Date stop = messages.get(messages.size() - 1).getTimestamp();

        return 1 + (stop.getTime() - start.getTime()) / 1000.0;
    }
}
