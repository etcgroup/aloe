/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package etc.aloe.controllers;

import etc.aloe.data.ExampleSet;
import etc.aloe.data.FeatureSpecification;
import etc.aloe.data.Model;
import etc.aloe.data.SegmentSet;
import etc.aloe.processes.Balancing;
import etc.aloe.processes.FeatureExtraction;
import etc.aloe.processes.FeatureGeneration;
import etc.aloe.processes.FeatureWeighting;
import etc.aloe.processes.Training;
import java.util.List;
import java.util.Map;

/**
 *
 * @author kuksenok
 */
public class TrainingController {

    private SegmentSet segmentSet;
    private FeatureSpecification featureSpecification;
    private Model model;
    private FeatureExtraction featureExtractionImpl;
    private Training trainingImpl;
    private FeatureGeneration featureGenerationImpl;
    private Balancing balancingImpl;
    private FeatureWeighting featureWeightingImpl;
    private static final int NUM_TOP_FEATURES = 10;
    private List<String> topFeatures;
    private List<Map.Entry<String, Double>> featureWeights;

    public List<String> getTopFeatures() {
        return topFeatures;
    }

    public List<Map.Entry<String, Double>> getFeatureWeights() {
        return featureWeights;
    }

    public void setFeatureWeightingImpl(FeatureWeighting featureWeightingImpl) {
        this.featureWeightingImpl = featureWeightingImpl;
    }

    public FeatureWeighting getFeatureWeightingImpl() {
        return featureWeightingImpl;
    }

    public void setSegmentSet(SegmentSet segments) {
        this.segmentSet = segments;
    }

    public FeatureSpecification getFeatureSpecification() {
        return this.featureSpecification;
    }

    public Model getModel() {
        return this.model;
    }

    public void run() {

        System.out.println("== Training Final Model ==");

        SegmentSet trainingSegments = segmentSet.onlyLabeled();
        if (getBalancingImpl() != null) {
            trainingSegments = getBalancingImpl().balance(trainingSegments);
        }

        ExampleSet basicExamples = trainingSegments.getBasicExamples();

        //Generate the features
        FeatureGeneration generation = getFeatureGenerationImpl();
        this.featureSpecification = generation.generateFeatures(basicExamples);

        //Extract features
        FeatureExtraction extraction = getFeatureExtractionImpl();
        ExampleSet examples = extraction.extractFeatures(basicExamples, this.featureSpecification);

        //Train the model
        Training training = getTrainingImpl();
        this.model = training.train(examples);

        //Get the top features
        this.topFeatures = getFeatureWeightingImpl().getTopFeatures(examples.getInstances(), this.model.getClassifier(), NUM_TOP_FEATURES);
        this.featureWeights = getFeatureWeightingImpl().getFeatureWeights(examples.getInstances(), this.model.getClassifier());
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

    public void setFeatureGenerationImpl(FeatureGeneration featureGenerationImpl) {
        this.featureGenerationImpl = featureGenerationImpl;
    }

    public FeatureGeneration getFeatureGenerationImpl() {
        return this.featureGenerationImpl;
    }

    public void setBalancingImpl(Balancing balancing) {
        this.balancingImpl = balancing;
    }

    public Balancing getBalancingImpl() {
        return balancingImpl;
    }
}
