package data;

import java.util.*;

/**
 *
 * @version 1.0
 */
public class EntitySet extends ArrayList<MultiRatedEntity> {

    public static void clearMetaData() {
        metaData.clear();
    }

    private final String facet;
    private Set<Integer> allUsers = new HashSet<Integer>();
    private Set<Integer> allCodes = new HashSet<Integer>();
    private static HashMap<Integer, EntityMetaData> metaData = new HashMap<Integer, EntityMetaData>();
    private int allRatingsCount = 0;
    
    
    public EntitySet() {
        this.facet = "all";
    }

    public EntitySet(String facet) {
        this.facet = facet;
    }
    
    public String getName() {
        return facet;
    }

    public void addMetaData(EntityMetaData metaData) {
        EntitySet.metaData.put(metaData.getEntityId(), metaData);
    }

    public EntityMetaData getMetaData(int entityId) {
        return EntitySet.metaData.get(entityId);
    }

    public EntityMetaData getMetaData(MultiRatedEntity entity) {
        return getMetaData(entity.getEntityId());
    }

    /**
     * Generates current lists of users and codes present in the data set.
     */
    public void takeStock() {
        allUsers.clear();
        allCodes.clear();
        allRatingsCount = 0;
        
        for (MultiRatedEntity entity : this) {
            allRatingsCount += entity.countRatings();
            
            for (Rating rating : entity.getRatings()) {
                allUsers.add(rating.getUserId());
                allCodes.add(rating.getCodeId());
            }
        }
    }
    
    public Set<Integer> getAllUserIds() {
        return allUsers;
    }

    public Set<Integer> getAllCodeIds() {
        return allCodes;
    }
    
    public int countAllRatings() {
        return allRatingsCount;
    }

    public static EntitySet constructTestData(int[][][] testData) {
        EntitySet testDataSet = new EntitySet();
        for (int id = 0; id < testData.length; id++) {
            MultiRatedEntity entity = new MultiRatedEntity(id, false);
            for (int r = 0; r < testData[id].length; r++) {
                entity.addRating(testData[id][r][0], testData[id][r][1]);
            }
            testDataSet.add(entity);
        }
        return testDataSet;
    }

    public void finishMetadata() {
        for (Map.Entry<Integer, EntityMetaData> entry : metaData.entrySet()) {
            entry.getValue().done();
        }
    }

}
