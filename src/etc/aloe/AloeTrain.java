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
import etc.aloe.data.ROC;
import etc.aloe.data.SegmentSet;
import etc.aloe.options.ModeOptions;
import etc.aloe.options.TrainOptions;
import etc.aloe.processes.Segmentation;
import java.io.File;
import java.util.List;
import java.util.Map;
import weka.core.Instances;

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

            saveCommand(options.outputCommandFile);
            
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
            crossValidationController.run();

            EvaluationReport evalReport = crossValidationController.getEvaluationReport();
            if (evalReport != null) {
                System.out.println("== Saving Results of Cross Validation ==");
                
                saveEvaluationReport(evalReport, options.outputEvaluationReportFile);
                
                System.out.println("Aggregated cross-validation report:");
                System.out.println(evalReport);
                System.out.println("---------");

                if (options.makeROC) {
                    options.outputROCDir.mkdirs();
                    
                    for (ROC roc : evalReport.getROCs()) {
                        String fileName = roc.getName() + FileNames.ROC_SUFFIX;
                        File outputFile = new File(options.outputROCDir, fileName);

                        saveROC(roc, outputFile);
                    }
                }
                
                if (options.outputTests) {
                    options.outputTestsDir.mkdirs();
                    List<SegmentSet> testSets = evalReport.getTestSets();
                    List<String> testSetNames = evalReport.getTestSetNames();
                    
                    SegmentSet combined = new SegmentSet();
                    
                    for (int i = 0; i < testSets.size(); i++) {
                        String fileName = testSetNames.get(i) + FileNames.TEST_DATA_SUFFIX;
                        SegmentSet testSet = testSets.get(i);
                        combined.addAll(testSet.getSegments());
                        
                        File outputFile = new File(options.outputTestsDir, fileName);
                        
                        saveMessages(testSet.getMessages(messages), outputFile);
                    }
                    
                    String fileName = FileNames.OUTPUT_TEST_DATA_COMBINED_NAME;
                    File outputFile = new File(options.outputTestsDir, fileName);
                    saveMessages(combined.getMessages(messages), outputFile);

                }
            }
            
            
            //Create a training controller for making the final model
            TrainingController trainingController = new TrainingController();
            //Configure the training controller
            factory.configureTraining(trainingController);

            //Run the full training
            trainingController.setSegmentSet(segments);
            trainingController.run();

            //Get the fruits of our labors
            System.out.println("== Saving Output ==");

            FeatureSpecification spec = trainingController.getFeatureSpecification();
            Model model = trainingController.getModel();
            List<String> topFeatures = trainingController.getTopFeatures();
            List<Map.Entry<String, Double>> featureWeights = trainingController.getFeatureWeights();

            saveFeatureSpecification(spec, options.outputFeatureSpecFile);
            saveModel(model, options.outputModelFile);
            saveTopFeatures(topFeatures, options.outputTopFeaturesFile);
            saveFeatureWeights(featureWeights, options.outputFeatureWeightsFile);
            
            if (options.outputFeatureValues) {
                Instances featureValues = trainingController.getFeatureValues();
                saveInstances(featureValues, options.outputFeatureValuesFile);
            }
        } else {
            throw new IllegalArgumentException("Options must be for Training");
        }
    }
}
