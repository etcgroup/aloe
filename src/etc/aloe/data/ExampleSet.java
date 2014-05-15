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

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.instance.RemoveWithValues;

/**
 * ExampleSet contains information about data points that have features
 * extracted. These data points are ready for labeling by a model.
 *
 * Instances in an ExampleSet always have at least these attributes: 'message' -
 * which contains the message text. '*id' - which is a unique integer
 * identifying the message. 'label' - the ground truth label for the instance (0
 * or 1)
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class ExampleSet {

    public final static String ID_ATTR_NAME = "*id";
    public final static String MESSAGE_ATTR_NAME = "message";
    public final static String LABEL_ATTR_NAME = "label";
    public final static String PARTICIPANT_ATTR_NAME = "participant";
    
    private Instances instances;

    /**
     * Construct an ExampleSet containing the given instances.
     *
     * @param instances
     */
    public ExampleSet(Instances instances) {
        this.instances = instances;
    }

    /**
     * Make a copy of the ExampleSet, copying the underlying instances.
     *
     * @return
     */
    public ExampleSet copy() {
        return new ExampleSet(new Instances(instances));
    }

    /**
     * The size of the example set.
     *
     * @return
     */
    public int size() {
        return instances.size();
    }

    /**
     * Returns a new example set containing only those examples with labels.
     *
     * @return
     */
    public ExampleSet onlyLabeled() {
        RemoveWithValues filter = new RemoveWithValues();
        filter.setAttributeIndex("" + (instances.classIndex() + 1));
        filter.setMatchMissingValues(true);
        filter.setInvertSelection(true);

        try {
            filter.setInputFormat(instances);
            Instances result = Filter.useFilter(instances, filter);
            ExampleSet resultSet = new ExampleSet(result);
            return resultSet;
        } catch (Exception ex) {
            System.err.println("Unable to apply filter!");
            return null;
        }
    }

    /**
     * Get the ith instance.
     *
     * @param i
     * @return
     */
    public Instance get(int i) {
        return instances.get(i);
    }

    /**
     * Get the underlying instances.
     *
     * @return
     */
    public Instances getInstances() {
        return instances;
    }

    /**
     * Gets the actual label of the given example. If the example is unlabeled,
     * returns null;
     *
     * @param i
     * @return
     */
    public Boolean getTrueLabel(int i) {
        Instance instance = instances.get(i);
        return getClassLabel(instance.classValue());
    }

    /**
     * Converts a double class value into a boolean given the string labels for
     * the class attribute in this data set. Returns null if the class value is
     * weka missing.
     *
     * @param classValue
     * @return
     */
    public Boolean getClassLabel(double classValue) {
        if (Double.isNaN(classValue)) {
            return null;
        }

        Attribute classAttr = instances.classAttribute();
        String classValueStr = classAttr.value((int) classValue);
        return Boolean.parseBoolean(classValueStr);
    }

    /**
     * Gets the confidence in the positive class.
     * @param classDistribution
     * @param classValue
     * @return
     */
    public Double getConfidence(double[] classDistribution, double classValue) {
        if (Double.isNaN(classValue)) {
            return null;
        }
        
        return classDistribution[(int) classValue];
    }

    /**
     * Set the underlying instances.
     *
     * @param instances
     */
    public void setInstances(Instances instances) {
        this.instances = instances;
    }
}
