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

import java.util.Date;

/**
 * Stores messages with id, participant, timestamp, and (optionally) labels.
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class Message implements Comparable<Message>, LabelableItem {

    private final int id;
    private final Date timestamp;
    private final String participant;
    private final String message;
    private Label trueLabel;
    private Label predictedLabel = null;
    private Double predictionConfidence = Double.NaN;
    private int segmentId = -1;

    /**
     * Construct a new un-labeled message.
     *
     * @param id
     * @param timestamp
     * @param participant
     * @param message
     */
    public Message(
            int id,
            Date timestamp,
            String participant,
            String message) {
        this(id, timestamp, participant, message, null, null, -1);
    }

    /**
     * Construct a new message. Leave trueLabel null if unlabeled.
     *
     * @param id
     * @param timestamp
     * @param participant
     * @param message
     * @param trueLabel
     */
    public Message(
            int id,
            Date timestamp,
            String participant,
            String message,
            Label trueLabel) {
        this(id, timestamp, participant, message, trueLabel, null, -1);
    }

    /**
     * Construct a new message. Leave trueLabel and predictedLabel null if
     * unlabeled.
     *
     * @param id
     * @param timestamp
     * @param participant
     * @param message
     * @param trueLabel
     * @param predictedLabel
     */
    public Message(
            int id,
            Date timestamp,
            String participant,
            String message,
            Label trueLabel,
            Label predictedLabel) {
        this(id, timestamp, participant, message, trueLabel, predictedLabel, -1);
    }

    /**
     * Construct a new message. Leave trueLabel and predictedLabel null if
     * unlabeled. Leave segmentId -1 if not segmented.
     *
     * @param id
     * @param timestamp
     * @param participant
     * @param message
     * @param trueLabel
     * @param predictedLabel
     * @param segmentId
     */
    public Message(
            int id,
            Date timestamp,
            String participant,
            String message,
            Label trueLabel,
            Label predictedLabel,
            int segmentId) {

        this.id = id;
        this.timestamp = timestamp;
        this.participant = participant;
        this.message = message;
        this.trueLabel = trueLabel;
        this.predictedLabel = predictedLabel;
        this.segmentId = segmentId;
    }

    /**
     * Get the message's id.
     *
     * @return
     */
    public int getId() {
        return id;
    }

    /**
     * Get the message string.
     *
     * @return
     */
    public String getMessage() {
        return message;
    }

    /**
     * Get the time the message occurred.
     *
     * @return
     */
    public Date getTimestamp() {
        return timestamp;
    }

    /**
     * Get the name of the person who sent the message.
     *
     * @return
     */
    public String getParticipant() {
        return participant;
    }

    @Override
    public Label getTrueLabel() {
        return trueLabel;
    }

    @Override
    public void setTrueLabel(Label truth) {
        this.trueLabel = truth;
    }

    @Override
    public boolean hasTrueLabel() {
        return trueLabel != null;
    }

    @Override
    public Label getPredictedLabel() {
        return predictedLabel;
    }

    @Override
    public void setPredictedLabel(Label prediction) {
        this.predictedLabel = prediction;
    }

    @Override
    public boolean hasPredictedLabel() {
        return predictedLabel != null;
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

    @Override
    public int compareTo(Message o) {
        return getTimestamp().compareTo(o.getTimestamp());
    }

    /**
     * Get the id of the segment this message is assigned to. If not set,
     * returns -1.
     *
     * @return
     */
    public int getSegmentId() {
        return segmentId;
    }

    /**
     * True if the message has been assigned to a segment.
     *
     * @return
     */
    public boolean hasSegmentId() {
        return segmentId != -1;
    }

    /**
     * Set the message's assigned segment.
     *
     * @param segId
     */
    public void setSegmentId(int segId) {
        this.segmentId = segId;
    }
}
