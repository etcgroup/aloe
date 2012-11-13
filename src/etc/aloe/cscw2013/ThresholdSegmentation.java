package etc.aloe.cscw2013;

import etc.aloe.data.Message;
import etc.aloe.data.MessageSet;
import etc.aloe.data.Segment;
import etc.aloe.data.SegmentSet;
import etc.aloe.processes.SegmentResolution;
import etc.aloe.processes.Segmentation;

/**
 *
 */
public class ThresholdSegmentation implements Segmentation {

    private final int thresholdSeconds;
    private final boolean byParticipant;
    private SegmentResolution resolution;

    public ThresholdSegmentation(int thresholdSeconds, boolean byParticipant) {
        this.thresholdSeconds = thresholdSeconds;
        this.byParticipant = byParticipant;
    }

    @Override
    public SegmentSet segment(MessageSet messages) {
        // TODO implement me better!

        SegmentSet segments = new SegmentSet();
        for (Message message : messages.getMessages()) {

            Segment segment = new Segment();

            segment.add(message);
            segment.setTrueLabel(this.resolution.resolveLabel(segment));

            segments.add(segment);
        }

        System.out.println("Created " + segments.size() + " segments from messages.");

        return segments;
    }

    @Override
    public void setSegmentResolution(SegmentResolution resolution) {
        this.resolution = resolution;
    }
}
