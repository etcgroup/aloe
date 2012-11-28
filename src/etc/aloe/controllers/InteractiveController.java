/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package etc.aloe.controllers;

import etc.aloe.data.ExampleSet;
import etc.aloe.data.FeatureSpecification;
import etc.aloe.data.Message;
import etc.aloe.data.MessageSet;
import etc.aloe.data.Model;
import etc.aloe.data.Segment;
import etc.aloe.data.SegmentSet;
import etc.aloe.processes.FeatureExtraction;
import etc.aloe.processes.LabelMapping;
import etc.aloe.processes.Logging;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author kuksenok
 */
public class InteractiveController {

    private FeatureSpecification featureSpecification;
    private Model model;
    private FeatureExtraction featureExtractionImpl;
    private MessageSet messages;
    private LabelMapping mappingImpl;

    public void setFeatureSpecification(FeatureSpecification spec) {
        this.featureSpecification = spec;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public MessageSet getMessageSet() {
        return messages;
    }

    public void run() {

        System.out.println("== Interactive Mode ==");

        Scanner scan = new Scanner(System.in);

        messages = new MessageSet();

        int msgIdInc = 0;
        System.out.print("MSG > ");
        while (scan.hasNextLine()) {

            String messageStr = scan.nextLine();
            String participant = "user";
            Date time = new Date();

            Message message = new Message(msgIdInc++, time, participant, messageStr);
            messages.add(message);

            //Make a segment for the message
            Segment segment = new Segment();
            segment.add(message);
            SegmentSet segmentSet = new SegmentSet();
            segmentSet.add(segment);

            //First extract features
            FeatureExtraction extraction = getFeatureExtractionImpl();
            extraction.setVerbosity(Logging.Verbosity.Quiet);

            ExampleSet examples = extraction.extractFeatures(segmentSet.getBasicExamples(), featureSpecification);

            //Predict the labels
            List<Boolean> predictedLabels = this.model.getPredictedLabels(examples);

            //Map back onto messages
            LabelMapping mapping = getMappingImpl();
            mapping.map(predictedLabels, segmentSet);


            //Print out the label
            System.out.println("Predicted label: " + message.getPredictedLabel());
            System.out.println();
            System.out.print("MSG >");
        }
    }

    public FeatureExtraction getFeatureExtractionImpl() {
        return this.featureExtractionImpl;
    }

    public void setFeatureExtractionImpl(FeatureExtraction featureExtractor) {
        this.featureExtractionImpl = featureExtractor;
    }

    public LabelMapping getMappingImpl() {
        return this.mappingImpl;
    }

    public void setMappingImpl(LabelMapping mapping) {
        this.mappingImpl = mapping;
    }
}
