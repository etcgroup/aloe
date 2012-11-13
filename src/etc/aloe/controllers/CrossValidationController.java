package etc.aloe.controllers;

import etc.aloe.cscw2013.EvaluationImpl;
import etc.aloe.cscw2013.FeatureExtractionImpl;
import etc.aloe.cscw2013.FeatureGenerationImpl;
import etc.aloe.cscw2013.TrainingImpl;
import etc.aloe.data.EvaluationReport;
import etc.aloe.data.ExampleSet;
import etc.aloe.data.FeatureSpecification;
import etc.aloe.data.Model;
import etc.aloe.data.SegmentSet;
import etc.aloe.processes.Evaluation;
import etc.aloe.processes.FeatureExtraction;
import etc.aloe.processes.FeatureGeneration;
import etc.aloe.processes.Training;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that performs cross validation and produces results.
 */
public class CrossValidationController {

    private final int folds;
    private EvaluationReport evaluationReport;
    private SegmentSet segmentSet;
    private FeatureGeneration featureGenerationImpl;
    private FeatureExtraction featureExtractionImpl;
    private Training trainingImpl;
    private Evaluation evaluationImpl;

    public EvaluationReport getEvaluationReport() {
        return evaluationReport;
    }

    public void setSegmentSet(SegmentSet segments) {
        this.segmentSet = segments;
    }

    public CrossValidationController(int folds) {
        this.folds = folds;
    }

    public void run() {

        segmentSet.prepareForCrossValidation(this.folds);

        evaluationReport = new EvaluationReport();

        for (int foldIndex = 0; foldIndex < this.folds; foldIndex++) {

            SegmentSet trainingSegments = segmentSet.getTrainingForFold(foldIndex);
            SegmentSet testingSegments = segmentSet.getTestingForFold(foldIndex);

            FeatureGeneration generation = getFeatureGenerationImpl();
            FeatureSpecification spec = generation.generateFeatures(segmentSet);

            FeatureExtraction extraction = getFeatureExtractionImpl();
            ExampleSet trainingSet = extraction.extractFeatures(trainingSegments, spec);
            ExampleSet testingSet = extraction.extractFeatures(testingSegments, spec);

            Training training = getTrainingImpl();
            Model model = training.train(trainingSet);

            Evaluation evaluation = getEvaluationImpl();
            EvaluationReport report = evaluation.evaluate(model, testingSet);

            evaluationReport.addPartial(report);
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

    public Evaluation getEvaluationImpl() {
        return this.evaluationImpl;
    }

    public void setEvaluationImpl(Evaluation evaluation) {
        this.evaluationImpl = evaluation;
    }
}
