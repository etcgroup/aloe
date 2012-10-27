/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cmd.config;

import weka.classifiers.Classifier;

/**
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public interface ClassifierConfig {
    Classifier getConfiguredClassifier();
    
    String getConfigString();

    public String getClassifierType();
}
