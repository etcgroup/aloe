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
package etc.aloe.wt2013;

import etc.aloe.cscw2013.NullSegmentation;
import etc.aloe.cscw2013.ThresholdSegmentation;
import etc.aloe.data.Message;
import etc.aloe.data.MessageSet;
import etc.aloe.data.SegmentSet;
import etc.aloe.processes.SegmentResolution;
import java.util.List;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

/**
 * Segments chat data based on rate of message change, using an implicit 'heatmap'.
 * In essence, we attempt to segment related portions of the chat by looking
 * at the message rate over time and grouping peaks together. Hopefully this is
 * helpful and not full of wrongness.
 * 
 *       messages/min
 * |
 * |        ...
 * |  ..   .   .    ...
 * | .  ...     .. .   .
 * |._____________._____.__ [time]->
 * ^     ^        ^     ^
 *  [Segment Boundaries]
 * 
 * Currently prototyping, which is why this class is extending ThresholdSegmentation.
 * The final class will extend only Segmentation.
 * 
 * @author Dan Barella <dan.barella@gmail.com>
 */
public class HeatSegmentation extends ThresholdSegmentation {
    //PROTO
    private final int thresholdSeconds;
    private final boolean byParticipant;
    private SegmentResolution resolution;
    
    //PROTO
    public HeatSegmentation(int thresholdSeconds, boolean byParticipant) {
        super(thresholdSeconds, byParticipant);
        
        this.thresholdSeconds = thresholdSeconds;
        this.byParticipant = byParticipant;
    }
    
    @Override
    public SegmentSet segment(MessageSet messageSet) {
        float rateThreshold = 5.0f; //Completely arbitrary number, will be replaced with flag.
        SegmentSet segments = new SegmentSet();
        
        //Sort the message set by time
        List<Message> messages = sortByTime(messageSet.getMessages());
        
        //Find critical (inflection) points - we're not interested in where the data flattens, 
        //only where it changes direcitons
        
        //Iterate over messages
            //Calculate the message rate at this point in time (Separate method)
            //Wherever the message rate crosses @rateThreshold
                //Get the previous inflection point and cut there
        
        System.out.println("Segmenting by heatmap with " + rateThreshold + " rate threshold.");
        
        return segments;
    }
}
