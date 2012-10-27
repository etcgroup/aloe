/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data.analysis;

import data.EntitySet;
import data.MultiRatedEntity;
import data.indexes.UserNames;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class UserCrossTab extends CrossTab {

    /**
     * Generates a table of counts reflecting how
     * often each pair of users rated the same entity.<br/>
     * Interpretation:<br/>
     * Cell: the number of entities that this pair both rated.<br/>
     * User total: The number of times this user rated with another user (not distinct).<br/>
     * Grand total: The total number of user co-ratings (not distinct)
     * @param dataSet
     * @return 
     */
    @Override
    public AnalysisResult analyze(EntitySet dataSet) {
        dataSet.takeStock();
        Set<Integer> allUsers = dataSet.getAllUserIds();

        String explanation = "Counts how often each pair of users rated the same entity. Totals are not very useful.";
        
        Result result = new Result(explanation, allUsers);
        result.setNameLookup(UserNames.instance);
        
        for (MultiRatedEntity entity : dataSet) {
            countPairs(entity, result);
        }

        return result;
    }

    private static void countPairs(MultiRatedEntity entity, Result result) {
        List<Integer> users = new ArrayList<Integer>(entity.getDistinctUsers());

        for (int i = 0; i < users.size(); i++) {
            for (int j = i + 1; j < users.size(); j++) {
                result.increment(users.get(i), users.get(j));
            }
        }
    }

    @Override
    public String getName() {
        return "UserCrossTab";
    }
}