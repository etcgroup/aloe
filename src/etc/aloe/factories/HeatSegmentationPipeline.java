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

import etc.aloe.processes.Segmentation;

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
        //The most reliable method ever.
        return null;  
    }
}
