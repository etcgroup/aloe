/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data.analysis;

import daisy.io.CSV;
import data.EntitySet;
import data.indexes.UserNames;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public abstract class CrossTab implements Analysis<EntitySet> {

    public static class Result implements AnalysisResult {

        private final HashMap<Integer, Integer> idToIndex = new HashMap<Integer, Integer>();
        private final HashMap<Integer, Integer> indexToId = new HashMap<Integer, Integer>();
        private final double[][] crossTab;
        private final int numObjects;
        private final int totalIndex;
        private HashMap<Integer, String> nameLookup = null;
        private String explanation;

        protected Result(String explanation, Set<Integer> allObjects) {
            this.explanation = explanation;
            
            numObjects = allObjects.size();
            totalIndex = numObjects;

            crossTab = new double[numObjects + 1][numObjects + 1];
            int index = 0;
            for (int id : allObjects) {
                idToIndex.put(id, index);
                indexToId.put(index, id);

                for (int i = 0; i <= index; i++) {
                    crossTab[index][i] = 0;
                    crossTab[i][index] = 0;
                }

                index++;
            }
        }

        protected void setNameLookup(HashMap<Integer, String> nameLookup) {
            this.nameLookup = nameLookup;
        }

        protected void increment(int id1, int id2) {
            int index1 = idToIndex.get(id1);
            int index2 = idToIndex.get(id2);

            crossTab[index1][index2]++;
            crossTab[index1][totalIndex]++;
            crossTab[totalIndex][index1]++;
            crossTab[totalIndex][totalIndex]++;

            if (index1 != index2) {
                crossTab[index2][index1]++;
                crossTab[index2][totalIndex]++;
                crossTab[totalIndex][index2]++;
            }
        }

        @Override
        public String getAsString(boolean humanFriendly) {

            List<String> names = getNames(humanFriendly);

            return getExplanation() + "\n" + PrintUtils.printMatrix(crossTab, names);
        }

        private List<String> getNames(boolean humanFriendly) {
            List<String> names = new ArrayList<String>();
            //Generate the names list
            for (int i = 0; i < numObjects; i++) {
                int id = indexToId.get(i);
                String name = Integer.toString(id);
                if (humanFriendly && nameLookup != null && nameLookup.containsKey(id)) {
                    name = nameLookup.get(id);
                }
                names.add(name);
            }
            names.add("TOTAL");
            return names;
        }

        @Override
        public void writeToCSV(boolean humanFriendly, CSV csv) {
            List<String> names = getNames(humanFriendly);
            
            PrintUtils.writeMatrixToCSV(csv, crossTab, names);
        }

        public double getCell(int id1, int id2) {
            if (idToIndex.containsKey(id1) && idToIndex.containsKey(id2)) {
                int index1 = idToIndex.get(id1);
                int index2 = idToIndex.get(id2);
                return crossTab[index1][index2];
            } else {
                return 0;
            }
        }

        public double getTotal(int id) {
            if (idToIndex.containsKey(id)) {
                int index = idToIndex.get(id);
                return crossTab[index][totalIndex];
            } else {
                return 0;
            }
        }

        public double getGrandTotal() {
            return crossTab[totalIndex][totalIndex];
        }

        public int getNumObjects() {
            return numObjects;
        }
        
        @Override
        public String getExplanation() {
            return explanation;
        }

        private void setExplanation(String explanation) {
            this.explanation = explanation;
        }
    }
}
