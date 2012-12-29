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
package etc.aloe.factories;

import etc.aloe.controllers.CrossValidationController;
import etc.aloe.controllers.TrainingController;
import etc.aloe.data.Model;
import etc.aloe.options.InteractiveOptions;
import etc.aloe.options.LabelOptions;
import etc.aloe.options.ModeOptions;
import etc.aloe.options.SingleOptions;
import etc.aloe.options.TrainOptions;
import etc.aloe.processes.Balancing;
import etc.aloe.processes.Evaluation;
import etc.aloe.processes.FeatureExtraction;
import etc.aloe.processes.FeatureGeneration;
import etc.aloe.processes.FeatureWeighting;
import etc.aloe.processes.LabelMapping;
import etc.aloe.processes.SegmentResolution;
import etc.aloe.processes.Segmentation;
import etc.aloe.processes.Training;
import java.text.DateFormat;

/**
 * Required methods for a pipeline implementation.
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public interface PipelineFactory {

    void initialize();

    Balancing constructBalancing();

    Evaluation constructEvaluation();

    FeatureExtraction constructFeatureExtraction();

    FeatureGeneration constructFeatureGeneration();

    FeatureWeighting constructFeatureWeighting();

    LabelMapping constructLabelMapping();

    Model constructModel();

    SegmentResolution constructSegmentResolution();

    Segmentation constructSegmentation();

    Training constructTraining();

    void configureCrossValidation(CrossValidationController crossValidationController);

    void configureTraining(TrainingController trainingController);

    DateFormat constructDateFormat();

    InteractiveOptions constructInteractiveOptions();

    LabelOptions constructLabelOptions();

    TrainOptions constructTrainOptions();

    SingleOptions constructSingleOptions();

    void setOptions(ModeOptions options);
}
