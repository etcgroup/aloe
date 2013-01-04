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
package etc.aloe.factories;

import etc.aloe.controllers.CrossValidationController;
import etc.aloe.controllers.TrainingController;
import etc.aloe.cscw2013.CostTrainingImpl;
import etc.aloe.cscw2013.DownsampleBalancing;
import etc.aloe.cscw2013.EvaluationImpl;
import etc.aloe.cscw2013.FeatureExtractionImpl;
import etc.aloe.cscw2013.FeatureGenerationImpl;
import etc.aloe.cscw2013.LabelMappingImpl;
import etc.aloe.cscw2013.NullSegmentation;
import etc.aloe.cscw2013.ResolutionImpl;
import etc.aloe.cscw2013.SMOFeatureWeighting;
import etc.aloe.cscw2013.ThresholdSegmentation;
import etc.aloe.cscw2013.TrainingImpl;
import etc.aloe.cscw2013.UpsampleBalancing;
import etc.aloe.cscw2013.WekaModel;
import etc.aloe.data.Model;
import etc.aloe.filters.StringToDictionaryVector;
import etc.aloe.options.InteractiveOptions;
import etc.aloe.options.LabelOptions;
import etc.aloe.options.ModeOptions;
import etc.aloe.options.SingleOptions;
import etc.aloe.options.TrainOptions;
import etc.aloe.processes.Balancing;
import etc.aloe.processes.Evaluation;
import etc.aloe.processes.FeatureExtraction;
import etc.aloe.processes.FeatureGeneration;
import etc.aloe.processes.FeatureWeighting;
import etc.aloe.processes.LabelMapping;
import etc.aloe.processes.SegmentResolution;
import etc.aloe.processes.Segmentation;
import etc.aloe.processes.Training;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import org.kohsuke.args4j.Option;

