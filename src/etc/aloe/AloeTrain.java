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
import etc.aloe.data.EvaluationReport;
import etc.aloe.data.FeatureSpecification;
import etc.aloe.data.MessageSet;
import etc.aloe.data.Model;
import etc.aloe.data.SegmentSet;
import etc.aloe.options.ModeOptions;
import etc.aloe.options.TrainOptions;
import etc.aloe.processes.Segmentation;
import java.util.List;
import java.util.Map;

/**
 * Class that takes input training data, uses cross validation to evaluate the
 * model, then trains a final model on the full training set.
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class AloeTrain extends Aloe {

    @Override
    public void run(ModeOptions modeOptions) {
        System.out.println("== Preparation ==");

        if (modeOptions instanceof TrainOptions) {
            TrainOptions options = (TrainOptions) modeOptions;

            //Get and preprocess the data
            MessageSet messages = this.loadMessages(options.inputCSVFile);
            Segmentation segmentation = factory.constructSegmentation();
            SegmentSet segments = segmentation.segment(messages);

            //Set up a cross validation controller.
            CrossValidationController crossValidationController = new CrossValidationController();
            //Configure controller
            factory.configureCrossValidation(crossValidationController);

            //Run cross validation
            crossValidationController.setSegmentSet(segments);
            //crossValidationController.run();

            //Create a training controller for making the final model
            TrainingController trainingController = new TrainingController();
            //Configure the training controller
            factory.configureTraining(trainingController);

            //Run the full training
            trainingController.setSegmentSet(segments);
            trainingController.run();

            //Get the fruits of our labors
            System.out.println("== Saving Output ==");

            EvaluationReport evalReport = crossValidationController.getEvaluationReport();
            FeatureSpecification spec = trainingController.getFeatureSpecification();
            Model model = trainingController.getModel();
            List<String> topFeatures = trainingController.getTopFeatures();
            List<Map.Entry<String, Double>> featureWeights = trainingController.getFeatureWeights();

            saveFeatureSpecification(spec, options.outputFeatureSpecFile);
            saveModel(model, options.outputModelFile);
            saveTopFeatures(topFeatures, options.outputTopFeaturesFile);
            saveFeatureWeights(featureWeights, options.outputFeatureWeightsFile);
            if (evalReport != null) {
                saveEvaluationReport(evalReport, options.outputEvaluationReportFile);
                System.out.println("Aggregated cross-validation report:");
                System.out.println(evalReport);
                System.out.println("---------");
            }

        } else {
            throw new IllegalArgumentException("Options must be for Training");
        }
    }
}
