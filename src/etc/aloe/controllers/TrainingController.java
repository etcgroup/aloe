/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package etc.aloe.controllers;

import etc.aloe.cscw2013.FeatureExtractionImpl;
import etc.aloe.cscw2013.TrainingImpl;
import etc.aloe.data.ExampleSet;
import etc.aloe.data.FeatureSpecification;
import etc.aloe.data.Model;
import etc.aloe.data.SegmentSet;
import etc.aloe.processes.FeatureExtraction;
import etc.aloe.processes.Training;

/**
 *
 * @author kuksenok
 */
public class TrainingController {

    private SegmentSet segmentSet;
    private FeatureSpecification featureSpecification;
    private Model model;

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

        //Extract features
        FeatureExtraction extraction = new FeatureExtractionImpl();
        ExampleSet examples = extraction.extractFeatures(segmentSet, featureSpecification);

        //Train the model
        Training training = new TrainingImpl();
        this.model = training.train(examples);
    }
}
