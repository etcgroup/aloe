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
package etc.aloe.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Stores a list of LabeledMessages, associated with an unique ID for the
 * segment (auto-increment, zero-index)
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class Segment implements LabelableItem {

    private static int ID_COUNTER = 0;
    private int id;
    private List<Message> messages;
    private Boolean trueLabel;
    private Boolean predictedLabel;
    private Double predictionConfidence;

    /**
     * Construct an empty, unlabeled segment.
     */
    public Segment() {
        messages = new ArrayList<Message>();
        id = ID_COUNTER;
        ID_COUNTER++;
    }

    /**
     * Convenience method, mostly for testing.
     *
     * @param trueLabel
     * @param predictedLabel
     */
    public Segment(Boolean trueLabel, Boolean predictedLabel) {
        this();
        this.trueLabel = trueLabel;
        this.predictedLabel = predictedLabel;
    }

    /**
     * Get the segment's id.
     *
     * @return
     */
    public int getId() {
        return id;
    }

    /**
     * Concatenate the message strings with a space separator.
     *
     * @return
     */
    public String concatMessages() {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Message item : getMessages()) {
            if (first) {
                first = false;
            } else {
                sb.append(" ");
            }
            sb.append(item.getMessage());
        }
        return sb.toString();
    }
    
    /**
     * Concatenate the participant strings with a space separator.
     *
     * @return
     */
    public String concatParticipants() {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Message item : getMessages()) {
            if (first) {
                first = false;
            } else {
                sb.append(" ");
            }
            sb.append(item.getParticipant());
        }
        return sb.toString();
    }

    /**
     * Add a message to the segment.
     *
     * @param message
     */
    public void add(Message message) {
        messages.add(message);
        message.setSegmentId(this.id);
    }

    /**
     * Get the underlying message list.
     *
     * @return
     */
    public List<Message> getMessages() {
        return messages;
    }

    @Override
    public Boolean getTrueLabel() {
        return trueLabel;
    }

    @Override
    public void setTrueLabel(Boolean truth) {
        this.trueLabel = truth;
    }

    @Override
    public boolean hasTrueLabel() {
        return trueLabel != null;
    }

    @Override
    public Boolean getPredictedLabel() {
        return predictedLabel;
    }

    @Override
    public void setPredictedLabel(Boolean prediction) {
        this.predictedLabel = prediction;
    }

    @Override
    public boolean hasPredictedLabel() {
        return predictedLabel != null;
    }

    /**
     * Get the duration of the segment in seconds. If the segment is empty,
     * returns 0.
     *
     * @return
     */
    public double getDurationInSeconds() {
        if (messages.isEmpty()) {
            return 0;
        }

        Date start = messages.get(0).getTimestamp();
        Date stop = messages.get(messages.size() - 1).getTimestamp();

        return 1 + (stop.getTime() - start.getTime()) / 1000.0;
    }

    @Override
    public Double getPredictionConfidence() {
        return predictionConfidence;
    }

    @Override
    public void setPredictionConfidence(Double predictionConfidence) {
        this.predictionConfidence = predictionConfidence;
    }

    @Override
    public boolean hasPredictionConfidence() {
        return predictionConfidence != null;
    }
}
