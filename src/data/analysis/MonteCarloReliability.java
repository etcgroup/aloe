/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data.analysis;

import daisy.io.CSV;
import data.EntitySet;
import data.MultiRatedEntity;
import data.Rating;
import data.indexes.CodeNames;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class MonteCarloReliability implements Analysis<EntitySet> {

    private int numUsers;
    private int numCodes;
    private double[] userCodeSums;
    private double[] userRatingNumSums;
    private double[][] p_userAppliesCode;
    private double[][] p_userAppliesNum;
    private double[] p_observedAgreementOnCode;
    private double p_overallObservedAgreement;
    private double[] p_chanceAgreementOnCode;
    private double p_overallChanceAgreement;
    private HashMap<String, Integer> entityProfileCounts;
    private HashMap<String, List<Integer>> entityProfiles;
    private ArrayList<Integer> codeIds;
    private ArrayList<Integer> userIds;
    private Integer maxUserId;
    private Integer maxCodeId;
    private int maxPossibleRatings;
    private Random random;
    double laplace = 1.0;

    private String getProfileName(List<Integer> users) {
        Collections.sort(users);
        StringBuilder sb = new StringBuilder();
        for (int i : users) {
            sb.append(i).append(",");
        }
        return sb.toString();
    }

    private void estimateRandomAgreement() {

        random = new Random();

        p_overallChanceAgreement = 0.0;
        p_chanceAgreementOnCode = new double[maxCodeId + 1];

        //Simulate each profiile separately
        int totalProfileCounts = 0;
        for (Map.Entry<String, Integer> entry : entityProfileCounts.entrySet()) {
            int profileCount = entry.getValue();

            double[] p_agreementByCodeInProfile = new double[maxCodeId + 1];
            double probabilityOfAgreement = simulateProfile(entry.getKey(), p_agreementByCodeInProfile);

            for (int c = 0; c < codeIds.size(); c++) {
                int codeId = codeIds.get(c);

                double probability = p_agreementByCodeInProfile[codeId];
                p_chanceAgreementOnCode[codeId] += probability * profileCount;
            }
            p_overallChanceAgreement += probabilityOfAgreement * profileCount;

            totalProfileCounts += profileCount;
        }

        p_overallChanceAgreement /= totalProfileCounts;
        for (int c = 0; c < codeIds.size(); c++) {
            int codeId = codeIds.get(c);
            p_chanceAgreementOnCode[codeId] /= totalProfileCounts;
        }
    }

    private double simulateProfile(String profile, double[] p_agreementByCode) {
        List<Integer> users = entityProfiles.get(profile);

        double[] agreementsByCode = new double[maxCodeId + 1];
        double totalAgreements = 0;

        int maxSimulations = 200000;
        double delta = Double.POSITIVE_INFINITY;
        double min_delta = 0.0001;
        int simulatedEntities = 0;
        while (simulatedEntities < maxSimulations && delta > min_delta) {
            totalAgreements += simulateEntity(users, agreementsByCode);
            simulatedEntities++;

            //Update the probabilities
            if (simulatedEntities % 1000 == 0) {
                delta = 0;
                for (int c = 0; c < codeIds.size(); c++) {
                    int codeId = codeIds.get(c);
                    double probability = (laplace + agreementsByCode[codeId]) / (laplace * codeIds.size() + simulatedEntities);
                    delta = Math.max(delta, Math.abs(probability - p_agreementByCode[codeId]));
                    p_agreementByCode[codeId] = probability;
                }
            }
        }

        System.out.println("ran " + simulatedEntities + " entities, reaching delta " + delta);

        double probabilityOfAgreement = (laplace + totalAgreements) / (laplace * 2 + simulatedEntities);
        for (int c = 0; c < codeIds.size(); c++) {
            int codeId = codeIds.get(c);
            double probability = (laplace + agreementsByCode[codeId]) / (laplace * codeIds.size() + simulatedEntities);
            p_agreementByCode[codeId] = probability;
        }

        return probabilityOfAgreement;
    }

    private double simulateEntity(List<Integer> users, double[] agreementsByCode) {
//        ArrayList<HashSet<Integer>> userCodes = new ArrayList<HashSet<Integer>>(users.size());
        HashMap<Integer, Integer> codeSelectionCounts = new HashMap<Integer, Integer>();

        //Simulate each user's choices
        for (int u = 0; u < users.size(); u++) {
            int userId = users.get(u);


            //How many codes to apply?
            int ratingsApplied = 0;
            {
                double q = random.nextDouble();
                double checked = 0;
                for (int ratings = 1; ratings <= maxPossibleRatings; ratings++) {
                    checked += p_userAppliesNum[userId][ratings];
                    if (q <= checked) {
                        ratingsApplied = ratings;
                        break;
                    }

                    if (ratings == maxPossibleRatings) {
                        ratingsApplied = ratings;
                        break;
                    }
                }
            }

            HashSet<Integer> codesApplied = new HashSet<Integer>();
            for (int r = 0; r < ratingsApplied; r++) {
                //Which code to apply?
                int codeSelected = -1;
                boolean cancel = false;

                int attempts = 0;
                while (codeSelected == -1 || codesApplied.contains(codeSelected)) {
                    double q = random.nextDouble();
                    double checked = 0;
                    for (int c = 0; c < codeIds.size(); c++) {
                        int codeId = codeIds.get(c);
                        checked += p_userAppliesCode[userId][codeId];
                        if (q <= checked) {
                            codeSelected = codeId;
                            break;
                        }
                        if (c == codeIds.size() - 1) {
                            codeSelected = codeId;
                            break;
                        }
                    }
                    attempts++;

                    if (attempts > 50) {
                        System.err.println("Too many attempts choosing code " + r + " of " + ratingsApplied + " for user " + userId);
                        System.err.println("Probs: " + Arrays.toString(p_userAppliesCode[userId]));
                        cancel = true;
                        break;
                    }

                }

                if (cancel) {
                    break;
                }

                //We've selected a code
                Integer count = codeSelectionCounts.get(codeSelected);
                if (count == null) {
                    count = 0;
                }

                codesApplied.add(codeSelected);
                codeSelectionCounts.put(codeSelected, count + 1);
            }
        }

        //Now count agreements
        int totalAgreements = 0;
        int majorityThreshold = users.size() / 2;
        for (int c = 0; c < codeIds.size(); c++) {
            int codeId = codeIds.get(c);
            if (!codeSelectionCounts.containsKey(codeId)) {
                agreementsByCode[codeId]++;
            } else {
                if (codeSelectionCounts.get(codeId) > majorityThreshold) {
                    agreementsByCode[codeId]++;
                    totalAgreements = 1;
                }
            }
        }

        return totalAgreements;
    }

    public static class ReliabilityResult implements AnalysisResult {

        double overallObserved;
        double overallChance;
        double overallKappa = 0;
        double[][] observedChanceKappaByCode;
        private final List<Integer> codeIds;

        public ReliabilityResult(List<Integer> codeIds, int maxCodeId) {
            this.observedChanceKappaByCode = new double[maxCodeId + 1][3];
            this.codeIds = codeIds;
        }

        private List<String> getNames(boolean humanFriendly) {
            List<String> names = new ArrayList<String>();
            //Generate the names list
            for (int id = 0; id < observedChanceKappaByCode.length; id++) {
                String name = Integer.toString(id);
                if (humanFriendly && CodeNames.instance != null && CodeNames.instance.containsKey(id)) {
                    name = CodeNames.instance.get(id);
                }
                names.add(name);
            }

            return names;
        }

        @Override
        public String getAsString(boolean humanFriendly) {
            List<String> names = getNames(humanFriendly);
            String output = "Kappa by code\n";
            output += PrintUtils.printMatrix(observedChanceKappaByCode, names, Arrays.asList("observed", "chance", "kappa"));
            output += "\nOverall: " + overallKappa + "\n";
            return output;
        }

        @Override
        public void writeToCSV(boolean humanFriendly, CSV csv) {
            List<String> names = getNames(humanFriendly);
            try {
                csv.println("Kappa by code");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            PrintUtils.writeMatrixToCSV(csv, observedChanceKappaByCode, names, Arrays.asList("observed", "chance", "kappa"));
            try {
                csv.println("Overall", Double.toString(overallObserved), Double.toString(overallChance), Double.toString(overallKappa));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public String getExplanation() {
            return "Monte Carlo Cohen's Kappa";
        }

        private void setProbability(int codeId, double observedAgree, double chanceAgree) {
            double kappa = (observedAgree - chanceAgree) / (1 - chanceAgree);
            observedChanceKappaByCode[codeId][0] = observedAgree;
            observedChanceKappaByCode[codeId][1] = chanceAgree;
            observedChanceKappaByCode[codeId][2] = kappa;
        }

        private void setOverallProbability(double observedAgree, double chanceAgree) {
            double kappa = (observedAgree - chanceAgree) / (1 - chanceAgree);
            overallObserved = observedAgree;
            overallChance = chanceAgree;
            overallKappa = kappa;
        }
    }

    @Override
    public AnalysisResult analyze(EntitySet dataSet) {
        dataSet.takeStock();

        maxPossibleRatings = 0;
        for (int i = 0; i < dataSet.size(); i++) {
            MultiRatedEntity entity = dataSet.get(i);
            if (entity.countRatings() > maxPossibleRatings) {
                maxPossibleRatings = entity.countRatings();
            }
        }

        userIds = new ArrayList<Integer>(dataSet.getAllUserIds());
        codeIds = new ArrayList<Integer>(dataSet.getAllCodeIds());
        numUsers = userIds.size();
        numCodes = codeIds.size();

        maxUserId = Collections.max(userIds);
        maxCodeId = Collections.max(codeIds);

        //First we need the probability of:
        //* a specific user applying a specific number of ratings
        //* a specific user applying a specific code

        //We also need to know what message profiles we need to estimate
        //where a message profile is a set of users who rated the same message
        scanEntities(dataSet);

        estimateRandomAgreement();

        ReliabilityResult result = new ReliabilityResult(codeIds, maxCodeId);
        for (int c = 0; c < codeIds.size(); c++) {
            int codeId = codeIds.get(c);
            double observedAgree = p_observedAgreementOnCode[codeId];
            double chanceAgree = p_chanceAgreementOnCode[codeId];
            result.setProbability(codeId, observedAgree, chanceAgree);
        }
        result.setOverallProbability(p_overallObservedAgreement, p_overallChanceAgreement);
        return result;
    }

    private void scanEntities(EntitySet dataSet) {

        int[][] userAppliesCode = new int[maxUserId + 1][maxCodeId + 1];
        int[] totalUserRatings = new int[maxUserId + 1];

        int[][] userRatesNum = new int[maxUserId + 1][maxPossibleRatings + 1];
        int[] totalUserEntities = new int[maxUserId + 1];

        int[] agreementByCode = new int[maxCodeId + 1];
        int numEntities = dataSet.size();

        entityProfiles = new HashMap<String, List<Integer>>();
        entityProfileCounts = new HashMap<String, Integer>();

        int entitiesWithAgreement = 0;
        int entitiesWithAgreementOpportunity = 0;
        for (int i = 0; i < dataSet.size(); i++) {
            MultiRatedEntity entity = dataSet.get(i);

            //Add the profile to the entity profiles if needed
            List<Integer> users = new ArrayList<Integer>(entity.getDistinctUsers());
            if (users.size() > 1) {
                //Only care about profiles with more than 1 rater
                String profileName = getProfileName(users);
                Integer current = entityProfileCounts.get(profileName);
                if (current == null) {
                    current = 0;
                    entityProfiles.put(profileName, users);
                }
                entityProfileCounts.put(profileName, current + 1);

                int majorityThreshold = users.size() / 2;
                Set<Integer> allCodes = entity.getDistinctCodes();
                int agreement = 0;
                for (int c = 0; c < codeIds.size(); c++) {
                    int codeId = codeIds.get(c);
                    if (!allCodes.contains(codeId)) {
                        agreementByCode[codeId]++;
                    } else {
                        if (entity.countUsersByCode(codeId) > majorityThreshold) {
                            agreementByCode[codeId]++;
                            agreement = 1;
                        }
                    }
                }

                entitiesWithAgreement += agreement;
                entitiesWithAgreementOpportunity++;
            }

            for (int u = 0; u < users.size(); u++) {
                int userId = users.get(u);
                Set<Integer> codes = entity.getCodesByUser(userId);

                for (int codeId : codes) {
                    userAppliesCode[userId][codeId]++;
                }
                totalUserRatings[userId] += codes.size();

                userRatesNum[userId][codes.size()]++;
                totalUserEntities[userId]++;
            }
        }

        //get the actual max possible ratings
        maxPossibleRatings = 1;
        for (int u = 0; u < userIds.size(); u++) {
            int userId = userIds.get(u);
            for (int n = maxPossibleRatings + 1; n < userRatesNum[userId].length; n++) {
                if (userRatesNum[userId][n] > 0) {
                    maxPossibleRatings = n;
                }
            }
        }

        //Now generate the probability tables
        p_userAppliesNum = new double[maxUserId + 1][maxPossibleRatings + 1];
        p_userAppliesCode = new double[maxUserId + 1][maxCodeId + 1];
//        userRatingNumSums = new double[maxUserId + 1];
//        userCodeSums = new double[maxUserId + 1];
        for (int u = 0; u < userIds.size(); u++) {
            int userId = userIds.get(u);

            int entitiesRated = totalUserEntities[userId];
            int numRatings = totalUserRatings[userId];

//            double totalRatingsProbability = 0;
            for (int ratings = 1; ratings <= maxPossibleRatings; ratings++) {
                int timesCoded = userRatesNum[userId][ratings];
                double probability = (laplace + timesCoded) / (laplace * maxPossibleRatings + entitiesRated);
                p_userAppliesNum[userId][ratings] = probability;
//                totalRatingsProbability += probability;
            }
//            userRatingNumSums[userId] = totalRatingsProbability;

//            double totalCodesProbability = 0;
            for (int c = 0; c < codeIds.size(); c++) {
                int codeId = codeIds.get(c);
                int timesCoded = userAppliesCode[userId][codeId];
                double probability = (laplace + timesCoded) / (laplace * codeIds.size() + numRatings);
                p_userAppliesCode[userId][codeId] = probability;
//                totalCodesProbability += probability;
            }
//            userCodeSums[userId] = totalCodesProbability;
        }

        //Calculate the observed percent agreement
        p_observedAgreementOnCode = new double[maxCodeId + 1];
        for (int c = 0; c < codeIds.size(); c++) {
            int codeId = codeIds.get(c);
            int timesAgreedOn = agreementByCode[codeId];
            double probability = (double)timesAgreedOn / entitiesWithAgreementOpportunity;
            p_observedAgreementOnCode[codeId] = probability;
        }
        p_overallObservedAgreement = (double)entitiesWithAgreement / entitiesWithAgreementOpportunity;
    }

    @Override
    public String getName() {
        return "MonteCarloReliability";
    }
}
