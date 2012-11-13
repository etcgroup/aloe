package etc.aloe.cscw2013;

import etc.aloe.data.MessageSet;
import etc.aloe.data.SegmentSet;
import etc.aloe.processes.Segmentation;

/**
 *
 */
public class ThresholdSegmentation implements Segmentation {

    private final int thresholdSeconds;
    private final boolean byParticipant;

    public ThresholdSegmentation(int thresholdSeconds, boolean byParticipant) {
        this.thresholdSeconds = thresholdSeconds;
        this.byParticipant = byParticipant;
    }

    @Override
    public SegmentSet segment(MessageSet messages) {
        // TODO implement me
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
