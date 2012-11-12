package etc.aloe.cscw2013;

import etc.aloe.processes.Segmentation;
import etc.aloe.data.LabeledMessage;
import etc.aloe.data.Segment;
import java.util.List;

/**
 *
 * @author kuksenok
 */
public class ThresholdSegmentation implements Segmentation {

    private final int thresholdSeconds;
    private final boolean byParticipant;

    public ThresholdSegmentation(int thresholdSeconds, boolean byParticipant) {
        this.thresholdSeconds = thresholdSeconds;
        this.byParticipant = byParticipant;
    }

    @Override
    public List<Segment> segment(List<LabeledMessage> messages) {
        // TODO implement me
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
