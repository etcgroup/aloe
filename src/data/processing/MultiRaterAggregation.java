/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data.processing;

import data.Rating;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class MultiRaterAggregation {
    public int majority(List<Rating> ratings) {
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
}
