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
import java.util.HashMap;
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
    private float rateThreshold = 15.0f;
    
    //PROTO
    public HeatSegmentation(int thresholdSeconds, boolean byParticipant) {
        super(thresholdSeconds, byParticipant);
        
        this.thresholdSeconds = thresholdSeconds;
        this.byParticipant = byParticipant;
    }
    
    @Override
    public SegmentSet segment(MessageSet messageSet) {
        
        //Iterate over messages
            //Calculate the message rate at this point in time (separate loop)
            //Wherever the message rate crosses @rateThreshold
                //Get the previous inflection point and cut there
        
        //!!EDGE CASE!!: If the neighboring point to the right is the same value, still cut.
        
        System.out.println("Segmenting by heatmap with a rate threshold of " + rateThreshold 
                + " messages per " + (timeResolution/60.0f) + " minutes.");
        
        //Sort the message set by time
        List<Message> messages = sortByTime(messageSet.getMessages());
        
        //---
        //Calculate the message rate
        
        int rIndex = 0; //Index of the right message
        int lIndex = 0; //Index of the left message
        
        boolean stepping = false; //@true if the window is currently being adjusted, @false otherwise
        int messageRate = 0; //Current count of messages in the window, aka the rate per @timeResolution
        
        HashMap<Integer, Integer> messageRates = new HashMap<Integer, Integer>(); //We'll map the message's ID to the rate at its position in time
        
        while(rIndex < messages.size()) {
            //Get the messages
            Message left = messages.get(lIndex); 
            Message right = messages.get(rIndex);
            
            //Get the message times
            long rightMsgSeconds = right.getTimestamp().getTime() / 1000;
            long leftMsgSeconds = left.getTimestamp().getTime() / 1000;
            
            //If the left message is out of the window, advance the left pointer
            if((rightMsgSeconds-leftMsgSeconds) > timeResolution) {
                stepping = true;
                
                lIndex++;
                messageRate--;
                //rIndex--;
            } else { //Move the right index
                stepping = false;
                
                rIndex++;
                messageRate++;
            }
            
            if(!stepping) { //Here the message rate is accurate for this iteration
                //System.out.println("Message rate for " + right.getMessage() + "\n" + messageRate);
                messageRates.put(right.getId(), messageRate);
            }
        }
        
        //---
        //Begin segmentation
        
        SegmentSet segments = new SegmentSet();
        Segment current = new Segment();
        
        boolean wasAboveThresh = false;
        boolean newSegment = false;
        
        int numLabeled = 0;
        
        //TODO: Check second derivative. This currently cuts right at the threshold.
        for(Message m : messages) {
            int rate = messageRates.get(m.getId());
            if(rate > rateThreshold) { //Can't cut without crossing the threshold
                wasAboveThresh = true;
            }
            if(rate < rateThreshold && wasAboveThresh) { //We cut here
                wasAboveThresh = false;
                newSegment = true;
            }
            
            //Blatant copy-paste
            if (newSegment) {
                if (this.resolution != null) {
                    current.setTrueLabel(this.resolution.resolveLabel(current));
                    if (current.hasTrueLabel()) {
                        numLabeled++;
                    }
                }
                segments.add(current);
                current = new Segment();
                newSegment = false;
            }
            
            current.add(m);
        }
        
        //If it ain't baroque, don't fixe it?
        if (current.getMessages().size() > 0) {
            if (this.resolution != null) {
                current.setTrueLabel(this.resolution.resolveLabel(current));
                if (current.hasTrueLabel()) {
                    numLabeled++;
                }
            }
            segments.add(current);
        }
        
        System.out.println("Grouped messages into " + segments.size() + " segments (" + numLabeled + " labeled).");
        return segments;
    }
}
