package etc.aloe.data;

/**
 * ExampleSet contains information about data points that have features
 * extracted. These data points are ready for labeling by a model.
 */
public abstract class ExampleSet {

    /**
     * The size of the entity set.
     * @return
     */
    public abstract int size();

    /**
     * Returns a new example set containing only those examples with labels.
     * @return
     */
    public ExampleSet onlyLabeled() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
