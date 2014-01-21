/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package etc.aloe.oilspill2010;

import etc.aloe.cscw2013.WekaModel;
import etc.aloe.data.ExampleSet;
import etc.aloe.processes.Training;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.Logistic;
import weka.classifiers.functions.SMO;
import weka.classifiers.functions.supportVector.RBFKernel;
import weka.classifiers.trees.RandomForest;

/**
 *
 * @author mjbrooks
 */
public class TrainingImpl implements Training {

    public TrainingImpl() {
    }

    @Override
    public WekaModel train(ExampleSet examples) {
        //These settings aren't terrible
        SMO smo = new SMO();
        RBFKernel rbf = new RBFKernel();
        rbf.setGamma(0.5);
        smo.setKernel(rbf);
        smo.setC(1.5);
        
        //These also work pretty ok
        Logistic log = new Logistic();
        log.setRidge(100);
        
        Classifier classifier = log;

        try {
            System.out.print("Training on " + examples.size() + " examples... ");
            classifier.buildClassifier(examples.getInstances());
            System.out.println("done.");

            WekaModel model = new WekaModel(classifier);
            return model;
        } catch (Exception ex) {
            System.err.println("Unable to train classifier.");
            System.err.println("\t" + ex.getMessage());
            return null;
        }
    }
    
}
