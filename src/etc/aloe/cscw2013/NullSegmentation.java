/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package etc.aloe.cscw2013;

import etc.aloe.data.Message;
import etc.aloe.data.MessageSet;
import etc.aloe.data.Segment;
import etc.aloe.data.SegmentSet;
import etc.aloe.processes.SegmentResolution;
import etc.aloe.processes.Segmentation;
import java.util.List;

/**
 *
 * @author michael
 */
public class NullSegmentation implements Segmentation {

    @Override
    public SegmentSet segment(MessageSet messages) {
        System.out.println("Applying no segmentation procedure.");
        SegmentSet segments = new SegmentSet();

        int numLabeled = 0;
        for (Message message : messages.getMessages()) {
            Segment current = new Segment();

            current.add(message);
            
            if (message.hasTrueLabel()) {
                current.setTrueLabel(message.getTrueLabel());
                numLabeled++;
            }

            segments.add(current);
        }

        System.out.println("Grouped messages into " + segments.size() + " segments (" + numLabeled + " labeled).");

        return segments;
    }

    @Override
    public void setSegmentResolution(SegmentResolution resolution) {
    }
}
