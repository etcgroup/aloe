/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import java.sql.Timestamp;

/**
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class MessageUnit implements Comparable<MessageUnit> {

    private final Timestamp time;
    private final int participantId;
    private final String message;

    public MessageUnit(Timestamp time, int participantId, String message) {
        this.time = time;
        this.participantId = participantId;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public int getParticipantId() {
        return participantId;
    }

    public Timestamp getTime() {
        return time;
    }

    @Override
    public int compareTo(MessageUnit o) {
        return time.compareTo(o.time);
    }
}
