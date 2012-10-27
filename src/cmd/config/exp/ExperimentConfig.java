/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cmd.config.exp;

import data.transforms.DataSetTransform;

/**
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public interface ExperimentConfig {
    
    public int getTestSetPercent();
    
    public DataSetTransform getBalancingTransform();
    
    public String describeBalancingStrategy();
    
    public int getNumRuns();
    
    public int getNumFolds();
    
    public boolean getRunNonRandom();

    public boolean isUseTestSet();
}
