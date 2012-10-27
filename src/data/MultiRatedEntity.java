/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import java.util.*;

/**
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class MultiRatedEntity {

    private final ArrayList<Rating> ratings = new ArrayList<Rating>();
    
    private final int entityId;
    private int numRaters = 0;
    private Set<Integer> distinctUsers = new HashSet<Integer>();
    private Set<Integer> distinctCodes = new HashSet<Integer>();
    private final boolean original;
    private final boolean testSet;
    
    public MultiRatedEntity(int entityId, boolean testSet) {
        this.entityId = entityId;
        this.original = true;
        this.testSet = testSet;
    }

    
    
    public MultiRatedEntity(MultiRatedEntity entity) {
        this.entityId = entity.getEntityId();
        this.numRaters = entity.getTotalRaters();
        this.original = false;
        this.testSet = entity.testSet;
    }

    public boolean isTestSet() {
        return testSet;
    }

    
    
    public boolean isOriginal() {
        return original;
    }

    public ArrayList<Rating> getRatings() {
        return ratings;
    }
    
    public int countRatings() {
        return ratings.size();
    }
    
    /**
     * Adds a rating to this entity.
     * @param userId
     * @param codeId 
     */
    public void addRating(int userId, int codeId) {
        ratings.add(new Rating(userId, codeId));
        this.distinctUsers.add(userId);
        this.distinctCodes.add(codeId);
        
        if (this.original) {
            numRaters = distinctUsers.size();
        }
    }
    
    public void addRating(Rating rating) {
        ratings.add(rating);
        this.distinctUsers.add(rating.getUserId());
        this.distinctCodes.add(rating.getCodeId());
        
        if (this.original) {
            numRaters = distinctUsers.size();
        }
    }
    
    
    public void addAllRatings(ArrayList<Rating> ratings) {
        for (Rating rating : ratings) {
            addRating(rating);
        }
    }

    /**
     * Gets the entity id
     * @return 
     */
    public int getEntityId() {
        return entityId;
    }

    /**
     * Gets the set of codes applied by the given user
     * @param userId
     * @return the set of codes
     */
    public Set<Integer> getCodesByUser(int userId) {
        Set<Integer> result = new HashSet<Integer>();
        for (Rating rating : ratings) {
            if (rating.getUserId() == userId) {
                result.add(rating.getCodeId());
            }
        }
        return result;
    }

    /**
     * Counts the number of codes applied by the given user
     * @param userId
     * @return the number of codes
     */
    public int countCodesByUser(int userId) {
        int result = 0;
        for (Rating rating : ratings) {
            if (rating.getUserId() == userId) {
                result++;
            }
        }
        return result;
    }

    /**
     * Counts the number of distinct raters
     * @return the number of raters
     */
    public int countDistinctUsers() {
        return distinctUsers.size();
    }

    /**
     * Counts the number of distinct codes applied
     * @return the number of codes
     */
    public int countDistinctCodes() {
        return distinctCodes.size();
    }
    
    /**
     * Gets the set of distinct users for this entity.
     * @return 
     */
    public Set<Integer> getDistinctUsers() {
        return distinctUsers;
    }
    
    /**
     * Gets the set of distinct codes for this entity.
     * @return 
     */
    public Set<Integer> getDistinctCodes() {
        return distinctCodes;
    }

    /**
     * Gets the set of users who applied the given code.
     * @param codeId
     * @return the set of users
     */
    public Set<Integer> getUsersByCode(int codeId) {
        Set<Integer> result = new HashSet<Integer>();
        for (Rating rating : ratings) {
            if (rating.getCodeId() == codeId) {
                result.add(rating.getUserId());
            }
        }
        return result;
    }

    /**
     * Counts the users who applied the given code.
     * @param codeId
     * @return the number of users
     */
    public int countUsersByCode(int codeId) {
        int result = 0;
        for (Rating rating : ratings) {
            if (rating.getCodeId() == codeId) {
                result++;
            }
        }
        return result;
    }

    /**
     * Determines the code that was applied most frequently.
     * @return the code id
     */
    public int majorityCodeId() {
        HashMap<Integer, Integer> codeVotes = new HashMap<Integer, Integer>();

        int majorityCodeId = -1;
        int majorityCount = -1;
        for (Rating rating : ratings) {
            int codeId = rating.getCodeId();
            if (!codeVotes.containsKey(codeId)) {
                codeVotes.put(codeId, 1);
                if (1 > majorityCount) {
                    majorityCount = 1;
                    majorityCodeId = codeId;
                }
            } else {
                int count = codeVotes.get(codeId) + 1;
                codeVotes.put(codeId, count);
                if (count > majorityCount) {
                    majorityCount = count;
                    majorityCodeId = codeId;
                }
            }
        }

        return majorityCodeId;
    }

    /**
     * Determines the percent of coders (who rated this entity at all) who 
     * applied the given code.
     * @param codeId
     * @return the percent
     */
    public double getPercentWhoApplied(int codeId) {
        double result = countUsersByCode(codeId);
        result /= countDistinctUsers();
        return result;
    }

    /**
     * For every code applied, calculates the percent of raters who applied it
     * (out of all the raters who rated this message at all).
     * @return a map from code ids to percentages
     */
    public HashMap<Integer, Double> getPercentApplied() {
        HashMap<Integer, Double> codePercents = new HashMap<Integer, Double>();
        for (Rating rating : ratings) {
            int codeId = rating.getCodeId();
            if (!codePercents.containsKey(codeId)) {
                codePercents.put(codeId, 1.0);
            } else {
                double count = codePercents.get(codeId) + 1;
                codePercents.put(codeId, count);
            }
        }

        //Divide each by total raters
        int totalRaters = this.countDistinctUsers();
        for (Map.Entry<Integer, Double> entry : codePercents.entrySet()) {
            entry.setValue(entry.getValue() / totalRaters);
        }

        return codePercents;
    }

    /**
     * Determines the set of code ids which were rated by at least
     * the given percentage of raters (out of all those who rated this message at all).
     * @param minAppliedPercent
     * @return the set of code ids
     */
    public Set<Integer> getCodesWithMinPercentApplied(double minAppliedPercent) {
        HashMap<Integer, Double> codePercents = this.getPercentApplied();
        HashSet<Integer> selectedCodes = new HashSet<Integer>();
        for (Map.Entry<Integer, Double> entry : codePercents.entrySet()) {
            if (entry.getValue() >= minAppliedPercent) {
                selectedCodes.add(entry.getKey());
            }
        }
        return selectedCodes;
    }

    /**
     * Gets the number of people that originally rated this entity, regardless
     * of any post-processing.
     * @return 
     */
    public int getTotalRaters() {
        return numRaters;
    }
}
