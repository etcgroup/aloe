/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cmd.config;

import java.util.logging.Level;
import java.util.logging.Logger;
import weka.classifiers.Classifier;
import weka.core.OptionHandler;
import weka.core.Utils;

/**
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class WekaClassifierConfig implements ClassifierConfig {
    private String classifierClass;
    private String classifierConfig;

    public WekaClassifierConfig(String classifierClass, String classifierConfig) {
        this.classifierClass = classifierClass;
        this.classifierConfig = classifierConfig;
    }

    @Override
    public Classifier getConfiguredClassifier() {
        try {
            Classifier classifier = (Classifier)Class.forName(classifierClass).newInstance();
            
            if (classifierConfig != null) {
                OptionHandler optionsClassifier = (OptionHandler)classifier;
                try {
                    optionsClassifier.setOptions(Utils.splitOptions(classifierConfig));
                } catch (Exception ex) {
                    System.err.println("Options " + classifierConfig + " not supported on " + classifierClass);
                    ex.printStackTrace();
                }
            }
            
            return classifier;
        } catch (ClassNotFoundException e) {
            System.err.println("Class " + classifierClass + " not found");
            e.printStackTrace();
            return null;
        } catch (IllegalAccessException e ) {
            System.err.println("Cannot access " + classifierClass + "");
            e.printStackTrace();
            return null;
        } catch (InstantiationException e) {
            System.err.println("Cannot instantiate " + classifierClass + "");
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getConfigString() {
        return classifierConfig;
    }

    @Override
    public String getClassifierType() {
        return classifierClass;
    }
    
    
}
