package etc.aloe.data;

/**
 * Reoresents an object that can have true and predicted labels.
 */
public interface LabelableItem {

    Boolean getTrueLabel();

    void setTrueLabel(Boolean truth);

    boolean hasTrueLabel();

    Boolean getPredictedLabel();

    void setPredictedLabel(Boolean prediction);

    boolean hasPredictedLabel();
}
