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
import etc.aloe.data.MessageSet;
import etc.aloe.data.Segment;
import etc.aloe.data.SegmentSet;
import etc.aloe.processes.SegmentResolution;
import etc.aloe.processes.Segmentation;

/**
 * Performs no segmentation. Each message is placed in its own segment.
 *
 * The SegmentResolution has no effect.
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
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
