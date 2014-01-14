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
package etc.aloe.controllers;

import etc.aloe.data.EvaluationReport;
import etc.aloe.data.ExampleSet;
import etc.aloe.data.FeatureSpecification;
import etc.aloe.data.Model;
import etc.aloe.data.Predictions;
import etc.aloe.data.Segment;
import etc.aloe.data.SegmentSet;
import etc.aloe.processes.Balancing;
import etc.aloe.processes.CrossValidationPrep;
import etc.aloe.processes.CrossValidationSplit;
import etc.aloe.processes.FeatureExtraction;
import etc.aloe.processes.FeatureGeneration;
import etc.aloe.processes.LabelMapping;
import etc.aloe.processes.Training;

/**
 * Class that performs cross validation on segmented data and and produces an
 * evaluation report.
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class CrossValidationController {

    private int folds;
    private EvaluationReport evaluationReport;
    private SegmentSet segmentSet;
    private FeatureGeneration featureGenerationImpl;
    private FeatureExtraction featureExtractionImpl;
    private Training trainingImpl;
    private Balancing balancingImpl;
    private double falsePositiveCost = 1;
    private double falseNegativeCost = 1;
    private boolean balanceTestSet;
    private LabelMapping mappingImpl;

    public EvaluationReport getEvaluationReport() {
        return evaluationReport;
    }

    public void setSegmentSet(SegmentSet segments) {
        this.segmentSet = segments;
    }

    public CrossValidationController() {
    }

    public void setFolds(int folds) {
        this.folds = folds;
    }

    public void setCosts(double falsePositiveCost, double falseNegativeCost) {
        this.falsePositiveCost = falsePositiveCost;
        this.falseNegativeCost = falseNegativeCost;
    }

    public void run() {

        if (this.folds > 0) {

            System.out.println("== " + this.folds + "-Fold Cross Validation ==");

            segmentSet = segmentSet.onlyLabeled();

            //Prepare for cross validation
            System.out.println("Randomizing and stratifying segments.");
            CrossValidationPrep<Segment> validationPrep = new CrossValidationPrep<Segment>();
            validationPrep.randomize(segmentSet.getSegments());
            segmentSet.setSegments(validationPrep.stratify(segmentSet.getSegments(), folds));

            evaluationReport = new EvaluationReport(this.folds + " Cross Validation", falsePositiveCost, falseNegativeCost);
            for (int foldIndex = 0; foldIndex < this.folds; foldIndex++) {
                System.out.println("- Starting fold " + (foldIndex + 1));
                //Split the data
                CrossValidationSplit<Segment> split = new CrossValidationSplit<Segment>();

                System.out.println("- Splitting out training set");
                SegmentSet trainingSegments = new SegmentSet();
                trainingSegments.setSegments(split.getTrainingForFold(segmentSet.getSegments(), foldIndex, this.folds));
                if (getBalancingImpl() != null) {
                    trainingSegments = getBalancingImpl().balance(trainingSegments);
                }

                System.out.println("- Extracting basic features from training set");
                ExampleSet basicTrainingExamples = trainingSegments.getBasicExamples();
                trainingSegments = null;

                FeatureGeneration generation = getFeatureGenerationImpl();
                System.out.println("- Generating features");
                FeatureSpecification spec = generation.generateFeatures(basicTrainingExamples);

                FeatureExtraction extraction = getFeatureExtractionImpl();
                System.out.println("- Extracting features from training set");
                ExampleSet trainingSet = extraction.extractFeatures(basicTrainingExamples, spec);
                basicTrainingExamples = null;

                Training training = getTrainingImpl();
                Model model = training.train(trainingSet);
                trainingSet = null;
                
                System.out.println("- Splitting out test set");
                SegmentSet testingSegments = new SegmentSet();
                testingSegments.setSegments(split.getTestingForFold(segmentSet.getSegments(), foldIndex, this.folds));
                if (getBalancingImpl() != null && balanceTestSet) {
                    testingSegments = getBalancingImpl().balance(testingSegments);
                }

                System.out.println("- Extracting basic features from test set");
                ExampleSet basicTestingExamples = testingSegments.getBasicExamples();

                System.out.println("- Extracting features from test set");
                ExampleSet testingSet = extraction.extractFeatures(basicTestingExamples, spec);
                basicTestingExamples = null;

                Predictions predictions = model.getPredictions(testingSet);
                EvaluationReport report = new EvaluationReport("Fold " + (foldIndex + 1), falsePositiveCost, falseNegativeCost);
                report.addPredictions(predictions);
                
                LabelMapping mapping = getMappingImpl();
                mapping.map(predictions, testingSegments);
                report.addLabeledTestData(testingSegments);
                
                evaluationReport.addPartial(report);
                int numCorrect = report.getTrueNegativeCount() + report.getTruePositiveCount();
                System.out.println("- Fold " + (foldIndex + 1) + " completed (" + numCorrect + "/" + testingSet.size() + " correct).");
                System.out.println();
            }
        } else {
            System.out.println("== Skipping Cross Validation ==");
        }
    }

    public FeatureGeneration getFeatureGenerationImpl() {
        return this.featureGenerationImpl;
    }

    public void setFeatureGenerationImpl(FeatureGeneration featureGenerator) {
        this.featureGenerationImpl = featureGenerator;
    }

    public FeatureExtraction getFeatureExtractionImpl() {
        return this.featureExtractionImpl;
    }

    public void setFeatureExtractionImpl(FeatureExtraction featureExtractor) {
        this.featureExtractionImpl = featureExtractor;
    }

    public Training getTrainingImpl() {
        return this.trainingImpl;
    }

    public void setTrainingImpl(Training training) {
        this.trainingImpl = training;
    }

    public Balancing getBalancingImpl() {
        return this.balancingImpl;
    }

    public void setBalancingImpl(Balancing balancing) {
        this.balancingImpl = balancing;
    }

    public void setBalanceTestSet(boolean balanceTestSet) {
        this.balanceTestSet = balanceTestSet;
    }
    
    public LabelMapping getMappingImpl() {
        return this.mappingImpl;
    }

    public void setMappingImpl(LabelMapping mapping) {
        this.mappingImpl = mapping;
    }

}
