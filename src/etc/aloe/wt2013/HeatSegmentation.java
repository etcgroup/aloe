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
    HashMap<Integer, Integer> messageOccurrences;
    
    /** Messages within the last timeWindow seconds */
    private float timeWindow; // = 10.0f * 60.0f;
    
    /** 
     * Boundary message rate - completely arbitrary, will be replaced with an actual metric.
     * Units are messages per timeWindow seconds
     */
    private float occurrenceThreshold; // = 30.0f;
    
    //PROTO
    private float meanMessageRate = 0.0f;
    private float meanMessageOccurrence = 0.0f;
    
    private boolean inferOccurrences = false;
    
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
     * 1-Param constructor. Calculates the mean message occurrence within the given time window
     * and uses this value as the occurrence threshold.
     * @param timeWindow The window of time from which message rate will be calculated, in seconds.
     *                       i.e. messages per timeResolution seconds
     */
    public HeatSegmentation(float timeWindow) {
        this.timeWindow = timeWindow;
        inferOccurrences = true;
    }
    
    /**
     * 2-Param constructor.
     * @param timeWindow The window of time from which message rate will be calculated, in seconds.
     *                       i.e. messages per timeResolution seconds
     * @param occurrenceThreshold The message rate threshold after which segmentation occurs,
     *                       in messages per timeResolution seconds.
     */ 
    public HeatSegmentation(float timeWindow, float occurrenceThreshold) {
        this.timeWindow = timeWindow;
        this.occurrenceThreshold = occurrenceThreshold;
    }
    
    /**
     * Return a list of messages sorted by participant name (ascending).
     *
     * @param original
     * @return
     */
    protected List<Message> sortByParticipant(List<Message> original) {
        List<Message> messages = new ArrayList<Message>(original);

        Collections.sort(messages, new Comparator<Message>() {
            @Override
            public int compare(Message o1, Message o2) {
                return o1.getParticipant().compareTo(o2.getParticipant());
            }
        });

        return messages;
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
    
    /**
     * For each message, calculate the message occurrence within the user-specified time window.
     * This data is contained within a hashmap @see messageOccurrences
     * @param messages messages sorted by time (ascending)
     */
    private void calculateOccurrences(List<Message> messages) {
        messageOccurrences = new HashMap<Integer, Integer>();
        
        int rIndex = 0; //Index of the right message
        int lIndex = 0; //Index of the left message
        
        boolean stepping = false; //@true if the window is currently being adjusted, @false otherwise
        int messageOccurrence = -1; //Current count of messages in the window, aka the occurrence within the time window.
        
        while(rIndex < messages.size()) {
            //Get the messages
            Message left = messages.get(lIndex); 
            Message right = messages.get(rIndex);
            
            //Get the message times
            long rightMsgSeconds = right.getTimestamp().getTime() / 1000;
            long leftMsgSeconds = left.getTimestamp().getTime() / 1000;
            
            //If the left message is out of the window, advance the left pointer
            if((rightMsgSeconds-leftMsgSeconds) > timeWindow) {
                stepping = true;
                
                lIndex++;
                messageOccurrence--;
                //rIndex--;
            } else { //Move the right index
                stepping = false;
                
                rIndex++;
                messageOccurrence++;
            }
            
            if(!stepping) { //Here the message rate is accurate for this iteration
                //System.out.println(/*"Message rate for " + right.getMessage() + "\n" + */messageRate);
                //System.out.println("Timestamp: " + right.getTimestamp() + " | Current occurrences: " + messageOccurrence);
                
                meanMessageOccurrence += messageOccurrence; //PROTO
                messageOccurrences.put(right.getId(), messageOccurrence);
            }
        }
    }
    
    /**
     * Calculate mean values.
     * @param messages messages sorted by time (ascending)
     */
    private void calcMeanValues(List<Message> messages) {
        int totalMessages = messages.size();
        float timeDifference = (messages.get(totalMessages-1).getTimestamp().getTime() 
                - messages.get(0).getTimestamp().getTime()) / (1000);
        
        meanMessageRate = ((float) totalMessages) / timeDifference;
        meanMessageOccurrence = ((float) meanMessageOccurrence)/messages.size();
        
        if(inferOccurrences) { //If specified, use the mean values as the threshold
            occurrenceThreshold = meanMessageOccurrence;
        }
    }
    
    @Override
    public SegmentSet segment(MessageSet messageSet) {
        
        //Sort the message set by time
        List<Message> messages = sortByTime(messageSet.getMessages());
        
        //PROTO - Sort by participant too just for shits and giggles.
        //messages = sortByParticipant(messages);
        
        //Build the occurence count hashmap, if it hasn't been done already
        if(messageOccurrences == null) {
            calculateOccurrences(messages);
        }
        
        calcMeanValues(messages);
        
        //---
        //Begin segmentation
        
        System.out.println("Mean message rate over the entire time interval is " + meanMessageRate + " messages per second."
                + "\nMean message occurrence within the set is " + meanMessageOccurrence 
                + " messages within a " + timeWindow + " second window.");
        
        System.out.println("Segmenting by heatmap with an occurrence threshold of " + occurrenceThreshold 
                + " messages within a " + (timeWindow/*/60.0f*/) + " second window.");
        
        SegmentSet segments = new SegmentSet();
        Segment current = new Segment();
        
        boolean wasAboveThresh = false;
        boolean newSegment = false;
        
        int numLabeled = 0;
        
        //TODO: Check second derivative. This currently cuts right at the threshold.
        for(Message m : messages) {
            int currOccurrences = messageOccurrences.get(m.getId());
            if(currOccurrences > occurrenceThreshold) { //Can't cut without crossing the threshold
                wasAboveThresh = true;
            }
            if(/*isLocalMin(m) && */(currOccurrences <= occurrenceThreshold && wasAboveThresh) /*|| (rate >= rateThreshold && !wasAboveThresh)*/) { //We cut here
                wasAboveThresh = false;
                newSegment = true;
            }
            
            //Begin blatant copy-paste
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
        
        if (current.getMessages().size() > 0) {
            if (this.resolution != null) {
                current.setTrueLabel(this.resolution.resolveLabel(current));
                if (current.hasTrueLabel()) {
                    numLabeled++;
                }
            }
            segments.add(current);
        } //End blatant copy-paste
        
        System.out.println("Grouped messages into " + segments.size() + " segments (" + numLabeled + " labeled).");
        
        return segments;
    }
    
    /**
     * Determines if a message is a local minimum.
     * @return @true if the message is a local minimum, @false otherwise.
     */
    private boolean isLocalMin(Message m) {
        int mRate = messageOccurrences.get(m.getId());
        int lRate; int rRate;
        
        try {
            lRate = messageOccurrences.get(m.getId()-1);
            rRate = messageOccurrences.get(m.getId()+1);
            
            return mRate <= lRate && mRate <= rRate;
        } catch(NullPointerException e) {
            //This can only occur at the endpoints of the message set, so we ignore it.
        }
        
        return false;
    }
    
    private float getMeanMessageRate() {
        return meanMessageRate;
    }
    
    private float getMeanMessageOccurrence() {
        return meanMessageOccurrence;
    }
    
    @Override
    public void setSegmentResolution(SegmentResolution resolution) {
        this.resolution = resolution;
    }
}
