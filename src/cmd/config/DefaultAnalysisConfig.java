/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cmd.config;

import data.EntitySet;
import data.analysis.*;
import data.processing.attrs.BinaryCodeClassProducer.CodePresenceStrategy;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class DefaultAnalysisConfig implements AnalysisConfig {

    @Override
    public List<Integer> getCodeIds() {
        ArrayList<Integer> codeIds = new ArrayList<Integer>();
        int[] codesOver100 = new int[]{93, 87, 85, 82, 122, 88, 90, 116, 91, 108, 89, 99, 123, 79, 94, 117, 74, 73, 83, 96, 84, 80, 120, 118, 81, 77, 121, 124, 119, 113};
        for (int i = 0; i < codesOver100.length; i++) {
            codeIds.add(codesOver100[i]);
        }
        return codeIds;
    }

    @Override
    public List<Analysis<EntitySet>> getPreFilterAnalyses() {
        List<Analysis<EntitySet>> analyses = new ArrayList<Analysis<EntitySet>>();
//        analyses.add(new FrequencyAnalysis());
        analyses.add(new MonteCarloReliability());
        return analyses;
    }

    @Override
    public List<Analysis<EntitySet>> getPostFilterAnalyses() {
        List<Analysis<EntitySet>> analyses = new ArrayList<Analysis<EntitySet>>();

        ParticipantCodeCounts pCodeCount = new ParticipantCodeCounts();
        pCodeCount.setCountType(ParticipantCodeCounts.CountType.DistinctRatings);
        analyses.add(pCodeCount);
        
        CodeCrossTab codeCrossTab = new CodeCrossTab();
        codeCrossTab.setUserMode(CodeCrossTab.UserMode.AllPairs);
        analyses.add(codeCrossTab);

        UserCrossTab userCrossTab = new UserCrossTab();
        analyses.add(userCrossTab);

        UserCodeCounts counts = new UserCodeCounts();
        counts.setCountType(UserCodeCounts.CountType.Ratings);
        analyses.add(counts);

        return analyses;
    }

    @Override
    public CodePresenceStrategy getCodePresenceStrategy() {
        return CodePresenceStrategy.Any;
    }

    @Override
    public boolean isReportHumanReadable() {
        return true;
    }
}
