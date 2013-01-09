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
 * rate-of-change of the message flow over time, aka the 'heat'.
 * 
 * @author Dan Barella <dan.barella@gmail.com>
 */
public class HeatSegmentationPipeline extends CSCW2013 {
    
    /**
     * This method uses HeatSegmentation instead of ThresholdSegmenation.
     * 
     * @return A Heatmap-based segmentation of the chatlog
     */
    @Override
    public Segmentation constructSegmentation() {
        //PROTO 
        boolean disableSegmentation = false;
        int segmentationThresholdSeconds = 30;
        boolean ignoreParticipants = false;

        if (options instanceof TrainOptionsImpl) {
            TrainOptionsImpl trainOpts = (TrainOptionsImpl) options;
            disableSegmentation = trainOpts.disableSegmentation;
            segmentationThresholdSeconds = trainOpts.segmentationThresholdSeconds;
            ignoreParticipants = trainOpts.ignoreParticipants;
        } else if (options instanceof LabelOptionsImpl) {
            LabelOptionsImpl labelOpts = (LabelOptionsImpl) options;
            disableSegmentation = labelOpts.disableSegmentation;
            segmentationThresholdSeconds = labelOpts.segmentationThresholdSeconds;
            ignoreParticipants = labelOpts.ignoreParticipants;
        } else {
            throw new IllegalArgumentException("Options should be for Training or Labeling");
        }

        if (disableSegmentation) {
            return new NullSegmentation();
        } else {
            Segmentation segmentation = new HeatSegmentation(segmentationThresholdSeconds,
                    !ignoreParticipants);
            segmentation.setSegmentResolution(new ResolutionImpl());
            return segmentation;
        }
    }
}
