/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data.processing;

import data.DataSet;
import data.EntitySet;
import java.util.List;

/**
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public interface EntitiesToDataSet {
    public DataSet toDataSet(EntitySet entities);
}
