package etc.aloe.processes;

import etc.aloe.data.LabelableItem;
import java.util.List;

/**
 * CrossValidationPrep prepares a set of labeled entities for cross validation.
 */
public interface CrossValidationPrep<T extends LabelableItem> {

    /**
     * Randomizes the instances in place.
     *
     * @param instances
     */
    void randomize(List<T> instances);

    /**
     * Stratifies the instances and returns a new list.
     *
     * @param instances
     * @param numFolds
     * @return
     */
    List<T> stratify(List<T> instances, int numFolds);
}
