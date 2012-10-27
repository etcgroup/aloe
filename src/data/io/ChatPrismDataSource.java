/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data.io;

import daisy.io.DB;
import data.EntityMetaData;
import data.EntitySet;
import data.MultiRatedEntity;
import data.Rating;
import data.indexes.CodeNames;
import data.indexes.ParticipantNames;
import data.indexes.UserNames;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class ChatPrismDataSource implements DataSource {

    private String host;
    private String schema;
    private String username;
    private String password;
    protected DB db;
    private String codeFilter;
    private String userFilter;
    private int segmentationId;
    private String messageFilter;
    private boolean verbose = false;
    private int codeSchemaId;
    private String segmentFilter;
    private final String name;
    private String instanceFilter;
    private List<String> testSetDates;

    public ChatPrismDataSource(String name) {
        this.name = name;
    }

    public String getInstanceFilter() {
        return instanceFilter;
    }

    public void setInstanceFilter(String instanceFilter) {
        this.instanceFilter = instanceFilter;
    }

    @Override
    public void setTestSetDates(List<String> testSetDates) {
        this.testSetDates = testSetDates;
    }

    public List<String> getTestSetDates() {
        return testSetDates;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDatabaseSchema() {
        return schema;
    }

    public void setDatabaseSchema(String schema) {
        this.schema = schema;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String user) {
        this.username = user;
    }

    @Override
    public void initialize() {
        db = new DB(getHost(), getDatabaseSchema(), getUsername(), getPassword());
    }

    @Override
    public void loadIndexes() {
        String userQuery = "SELECT id, name FROM users ";
        if (this.userFilter != null) {
            userQuery += "WHERE " + this.userFilter;
        }

        verbose(userQuery);

        db.statement("users", userQuery, DB.FLAT);
        List users = (List) db.execute("users");
        for (int i = 0; i < users.size(); i++) {
            List row = (List) users.get(i);
            int userId = (Integer) row.get(0);
            String name = (String) row.get(1);

            UserNames.instance.put(userId, name);
        }

        String codeQuery = "SELECT id, name FROM coding_codes ";

        verbose(codeQuery);

        db.statement("codes", codeQuery, DB.FLAT);
        List codes = (List) db.execute("codes");
        for (int i = 0; i < codes.size(); i++) {
            List row = (List) codes.get(i);
            int codeId = (Integer) row.get(0);
            String name = (String) row.get(1);

            CodeNames.instance.put(codeId, name);
        }


        String participantQuery = "SELECT id, name FROM data_participants ";

        verbose(participantQuery);

        db.statement("participants", participantQuery, DB.FLAT);
        List participants = (List) db.execute("participants");
        for (int i = 0; i < participants.size(); i++) {
            List row = (List) participants.get(i);
            int participantId = (Integer) row.get(0);
            String name = (String) row.get(1);

            ParticipantNames.instance.put(participantId, name);
        }
    }

    protected void verbose(String message) {
        if (this.verbose) {
            System.out.println(message);
        }
    }

    private List getSegments() {
        if (this.segmentationId <= 0) {
            throw new IllegalStateException("Segmentation scheme id has not been set!");
        }

        String segmentsQuery = "SELECT seg_id AS id, time, participant_id, message, data_points.id as message_id";
        segmentsQuery += " FROM seg_segments";
        segmentsQuery += " JOIN data_points ON data_points.id = seg_segments.data_point_id";
        segmentsQuery += " WHERE seg_segments.seg_scheme_id = ?";
        if (this.messageFilter != null) {
            segmentsQuery += " AND " + this.messageFilter;
        }

        verbose(segmentsQuery);

        db.statement("segments", segmentsQuery, DB.FLAT);

        List segments = (List) db.execute("segments", segmentationId);
        return segments;
    }

    protected List getDataPoints() {
        String segmentsQuery = "SELECT id, time, participant_id, message, data_points.id as message_id";
        segmentsQuery += " FROM data_points";
        if (this.messageFilter != null) {
            segmentsQuery += " WHERE " + this.messageFilter;
        }

        verbose(segmentsQuery);

        db.statement("datapoints", segmentsQuery, DB.FLAT);

        List segments = (List) db.execute("datapoints");
        return segments;
    }

    private List getCodeInstances() {
        if (this.codeSchemaId <= 0) {
            throw new IllegalStateException("Code schema id has not been set!");
        }

        String instancesQuery = "SELECT message_id, user_id, code_id";
        instancesQuery += " FROM coding_instances";
        instancesQuery += " JOIN coding_codes ON coding_codes.id = coding_instances.code_id";
        instancesQuery += " WHERE coding_codes.schema_id = ?";
        if (this.codeFilter != null) {
            instancesQuery += " AND " + codeFilter;
        }
        if (this.instanceFilter != null) {
            instancesQuery += " AND " + instanceFilter;
        }

        verbose(instancesQuery);

        db.statement("instances", instancesQuery, DB.FLAT);

        List instances = (List) db.execute("instances", this.codeSchemaId);
        return instances;
    }

    protected HashMap<Integer, ArrayList<Rating>> getRatingsByMessage() {
        List instances = getCodeInstances();

        System.out.println("Retrieved " + instances.size() + " instances.");
        HashMap<Integer, ArrayList<Rating>> ratingsByMessage = new HashMap<Integer, ArrayList<Rating>>();
        for (int i = 0; i < instances.size(); i++) {
            List instance = (List) instances.get(i);
            int messageId = (Integer) instance.get(0);
            int userId = (Integer) instance.get(1);
            int codeId = (Integer) instance.get(2);
            Rating rating = new Rating(userId, codeId);

            ArrayList<Rating> ratings = ratingsByMessage.get(messageId);
            if (ratings == null) {
                ratings = new ArrayList<Rating>();
                ratingsByMessage.put(messageId, ratings);
            }

            ratings.add(rating);
        }
        System.out.println("Processed " + instances.size() + " instances on " + ratingsByMessage.size() + " messages");

        return ratingsByMessage;
    }

    @Override
    public EntitySet getData() {

        EntitySet.clearMetaData();
        EntitySet entities = new EntitySet(this.name);

        //Get all the ratings organized by message
        HashMap<Integer, ArrayList<Rating>> ratingsByMessage = getRatingsByMessage();

        List segments = null;

        if (this.segmentationId > 0) {
            segments = getSegments();
        } else {
            segments = getDataPoints();
        }

        System.out.println("Retrieved " + segments.size() + " segment entries...");

        HashMap<Integer, MultiRatedEntity> entityById = new HashMap<Integer, MultiRatedEntity>();
        for (int i = 0; i < segments.size(); i++) {
            List row = (List) segments.get(i);

            int id = (Integer) row.get(0);
            Timestamp time = (Timestamp) row.get(1);
            int participantId = (Integer) row.get(2);
            String message = (String) row.get(3);
            int messageId = (Integer) row.get(4);

            MultiRatedEntity entity = entityById.get(id);
            EntityMetaData meta = entities.getMetaData(id);
            if (entity == null) {
                boolean testSet = isTestSetDate(time);
                
                entity = new MultiRatedEntity(id, testSet);
                meta = new EntityMetaData(id);

                entityById.put(id, entity);
                entities.add(entity);
                entities.addMetaData(meta);
            }

            meta.add(time, participantId, message);

            //Add all the ratings for this message
            ArrayList<Rating> ratings = ratingsByMessage.get(messageId);
            if (ratings != null) {
                entity.addAllRatings(ratings);
            }
        }
        System.out.println("Processed " + entities.size() + " entities.");

        entities.finishMetadata();

        return entities;
    }

    @Override
    public void setMessageFilter(String filter) {
        this.messageFilter = filter;
    }

    @Override
    public void setSegmentationId(int segId) {
        this.segmentationId = segId;
    }

    @Override
    public void setCodeFilter(String filter) {
        this.codeFilter = filter;
    }

    @Override
    public void setUserFilter(String filter) {
        this.userFilter = filter;
    }

    @Override
    public void setCodeSchemaId(int schemaId) {
        this.codeSchemaId = schemaId;
    }

    @Override
    public void setSegmentFilter(String filter) {
        this.segmentFilter = filter;
    }

    public int getSegmentationId() {
        return segmentationId;
    }

    public String getName() {
        return name;
    }

    public String getMessageFilter() {
        return messageFilter;
    }

    protected boolean isTestSetDate(Timestamp mainTimestamp) {
        boolean testSet = false;
        if (testSetDates != null) {
            String datePart = mainTimestamp.toString();
            datePart = datePart.split(" ")[0];
            for (int t = 0; t < testSetDates.size(); t++) {
                String testDate = testSetDates.get(t);
                if (datePart.equals(testDate)) {
                    testSet = true;
                    break;
                }
            }
        }
        return testSet;
    }
}
