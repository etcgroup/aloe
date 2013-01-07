/*
 * This file is part of ALOE.
 *
 * ALOE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * ALOE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with ALOE.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2012 SCCL, University of Washington (http://depts.washington.edu/sccl)
 */
package etc.aloe.cscw2013;

import etc.aloe.data.Message;
import etc.aloe.data.Predictions;
import etc.aloe.data.Segment;
import etc.aloe.data.SegmentSet;
import etc.aloe.processes.LabelMapping;
import java.util.List;

/**
 * Given predicted labels, applies them to the given segments and their
 * messages.
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class LabelMappingImpl implements LabelMapping {

    @Override
    public void map(Predictions predictions, SegmentSet segments) {
        for (int s = 0; s < segments.size(); s++) {
            Segment segment = segments.get(s);
            Boolean predictedLabel = predictions.getPredictedLabel(s);
            Double confidence = predictions.getPredictionConfidence(s);

            segment.setPredictedLabel(predictedLabel);
            segment.setPredictionConfidence(confidence);

            for (Message message : segment.getMessages()) {
                message.setPredictedLabel(predictedLabel);
                message.setPredictionConfidence(confidence);
            }
        }
    }
}
