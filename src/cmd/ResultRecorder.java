/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cmd;

import cmd.config.ClassifierConfig;
import cmd.config.FeatureConfig;
import cmd.config.ParameterizedFeatureConfig;
import cmd.config.exp.ExperimentConfig;
import daisy.io.CSV;
import data.indexes.CodeNames;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import weka.classifiers.Evaluation;

/**
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class ResultRecorder {

    private final String destinationDir;
    private CSV csv;
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private DateFormat fileDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
    public ResultRecorder(String destinationDir) {
        this.destinationDir = destinationDir;
    }

    public String getDestinationDir() {
        return destinationDir;
    }

    
    
    public void open() throws IOException {
        File dest = new File(destinationDir);
        if (!dest.exists()) {
            if (!dest.mkdirs()) {
                throw new IllegalArgumentException("Destination directory could not be created");
            }
        }
        if (!dest.isDirectory()) {
            throw new IllegalArgumentException("Destination is not a directory");
        }
        
        ArrayList<String> headers = getHeaders();

        String now = fileDateFormat.format(new Date());
        String filename = destinationDir + "/classifier_results." + now + ".csv";
        
        this.csv = new CSV(filename, headers);
    }

    private ArrayList<String> getHeaders() {
        ArrayList<String> headers = new ArrayList<String>();
        
        headers.add("DataName");
        headers.add("StartTime");
        headers.add("StopTime");
        headers.add("CodeId");
        headers.add("CodeName");
        headers.add("Runs");
        headers.add("FoldsPerRun");
        
        headers.add("e_TestSetPercent");
        headers.add("e_BalancingStrategy");

        headers.add("FeaturesName");
        headers.add("NumFeatures");
        
        FeatureConfig features = new ParameterizedFeatureConfig("null");
        String[] featuresHeader = features.describeFeaturesHeader();
        for (int i = 0; i < featuresHeader.length; i++) {
            headers.add("f_" + featuresHeader[i]);
        }

        headers.add("c_Type");
        headers.add("c_Options");

        headers.add("AvgNumInstances");
        headers.add("AvgNumPositive");
        headers.add("AvgNumNegative");

        headers.add("AvgNumIncorrect");
        headers.add("AvgErrorRate");

        headers.add("AvgFalsePositives");
        headers.add("AvgFalsePositiveRate");

        headers.add("AvgTruePositives");
        headers.add("AvgTruePositiveRate");

        headers.add("AvgFalseNegatives");
        headers.add("AvgFalseNegativeRate");

        headers.add("AvgTrueNegatives");
        headers.add("AvgTrueNegativeRate");

        headers.add("AvgPrecision");
        headers.add("AvgRecall");

        headers.add("AvgFMeasure");

        return headers;
    }

    void recordResult(String dataName, Date startTime, Date stopTime, int codeId, ExperimentConfig experimentConfig, FeatureConfig featureConfig, ClassifierConfig classifierConfig, List<Evaluation> evaluations) {
        String codeName = CodeNames.instance.get(codeId);

        //Experiment config factors
        int testSetPercent = experimentConfig.getTestSetPercent();
        String balancingStrategy = experimentConfig.describeBalancingStrategy();

        //Feature config factors
        String[] featureDesc = featureConfig.describeFeatures();

        String classifierType = classifierConfig.getClassifierType();
        String classifierOptions = classifierConfig.getConfigString();

        int runs = evaluations.size();
        int folds = experimentConfig.getNumFolds();

        int numFeatures = 0;
        
        double avgNumInstances = 0;
        double avgNumPositive = 0;
        double avgNumNegative = 0;

        double avgNumIncorrect = 0;
        double avgErrorRate = 0;

        double avgFalsePositives = 0;
        double avgFalsePositiveRate = 0;

        double avgTruePositives = 0;
        double avgTruePositiveRate = 0;

        double avgFalseNegatives = 0;
        double avgFalseNegativeRate = 0;

        double avgTrueNegatives = 0;
        double avgTrueNegativeRate = 0;

        double avgPrecision = 0;
        double avgRecall = 0;
        double avgFMeasure = 0;

        int positiveClass = 1;
        for (int r = 0; r < runs; r++) {
            Evaluation eval = evaluations.get(r);
            
            numFeatures += eval.getHeader().numAttributes() - 1;
            
            double[][] confusionMatrix = eval.confusionMatrix();

            avgNumInstances += eval.numInstances();

            //m_ConfusionMatrix[actualClass][predictedClass]
            avgNumPositive += confusionMatrix[1][0] + confusionMatrix[1][1];
            avgNumNegative += confusionMatrix[0][0] + confusionMatrix[0][1];

            avgNumIncorrect += eval.incorrect();
            avgErrorRate += eval.errorRate();

            avgFalsePositives += eval.numFalsePositives(positiveClass);
            avgFalsePositiveRate += eval.falsePositiveRate(positiveClass);

            avgTruePositives += eval.numTruePositives(positiveClass);
            avgTruePositiveRate += eval.truePositiveRate(positiveClass);

            avgFalseNegatives += eval.numFalseNegatives(positiveClass);
            avgFalseNegativeRate += eval.falseNegativeRate(positiveClass);

            avgTrueNegatives += eval.numTrueNegatives(positiveClass);
            avgTrueNegativeRate += eval.trueNegativeRate(positiveClass);

            avgPrecision += eval.precision(positiveClass);
            avgRecall += eval.recall(positiveClass);

            avgFMeasure += eval.fMeasure(positiveClass);
        }

        numFeatures /= runs;
        avgNumInstances /= runs;
        avgNumPositive /= runs;
        avgNumNegative /= runs;
        avgNumIncorrect /= runs;
        avgErrorRate /= runs;
        avgFalsePositives /= runs;
        avgFalsePositiveRate /= runs;
        avgTruePositives /= runs;
        avgTruePositiveRate /= runs;
        avgFalseNegatives /= runs;
        avgFalseNegativeRate /= runs;
        avgTrueNegatives /= runs;
        avgTrueNegativeRate /= runs;
        avgPrecision /= runs;
        avgRecall /= runs;
        avgFMeasure /= runs;

        ArrayList<String> values = new ArrayList<String>();
        values.add(dataName);
        values.add(dateFormat.format(startTime));
        values.add(dateFormat.format(stopTime));
        values.add(Integer.toString(codeId));
        values.add(codeName);
        values.add(Integer.toString(runs));
        values.add(Integer.toString(folds));

        values.add(testSetPercent + "%");
        values.add(balancingStrategy);
        
        values.add(featureConfig.getName());
        values.add(Integer.toString(numFeatures));
        values.addAll(Arrays.asList(featureDesc));

        values.add(classifierType);
        values.add(classifierOptions);

        values.add(Double.toString(avgNumInstances));
        values.add(Double.toString(avgNumPositive));
        values.add(Double.toString(avgNumNegative));
        
        values.add(Double.toString(avgNumIncorrect));
        values.add(Double.toString(avgErrorRate));

        values.add(Double.toString(avgFalsePositives));
        values.add(Double.toString(avgFalsePositiveRate));

        values.add(Double.toString(avgTruePositives));
        values.add(Double.toString(avgTruePositiveRate));

        values.add(Double.toString(avgFalseNegatives));
        values.add(Double.toString(avgFalseNegativeRate));

        values.add(Double.toString(avgTrueNegatives));
        values.add(Double.toString(avgTrueNegativeRate));

        values.add(Double.toString(avgPrecision));
        values.add(Double.toString(avgRecall));

        values.add(Double.toString(avgFMeasure));
        
        try {
            csv.println(values.toArray(new String[]{}));
        } catch (IOException ex) {
            System.err.println("ERROR RECORDING RESULT!");
            ex.printStackTrace();
        }
    }
}
