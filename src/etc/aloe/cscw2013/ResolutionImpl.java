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

import etc.aloe.data.Label;
import etc.aloe.data.Message;
import etc.aloe.data.Segment;
import etc.aloe.processes.SegmentResolution;

/**
 * A basic disagreement resolution strategy for labeling segments.
 *
 * If any of the messages contained in the segment have a true label, the
 * segment is given a true label.
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class ResolutionImpl implements SegmentResolution {

    @Override
    public Label resolveLabel(Segment segment) {
        if (Label.getLabelCount() != 2) {
            throw new IllegalStateException("This label resolution technique only works for binary classification!");
        }

        Label pos = Label.TRUE();

        boolean labelSetBySomeone = false;
        for (Message message : segment.getMessages()) {
            if (message.hasTrueLabel()) {
                labelSetBySomeone = true;
                if (message.getTrueLabel() == pos) {
                    return pos;
                }
            }
        }

        if (labelSetBySomeone) {
            return Label.FALSE();
        } else {
            return null;
        }
    }
}
