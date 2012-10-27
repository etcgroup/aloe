/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data.analysis;

import daisy.io.CSV;
import data.EntityMetaData;
import data.EntitySet;
import data.MultiRatedEntity;
import data.Rating;
import data.indexes.CodeNames;
import data.indexes.ParticipantNames;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class ParticipantCodeCounts implements Analysis<EntitySet> {

    public static class Result implements AnalysisResult {

        private final int numParticipants;
        private final int numCodes;
        private final int participantTotalColumn;
        private final int codeTotalRow;
        private HashMap<Integer, Integer> codeIdToIndex = new HashMap<Integer, Integer>();
        private HashMap<Integer, Integer> indexToCodeId = new HashMap<Integer, Integer>();
        private HashMap<Integer, Integer> participantIdToIndex = new HashMap<Integer, Integer>();
        private HashMap<Integer, Integer> indexToParticipantId = new HashMap<Integer, Integer>();
        double[][] entityCountsByParticipantCode;
        private String explanation;

        public Result(String explanation, Set<Integer> allParticipants, Set<Integer> allCodes) {
            this.explanation = explanation;

            numParticipants = allParticipants.size();
            numCodes = allCodes.size();
            participantTotalColumn = numCodes;
            codeTotalRow = numParticipants;

            entityCountsByParticipantCode = new double[numParticipants + 1][numCodes + 1];

            int codeIndex = 0;
            for (int codeId : allCodes) {
                codeIdToIndex.put(codeId, codeIndex);
                indexToCodeId.put(codeIndex, codeId);
                codeIndex++;
            }

            int participantIndex = 0;
            for (int participantId : allParticipants) {
                participantIdToIndex.put(participantId, participantIndex);
                indexToParticipantId.put(participantIndex, participantId);
                participantIndex++;
            }

            for (int i = 0; i < numParticipants + 1; i++) {
                for (int j = 0; j < numCodes + 1; j++) {
                    entityCountsByParticipantCode[i][j] = 0;
                }
            }
        }

        @Override
        public String getAsString(boolean humanFriendly) {
            List<String> codeNames = getCodeNames(humanFriendly);
            List<String> participantNames = getParticipantNames(humanFriendly);
            String result = explanation + "\n" + PrintUtils.printMatrix(entityCountsByParticipantCode, participantNames, codeNames);
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

        private List<String> getParticipantNames(boolean humanFriendly) {
            List<String> participantNames = new ArrayList<String>();
            //Generate the names list
            for (int i = 0; i < numParticipants; i++) {
                int participantId = indexToParticipantId.get(i);
                String name = Integer.toString(participantId);
                if (humanFriendly) {
                    name = ParticipantNames.instance.get(participantId);
                }
                participantNames.add(name);
            }
            participantNames.add("TOTAL");
            return participantNames;
        }

        @Override
        public void writeToCSV(boolean humanFriendly, CSV csv) {
            List<String> codeNames = getCodeNames(humanFriendly);
            List<String> participantNames = getParticipantNames(humanFriendly);

            PrintUtils.writeMatrixToCSV(csv, entityCountsByParticipantCode, participantNames, codeNames);
        }

        private void increment(int participantId, int codeId) {
            this.increment(participantId, codeId, 1.0);
        }

        private void increment(int participantId, int codeId, double codeShare) {
            int participantIndex = participantIdToIndex.get(participantId);
            int codeIndex = codeIdToIndex.get(codeId);

            entityCountsByParticipantCode[participantIndex][codeIndex] += codeShare;
            entityCountsByParticipantCode[participantIndex][participantTotalColumn] += codeShare;
            entityCountsByParticipantCode[codeTotalRow][codeIndex] += codeShare;
            entityCountsByParticipantCode[codeTotalRow][participantTotalColumn] += codeShare;
        }

        public int getNumParticipants() {
            return numParticipants;
        }

        public int getNumCodes() {
            return numCodes;
        }

        public double getTotalParticipantEntities(int participantId) {
            if (participantIdToIndex.containsKey(participantId)) {
                int participantIndex = participantIdToIndex.get(participantId);
                return entityCountsByParticipantCode[participantIndex][participantTotalColumn];
            } else {
                return 0;
            }
        }

        public double getTotalCodeEntities(int codeId) {
            if (codeIdToIndex.containsKey(codeId)) {
                int codeIndex = codeIdToIndex.get(codeId);
                return entityCountsByParticipantCode[codeTotalRow][codeIndex];
            } else {
                return 0;
            }
        }

        public double getTotalEntities() {
            return entityCountsByParticipantCode[codeTotalRow][participantTotalColumn];
        }

        public double getParticipantCodeEntities(int participantId, int codeId) {
            if (participantIdToIndex.containsKey(participantId) && codeIdToIndex.containsKey(codeId)) {
                int participantIndex = participantIdToIndex.get(participantId);
                int codeIndex = codeIdToIndex.get(codeId);
                return entityCountsByParticipantCode[participantIndex][codeIndex];
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
     * @see CountType#AllRatings
     * @see CountType#DistinctRatings
     * @see CountType#EntityNormed
     */
    public enum CountType {

        /**
         * Counts the number of times any coder placed each code on an entity
         * containing a message by each participant. If two coders code the same
         * entity with the same code, that counts twice.<br/>
         * Interpretation:<br/> Cell: The number of times the participant
         * received the code<br/> Code total: meaningless?<br/> Participant
         * total: the number of times codes were applied to entities containing
         * this participant's messages<br/> Grand total: meaningless?
         */
        AllRatings,
        /**
         * Counts the number of entities where this participant's messages were
         * coded with the given code. Handles duplicate codes by different
         * coders. <br/> Interpretation:<br/> Cell: The the number of entities
         * containing the participant's comments that received the code<br/>
         * Code total: meaningless?<br/> Participant total: The number of
         * de-duped codes within entities accrued by the participant<br/> Grand
         * total: meaningless?
         */
        DistinctRatings
    }
    private CountType countType = CountType.AllRatings;

    public CountType getCountType() {
        return countType;
    }

    public void setCountType(CountType countType) {
        this.countType = countType;
    }

    @Override
    public String getName() {
        return "ParticipantCodeCounts(" + getCountType() + ")";
    }

    private String generateExplanation() {
        String explanation = "Counts the frequency of participant-code pairs in the data. Works at the entity level!";
        explanation += "Type: " + getCountType() + ". \n";
        switch (getCountType()) {
            case AllRatings:
                explanation += CountType.AllRatings + " counts all applications of codes for participants, regardless of duplicate coders. "
                        + "Each cell has the number of times the participant received the code. Totals are mostly uninterpretable.";
                break;
            case DistinctRatings:
                explanation += CountType.DistinctRatings + " counts all applications of codes for participants, removing duplicate codes applications by different coders. "
                        + "Each cell has the number of unique entities where the participant received the code. Totals are mostly uninterpretable.";
                break;
        }
        
        return explanation;
    }

    /**
     * Generates a table containing counts reflecting the frequency of
     * participant-code pairs in the data. Totals for each participant and each
     * code, and a grand total, are also calculated.<br/> Several different
     * normalization/voting schemes are available. See {@link CountType CountType}
     * for interpretations.
     *
     * @param dataSet
     * @return
     */
    @Override
    public AnalysisResult analyze(EntitySet dataSet) {
        dataSet.takeStock();
        Set<Integer> allParticipants = ParticipantNames.instance.keySet();
        Set<Integer> allCodes = dataSet.getAllCodeIds();

        String explanation = generateExplanation();

        Result result = new Result(explanation, allParticipants, allCodes);

        for (MultiRatedEntity entity : dataSet) {
            EntityMetaData meta = dataSet.getMetaData(entity.getEntityId());
            
            switch (getCountType()) {
                case AllRatings:
                    allRatingCounts(entity, meta, result);
                    break;
                case DistinctRatings:
                    distinctRatingCounts(entity, meta, result);
            }
        }

        return result;
    }

    private static void allRatingCounts(MultiRatedEntity entity, EntityMetaData meta, Result result) {
        List<Integer> participants = new ArrayList<Integer>(meta.getDistinctParticipants());
        
        for (int p = 0; p < participants.size(); p++) {
            int participantId = participants.get(p);
            
            for (Rating rating : entity.getRatings()) {
                result.increment(participantId, rating.getCodeId());
            }
        }
    }
    
    
    private void distinctRatingCounts(MultiRatedEntity entity, EntityMetaData meta, Result result) {
        List<Integer> participants = new ArrayList<Integer>(meta.getDistinctParticipants());
        List<Integer> codes = new ArrayList<Integer>(entity.getDistinctCodes());
        for (int p = 0; p < participants.size(); p++) {
            int participantId = participants.get(p);
            
            for (int codeId : codes) {
                result.increment(participantId, codeId);
            }
        }
    }
}
