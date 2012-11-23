/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package etc.aloe.controllers;

import etc.aloe.data.EvaluationReport;
import etc.aloe.data.ExampleSet;
import etc.aloe.data.FeatureSpecification;
import etc.aloe.data.Model;
import etc.aloe.data.SegmentSet;
import etc.aloe.processes.Evaluation;
import etc.aloe.processes.FeatureExtraction;
import etc.aloe.processes.LabelMapping;
import java.util.List;

/**
 *
 * @author kuksenok
 */
public class LabelingController {

    private SegmentSet segmentSet;
    private FeatureSpecification featureSpecification;
    private EvaluationReport evaluationReport;
    private Model model;
    private FeatureExtraction featureExtractionImpl;
    private LabelMapping predictionImpl;
    private Evaluation evaluationImpl;

    public void setSegmentSet(SegmentSet segments) {
        this.segmentSet = segments;
    }

    public void setFeatureSpecification(FeatureSpecification spec) {
        this.featureSpecification = spec;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public EvaluationReport getEvaluationReport() {
        return this.evaluationReport;
    }

    public void run() {

        //First extract features
        FeatureExtraction extraction = getFeatureExtractionImpl();
        ExampleSet examples = extraction.extractFeatures(segmentSet, featureSpecification);

        //Predict the labels
        List<Boolean> predictedLabels = this.model.getPredictedLabels(examples);

        //Map back onto messages
        LabelMapping prediction = getPredictionImpl();
        prediction.map(predictedLabels, segmentSet);

        //Evaluate the model on any labeled examples
        ExampleSet labeledExamples = examples.onlyLabeled();
        if (labeledExamples.size() > 0) {
            Evaluation evaluation = getEvaluationImpl();
            this.evaluationReport = evaluation.evaluate(this.model, labeledExamples);
        }
    }

    public FeatureExtraction getFeatureExtractionImpl() {
        return this.featureExtractionImpl;
    }

    public void setFeatureExtractionImpl(FeatureExtraction featureExtractor) {
        this.featureExtractionImpl = featureExtractor;
    }

    public LabelMapping getPredictionImpl() {
        return this.predictionImpl;
    }

    public void setPredictionImpl(LabelMapping prediction) {
        this.predictionImpl = prediction;
    }

    public Evaluation getEvaluationImpl() {
        return this.evaluationImpl;
    }

    public void setEvaluationImpl(Evaluation evaluation) {
        this.evaluationImpl = evaluation;
    }
}
