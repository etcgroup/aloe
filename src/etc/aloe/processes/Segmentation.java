package etc.aloe.processes;

import etc.aloe.data.MessageSet;
import etc.aloe.data.SegmentSet;

/**
 * Segments a set of messages.
 */
public interface Segmentation {

    /**
     * Segments messages.
     *
     * @param messages The messages to segment
     * @return Segmented messages
     */
    public SegmentSet segment(MessageSet messages);

    /**
     * Set the segment resolution strategy that will be used.
     * @param resolution
     */
    public void setSegmentResolution(SegmentResolution resolution);
}
