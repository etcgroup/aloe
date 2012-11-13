/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package etc.aloe.controllers;

import etc.aloe.cscw2013.EvaluationImpl;
import etc.aloe.cscw2013.FeatureExtractionImpl;
import etc.aloe.cscw2013.PredictionImpl;
import etc.aloe.data.EvaluationReport;
import etc.aloe.data.ExampleSet;
import etc.aloe.data.FeatureSpecification;
import etc.aloe.data.MessageSet;
import etc.aloe.data.Model;
import etc.aloe.data.SegmentSet;
import etc.aloe.processes.Evaluation;
import etc.aloe.processes.FeatureExtraction;
import etc.aloe.processes.Prediction;

/**
 *
 * @author kuksenok
 */
public class LabelingController {

    private SegmentSet segmentSet;
    private FeatureSpecification featureSpecification;
    private MessageSet labeledMessages;
    private EvaluationReport evaluationReport;
    private Model model;
    private MessageSet rawMessages;

    public void setRawMessageSet(MessageSet messages) {
        this.rawMessages = messages;
    }

    public void setSegmentSet(SegmentSet segments) {
        this.segmentSet = segments;
    }

    public void setFeatureSpecification(FeatureSpecification spec) {
        this.featureSpecification = spec;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public MessageSet getLabeledMessages() {
        return labeledMessages;
    }

    public EvaluationReport getEvaluationReport() {
        return this.evaluationReport;
    }

    public void run() {

        //First extract features
        FeatureExtraction extraction = new FeatureExtractionImpl();
        ExampleSet examples = extraction.extractFeatures(segmentSet, featureSpecification);

        //Predict the labels
        Prediction prediction = new PredictionImpl();
        this.labeledMessages = prediction.predict(examples, this.model, rawMessages);

        //Evaluate the model on any labeled examples
        ExampleSet labeledExamples = examples.onlyLabeled();
        if (labeledExamples.size() > 0) {
            Evaluation evaluation = new EvaluationImpl();
            this.evaluationReport = evaluation.evaluate(this.model, labeledExamples);
        }
    }
}
