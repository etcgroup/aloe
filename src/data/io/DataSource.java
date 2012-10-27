package data.io;

import data.EntitySet;
import java.util.List;

/**
 * An abstraction for data retrieval. In ALOE 1.0, the only data source
 * implementation provided is the CSV reading data source. In you have, for
 * example, a database or an alternatively-formatted file, you must create a
 * class implementing this.
 *
 * @version 1.0
 */
public interface DataSource {

    /**
     * Gives the data source a chance to set up.
     */
    void initialize() throws Exception;

    /**
     * Gets and sets the indexes.
     */
    void loadIndexes();

    /**
     * Returns the data set
     *
     * @return
     */
    EntitySet getData();

    /**
     * Sets a filter for codes. Text is implementation specific.
     *
     * @param filter
     */
    void setCodeFilter(String filter);

    /**
     * Sets a filter for users. Text is implementation specific.
     *
     * @param filter
     */
    void setUserFilter(String filter);

    /**
     * Sets a filter for messages. Text is implementation specific.
     *
     * @param filter
     */
    void setMessageFilter(String filter);

    /**
     * Sets a filter for code instances. Text is implementation specific.
     *
     * @param filter
     */
    void setInstanceFilter(String filter);

    /**
     * Sets the segmentation id. May not do anything, depending on
     * implementation.
     *
     * @param segId
     */
    void setSegmentationId(int segId);

    /**
     * Sets the code scheme to retrieve. May not do anything, depending on the
     * implementation.
     *
     * @param schemaId
     */
    void setCodeSchemaId(int schemaId);

    /**
     * Sets a filter on segments. Text is implementation specific.
     *
     * @param filter
     */
    void setSegmentFilter(String filter);

    /**
     * Sets the dates on which items will be marked test set
     */
    void setTestSetDates(List<String> testSetDates);
}
