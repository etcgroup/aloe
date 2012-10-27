/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cmd.studies;

import cmd.ExperimentManager;
import cmd.config.*;
import cmd.config.exp.DefaultExperimentConfig;
import cmd.config.exp.ExperimentConfig;
import data.processing.attrs.BinaryCodeClassProducer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

/**
 *
 * @author michael
 */
public class PreliminaryClassifierStudy {
    
    public static void main(String[] args) {
        String studyName = "PrelimClassifier-human";
        //Three sets of codes - those 13 with over 1000 applications
//        List<Integer> codesToTest = Arrays.asList(77, 83, 74, 94);//interest, confusion, apprehension, supportive
//        List<Integer> codesToTest = Arrays.asList(81, 80, 96, 99);//amusement, agreement, acceptance, anticipation
        List<Integer> codesToTest = Arrays.asList(84, 73, 89, 79, 108);//considering, annoyance, frustration, surprise, serenity
        
        List<FeatureConfig> featureConfigs = new ArrayList<FeatureConfig>();

        {//Base: optimal bag of words and everything else
            ParameterizedFeatureConfig features = new ParameterizedFeatureConfig("the-works");
            features.setFeatureSets(EnumSet.allOf(ParameterizedFeatureConfig.FeatureSet.class));
            features.setEmoticonSettings(EnumSet.of(ParameterizedFeatureConfig.EmoticonSettings.GlobalBasis, ParameterizedFeatureConfig.EmoticonSettings.LargeMinFrequency, ParameterizedFeatureConfig.EmoticonSettings.WordCounts));
            features.setBagOfWordsSettings(EnumSet.of(ParameterizedFeatureConfig.BagOfWordsSettings.GlobalBasis, ParameterizedFeatureConfig.BagOfWordsSettings.LargeMinFrequency, ParameterizedFeatureConfig.BagOfWordsSettings.LowerCase, ParameterizedFeatureConfig.BagOfWordsSettings.Stemming, ParameterizedFeatureConfig.BagOfWordsSettings.ExcludeNonsense, ParameterizedFeatureConfig.BagOfWordsSettings.ImposeHardLimit, ParameterizedFeatureConfig.BagOfWordsSettings.TFIDFTransform, ParameterizedFeatureConfig.BagOfWordsSettings.WordCounts));
            features.countRegexLengths(true);
            featureConfigs.add(features);
        }

        List<ExperimentConfig> experimentConfigs = new ArrayList<ExperimentConfig>();
        experimentConfigs.add(new DefaultExperimentConfig(DefaultExperimentConfig.BalancingStrategy.Downsample));

        List<ClassifierConfig> classifierConfigs = new ArrayList<ClassifierConfig>();
        classifierConfigs.add(new WekaClassifierConfig("weka.classifiers.functions.SMO", "-C 1.0 -L 0.0010 -P 1.0E-12 -N 0 -V -1 -W 1 -K \"weka.classifiers.functions.supportVector.PolyKernel -C 250007 -E 1.0\""));
        classifierConfigs.add(new WekaClassifierConfig("weka.classifiers.bayes.NaiveBayes", ""));
        classifierConfigs.add(new WekaClassifierConfig("weka.classifiers.trees.J48", "-C 0.25 -M 2"));
        classifierConfigs.add(new WekaClassifierConfig("weka.classifiers.functions.Logistic", "-R 1.0 -M -1"));

        List<DataConfig> dataConfigs = new ArrayList<DataConfig>();
        {
            DefaultDataConfig dataConfig = new DefaultDataConfig("no-seg-human");
            dataConfig.setSegmentationId(0);
            dataConfig.setRemoveSystemMessages(true);
            dataConfigs.add(dataConfig);
        }
        { //and try a middle segmentation (25 seconds)
            DefaultDataConfig dataConfig = new DefaultDataConfig("seg-5-human");
            dataConfig.setSegmentationId(5);
            dataConfig.setRemoveSystemMessages(true);
            dataConfigs.add(dataConfig);
        }
        { //and try a sliding window
            SlidingWindowDataConfig dataConfig = new SlidingWindowDataConfig("wide-3-1-human");
            dataConfig.setMaxWindowDuration(60);
            dataConfig.setMaxWindowSize(3);
            dataConfig.setRatingsBasis(3);
            dataConfig.setRemoveSystemMessages(true);
            dataConfigs.add(dataConfig);
        }
        ExperimentManager.runStudy(studyName, dataConfigs, codesToTest, BinaryCodeClassProducer.CodePresenceStrategy.Any, featureConfigs, experimentConfigs, classifierConfigs);
    }
}
