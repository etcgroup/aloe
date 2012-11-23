package etc.aloe.cscw2013;

import etc.aloe.data.Message;
import etc.aloe.data.Segment;
import etc.aloe.data.SegmentSet;
import etc.aloe.processes.LabelMapping;
import java.util.List;

/**
 *
 */
public class LabelMappingImpl implements LabelMapping {

    @Override
    public void map(List<Boolean> predictedLabels, SegmentSet segments) {
        for (int s = 0; s < segments.size(); s++) {
            Segment segment = segments.get(s);
            Boolean predictedLabel = predictedLabels.get(s);

            for (Message message : segment.getMessages()) {
                message.setPredictedLabel(predictedLabel);
            }
        }
    }
}
