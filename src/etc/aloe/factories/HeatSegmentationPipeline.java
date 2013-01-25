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

package etc.aloe.factories;

import etc.aloe.cscw2013.NullSegmentation;
import etc.aloe.cscw2013.ResolutionImpl;
import etc.aloe.processes.Segmentation;
import etc.aloe.wt2013.HeatSegmentation;

/**
 * An experimental pipeline that segments chat data based on the 
 * change of message occurrences within a specified time window.
 * 
 * The basic concept revolves around the idea of a heatmap
 *  - we segment when the heat function is zero, or when
 *  the heat function crosses some occurrence threshold.
 * 
 * The user must specify a time window, and an optional occurrence threshold.
 * 
 * @author Dan Barella <dan.barella@gmail.com>
 */
public class HeatSegmentationPipeline extends CSCW2013 {
    
    /**
     * This method uses HeatSegmentation instead of ThresholdSegmenation.
     * 
     * @return A Occurrence-based segmentation of the chatlog.
     */
    @Override
    public Segmentation constructSegmentation() {
        boolean disableSegmentation = false;
        float timeWindow = 0.0f;
        Float occurrenceThreshold;

        if (options instanceof TrainOptionsImpl) {
            TrainOptionsImpl trainOpts = (TrainOptionsImpl) options;
            disableSegmentation = trainOpts.disableSegmentation;
            
                timeWindow = trainOpts.timeWindow;
            occurrenceThreshold = trainOpts.occurrenceThreshold;
        } else if (options instanceof LabelOptionsImpl) {
            LabelOptionsImpl labelOpts = (LabelOptionsImpl) options;
            disableSegmentation = labelOpts.disableSegmentation;
            
            timeWindow = labelOpts.timeWindow;
            occurrenceThreshold = labelOpts.occurrenceThreshold;
        } else {
            throw new IllegalArgumentException("Options should be for Training or Labeling");
        }

        if (disableSegmentation) {
            return new NullSegmentation();
        } else if (occurrenceThreshold == null) { //Use mean occurrence value as threshold
            Segmentation segmentation = new HeatSegmentation(timeWindow);
            segmentation.setSegmentResolution(new ResolutionImpl());
            return segmentation;
        } else { //Use user-specified occurrence threshold
            Segmentation segmentation = new HeatSegmentation(timeWindow, occurrenceThreshold);
            segmentation.setSegmentResolution(new ResolutionImpl());
            return segmentation;
        }
    }
}
