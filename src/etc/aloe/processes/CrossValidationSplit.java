/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package etc.aloe.processes;

import etc.aloe.data.LabelableItem;
import java.util.List;
import weka.core.Instances;

/**
 *
 * @author michael
 */
public interface CrossValidationSplit<T extends LabelableItem> {

    /**
     * Creates the training set for one fold of a cross-validation on the
     * dataset.
     *
     * @param instances the data to split
     * @param foldIndex 0 for the first fold, 1 for the second, ...
     * @param numFolds the number of folds in the cross-validation. Must be
     * greater than 1.
     * @return the training set
     */
    List<T> getTrainingForFold(List<T> instances, int foldIndex, int numFolds);

    /**
     * Creates the test set for one fold of a cross-validation on the dataset.
     *
     * @param instances the data to split
     * @param foldIndex 0 for the first fold, 1 for the second, ...
     * @param numFolds the number of folds in the cross-validation. Must be
     * greater than 1.
     * @return the test set as a set
     */
    List<T> getTestingForFold(List<T> instances, int foldIndex, int numFolds);
}
