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

import etc.aloe.oilspill2010.BigramFeatureGenerationImpl;
import etc.aloe.oilspill2010.TrainingImpl;
import etc.aloe.oilspill2010.FeatureGenerationImpl;
import etc.aloe.oilspill2010.NullFeatureWeighting;
import etc.aloe.processes.FeatureGeneration;
import etc.aloe.processes.FeatureWeighting;
import etc.aloe.processes.Training;
import java.util.List;

/**
 * Provides implementations for the CSCW 2013 pipeline.
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class OilSpill2010 extends CSCW2013 {

    @Override
    public FeatureGeneration constructFeatureGeneration() {
        if (options instanceof TrainOptionsImpl) {
            TrainOptionsImpl trainOpts = (TrainOptionsImpl) options;
            //Read the emoticons
            List<String> termList = loadTermList(trainOpts.emoticonFile);
            //return new FeatureGenerationImpl(termList);
            return new BigramFeatureGenerationImpl(termList);
        } else {
            throw new IllegalArgumentException("Options not for Training");
        }
    }
    
    
    @Override
    public FeatureWeighting constructFeatureWeighting() {
        return super.constructFeatureWeighting();
        //return new NullFeatureWeighting();
    }

    
    
}
