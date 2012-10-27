/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cmd;

import cmd.config.DefaultDataConfig;
import daisy.io.CSV;
import data.EntitySet;
import data.MultiRatedEntity;
import data.analysis.PrintUtils;
import data.indexes.CodeNames;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class SegmentCodeCounter {

    public static void main(String[] args) {
        int numSegmentations = 19;
        int numCodes = 125;

        String[] segNames = new String[numSegmentations];
        String[] codeNames = new String[numCodes + 1];

        double[][] entityCountsBySegCode = new double[numSegmentations][numCodes + 1];
        double[] entityCountsBySeg = new double[numSegmentations];

        CSV csv = null;
        try {
            csv = new CSV("seg_counts.csv", "Counts of stuff for segments");
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        for (int segId = 0; segId < numSegmentations; segId++) {
            String segName = "seg-" + segId;
            segNames[segId] = segName;

            DefaultDataConfig dataConfig = new DefaultDataConfig(segName);
            dataConfig.setSegmentationId(segId);
            dataConfig.setRemoveSystemMessages(true);

            DataPreparer preparer = new DataPreparer(dataConfig);
            EntitySet entities = preparer.getData();

            if (segId == 0) {
                for (int c = 0; c < codeNames.length; c++) {
                    codeNames[c] = "null";
                }

                for (Map.Entry<Integer, String> entry : CodeNames.instance.entrySet()) {
                    codeNames[entry.getKey()] = entry.getValue();
                }
            }

            for (int i = 0; i < entities.size(); i++) {
                MultiRatedEntity entity = entities.get(i);
                Set<Integer> codes = entity.getDistinctCodes();
                for (int codeId : codes) {
                    entityCountsBySegCode[segId][codeId]++;
                }
            }
            entityCountsBySeg[segId] = entities.size();
        }
        try {
            csv.println("Entities with Codes");
            PrintUtils.writeMatrixToCSV(csv, entityCountsBySegCode, Arrays.asList(segNames), Arrays.asList(codeNames));
            csv.println("Entity Counts");
            PrintUtils.writeArrayToCSV(csv, entityCountsBySeg, Arrays.asList(segNames), "entities");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
