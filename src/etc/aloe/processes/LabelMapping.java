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
package etc.aloe.processes;

import etc.aloe.data.SegmentSet;
import java.util.List;

/**
 * LabelMapping maps predicted labels onto un-classified messages, producing
 * labeled messages.
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public interface LabelMapping {

    /**
     * Modifies the message set with predicted labels based on the provided
     * labels and segments.
     *
     * The predictions are superimposed over the raw messages to produce the
     * output.
     *
     * @param predictedLabels Labels produced by a model
     * @param segments The unlabeled segments, whose messages will receive the
     * new labels.
     * @return
     */
    void map(List<Boolean> predictedLabels, SegmentSet segments);
}
