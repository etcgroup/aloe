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
 * Instances in an ExampleSet always have at least these attributes:
 * 'message' - which contains the message text.
 * '*id' - which is a unique integer identifying the message.
 * 'label' - the ground truth label for the instance (0 or 1)
 */
public class ExampleSet {

    private Instances instances;

    public ExampleSet(Instances instances) {
        this.instances = instances;
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

    public Instance get(int i) {
        return instances.get(i);
    }

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
    Boolean getClassLabel(double classValue) {
        if (Double.isNaN(classValue)) {
            return null;
        }

        Attribute classAttr = instances.classAttribute();
        String classValueStr = classAttr.value((int) classValue);
        return Boolean.parseBoolean(classValueStr);
    }
}
