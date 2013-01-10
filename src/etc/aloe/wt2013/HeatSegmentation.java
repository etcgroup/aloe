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

import etc.aloe.cscw2013.ThresholdSegmentation;
import etc.aloe.data.Message;
import etc.aloe.data.MessageSet;
import etc.aloe.data.Segment;
import etc.aloe.data.SegmentSet;
import etc.aloe.processes.SegmentResolution;
import java.util.List;

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
    
    //Messages per timeResolution seconds
    private float timeResolution = 10.0f * 60.0f;
    
    //Boundary message rate - completely arbitrary, will be replaced with an actual metric
    private float rateThreshold = 5.0f;
    
    //PROTO
    public HeatSegmentation(int thresholdSeconds, boolean byParticipant) {
        super(thresholdSeconds, byParticipant);
        
        this.thresholdSeconds = thresholdSeconds;
        this.byParticipant = byParticipant;
    }
    
    @Override
    public SegmentSet segment(MessageSet messageSet) {
        
        System.out.println("Segmenting by heatmap with a rate threshold of " + rateThreshold 
                + " messages per " + (timeResolution/60.0f) + " minutes.");
        
        SegmentSet segments = new SegmentSet();
        
        //Sort the message set by time
        List<Message> messages = sortByTime(messageSet.getMessages());
        
        int rIndex = 0; //Index of the right message
        int lIndex = 0; //Index of the left message
        
        int messageRate = 0; //Current count of messages in the window, aka the rate per @timeResolution
        
        Segment current = new Segment();
        
        //Calculate the message rate for each message
        while(rIndex < messages.size()) {
            //Get the messages
            Message left = messages.get(lIndex); 
            Message right = messages.get(rIndex);
            
            //Get the message times
            long rightMsgSeconds = right.getTimestamp().getTime() / 1000;
            long leftMsgSeconds = left.getTimestamp().getTime() / 1000;
            
            //If the left message is out of the window, advance the left pointer
            if((rightMsgSeconds-leftMsgSeconds) > timeResolution) {
                lIndex++;
                messageRate--;
                //rIndex--;
            } else { //Move the right index
                rIndex++;
                messageRate++;
            }
            
            //Here the message rate is accurate for this iteration
            System.out.println("Message rate for " + right.getMessage() + "\n" + messageRate);
        }
        //Iterate over messages
            //Calculate the message rate at this point in time (Separate method)
            //Wherever the message rate crosses @rateThreshold
                //Get the previous inflection point and cut there
        
        //!!EDGE CASE!!: If the neighboring point to the right is the same value, still cut.
        
        return segments;
    }
}
