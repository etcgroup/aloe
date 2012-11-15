/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package etc.aloe.cscw2013;

import etc.aloe.data.LabelableItem;

/**
 * Simple LabelableItem implementation.
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
