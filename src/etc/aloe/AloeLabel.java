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

import etc.aloe.controllers.LabelingController;
import etc.aloe.cscw2013.EvaluationImpl;
import etc.aloe.cscw2013.FeatureExtractionImpl;
import etc.aloe.cscw2013.LabelMappingImpl;
import etc.aloe.cscw2013.NullSegmentation;
import etc.aloe.cscw2013.ResolutionImpl;
import etc.aloe.cscw2013.ThresholdSegmentation;
import etc.aloe.data.EvaluationReport;
import etc.aloe.data.FeatureSpecification;
import etc.aloe.data.MessageSet;
import etc.aloe.data.Model;
import etc.aloe.data.SegmentSet;
import etc.aloe.processes.Segmentation;
import java.io.File;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

/**
 * Class that takes input data (may be labeled, unlabeled, or mixed) and
 * applies an existing model (and feature set) to it in order to generate
 * labels.
 *
 * If there are any labeled examples in the input data, an evaluation is
 * generated comparing the predicted labels to the true labels.
 *
 * @author Michael Brooks <mjbrooks@uw.edu
 */
public class AloeLabel extends Aloe {

    @Argument(index = 0, usage = "input CSV file containing messages", required = true, metaVar = "INPUT_CSV")
    private File inputCSVFile;

    @Argument(index = 1, usage = "output directory (contents may be overwritten)", required = true, metaVar = "OUTPUT_DIR")
    private void setOutputDir(File dir) {
        this.outputDir = dir;
        dir.mkdir();

        outputCSVFile = new File(dir, FileNames.OUTPUT_CSV_NAME);
        outputEvaluationReportFile = new File(dir, FileNames.OUTPUT_EVALUTION_REPORT_NAME);
    }
    private File outputDir;
    private File outputCSVFile;
    private File outputEvaluationReportFile;
    @Option(name = "--model", aliases = {"-m"}, usage = "use an existing model file", required = true, metaVar="MODEL_FILE")
    private File inputModelFile;
    @Option(name = "--features", aliases = {"-f"}, usage = "use an existing feature specification file", required = true, metaVar="FEATURES_FILE")
    private File inputFeatureSpecFile;
    @Option(name = "--fp-cost", usage = "the cost of a false positive (default 1)")
    private double falsePositiveCost = 1;
    @Option(name = "--fn-cost", usage = "the cost of a false negative (default 1)")
    private double falseNegativeCost = 1;

    @Override
    public void printUsage() {
        System.err.println("java -jar aloe.jar Label INPUT_CSV OUTPUT_DIR -m MODEL_FILE -f FEATURES_FILE [options...]");
    }

    @Override
    public void run() {
        System.out.println("== Preparation ==");

        //Normalize the cost factors (sum to 2)
        double costNormFactor = 0.5 * (falseNegativeCost + falsePositiveCost);
        falseNegativeCost /= costNormFactor;
        falsePositiveCost /= costNormFactor;
        System.out.println("Costs normalized to " + falseNegativeCost + " (FN) " + falsePositiveCost + " (FP).");

        //Set up the segmentation
        Segmentation segmentation;
        if (disableSegmentation) {
            segmentation = new NullSegmentation();
        } else {
            segmentation = new ThresholdSegmentation(this.segmentationThresholdSeconds, !ignoreParticipants);
            segmentation.setSegmentResolution(new ResolutionImpl());
        }

        //Create a labeling controller
        LabelingController labelingController = new LabelingController();

        //Provide implementations of the needed processes
        labelingController.setFeatureExtractionImpl(new FeatureExtractionImpl());
        labelingController.setEvaluationImpl(new EvaluationImpl(falsePositiveCost, falseNegativeCost));
        labelingController.setMappingImpl(new LabelMappingImpl());

        //Process the input messages
        MessageSet messages = this.loadMessages(dateFormatString, inputCSVFile);
        FeatureSpecification spec = this.loadFeatureSpecification(inputFeatureSpecFile);
        Model model = this.loadModel(inputModelFile);

        SegmentSet segments = segmentation.segment(messages);

        //Run the labeling process
        labelingController.setModel(model);
        labelingController.setSegmentSet(segments);
        labelingController.setFeatureSpecification(spec);
        labelingController.run();

        //Get the outputs
        EvaluationReport evalReport = labelingController.getEvaluationReport();

        System.out.println("== Saving Output ==");

        saveEvaluationReport(evalReport, outputEvaluationReportFile);
        saveMessages(messages, outputCSVFile);

        System.out.println("Testing Report:");
        System.out.println(evalReport);
        System.out.println("---------");
    }
}
