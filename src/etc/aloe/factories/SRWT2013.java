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


import etc.aloe.processes.FeatureExtraction;
import etc.aloe.processes.FeatureGeneration;
import etc.aloe.wt2013.FrenchFeatureExtractionImpl;
import etc.aloe.wt2013.FrenchFeatureGenerationImpl;

import java.util.List;

/**
 * A secondary pipeline class (which implements CSCW2013)
 * for including feature extraction and generation for 
 * french.
 * @author Sayer Rippey <srippey@oberlin.edu>
 * made for Winter Term 2013
 */

public class SRWT2013 extends CSCW2013 {
    @Override
    public FeatureExtraction constructFeatureExtraction() {
        return new FrenchFeatureExtractionImpl();
    }

    @Override
    public FeatureGeneration constructFeatureGeneration() {
        if (options instanceof TrainOptionsImpl) {
            TrainOptionsImpl trainOpts = (TrainOptionsImpl) options;
            //Read the emoticons
            List<String> termList;
            termList = loadTermList(trainOpts.emoticonFile);
            return new FrenchFeatureGenerationImpl(termList);
        } else {
            throw new IllegalArgumentException("Options not for Training");
        }
    }

}
