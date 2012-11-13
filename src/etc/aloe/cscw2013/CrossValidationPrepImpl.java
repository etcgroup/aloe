/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package etc.aloe.cscw2013;

import etc.aloe.RandomProvider;
import etc.aloe.data.LabelableItem;
import etc.aloe.processes.CrossValidationPrep;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author michael
 */
public class CrossValidationPrepImpl<T extends LabelableItem> implements CrossValidationPrep<T> {

    /**
     * Shuffles the segments in the set so that they are ordered randomly.
     *
     * Adapted from weka.core.Instances.randomize().
     *
     * @param random a random number generator
     */
    @Override
    public void randomize(List<T> instances) {

        Random random = RandomProvider.getRandom();

        for (int j = instances.size() - 1; j > 0; j--) {
            swap(instances, j, random.nextInt(j + 1));
        }
    }

    /**
     * Swaps two instances in the set.
     *
     * @param i the first segment's index (index starts with 0)
     * @param j the second segment's index (index starts with 0)
     */
    protected void swap(List<T> instances, int i, int j) {
        T in = instances.get(i);
        instances.set(i, instances.get(j));
        instances.set(j, in);
    }

    /**
     * Stratifies a set of instances according to its class values if the class
     * attribute is nominal (so that afterwards a stratified cross-validation
     * can be performed).
     *
     * @param instances
     * @param numFolds the number of folds in the cross-validation
     */
    @Override
    public List<T> stratify(List<T> instances, int numFolds) {

        // sort by class
        int index = 1;
        while (index < instances.size()) {
            T item1 = instances.get(index - 1);
            for (int j = index; j < instances.size(); j++) {
                T item2 = instances.get(j);
                if ((item1.getTrueLabel() == item2.getTrueLabel())
                        || (!item1.hasTrueLabel() && !item2.hasTrueLabel())) {
                    swap(instances, index, j);
                    index++;
                }
            }
            index++;
        }
        return stratStep(instances, numFolds);
    }

    /**
     * Help function needed for stratification of set.
     *
     * @param numFolds the number of folds for the stratification
     */
    private List<T> stratStep(List<T> instances, int numFolds) {

        List<T> newInstances = new ArrayList<T>(instances.size());
        int start = 0, j;

        // create stratified batch
        while (newInstances.size() < instances.size()) {
            j = start;
            while (j < instances.size()) {
                newInstances.add(instances.get(j));
                j = j + numFolds;
            }
            start++;
        }
        return newInstances;
    }
}
