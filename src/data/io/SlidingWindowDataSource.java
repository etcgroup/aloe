package data.io;

import etc.aloe.data.LabeledMessage;
import daisy.io.DB;
import data.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * This provides the ability to read 
 * Although arguably t
 * 
 * @version 1.0
 */
public class SlidingWindowDataSource extends ChatPrismDataSource {

    private int maxWindowSize = 10;
    private double maxWindowDuration = 300;//5 minutes in seconds
    private int ratingsBasis = 1;
    private boolean prescient = false;

    public SlidingWindowDataSource(String name) {
        super(name);
    }

    public int getMaxWindowSize() {
        return maxWindowSize;
    }

    public void setMaxWindowSize(int maxWindowSize) {
        this.maxWindowSize = maxWindowSize;
    }

    public double getMaxWindowDuration() {
        return maxWindowDuration;
    }

    public void setMaxWindowDuration(double seconds) {
        this.maxWindowDuration = seconds;
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
        double totalWindowDuration = getMaxWindowDuration();

        //Default to no front window
        int backWindowSize = totalWindowSize - 1;
        double backWindowDuration = totalWindowDuration;
        int frontWindowSize = 0;
        double frontWindowDuration = 0;
        int numRatingsToInclude = getRatingsBasis();

        if (isPrescient()) {
            frontWindowSize = (totalWindowSize - 1) / 2;
            backWindowSize = totalWindowSize - 1 - frontWindowSize;
            frontWindowDuration = totalWindowDuration / 2;
            backWindowDuration = totalWindowDuration / 2;
        }

        int entityId = 1;
        for (int i = 0; i < messages.size(); i++) {

            Timestamp mainTimestamp = (Timestamp) ((List) messages.get(i)).get(0);
            long mainTime = mainTimestamp.getTime();

            int windowStart = i;
            int windowStop = i;
            for (int s = i - 1; s >= 0; s--) {
                Timestamp time = (Timestamp) ((List) messages.get(s)).get(0);
                double backQueueDuration = (mainTime - time.getTime()) / 1000.0;
                int backSize = i - windowStart;
                if (backQueueDuration < backWindowDuration && backSize < backWindowSize) {
                    windowStart--;
                } else {
                    break;
                }
            }
            for (int s = i + 1; s < messages.size(); s++) {
                Timestamp time = (Timestamp) ((List) messages.get(s)).get(0);
                double frontQueueDuration = (time.getTime() - mainTime) / 1000.0;
                int frontSize = windowStop - i;
                if (frontQueueDuration < frontWindowDuration && frontSize < frontWindowSize) {
                    windowStop++;
                } else {
                    break;
                }
            }

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
                LabeledMessage messageUnit = new LabeledMessage(time, participantId, message);

                meta.add(messageUnit);
                //If we could still add more ratings
                if (ratings != null && numRatingsIncluded < numRatingsToInclude) {
                    //If this rating is close to the core
                    if (2 * Math.abs(s - i) <= numRatingsToInclude) {
                        entity.addAllRatings(ratings);
                        numRatingsIncluded++;
                    }
                }
            }

            entities.add(entity);
            entities.addMetaData(meta);
        }

        System.out.println("Processed " + entities.size() + " entities.");

        entities.finishMetadata();

        return entities;
    }

    public void setRatingsBasis(int ratingsBasis) {
        this.ratingsBasis = ratingsBasis;
    }

    public int getRatingsBasis() {
        return ratingsBasis;
    }

    public void setPrescient(boolean prescient) {
        this.prescient = prescient;
    }

    public boolean isPrescient() {
        return prescient;
    }

}