/**
 * Provides implementations for the CSCW 2013 pipeline.
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class CSCW2013 implements PipelineFactory {

    public ModeOptions options;
    private double falseNegativeCost = 1;
    private double falsePositiveCost = 1;

    @Override
    public void initialize() {
        if (options instanceof TrainOptionsImpl) {
            TrainOptionsImpl trainOpts = (TrainOptionsImpl) options;
            falseNegativeCost = trainOpts.falseNegativeCost;
            falsePositiveCost = trainOpts.falsePositiveCost;
        } else if (options instanceof LabelOptionsImpl) {
            LabelOptionsImpl labelOpts = (LabelOptionsImpl) options;
            falseNegativeCost = labelOpts.falseNegativeCost;
            falsePositiveCost = labelOpts.falsePositiveCost;
        }

        //Normalize the cost factors (sum to 2)
        double costNormFactor = 0.5 * (falseNegativeCost + falsePositiveCost);
        falseNegativeCost /= costNormFactor;
        falsePositiveCost /= costNormFactor;
        System.out.println("Costs normalized to " + falseNegativeCost + " (FN) " + falsePositiveCost + " (FP).");

        if (options instanceof TrainOptionsImpl) {
            TrainOptionsImpl trainOpts = (TrainOptionsImpl) options;
            trainOpts.falseNegativeCost = falseNegativeCost;
            trainOpts.falsePositiveCost = falsePositiveCost;
        } else if (options instanceof LabelOptionsImpl) {
            LabelOptionsImpl labelOpts = (LabelOptionsImpl) options;
            labelOpts.falseNegativeCost = falseNegativeCost;
            labelOpts.falsePositiveCost = falsePositiveCost;
        }
    }

    protected List<String> loadTermList(File emoticonFile) {
        try {
            return StringToDictionaryVector.readDictionaryFile(emoticonFile);
        } catch (FileNotFoundException ex) {
            System.err.println("Unable to read emoticon dictionary file " + emoticonFile);
            System.err.println("\t" + ex.getMessage());
            System.exit(1);
        }
        return null;
    }

    @Override
    public Model constructModel() {
        return new WekaModel();
    }

    @Override
    public Evaluation constructEvaluation() {
        return new EvaluationImpl(falsePositiveCost, falseNegativeCost);
    }

    @Override
    public FeatureExtraction constructFeatureExtraction() {
        return new FeatureExtractionImpl();
    }

    @Override
    public FeatureGeneration constructFeatureGeneration() {
        if (options instanceof TrainOptionsImpl) {
            TrainOptionsImpl trainOpts = (TrainOptionsImpl) options;
            //Read the emoticons
            List<String> termList = loadTermList(trainOpts.emoticonFile);
            return new FeatureGenerationImpl(termList);
        } else {
            throw new IllegalArgumentException("Options not for Training");
        }
    }

    @Override
    public LabelMapping constructLabelMapping() {
        return new LabelMappingImpl();
    }

    @Override
    public SegmentResolution constructSegmentResolution() {
        return new ResolutionImpl();
    }

    @Override
    public FeatureWeighting constructFeatureWeighting() {
        return new SMOFeatureWeighting();
    }

    @Override
    public Segmentation constructSegmentation() {
        boolean disableSegmentation = false;
        int segmentationThresholdSeconds = 30;
        boolean ignoreParticipants = false;

        if (options instanceof TrainOptionsImpl) {
            TrainOptionsImpl trainOpts = (TrainOptionsImpl) options;
            disableSegmentation = trainOpts.disableSegmentation;
            segmentationThresholdSeconds = trainOpts.segmentationThresholdSeconds;
            ignoreParticipants = trainOpts.ignoreParticipants;
        } else if (options instanceof LabelOptionsImpl) {
            LabelOptionsImpl labelOpts = (LabelOptionsImpl) options;
            disableSegmentation = labelOpts.disableSegmentation;
            segmentationThresholdSeconds = labelOpts.segmentationThresholdSeconds;
            ignoreParticipants = labelOpts.ignoreParticipants;
        } else {
            throw new IllegalArgumentException("Options should be for Training or Labeling");
        }

        if (disableSegmentation) {
            return new NullSegmentation();
        } else {
            Segmentation segmentation = new ThresholdSegmentation(segmentationThresholdSeconds,
                    !ignoreParticipants);
            segmentation.setSegmentResolution(new ResolutionImpl());
            return segmentation;
        }
    }

    @Override
    public Training constructTraining() {
        if (options instanceof TrainOptionsImpl) {
            TrainOptionsImpl trainOpts = (TrainOptionsImpl) options;
            Training trainingImpl = new TrainingImpl();
            if (trainOpts.useMinCost || trainOpts.useReweighting) {
                trainingImpl = new CostTrainingImpl(trainOpts.falsePositiveCost, trainOpts.falseNegativeCost, trainOpts.useReweighting);
            }
            return trainingImpl;
        } else {
            throw new IllegalArgumentException("Options must be for Training");
        }
    }

    @Override
    public Balancing constructBalancing() {
        if (options instanceof TrainOptionsImpl) {
            TrainOptionsImpl trainOpts = (TrainOptionsImpl) options;
            if (trainOpts.useDownsampling) {
                return new DownsampleBalancing(trainOpts.falsePositiveCost, trainOpts.falseNegativeCost);
            } else if (trainOpts.useUpsampling) {
                return new UpsampleBalancing(trainOpts.falsePositiveCost, trainOpts.falseNegativeCost);
            } else {
                return null;
            }
        } else {
            throw new IllegalArgumentException("Options must be for Training");
        }
    }

    @Override
    public void configureCrossValidation(CrossValidationController crossValidationController) {
        if (options instanceof TrainOptionsImpl) {
            TrainOptionsImpl trainOpts = (TrainOptionsImpl) options;
            //Implementations
            crossValidationController.setFeatureGenerationImpl(this.constructFeatureGeneration());
            crossValidationController.setFeatureExtractionImpl(this.constructFeatureExtraction());
            crossValidationController.setTrainingImpl(this.constructTraining());
            crossValidationController.setEvaluationImpl(this.constructEvaluation());
            crossValidationController.setBalancingImpl(this.constructBalancing());

            //Options
            crossValidationController.setFolds(trainOpts.crossValidationFolds);
            crossValidationController.setCosts(trainOpts.falsePositiveCost, trainOpts.falseNegativeCost);
            crossValidationController.setBalanceTestSet(trainOpts.balanceTestSet);
        } else {
            throw new IllegalArgumentException("Options must be for Training");
        }
    }

    @Override
    public void configureTraining(TrainingController trainingController) {
        trainingController.setFeatureGenerationImpl(this.constructFeatureGeneration());
        trainingController.setFeatureExtractionImpl(this.constructFeatureExtraction());
        trainingController.setTrainingImpl(this.constructTraining());
        trainingController.setFeatureWeightingImpl(this.constructFeatureWeighting());
        trainingController.setBalancingImpl(this.constructBalancing());
    }

    @Override
    public DateFormat constructDateFormat() {
        return new SimpleDateFormat(options.dateFormatString);
    }

    @Override
    public InteractiveOptions constructInteractiveOptions() {
        return new InteractiveOptionsImpl();
    }

    @Override
    public LabelOptions constructLabelOptions() {
        return new LabelOptionsImpl();
    }

    @Override
    public TrainOptions constructTrainOptions() {
        return new TrainOptionsImpl();
    }

    @Override
    public SingleOptions constructSingleOptions() {
        return new SingleOptionsImpl();
    }

    @Override
    public void setOptions(ModeOptions options) {
        this.options = options;
    }

    static class InteractiveOptionsImpl extends InteractiveOptions {
    }

    static class SingleOptionsImpl extends SingleOptions {
    }

    static class LabelOptionsImpl extends LabelOptions {

        @Option(name = "--fp-cost", usage = "the cost of a false positive (default 1)", metaVar = "COST")
        public double falsePositiveCost = 1;
        @Option(name = "--fn-cost", usage = "the cost of a false negative (default 1)", metaVar = "COST")
        public double falseNegativeCost = 1;
        @Option(name = "--ignore-participants", usage = "ignore participants during segmentation")
        public boolean ignoreParticipants = false;
        @Option(name = "--threshold", aliases = {"-t"}, usage = "segmentation threshold in seconds (default 30)", metaVar = "SECONDS")
        public int segmentationThresholdSeconds = 30;
        @Option(name = "--no-segmentation", usage = "disable segmentation (each message is in its own segment)")
        public boolean disableSegmentation = false;
    }

    static class TrainOptionsImpl extends TrainOptions {

        @Option(name = "--fp-cost", usage = "the cost of a false positive (default 1)", metaVar = "COST")
        public double falsePositiveCost = 1;
        @Option(name = "--fn-cost", usage = "the cost of a false negative (default 1)", metaVar = "COST")
        public double falseNegativeCost = 1;
        @Option(name = "--ignore-participants", usage = "ignore participants during segmentation")
        public boolean ignoreParticipants = false;
        @Option(name = "--threshold", aliases = {"-t"}, usage = "segmentation threshold in seconds (default 30)", metaVar = "SECONDS")
        public int segmentationThresholdSeconds = 30;
        @Option(name = "--no-segmentation", usage = "disable segmentation (each message is in its own segment)")
        public boolean disableSegmentation = false;
        @Option(name = "--upsample", aliases = {"-us"}, usage = "upsample the minority class in training sets to match the cost ratio")
        public boolean useUpsampling = false;
        @Option(name = "--reweight", aliases = {"-rw"}, usage = "reweight the training data")
        public boolean useReweighting = false;
        @Option(name = "--min-cost", usage = "train a classifier that uses the min-cost criterion")
        public boolean useMinCost = false;
        @Option(name = "--downsample", aliases = {"-ds"}, usage = "downsample the majority class in training sets to match the cost ratio")
        public boolean useDownsampling = false;
        @Option(name = "--folds", aliases = {"-k"}, usage = "number of cross-validation folds (default 10, 0 to disable cross validation)", metaVar = "FOLDS")
        public int crossValidationFolds = 10;
        @Option(name = "--balance-test-set", usage = "apply balancing to the test set as well as the training set")
        public boolean balanceTestSet = false;
        @Option(name = "--emoticons", aliases = {"-e"}, usage = "emoticon dictionary file (default emoticons.txt)")
        public File emoticonFile = new File("emoticons.txt");
    }
}
