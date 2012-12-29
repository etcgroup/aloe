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

import etc.aloe.data.ExampleSet;
import etc.aloe.data.FeatureSpecification;
import etc.aloe.data.Message;
import etc.aloe.data.MessageSet;
import etc.aloe.data.Model;
import etc.aloe.data.Segment;
import etc.aloe.data.SegmentSet;
import etc.aloe.processes.FeatureExtraction;
import etc.aloe.processes.LabelMapping;
import etc.aloe.processes.Loggable;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

/**
 * Class that takes messages from the user interactively and classifies them
 * using and existing model and feature set.
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
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
            extraction.setVerbosity(Loggable.Verbosity.Quiet);

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
