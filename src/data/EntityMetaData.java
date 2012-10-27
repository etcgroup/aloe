/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import data.indexes.ParticipantNames;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class EntityMetaData extends ArrayList<MessageUnit> {
    private final int entityId;
    private boolean done = false;
    private Set<Integer> distinctParticipants = new HashSet<Integer>();
    
    public EntityMetaData(int entityId) {
        this.entityId = entityId;
    }

    public int getEntityId() {
        return entityId;
    }

    public void add(Timestamp time, int participantId, String message) {
        this.add(new MessageUnit(time, participantId, message));
        this.distinctParticipants.add(participantId);
    }

    @Override
    public boolean add(MessageUnit e) {
        this.distinctParticipants.add(e.getParticipantId());
        return super.add(e);
    }
    
    
    
    public void done() {
        if (done) {
            throw new IllegalStateException("EntityMetaData was already marked done");
        }
        
        this.done = true;
        
        //Sort the messages by time
        Collections.sort(this);
        this.trimToSize();
    }
    
    public Timestamp getStartTime() {
        if (!done) {
            throw new IllegalStateException("Cannot call getStartTime() before calling done()");
        }
        MessageUnit first = this.get(0);
        return first.getTime();
    }
    
    public Timestamp getStopTime() {
        if (!done) {
            throw new IllegalStateException("Cannot call getStopTime() before calling done()");
        }
        
        MessageUnit last = this.get(this.size() - 1);
        return last.getTime();
    }
    
    public double getDurationInSeconds() {
        Timestamp start = getStartTime();
        Timestamp stop = getStopTime();
        
        return 1 + (stop.getTime() - start.getTime()) / 1000.0;
    }
    
    public Set<Integer> getDistinctParticipants() {
        return distinctParticipants;
    }

    public String concatMessages() {
        return concatMessages(" ");
    }
    public String concatMessages(String separator) {
        StringBuilder combined = new StringBuilder();
        for (int i = 0; i < this.size(); i++) {
            combined.append(this.get(i).getMessage());
            if (i < this.size() - 1) {
                combined.append(separator);
            }
        }
        
        String combinedString = combined.toString();
        return combinedString;
    }

    public String getParticipantNames() {
        StringBuilder combined = new StringBuilder();
        for (int i = 0; i < this.size(); i++) {
            combined.append(ParticipantNames.instance.get(this.get(i).getParticipantId()));
            if (i < this.size() - 1) {
                combined.append(", ");
            }
        }
        
        String combinedString = combined.toString();
        return combinedString;
    }
    
}
