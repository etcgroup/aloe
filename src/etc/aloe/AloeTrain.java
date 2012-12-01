/*
 * This file is part of ALOE.
 *
 * ALOE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * ALOE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with ALOE.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2012 SCCL, University of Washington (http://depts.washington.edu/sccl)
 */
package etc.aloe;

import etc.aloe.controllers.CrossValidationController;
import etc.aloe.controllers.TrainingController;
import etc.aloe.cscw2013.CostTrainingImpl;
import etc.aloe.cscw2013.CrossValidationPrepImpl;
import etc.aloe.cscw2013.CrossValidationSplitImpl;
import etc.aloe.cscw2013.DownsampleBalancing;
import etc.aloe.cscw2013.EvaluationImpl;
import etc.aloe.cscw2013.FeatureExtractionImpl;
import etc.aloe.cscw2013.FeatureGenerationImpl;
import etc.aloe.cscw2013.NullSegmentation;
import etc.aloe.cscw2013.ResolutionImpl;
import etc.aloe.cscw2013.SMOFeatureWeighting;
import etc.aloe.cscw2013.ThresholdSegmentation;
import etc.aloe.cscw2013.TrainingImpl;
import etc.aloe.cscw2013.UpsampleBalancing;
import etc.aloe.data.EvaluationReport;
import etc.aloe.data.FeatureSpecification;
import etc.aloe.data.MessageSet;
import etc.aloe.data.Model;
import etc.aloe.data.Segment;
import etc.aloe.data.SegmentSet;
import etc.aloe.processes.Segmentation;
import etc.aloe.processes.Training;
import java.io.File;
import java.util.List;
import java.util.Map;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

