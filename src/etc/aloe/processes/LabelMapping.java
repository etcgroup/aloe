package etc.aloe.processes;

import etc.aloe.data.MessageSet;
import etc.aloe.data.SegmentSet;
import java.util.List;

/**
 * LabelMapping maps predicted labels onto un-classified messages, producing labeled messages.
 */
public interface LabelMapping {

    /**
     * Modifies the message set with predicted labels based on the provided
     * labels and segments.
     *
     * The predictions are superimposed over the raw messages to produce the
     * output.
     *
     * @param predictedLabels Labels produced by a model
     * @param segments The unlabeled segments, whose messages will receive the new labels.
     * @return
     */
    void map(List<Boolean> predictedLabels, SegmentSet segments);
}
