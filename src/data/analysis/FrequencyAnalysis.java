/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data.analysis;

import daisy.io.CSV;
import data.EntityMetaData;
import data.EntitySet;
import data.MultiRatedEntity;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

/**
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class FrequencyAnalysis implements Analysis<EntitySet> {

    public class FrequencyResult implements AnalysisResult {

        private String explanation;

        private void setExplanation(String explanation) {
            this.explanation = explanation;
        }

        @Override
        public String getAsString(boolean humanFriendly) {
            String result = "Segment String Lengths\n";
            double[][] lengthsHist = histogram(messageLengths, minLength, maxLength, 5);
            result += PrintUtils.printMatrix(lengthsHist, null, Arrays.asList("Value", "Count"));

            result += "\nSegment Separations\n";
            double[][] separationsHist = histogram(separations, minSep, maxSep, 1);
            result += PrintUtils.printMatrix(separationsHist, null, Arrays.asList("Value", "Count"));

            result += "\nEntity Sizes\n";
            double[][] sizeHist = histogram(entitySizes, minSize, maxSize, 1);
            result += PrintUtils.printMatrix(sizeHist, null, Arrays.asList("Value", "Count"));
            return result;
        }

        @Override
        public void writeToCSV(boolean humanFriendly, CSV csv) {
            try {
                csv.println("Segment String Lengths");
                double[][] lengthsHist = histogram(messageLengths, minLength, maxLength, 5);
                PrintUtils.writeMatrixToCSV(csv, lengthsHist, null, Arrays.asList("Value", "Count"));

                csv.println("Segment Separations");
                double[][] separationsHist = histogram(separations, minSep, maxSep, 1);
                PrintUtils.writeMatrixToCSV(csv, separationsHist, null, Arrays.asList("Value", "Count"));

                csv.println("Segment Sizes (number of messages)");
                double[][] sizeHist = histogram(entitySizes, minSize, maxSize, 1);
                PrintUtils.writeMatrixToCSV(csv, sizeHist, null, Arrays.asList("Value", "Count"));
            } catch (IOException e) {
                System.err.println("Error writing to CSV");
                e.printStackTrace();
            }
        }

        @Override
        public String getExplanation() {
            return explanation;
        }
        private List<Double> messageLengths = new ArrayList<Double>();
        private double minLength = Double.POSITIVE_INFINITY;
        private double maxLength = Double.NEGATIVE_INFINITY;

        private void recordMessageLength(double length) {
            messageLengths.add(length);
            if (length < minLength) {
                minLength = length;
            }
            if (length > maxLength) {
                maxLength = length;
            }
        }
        private List<Double> separations = new ArrayList<Double>();
        private double minSep = Double.POSITIVE_INFINITY;
        private double maxSep = Double.NEGATIVE_INFINITY;

        private void recordEntitySeparation(double timeInSeconds) {
            separations.add(timeInSeconds);
            if (timeInSeconds < minSep) {
                minSep = timeInSeconds;
            }
            if (timeInSeconds > maxSep) {
                maxSep = timeInSeconds;
            }
        }
        private List<Double> entitySizes = new ArrayList<Double>();
        private double minSize = Double.POSITIVE_INFINITY;
        private double maxSize = Double.NEGATIVE_INFINITY;

        private void recordEntitySize(double size) {
            entitySizes.add(size);
            if (size < minSize) {
                minSize = size;
            }
            if (size > maxSize) {
                maxSize = size;
            }
        }

        private double[][] histogram(List<Double> data, double min, double max, double binSize) {
            double range = max - min;
            int numBins = (int) (range / binSize);

//            double[][] bins = new double[numBins + 1][2];
//            for (int i = 0; i < numBins; i++) {
//                bins[i][1] = 0;
//                bins[i][0] = i * range + min;
//            }

            HashMap<Double, Integer> counts = new HashMap<Double, Integer>();

            for (int i = 0; i < data.size(); i++) {
                double val = data.get(i);
                int bin = (int) (numBins * (val - min) / range);
                double normVal = bin * (range / numBins) + min;
//                bins[bin][1]++;
                if (counts.containsKey(normVal)) {
                    counts.put(normVal, counts.get(normVal) + 1);
                } else {
                    counts.put(normVal, 1);
                }
            }
            
            List<Double> keys = new ArrayList<Double>(counts.keySet());
            Collections.sort(keys);
            double[][] bins = new double[keys.size()][2];
            for (int i = 0; i < keys.size(); i++) {
                bins[i][0] = keys.get(i);
                bins[i][1] = counts.get(keys.get(i));
            }
            return bins;
        }
    }

    @Override
    public AnalysisResult analyze(EntitySet dataSet) {
        List<Timestamp> entityTimes = new ArrayList<Timestamp>();
        FrequencyResult result = new FrequencyResult();

        for (MultiRatedEntity entity : dataSet) {
            EntityMetaData meta = dataSet.getMetaData(entity.getEntityId());
            Timestamp start = meta.getStartTime();
            entityTimes.add(start);

            result.recordMessageLength(meta.concatMessages().length());
            result.recordEntitySize(meta.size());
        }

        Collections.sort(entityTimes);
        for (int i = 1; i < entityTimes.size(); i++) {
            Timestamp prev = entityTimes.get(i - 1);
            Timestamp curr = entityTimes.get(i);
            double diff = (curr.getTime() - prev.getTime()) / 1000.0;
            result.recordEntitySeparation(diff);
        }
        return result;
    }

    @Override
    public String getName() {
        return "Frequencies";
    }
}