/**
 * Class that takes input training data, uses cross validation to evaluate the
 * model, then trains a final model on the full training set.
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class AloeTrain extends Aloe {

    @Argument(index = 0, usage = "input CSV file containing messages", required = true, metaVar = "INPUT_CSV")
    private File inputCSVFile;

    @Argument(index = 1, usage = "output directory (contents may be overwritten)", required = true, metaVar = "OUTPUT_DIR")
    private void setOutputDir(File dir) {
        this.outputDir = dir;
        dir.mkdir();

        outputEvaluationReportFile = new File(dir, FileNames.OUTPUT_EVALUTION_REPORT_NAME);
        outputFeatureSpecFile = new File(dir, FileNames.OUTPUT_FEATURE_SPEC_NAME);
        outputModelFile = new File(dir, FileNames.OUTPUT_MODEL_NAME);
        outputTopFeaturesFile = new File(dir, FileNames.OUTPUT_TOP_FEATURES_NAME);
        outputFeatureWeightsFile = new File(dir, FileNames.OUTPUT_FEATURE_WEIGHTS_NAME);
    }
    private File outputDir;
    private File outputEvaluationReportFile;
    private File outputFeatureSpecFile;
    private File outputModelFile;
    private File outputTopFeaturesFile;
    private File outputFeatureWeightsFile;
    private List<String> termList;
    @Option(name = "--folds", aliases = {"-k"}, usage = "number of cross-validation folds (default 10, 0 to disable cross validation)", metaVar = "FOLDS")
    private int crossValidationFolds = 10;
    @Option(name = "--emoticons", aliases = {"-e"}, usage = "emoticon dictionary file (default emoticons.txt)")
    private File emoticonFile = new File("emoticons.txt");
    @Option(name = "--downsample", aliases = {"-ds"}, usage = "downsample the majority class in training sets to match the cost ratio")
    private boolean useDownsampling = false;
    @Option(name = "--upsample", aliases = {"-us"}, usage = "upsample the minority class in training sets to match the cost ratio")
    private boolean useUpsampling = false;
    @Option(name = "--reweight", aliases = {"-rw"}, usage = "reweight the training data")
    private boolean useReweighting = false;
    @Option(name = "--min-cost", usage = "train a classifier that uses the min-cost criterion")
    private boolean useMinCost = false;
    @Option(name = "--fp-cost", usage = "the cost of a false positive (default 1)", metaVar = "COST")
    private double falsePositiveCost = 1;
    @Option(name = "--fn-cost", usage = "the cost of a false negative (default 1)", metaVar = "COST")
    private double falseNegativeCost = 1;
    @Option(name = "--balance-test-set", usage = "apply balancing to the test set as well as the training set")
    private boolean balanceTestSet = false;

    @Override
    public void printUsage() {
        System.err.println("java -jar aloe.jar train INPUT_CSV OUTPUT_DIR [options...]");
    }

    @Override
    public void run() {
        System.out.println("== Preparation ==");

        //Read the emoticons
        termList = loadTermList(emoticonFile);

        //Normalize the costs (to sum to 2)
        double costNormFactor = 0.5 * (falseNegativeCost + falsePositiveCost);
        falseNegativeCost /= costNormFactor;
        falsePositiveCost /= costNormFactor;
        System.out.println("Costs normalized to " + falseNegativeCost + " (FN) " + falsePositiveCost + " (FP).");

        CrossValidationController crossValidationController = null;
        if (crossValidationFolds > 0) {
            //If we're doing cross validation, set up a cross validation controller.
            crossValidationController = new CrossValidationController(this.crossValidationFolds);

            //Provide implementations of the needed processes
            crossValidationController.setCrossValidationPrepImpl(new CrossValidationPrepImpl<Segment>());
            crossValidationController.setCrossValidationSplitImpl(new CrossValidationSplitImpl<Segment>());
            crossValidationController.setFeatureGenerationImpl(new FeatureGenerationImpl(termList));
            crossValidationController.setFeatureExtractionImpl(new FeatureExtractionImpl());
            Training trainingImpl = new TrainingImpl();
            if (useMinCost || useReweighting) {
                trainingImpl = new CostTrainingImpl(falsePositiveCost, falseNegativeCost, useReweighting);
            }
            crossValidationController.setTrainingImpl(trainingImpl);
            crossValidationController.setEvaluationImpl(new EvaluationImpl(falsePositiveCost, falseNegativeCost));
            if (useDownsampling) {
                crossValidationController.setBalancingImpl(new DownsampleBalancing(falsePositiveCost, falseNegativeCost));
            } else if (useUpsampling) {
                crossValidationController.setBalancingImpl(new UpsampleBalancing(falsePositiveCost, falseNegativeCost));
            }

            //Set some other options
            crossValidationController.setCosts(falsePositiveCost, falseNegativeCost);
            crossValidationController.setBalanceTestSet(balanceTestSet);
        }

        //Create a training controller for making the final model
        TrainingController trainingController = new TrainingController();

        //Implementations for the training controller
        trainingController.setFeatureGenerationImpl(new FeatureGenerationImpl(termList));
        trainingController.setFeatureExtractionImpl(new FeatureExtractionImpl());
        Training trainingImpl = new TrainingImpl();
        if (useMinCost || useReweighting) {
            trainingImpl = new CostTrainingImpl(falsePositiveCost, falseNegativeCost, useReweighting);
        }
        trainingController.setTrainingImpl(trainingImpl);
        trainingController.setFeatureWeightingImpl(new SMOFeatureWeighting());
        if (useDownsampling) {
            trainingController.setBalancingImpl(new DownsampleBalancing(falsePositiveCost, falseNegativeCost));
        } else if (useUpsampling) {
            trainingController.setBalancingImpl(new UpsampleBalancing(falsePositiveCost, falseNegativeCost));
        }

        //Get and preprocess the data
        MessageSet messages = this.loadMessages(dateFormatString, inputCSVFile);
        Segmentation segmentation;
        if (disableSegmentation) {
            segmentation = new NullSegmentation();
        } else {
            segmentation = new ThresholdSegmentation(this.segmentationThresholdSeconds, !ignoreParticipants);
            segmentation.setSegmentResolution(new ResolutionImpl());
        }
        SegmentSet segments = segmentation.segment(messages);

        //Run cross validation
        if (crossValidationFolds > 0) {
            crossValidationController.setSegmentSet(segments);
            crossValidationController.run();
        } else {
            System.out.println("== Skipping Cross Validation ==");
        }

        //Run the full training
        trainingController.setSegmentSet(segments);
        trainingController.run();

        //Get the fruits of our labors
        System.out.println("== Saving Output ==");

        EvaluationReport evalReport = null;
        if (crossValidationFolds > 0) {
            evalReport = crossValidationController.getEvaluationReport();
            saveEvaluationReport(evalReport, outputEvaluationReportFile);
        }
        FeatureSpecification spec = trainingController.getFeatureSpecification();
        Model model = trainingController.getModel();
        List<String> topFeatures = trainingController.getTopFeatures();
        List<Map.Entry<String, Double>> featureWeights = trainingController.getFeatureWeights();

        saveFeatureSpecification(spec, outputFeatureSpecFile);
        saveModel(model, outputModelFile);
        saveTopFeatures(topFeatures, outputTopFeaturesFile);
        saveFeatureWeights(featureWeights, outputFeatureWeightsFile);
        if (evalReport != null) {
            System.out.println("Aggregated cross-validation report:");
            System.out.println(evalReport);
            System.out.println("---------");
        }
    }
}
