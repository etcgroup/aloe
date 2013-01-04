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

import etc.aloe.cscw2013.SMOFeatureWeighting;
import etc.aloe.processes.FeatureWeighting;
import etc.aloe.wt2013.TrainingImplDecisionStump;
import etc.aloe.processes.Training;
import etc.aloe.wt2013.DecisionStumpFeatureWeighting;

/**
 *
 * @author erose
 */
public class HMMPipeline extends CSCW2013 {
    
    @Override
    public Training constructTraining() {
        if (options instanceof TrainOptionsImpl) {
            TrainOptionsImpl trainOpts = (TrainOptionsImpl) options;
            Training trainingImpl = new TrainingImplDecisionStump();
            return trainingImpl;
        } else {
            throw new IllegalArgumentException("Options must be for Training");
        }
    }
    
    @Override
    public FeatureWeighting constructFeatureWeighting() {
        return new DecisionStumpFeatureWeighting();
    }
}
