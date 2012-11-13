package etc.aloe.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores a list of LabeledMessages, associated with an unique ID for the
 * segment (auto-increment, zero-index)
 */
public class Segment implements LabelableItem{

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

    public void add(Message message) {
        messages.add(message);
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
}
