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
package etc.aloe.cscw2013;

import etc.aloe.data.LabelableItem;

/**
 * Simple LabelableItem implementation.
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
class TestLabelable implements LabelableItem {

    private Boolean trueLabel;
    private Boolean predictedLabel;
    private final String name;

    public TestLabelable(String name, Boolean trueLabel, Boolean predictedLabel) {
        this.name = name;
        this.trueLabel = trueLabel;
        this.predictedLabel = predictedLabel;
    }

    public String toString() {
        return name + "(" + trueLabel + "," + predictedLabel + ")";
    }

    @Override
    public Boolean getTrueLabel() {
        return trueLabel;
    }

    @Override
    public void setTrueLabel(Boolean truth) {
        trueLabel = truth;
    }

    @Override
    public boolean hasTrueLabel() {
        return trueLabel != null;
    }

    @Override
    public Boolean getPredictedLabel() {
        return predictedLabel;
    }

    @Override
    public void setPredictedLabel(Boolean prediction) {
        predictedLabel = prediction;
    }

    @Override
    public boolean hasPredictedLabel() {
        return predictedLabel != null;
    }
}
