/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data.transforms;

import data.DataSet;

/**
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public abstract class AbstractDataSetTransform implements DataSetTransform {
    
    @Override
    public DataSet transform(DataSet dataSet) throws Exception {
        DataSet copy = new DataSet(dataSet);
        return copy;
    }
    
}
