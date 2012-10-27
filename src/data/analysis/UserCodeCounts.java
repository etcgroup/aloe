/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data.analysis;

import daisy.io.CSV;
import data.EntitySet;
import data.MultiRatedEntity;
import data.indexes.CodeNames;
import data.indexes.UserNames;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class UserCodeCounts implements Analysis<EntitySet> {

    public static class Result implements AnalysisResult {

        private final int numUsers;
        private final int numCodes;
        private final int userTotalColumn;
        private final int codeTotalRow;
        private HashMap<Integer, Integer> codeIdToIndex = new HashMap<Integer, Integer>();
        private HashMap<Integer, Integer> indexToCodeId = new HashMap<Integer, Integer>();
        private HashMap<Integer, Integer> userIdToIndex = new HashMap<Integer, Integer>();
        private HashMap<Integer, Integer> indexToUserId = new HashMap<Integer, Integer>();
        double[][] entityCountsByUserCode;
        private String explanation;

        public Result(String explanation, Set<Integer> allUsers, Set<Integer> allCodes) {
            this.explanation = explanation;
            
            numUsers = allUsers.size();
            numCodes = allCodes.size();
            userTotalColumn = numCodes;
            codeTotalRow = numUsers;

            entityCountsByUserCode = new double[numUsers + 1][numCodes + 1];

            int codeIndex = 0;
            for (int codeId : allCodes) {
                codeIdToIndex.put(codeId, codeIndex);
                indexToCodeId.put(codeIndex, codeId);
                codeIndex++;
            }

            int userIndex = 0;
            for (int userId : allUsers) {
                userIdToIndex.put(userId, userIndex);
                indexToUserId.put(userIndex, userId);
                userIndex++;
            }

            for (int i = 0; i < numUsers + 1; i++) {
                for (int j = 0; j < numCodes + 1; j++) {
                    entityCountsByUserCode[i][j] = 0;
                }
            }
        }

        @Override
        public String getAsString(boolean humanFriendly) {
            List<String> codeNames = getCodeNames(humanFriendly);
            List<String> userNames = getUserNames(humanFriendly);
            String result = explanation + "\n" + PrintUtils.printMatrix(entityCountsByUserCode, userNames, codeNames);
            return result;
        }

        private List<String> getCodeNames(boolean humanFriendly) {
            List<String> codeNames = new ArrayList<String>();
            //Generate the names list
            for (int i = 0; i < numCodes; i++) {
                int codeId = indexToCodeId.get(i);
                String name = Integer.toString(codeId);
                if (humanFriendly) {
                    name = CodeNames.instance.get(codeId);
                }
                codeNames.add(name);
            }
            codeNames.add("TOTAL");
            return codeNames;
        }

        private List<String> getUserNames(boolean humanFriendly) {
            List<String> userNames = new ArrayList<String>();
            //Generate the names list
            for (int i = 0; i < numUsers; i++) {
                int userId = indexToUserId.get(i);
                String name = Integer.toString(userId);
                if (humanFriendly) {
                    name = UserNames.instance.get(userId);
                }
                userNames.add(name);
            }
            userNames.add("TOTAL");
            return userNames;
        }

        @Override
        public void writeToCSV(boolean humanFriendly, CSV csv) {
            List<String> codeNames = getCodeNames(humanFriendly);
            List<String> userNames = getUserNames(humanFriendly);

            PrintUtils.writeMatrixToCSV(csv, entityCountsByUserCode, userNames, codeNames);
        }

        private void increment(int userId, int codeId) {
            this.increment(userId, codeId, 1.0);
        }

        private void increment(int userId, int codeId, double codeShare) {
            int userIndex = userIdToIndex.get(userId);
            int codeIndex = codeIdToIndex.get(codeId);

            entityCountsByUserCode[userIndex][codeIndex] += codeShare;
            entityCountsByUserCode[userIndex][userTotalColumn] += codeShare;
            entityCountsByUserCode[codeTotalRow][codeIndex] += codeShare;
            entityCountsByUserCode[codeTotalRow][userTotalColumn] += codeShare;
        }

        public int getNumUsers() {
            return numUsers;
        }

        public int getNumCodes() {
            return numCodes;
        }

        public double getTotalUserEntities(int userId) {
            if (userIdToIndex.containsKey(userId)) {
                int userIndex = userIdToIndex.get(userId);
                return entityCountsByUserCode[userIndex][userTotalColumn];
            } else {
                return 0;
            }
        }

        public double getTotalCodeEntities(int codeId) {
            if (codeIdToIndex.containsKey(codeId)) {
                int codeIndex = codeIdToIndex.get(codeId);
                return entityCountsByUserCode[codeTotalRow][codeIndex];
            } else {
                return 0;
            }
        }

        public double getTotalEntities() {
            return entityCountsByUserCode[codeTotalRow][userTotalColumn];
        }

        public double getUserCodeEntities(int userId, int codeId) {
            if (userIdToIndex.containsKey(userId) && codeIdToIndex.containsKey(codeId)) {
                int userIndex = userIdToIndex.get(userId);
                int codeIndex = codeIdToIndex.get(codeId);
                return entityCountsByUserCode[userIndex][codeIndex];
            } else {
                return 0;
            }
        }

        @Override
        public String getExplanation() {
            return explanation;
        }

        private void setExplanation(String explanation) {
            this.explanation = explanation;
        }
    }

    /**
     * @see CountType#Ratings
     * @see CountType#UserNormed
     * @see CountType#EntityNormed
     */
    public enum CountType {

        /**
         * No normalization. Just counts distinct ratings.<br/>
         * Interpretation:<br/> Cell: The number of times the user applied the
         * code<br/> Code total: the number of times the code was applied<br/>
         * User total: the number of codes applied by the user<br/> Grand total:
         * the total number of distinct ratings
         */
        Ratings,
        /**
         * Within each entity, each user's contribution sums to 1. (each user is
         * allowed 1 "vote" per entity)<br/> Interpretation:<br/> Cell: The
         * amount of investment in the code by the user<br/> Code total: The
         * amount of user investment in the code<br/> User total: The number of
         * entities rated by the user<br/> Grand total: The total number of
         * "user efforts"
         */
        UserNormed,
        /**
         * Counts the applications of each code by each user. Counts are
         * normalized within each entity (each entity contributes a total of
         * 1)<br/> Interpretation:<br/> Cell: The share of all ratings involving
         * both the user and code (out of the grand total)<br/> Code total: The
         * share of all ratings involving the code (out of the grand total)<br/>
         * User total: The share of all ratings done by the user (out of the
         * grand total)<br/> Grand total: The total number of entities.
         */
        EntityNormed
    }
    private CountType countType = CountType.Ratings;

    public CountType getCountType() {
        return countType;
    }

    public void setCountType(CountType countType) {
        this.countType = countType;
    }

    @Override
    public String getName() {
        return "UserCodeCounts(" + getCountType() + ")";
    }

    private String generateExplanation() {
        String explanation = "Counts the frequency of user-code pairs in the data. ";
        explanation += "Type: " + getCountType() + ". \n";
        switch (getCountType()) {
            case Ratings:
                explanation += CountType.Ratings + " uses no normalization. Just counts distinct rating pairs. "
                        + "Each cell has the number of times the user applied the code. "
                        + "The code totals are the number of times the code was applied. "
                        + "The user totals are the number of codes applied by the user. "
                        + "The grand total is the total number of distinct ratings.";
                break;
            case UserNormed:
                explanation += CountType.UserNormed + ": within each entity (segment), each user's contribution sums to 1. "
                        + "Each cell reflects the amount of 'investment' in the code by the user. "
                        + "The code totals are the total amount of user investment in the code. "
                        + "The user totals are the number of entities rated by the user. "
                        + "The grand total is the total number of 'user efforts'.";
                break;
            case EntityNormed:
                explanation += CountType.Ratings + ": within each entity (segment) all ratings sum to 1. "
                        + "Each cell is the share of all ratings involving both the user and the code. "
                        + "The code totals are the share of all ratings involving the code."
                        + "The user totals are the share of all ratings done by the user. "
                        + "The grand total is the total number of entities.";
                break;
        }
        return explanation;
    }

    /**
     * Generates a table containing counts reflecting the frequency of user-code
     * pairs in the data. Totals for each user and each code, and a grand total,
     * are also calculated.<br/> Several different normalization/voting schemes
     * are available. See {@link CountType CountType} for interpretations.
     *
     * @param dataSet
     * @return
     */
    @Override
    public AnalysisResult analyze(EntitySet dataSet) {
        dataSet.takeStock();
        Set<Integer> allUsers = dataSet.getAllUserIds();
        Set<Integer> allCodes = dataSet.getAllCodeIds();

        String explanation = generateExplanation();

        Result result = new Result(explanation, allUsers, allCodes);

        for (MultiRatedEntity entity : dataSet) {
            switch (getCountType()) {
                case Ratings:
                    ratingCounts(entity, result);
                    break;
                case UserNormed:
                    userNormedCounts(entity, result);
                    break;
                case EntityNormed:
                    entityNormedCounts(entity, result);
            }
        }

        return result;
    }

    private static void ratingCounts(MultiRatedEntity entity, Result result) {
        List<Integer> users = new ArrayList<Integer>(entity.getDistinctUsers());
        for (int u = 0; u < users.size(); u++) {
            int userId = users.get(u);
            List<Integer> codes = new ArrayList<Integer>(entity.getCodesByUser(userId));

            for (int codeId : codes) {
                result.increment(userId, codeId);
            }
        }
    }

    private static void userNormedCounts(MultiRatedEntity entity, Result result) {
        List<Integer> users = new ArrayList<Integer>(entity.getDistinctUsers());
        for (int u = 0; u < users.size(); u++) {
            int userId = users.get(u);
            List<Integer> codes = new ArrayList<Integer>(entity.getCodesByUser(userId));

            double codeShare = 1.0 / codes.size();

            for (int codeId : codes) {
                result.increment(userId, codeId, codeShare);
            }
        }

    }

    private static void entityNormedCounts(MultiRatedEntity entity, Result result) {
        //First get the number of distinct ratings
        List<Integer> users = new ArrayList<Integer>(entity.getDistinctUsers());
        ArrayList<List<Integer>> codesByUserIdx = new ArrayList<List<Integer>>();
        int totalDistinctRatings = 0;
        for (int u = 0; u < users.size(); u++) {
            int userId = users.get(u);
            List<Integer> codes = new ArrayList<Integer>(entity.getCodesByUser(userId));
            codesByUserIdx.add(codes);

            totalDistinctRatings += codes.size();
        }

        //Now add all the pair counts
        double ratingShare = 1.0 / totalDistinctRatings;
        for (int u = 0; u < users.size(); u++) {
            int userId = users.get(u);
            List<Integer> codes = codesByUserIdx.get(u);

            for (int codeId : codes) {
                result.increment(userId, codeId, ratingShare);
            }
        }
    }
}
