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
import etc.aloe.data.SegmentSet;
import etc.aloe.processes.FeatureExtraction;
import etc.aloe.processes.LabelMapping;

/**
 * Class for using an existing model to label unlabeled data.
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class LabelingController {

    private SegmentSet segmentSet;
    private FeatureSpecification featureSpecification;
    private EvaluationReport evaluationReport;
    private Model model;
    private FeatureExtraction featureExtractionImpl;
    private LabelMapping mappingImpl;
    private double falsePositiveCost = 1;
    private double falseNegativeCost = 1;

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

        System.out.println("== Labeling and Testing ==");

        //First extract features
        FeatureExtraction extraction = getFeatureExtractionImpl();
        ExampleSet examples = extraction.extractFeatures(segmentSet.getBasicExamples(), featureSpecification);

        //Predict the labels
        Predictions predictions = this.model.getPredictions(examples);

        //Map back onto messages
        LabelMapping mapping = getMappingImpl();
        mapping.map(predictions, segmentSet);

        //Evaluate the model on labeled examples
        this.evaluationReport = new EvaluationReport("Unlabeled Data", falsePositiveCost, falseNegativeCost);
        this.evaluationReport.addPredictions(predictions);
    }

    public void setCosts(double falsePositiveCost, double falseNegativeCost) {
        this.falsePositiveCost = falsePositiveCost;
        this.falseNegativeCost = falseNegativeCost;
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
