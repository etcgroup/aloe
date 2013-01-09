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
package etc.aloe.data;

/**
 * Represents an object that can have true and predicted labels.
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public interface LabelableItem {

    /**
     * Get the gold-standard label of the item. The label is null if the item is
     * unlabeled.
     *
     * @return
     */
    Label getTrueLabel();

    /**
     * Set the gold-standard label of the item. Set to null to remove the label.
     *
     * @param truth
     */
    void setTrueLabel(Label truth);

    /**
     * True if the item has a gold-standard label.
     *
     * @return
     */
    boolean hasTrueLabel();

    /**
     * Get the predicted label of the item. Null if not labeled.
     *
     * @return
     */
    Label getPredictedLabel();

    /**
     * Set the predicted label. Use null to remove the label.
     *
     * @param prediction
     */
    void setPredictedLabel(Label prediction);

    /**
     * True if the item has a predicted label.
     *
     * @return
     */
    boolean hasPredictedLabel();

    /**
     * Get the confidence in the predicted label.
     *
     * @return
     */
    Double getPredictionConfidence();

    /**
     * Set the confidence in the predicted label;
     *
     * @param predictionConfidence
     */
    void setPredictionConfidence(Double predictionConfidence);

    /**
     * True if the item has prediction confidence.
     *
     * @return
     */
    boolean hasPredictionConfidence();
}
