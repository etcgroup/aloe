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
import etc.aloe.controllers.LabelingController;
import etc.aloe.controllers.TrainingController;
import etc.aloe.data.EvaluationReport;
import etc.aloe.data.FeatureSpecification;
import etc.aloe.data.MessageSet;
import etc.aloe.data.Model;
import etc.aloe.data.SegmentSet;
import etc.aloe.options.LabelOptions;
import etc.aloe.options.ModeOptions;
import etc.aloe.options.TrainOptions;
import etc.aloe.processes.Segmentation;
import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Class that takes input training data, uses cross validation to evaluate the
 * model, then trains a final model on the full training set.
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class AloeTrainOnSplitData extends Aloe {

    @Override
    public void run(ModeOptions modeOptions) {
        System.out.println("== Preparation ==");

        if (modeOptions instanceof TrainOptions) {
            TrainOptions trainOptions = (TrainOptions) modeOptions;

            //Get and preprocess the data
            MessageSet messages = this.loadMessages(trainOptions.inputCSVFile);
            Segmentation segmentation = factory.constructSegmentation();
            SegmentSet segments = segmentation.segment(messages);

            //Create a training controller for making the final model
            TrainingController trainingController = new TrainingController();
            //Configure the training controller
            factory.configureTraining(trainingController);

            //Run the full training
            trainingController.setSegmentSet(segments);
            trainingController.run();

            //Get the fruits of our labors
            System.out.println("== Saving Output ==");

            //EvaluationReport evalReport = crossValidationController.getEvaluationReport();
            FeatureSpecification spec = trainingController.getFeatureSpecification();
            Model model = trainingController.getModel();
            List<String> topFeatures = trainingController.getTopFeatures();
            List<Map.Entry<String, Double>> featureWeights = trainingController.getFeatureWeights();

            saveFeatureSpecification(spec, trainOptions.outputFeatureSpecFile);
            saveModel(model, trainOptions.outputModelFile);
            saveTopFeatures(topFeatures, trainOptions.outputTopFeaturesFile);
            saveFeatureWeights(featureWeights, trainOptions.outputFeatureWeightsFile);
            
            
            //Do the labeling now
            
            //Set up the segmentation for labeling
            segmentation = factory.constructSegmentation();

            //Create a labeling controller
            LabelingController labelingController = new LabelingController();

            //Provide implementations of the needed processes for labeling
            labelingController.setFeatureExtractionImpl(factory.constructFeatureExtraction());
            labelingController.setEvaluationImpl(factory.constructEvaluation());
            labelingController.setMappingImpl(factory.constructLabelMapping());

            //Process the input messages for labeling
            messages = this.loadMessages(new File("data_for_frustration_test.csv"));

            segments = segmentation.segment(messages);

            //Run the labeling process
            labelingController.setModel(model);
            labelingController.setSegmentSet(segments);
            labelingController.setFeatureSpecification(spec);
            labelingController.run();
            
            EvaluationReport evalReport = labelingController.getEvaluationReport();
            System.out.println("== Saving Output ==");

            saveEvaluationReport(evalReport, new File("dist/output/outputEvaluationReportFile"));
            saveMessages(messages, new File("dist/output/outputCSVFile"));

            System.out.println("Testing Report:");
            System.out.println(evalReport);
            System.out.println("---------");

        } else {
            throw new IllegalArgumentException("Options must be for Training");
        }
    }
}
