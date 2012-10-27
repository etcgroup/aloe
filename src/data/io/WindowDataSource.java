/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data.io;

import daisy.io.DB;
import data.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class WindowDataSource extends ChatPrismDataSource {

    private int maxWindowSize = 10;

    public WindowDataSource(String name) {
        super(name);
    }

    public int getMaxWindowSize() {
        return maxWindowSize;
    }

    public void setMaxWindowSize(int maxWindowSize) {
        this.maxWindowSize = maxWindowSize;
    }

    @Override
    protected List getDataPoints() {
        String segmentsQuery = "SELECT time, participant_id, message, data_points.id as message_id";
        segmentsQuery += " FROM data_points";
        if (this.getMessageFilter() != null) {
            segmentsQuery += " WHERE " + this.getMessageFilter();
        }
        segmentsQuery += " ORDER BY time ASC";

        verbose(segmentsQuery);

        db.statement("datapoints", segmentsQuery, DB.FLAT);

        List segments = (List) db.execute("datapoints");
        return segments;
    }

    @Override
    public EntitySet getData() {

        EntitySet.clearMetaData();
        EntitySet entities = new EntitySet(this.getName());

        //Get all the ratings organized by message
        HashMap<Integer, ArrayList<Rating>> ratingsByMessage = getRatingsByMessage();

        List messages = getDataPoints();

        System.out.println("Retrieved " + messages.size() + " messages...");

        int totalWindowSize = getMaxWindowSize();

        int entityId = 1;
        for (int i = 0; i < messages.size(); i++) {

            Timestamp mainTimestamp = (Timestamp) ((List) messages.get(i)).get(0);

            int windowStart = Math.max(0, i - totalWindowSize + 1);
            int windowStop = i;

            boolean testSet = isTestSetDate(mainTimestamp);

            MultiRatedEntity entity = new MultiRatedEntity(entityId++, testSet);
            EntityMetaData meta = new EntityMetaData(entity.getEntityId());

            //Now get the messages
            int numRatingsIncluded = 0;
            for (int s = windowStart; s <= windowStop; s++) {
                List row = (List) messages.get(s);

                Timestamp time = (Timestamp) row.get(0);
                int participantId = (Integer) row.get(1);
                String message = (String) row.get(2);
                int messageId = (Integer) row.get(3);

                ArrayList<Rating> ratings = ratingsByMessage.get(messageId);
                MessageUnit messageUnit = new MessageUnit(time, participantId, message);

                meta.add(messageUnit);
                //If we could still add more ratings
                if (ratings != null) {
                    entity.addAllRatings(ratings);
                }
            }

            entities.add(entity);
            entities.addMetaData(meta);
        }

        System.out.println("Processed " + entities.size() + " entities.");

        entities.finishMetadata();

        return entities;
    }

}
