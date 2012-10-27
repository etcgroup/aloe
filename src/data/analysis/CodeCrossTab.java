/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data.analysis;

import data.EntitySet;
import data.MultiRatedEntity;
import data.indexes.CodeNames;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class CodeCrossTab extends CrossTab {

    @Override
    public String getName() {
        return "CodeCrossTab(" + getUserMode() + ")";
    }

    /**
     * @see #UserAgnostic
     * @see #WithinUsers
     * @see #BetweenUsers
     * @see #AllPairs
     */
    public enum UserMode {

        /**
         * Discards all user information to get a single list of distinct codes,
         * then counts distinct code pairs for each entity.<br/>
         * Note: self-pairs are never counted in this method<br/>
         * Interpretation:<br/>
         * Cell: The number of entities where this particular pair occurred (any order).<br/>
         * Code total: The number of entities where this code was used.<br/>
         * Grand total: The total number of pairs that occurred anywhere (not distinct)
         */
        UserAgnostic,
        /**
         * Counts the number of distinct code pairs within each user's code instances for each entity.<br/>
         * Interpretation:<br/>
         * Cell: The number of times that any one user applied this pair of codes.<br/>
         * Code total: The number of within-user code pairs involving this code. (not very useful)<br/>
         * Grand total: The total number of within-user code pairs. (not very useful)<br/>
         */
        WithinUsers,
        /**
         * Counts the number of distinct code pairs between users in each entity. (excluding within-user pairs)<br/>
         * Interpretation:<br/>
         * Cell: The number of entities where more than one user applied this pair.<br/>
         * Code total: The number of between-user code pairs involving this code. (not very useful)<br/>
         * Grand total: The total number of between-user code pairs. (not very useful)<br/>
         */
        BetweenUsers,
        /**
         * Counts the number of distinct code pairs between and within users.<br/>
         * Ignores user delineations, like UserAgnostic, but doesn't throw away duplicates.
         * As a result, it can count self-pairs.<br/>
         * Interpretation:<br/>
         * Cell: The number of times this pair occurred anywhere, by anyone.<br/>
         * Code total: The number of pairs involving this code. (not very useful)<br/>
         * Grand total: The total number of pairs anywhere. (not very useful)
         */
        AllPairs
    }
    private UserMode userMode = UserMode.UserAgnostic;

    public UserMode getUserMode() {
        return userMode;
    }

    public void setUserMode(UserMode userMode) {
        this.userMode = userMode;
    }

    private String generateExplanation() {
        String explanation = "Counts the number of segments where codes occurred together. \n";
        
        switch (getUserMode()) {
            case UserAgnostic:
                explanation += UserMode.UserAgnostic + " ignores all user information to get a single list of distinct codes for each segment. "
                        + "Then counts distinct code pairs within this list. "
                        + "Cells contain the number of entities where this particular pair occurred. "
                        + "Totals for codes are the number of entities where the code was used by anyone. "
                        + "The grand total is the total number of pairs that occurred anywhere, but isn't very useful.";
                break;
            case WithinUsers:
                explanation += UserMode.WithinUsers + " counts the number of distinct code pairs within each user's ratings for each segment. "
                        + "Cells contain the number of times any one user applied the pair of codes. "
                        + "Code totals and grand totals are not very useful.";
                break;
            case BetweenUsers:
                explanation += UserMode.BetweenUsers + " counts the number of distinct code pairs between users in each segment. "
                        + "Cells contain the number of entities where two users together applied the pair. "
                        + "Code totals and grand totals are not very useful.";
                break;
            case AllPairs:
                explanation += UserMode.AllPairs + " counts the number of distinct code pairs among all users for each segment. "
                        + "Cells contain the number of times this pair was applied by any one or two raters. "
                        + "Code totals and grand totals are not very useful.";
                break;
        }
        
        return explanation;
    }
    
    /**
     * Generates a table containing code co-occurrence counts.
     * See {@link UserMode UserMode} for interpretations.
     * @param dataSet
     * @return 
     */
    @Override
    public AnalysisResult analyze(EntitySet dataSet) {
        dataSet.takeStock();
        Set<Integer> allCodes = dataSet.getAllCodeIds();

        String explanation = generateExplanation();
        
        Result result = new Result(explanation, allCodes);
        result.setNameLookup(CodeNames.instance);

        for (MultiRatedEntity entity : dataSet) {

            switch (getUserMode()) {
                case UserAgnostic:
                    countPairsAgnostic(entity, result);
                    break;
                case WithinUsers:
                    countPairsWithinUsers(entity, result);
                    break;
                case BetweenUsers:
                    countPairsBetweenUsers(entity, result);
                    break;
                case AllPairs:
                    countPairsAllPairs(entity, result);
                    break;
            }
        }

        return result;
    }

    private static void countPairsAgnostic(MultiRatedEntity entity, Result result) {
        List<Integer> codes = new ArrayList<Integer>(entity.getDistinctCodes());

        for (int i = 0; i < codes.size(); i++) {
            for (int j = i + 1; j < codes.size(); j++) {
                result.increment(codes.get(i), codes.get(j));
            }
        }
    }

    private static void countPairsWithinUsers(MultiRatedEntity entity, Result result) {
        List<Integer> users = new ArrayList<Integer>(entity.getDistinctUsers());
        for (int u = 0; u < users.size(); u++) {
            int userId = users.get(u);
            List<Integer> codes = new ArrayList<Integer>(entity.getCodesByUser(userId));

            for (int i = 0; i < codes.size(); i++) {
                for (int j = i + 1; j < codes.size(); j++) {
                    result.increment(codes.get(i), codes.get(j));
                }
            }
        }
    }

    private static void countPairsBetweenUsers(MultiRatedEntity entity, Result result) {
        List<Integer> users = new ArrayList<Integer>(entity.getDistinctUsers());
        HashMap<Integer, List<Integer>> usersCodes = new HashMap<Integer, List<Integer>>();
        for (int u = 0; u < users.size(); u++) {
            int userId = users.get(u);
            List<Integer> codes = new ArrayList<Integer>(entity.getCodesByUser(userId));
            usersCodes.put(u, codes);
        }

        for (int u1 = 0; u1 < users.size(); u1++) {
            List<Integer> user1Codes = usersCodes.get(u1);

            for (int i = 0; i < user1Codes.size(); i++) {
                int user1Code = user1Codes.get(i);

                for (int u2 = u1 + 1; u2 < users.size(); u2++) {
                    List<Integer> user2Codes = usersCodes.get(u2);

                    for (int j = 0; j < user2Codes.size(); j++) {
                        int user2Code = user2Codes.get(j);
                        
                        result.increment(user1Code, user2Code);
                    }
                }
            }
        }
    }
    
    private static void countPairsAllPairs(MultiRatedEntity entity, Result result) {
        List<Integer> users = new ArrayList<Integer>(entity.getDistinctUsers());
        List<Integer> allCodes = new ArrayList<Integer>();
        for (int u = 0; u < users.size(); u++) {
            int userId = users.get(u);
            allCodes.addAll(entity.getCodesByUser(userId));
        }

        for (int i = 0; i < allCodes.size(); i++) {
            for (int j = i + 1; j < allCodes.size(); j++) {
                result.increment(allCodes.get(i), allCodes.get(j));
            }
        }
    }
}