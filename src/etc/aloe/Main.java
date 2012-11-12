package etc.aloe;

import etc.aloe.processes.Segmentation;
import etc.aloe.cscw2013.ThresholdSegmentation;
import etc.aloe.data.LabeledMessage;
import etc.aloe.data.Segment;
import java.util.List;

/**
 *
 * @author kuksenok
 */
public class Main {

    public static void args(String[] args) {


        // TODO Parse in arguments, specifying input files; parameters; and
        // state


        // This sets up the components of the abstract pipeline with specific
        // implementations.
        Segmentation segmentation = new ThresholdSegmentation(35, true);
        // CrossValidation
        //

        // TODO If we are in training mode:
        List<LabeledMessage> messages = null; // TODO CSV read
        List<Segment> segments = segmentation.segment(messages);

    }
}
