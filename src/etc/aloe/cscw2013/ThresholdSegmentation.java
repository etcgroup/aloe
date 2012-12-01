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

import etc.aloe.data.Message;
import etc.aloe.data.MessageSet;
import etc.aloe.data.Segment;
import etc.aloe.data.SegmentSet;
import etc.aloe.processes.SegmentResolution;
import etc.aloe.processes.Segmentation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Segments messages according to a time threshold. Messages separated by more
 * than the threshold go into different segments. Can optionally separate
 * messages by participant as well.
 *
 * In order for labels to be applied to segments, be sure to set the segment
 * resolution procedure.
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class ThresholdSegmentation implements Segmentation {

    private final int thresholdSeconds;
    private final boolean byParticipant;
    private SegmentResolution resolution;

    /**
     * Construct a new ThresholdSegmentation.
     *
     * @param thresholdSeconds The number of segments to use as the segmentation
     * threshold.
     * @param byParticipant True if messages should be separated by participant.
     */
    public ThresholdSegmentation(int thresholdSeconds, boolean byParticipant) {
        this.thresholdSeconds = thresholdSeconds;
        this.byParticipant = byParticipant;
    }

    /**
     * Return a list of messages sorted by participant name (ascending).
     *
     * @param original
     * @return
     */
    private List<Message> sortByParticipant(List<Message> original) {
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
     * @param original
     * @return
     */
    private List<Message> sortByTime(List<Message> original) {
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
        System.out.println("Segmenting with " + thresholdSeconds + " second threshold," + (byParticipant ? "" : " not") + " separating by participant.");
        List<Message> messages = sortByTime(messageSet.getMessages());
        if (byParticipant) {
            messages = sortByParticipant(messages);
        }

        SegmentSet segments = new SegmentSet();

        Segment current = new Segment();
        long lastTime = 0;
        String lastParticipant = null;

        int numLabeled = 0;
        for (Message message : messages) {
            long msgSeconds = message.getTimestamp().getTime() / 1000;
            long diffSeconds = (msgSeconds - lastTime);

            boolean newSegment = false;
            if (lastTime > 0 && diffSeconds > thresholdSeconds) {
                newSegment = true;
            }
            if (byParticipant && lastParticipant != null && !lastParticipant.equals(message.getParticipant())) {
                newSegment = true;
            }

            if (newSegment) {
                if (this.resolution != null) {
                    current.setTrueLabel(this.resolution.resolveLabel(current));
                    if (current.hasTrueLabel()) {
                        numLabeled++;
                    }
                }
                segments.add(current);
                current = new Segment();
            }

            lastTime = msgSeconds;
            lastParticipant = message.getParticipant();
            current.add(message);
        }

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

    @Override
    public void setSegmentResolution(SegmentResolution resolution) {
        this.resolution = resolution;
    }
}
