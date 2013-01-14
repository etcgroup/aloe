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

import etc.aloe.data.Message;
import etc.aloe.data.MessageSet;
import etc.aloe.data.Segment;
import etc.aloe.data.SegmentSet;
import etc.aloe.processes.SegmentResolution;
import etc.aloe.processes.Segmentation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Segments chat data based on rate of message change, using an implicit 'heatmap'.
 * In essence, we attempt to segment related portions of the chat by looking
 * at the message rate over time and grouping peaks together.
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
 * @author Dan Barella <dan.barella@gmail.com>
 */
public class HeatSegmentation implements Segmentation {
    
    private SegmentResolution resolution;
    
    /** We map the message's ID to the message rate at its position in time. */
    HashMap<Integer, Integer> messageRates;
    
    /** Messages per timeResolution seconds */
    private float timeResolution; // = 10.0f * 60.0f;
    
    /** 
     * Boundary message rate - completely arbitrary, will be replaced with an actual metric.
     * Units are messages per {@see timeResolution} seconds
     */
    private float rateThreshold; // = 30.0f;
    
    //PROTO
    private float meanMessageRateOverTimeInt = 0.0f;
    private float meanMessageRateWithinSet = 0.0f;
    
    //TODO - this isn't needed anymore
    /**
     * Unparameterized constructor - initializes timeResolution to 10 minutes 
     * and rateThreshold to 30 messages/10 min. These values are arbitrary 
     * but seem to give good results on initial runs.
     */
    public HeatSegmentation() {
        this(0.5f * 60.0f, 2.0f);
    }
    
    /**
     * Parameterized constructor.
     * @param timeResolution The window of time from which message rate will be calculated, in seconds.
     *                       i.e. messages per timeResolution seconds
     * @param rateThreshold The message rate threshold after which segmentation occurs,
     *                       in messages per timeResolution seconds.
     */ 
    public HeatSegmentation(float timeResolution, float rateThreshold) {
        this.timeResolution = timeResolution;
        this.rateThreshold = rateThreshold;
        
        messageRates = new HashMap<Integer, Integer>();
    }
    
    /**
     * Return a list of messages sorted by time (ascending).
     *
     * @param original Unsorted message list
     * @return The sorted message list.
     */
    protected List<Message> sortByTime(List<Message> original) {
        List<Message> messages = new ArrayList<Message>(original);

        Collections.sort(messages, new Comparator<Message>() {
            @Override
            public int compare(Message o1, Message o2) {
                return o1.getTimestamp().compareTo(o2.getTimestamp());
            }
        });

        return messages;
    }
    
    @Override
    public SegmentSet segment(MessageSet messageSet) {
        
        System.out.println("Segmenting by heatmap with a rate threshold of " + rateThreshold 
                + " messages per " + (timeResolution/*/60.0f*/) + " seconds.");
        
        //Sort the message set by time
        List<Message> messages = sortByTime(messageSet.getMessages());
        
        //---
        //Calculate the message rate
        
        int rIndex = 0; //Index of the right message
        int lIndex = 0; //Index of the left message
        
        boolean stepping = false; //@true if the window is currently being adjusted, @false otherwise
        int messageRate = 0; //Current count of messages in the window, aka the rate per @timeResolution
        
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
                //System.out.println(/*"Message rate for " + right.getMessage() + "\n" + */messageRate);
                System.out.println("Timestamp: " + right.getTimestamp() + " | Current rate: " + messageRate);
                
                //PROTO
                meanMessageRateWithinSet += messageRate;
                
                messageRates.put(right.getId(), messageRate);
            }
        }
        
        //PROTO
        int totalMessages = messages.size();
        float timeDifference = (messages.get(totalMessages-1).getTimestamp().getTime() 
                - messages.get(0).getTimestamp().getTime()) / (1000);
        
        meanMessageRateOverTimeInt = ((float) totalMessages) / timeDifference;
        meanMessageRateWithinSet = ((float) meanMessageRateWithinSet)/messages.size();
        
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
            if(/*isLocalMin(m) && */(rate <= rateThreshold && wasAboveThresh) /*|| (rate >= rateThreshold && !wasAboveThresh)*/) { //We cut here
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
        System.out.println("Mean message rate over the entire time interval is " + meanMessageRateOverTimeInt + " messages per second."
                + "\nMean message rate within the set is: " + meanMessageRateWithinSet);
        return segments;
    }
    
    /**
     * Determines if a message is a local minimum.
     * @return @true if the message is a local minimum, @false otherwise.
     */
    private boolean isLocalMin(Message m) {
        int mRate = messageRates.get(m.getId());
        int lRate; int rRate;
        
        try {
            lRate = messageRates.get(m.getId()-1);
            rRate = messageRates.get(m.getId()+1);
            
            return mRate <= lRate && mRate <= rRate;
        } catch(NullPointerException e) {
            //This can only occur at the endpoints of the message set, so we ignore it.
        }
        
        return false;
    }
    
    private float getAvgMessageRate() {
        return meanMessageRateOverTimeInt;
    }
    
    @Override
    public void setSegmentResolution(SegmentResolution resolution) {
        this.resolution = resolution;
    }
}
