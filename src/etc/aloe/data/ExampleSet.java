package etc.aloe.data;

import java.util.logging.Level;
import java.util.logging.Logger;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.instance.RemoveWithValues;

/**
 * ExampleSet contains information about data points that have features
 * extracted. These data points are ready for labeling by a model.
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
}
