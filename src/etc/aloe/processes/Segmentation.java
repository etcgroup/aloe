package etc.aloe.processes;

import etc.aloe.data.LabeledMessage;
import etc.aloe.data.Segment;
import java.util.List;

/**
 * 
 */
public interface Segmentation {

    /**
     * Abstracts segmentation of labeled messages
     * @param messages
     * @return
     */
    public List<Segment> segment(List<LabeledMessage> messages);
}
