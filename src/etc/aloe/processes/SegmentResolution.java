package etc.aloe.processes;

import etc.aloe.data.Segment;

/**
 * Resolution resolves disagreement among labels applied to the messages within
 * a segment.
 */
public interface SegmentResolution {

    Boolean resolveLabel(Segment segment);
}
