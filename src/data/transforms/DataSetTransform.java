/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data.transforms;

import data.DataSet;

/**
 * Applies some kind of transformation to the given data set
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public interface DataSetTransform {

    /**
     * Applies the transform to the given data set. Returns a new transformed data set.
     * @param dataSet
     * @return
     * @throws Exception 
     */
    public DataSet transform(DataSet dataSet) throws Exception;

}
