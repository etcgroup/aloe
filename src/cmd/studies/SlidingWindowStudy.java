/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cmd.studies;

import cmd.ExperimentManager;
import cmd.config.*;
import cmd.config.ParameterizedFeatureConfig.BagOfWordsSettings;
import cmd.config.ParameterizedFeatureConfig.EmoticonSettings;
import cmd.config.ParameterizedFeatureConfig.FeatureSet;
import cmd.config.exp.DefaultExperimentConfig;
import cmd.config.exp.ExperimentConfig;
import data.processing.attrs.BinaryCodeClassProducer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

/**
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class SlidingWindowStudy {
        //A study of different segmentation parameters
    public static void main(String[] args) {
        String studyName = "DataSets";
        
//        List<Integer> codesToTest = Arrays.asList(77, 73, 88, 89, 97, 104);
        List<Integer> codesToTest = Arrays.asList(81, 84, 99, 94, 78, 114);
        
        //The top four affect codes (interest, amusement, considering, annoyance)
        //Based on analyses/data_points.2012-05-26-14-17-44/CodeCountType.xlsx
//        List<Integer> codesToTest = Arrays.asList(77, 81, 84, 73);
        
        //Top 8-16 of the most common affect codes (frustration, surprise, anticipation, supportive)
        //Based on analyses/data_points.2012-05-26-14-17-44/CodeCountType.xlsx
//        List<Integer> codesToTest = Arrays.asList(88, 89, 99, 94);
        
        //More rare of the most common affect codes (frustration, surprise, anticipation, supportive)
        //Based on analyses/data_points.2012-05-26-14-17-44/CodeCountType.xlsx
        //List<Integer> codesToTest = Arrays.asList(78, 114, 97, 104);
        
        List<FeatureConfig> featureConfigs = new ArrayList<FeatureConfig>();

//        {//Nothing but bag of words, with as many words removed as possible
//            ParameterizedFeatureConfig features = new ParameterizedFeatureConfig("bag-stemmed-unstopped-limited");
//            features.setFeatureSets(EnumSet.of(FeatureSet.BagOfWords));
//            features.setEmoticonSettings(EnumSet.noneOf(EmoticonSettings.class));
//            features.setBagOfWordsSettings(EnumSet.of(BagOfWordsSettings.GlobalBasis, BagOfWordsSettings.LargeMinFrequency, BagOfWordsSettings.LowerCase, BagOfWordsSettings.ExcludeNonsense, BagOfWordsSettings.Stemming, BagOfWordsSettings.LowerCase, BagOfWordsSettings.ImposeHardLimit));
//            featureConfigs.add(features);
//        }
        
//        {//Now the works
//            ParameterizedFeatureConfig features = new ParameterizedFeatureConfig("the-works-stemmed-unstopped-limited");
//            features.setFeatureSets(EnumSet.allOf(FeatureSet.class));
//            features.setEmoticonSettings(EnumSet.of(EmoticonSettings.GlobalBasis, EmoticonSettings.WordCounts));
//            features.setBagOfWordsSettings(EnumSet.of(BagOfWordsSettings.GlobalBasis, BagOfWordsSettings.LargeMinFrequency, BagOfWordsSettings.LowerCase, BagOfWordsSettings.ExcludeNonsense, BagOfWordsSettings.Stemming, BagOfWordsSettings.LowerCase, BagOfWordsSettings.ImposeHardLimit));
//            featureConfigs.add(features);
//        }
        
        {//Base: optimal bag of words and everything else
            ParameterizedFeatureConfig features = new ParameterizedFeatureConfig("the-works");
            features.setFeatureSets(EnumSet.allOf(ParameterizedFeatureConfig.FeatureSet.class));
            features.setEmoticonSettings(EnumSet.of(ParameterizedFeatureConfig.EmoticonSettings.GlobalBasis, ParameterizedFeatureConfig.EmoticonSettings.WordCounts));
            features.setBagOfWordsSettings(EnumSet.of(ParameterizedFeatureConfig.BagOfWordsSettings.GlobalBasis, ParameterizedFeatureConfig.BagOfWordsSettings.LowerCase, ParameterizedFeatureConfig.BagOfWordsSettings.Stemming, ParameterizedFeatureConfig.BagOfWordsSettings.ExcludeNonsense, ParameterizedFeatureConfig.BagOfWordsSettings.ImposeHardLimit, ParameterizedFeatureConfig.BagOfWordsSettings.TFIDFTransform, ParameterizedFeatureConfig.BagOfWordsSettings.WordCounts));
            features.countRegexLengths(true);
            featureConfigs.add(features);
        }
        
        List<ExperimentConfig> experimentConfigs = new ArrayList<ExperimentConfig>();
        experimentConfigs.add(new DefaultExperimentConfig(DefaultExperimentConfig.BalancingStrategy.Downsample));

        //Use 3 classic classifiers
        List<ClassifierConfig> classifierConfigs = new ArrayList<ClassifierConfig>();
        classifierConfigs.add(new WekaClassifierConfig("weka.classifiers.functions.SMO", "-C 1.0 -L 0.0010 -P 1.0E-12 -N 0 -V -1 -W 1 -K \"weka.classifiers.functions.supportVector.PolyKernel -C 250007 -E 1.0\""));
//        classifierConfigs.add(new WekaClassifierConfig("weka.classifiers.bayes.NaiveBayes", ""));
//        classifierConfigs.add(new WekaClassifierConfig("weka.classifiers.trees.J48", "-C 0.25 -M 2"));
//        classifierConfigs.add(new WekaClassifierConfig("weka.classifiers.functions.Logistic", "-R 1.0 -M -1"));
        
        List<DataConfig> dataConfigs = new ArrayList<DataConfig>();
        
        {
            DefaultDataConfig dataConfig = new DefaultDataConfig("no-seg");
            dataConfig.setSegmentationId(0);
            dataConfig.setRemoveSystemMessages(true);
            dataConfigs.add(dataConfig);
        }
        {
            DefaultDataConfig dataConfig = new DefaultDataConfig("seg-2");
            dataConfig.setSegmentationId(2);
            dataConfig.setRemoveSystemMessages(true);
            dataConfigs.add(dataConfig);
        }
        {
            DefaultDataConfig dataConfig = new DefaultDataConfig("seg-4");
            dataConfig.setSegmentationId(4);
            dataConfig.setRemoveSystemMessages(true);
            dataConfigs.add(dataConfig);
        }
        {
            DefaultDataConfig dataConfig = new DefaultDataConfig("seg-6");
            dataConfig.setSegmentationId(6);
            dataConfig.setRemoveSystemMessages(true);
            dataConfigs.add(dataConfig);
        }
        {
            DefaultDataConfig dataConfig = new DefaultDataConfig("seg-8");
            dataConfig.setSegmentationId(8);
            dataConfig.setRemoveSystemMessages(true);
            dataConfigs.add(dataConfig);
        }
        
        {
            SlidingWindowDataConfig dataConfig = new SlidingWindowDataConfig("wide-5-1");
            dataConfig.setMaxWindowDuration(60);//1 minute
            dataConfig.setMaxWindowSize(5);
            dataConfig.setRemoveSystemMessages(true);
            dataConfig.setRatingsBasis(3);
            dataConfigs.add(dataConfig);
        }
        {
            SlidingWindowDataConfig dataConfig = new SlidingWindowDataConfig("wide-3-1");
            dataConfig.setMaxWindowDuration(60);//1 minute
            dataConfig.setMaxWindowSize(3);
            dataConfig.setRemoveSystemMessages(true);
            dataConfig.setRatingsBasis(3);
            dataConfigs.add(dataConfig);
        }
        {
            SlidingWindowDataConfig dataConfig = new SlidingWindowDataConfig("wide-5-1-mid");
            dataConfig.setMaxWindowDuration(60);//1 minute
            dataConfig.setMaxWindowSize(5);
            dataConfig.setRemoveSystemMessages(true);
            dataConfig.setRatingsBasis(3);
            dataConfig.setPrescient(true);
            dataConfigs.add(dataConfig);
        }
        {
            SlidingWindowDataConfig dataConfig = new SlidingWindowDataConfig("wide-3-1-mid");
            dataConfig.setMaxWindowDuration(60);//1 minute
            dataConfig.setMaxWindowSize(3);
            dataConfig.setRemoveSystemMessages(true);
            dataConfig.setRatingsBasis(3);
            dataConfig.setPrescient(true);
            dataConfigs.add(dataConfig);
        }
        
        {
            SlidingWindowDataConfig dataConfig = new SlidingWindowDataConfig("narrow-5-1");
            dataConfig.setMaxWindowDuration(60);//1 minute
            dataConfig.setMaxWindowSize(5);
            dataConfig.setRemoveSystemMessages(true);
            dataConfigs.add(dataConfig);
        }
        {
            SlidingWindowDataConfig dataConfig = new SlidingWindowDataConfig("narrow-3-1");
            dataConfig.setMaxWindowDuration(60);//1 minute
            dataConfig.setMaxWindowSize(3);
            dataConfig.setRemoveSystemMessages(true);
            dataConfigs.add(dataConfig);
        }
        {
            SlidingWindowDataConfig dataConfig = new SlidingWindowDataConfig("narrow-5-1-mid");
            dataConfig.setMaxWindowDuration(60);//1 minute
            dataConfig.setMaxWindowSize(5);
            dataConfig.setRemoveSystemMessages(true);
            dataConfig.setPrescient(true);
            dataConfigs.add(dataConfig);
        }
        {
            SlidingWindowDataConfig dataConfig = new SlidingWindowDataConfig("narrow-3-1-mid");
            dataConfig.setMaxWindowDuration(60);//1 minute
            dataConfig.setMaxWindowSize(3);
            dataConfig.setRemoveSystemMessages(true);
            dataConfig.setPrescient(true);
            dataConfigs.add(dataConfig);
        }
        
        ExperimentManager.runStudy(studyName, dataConfigs, codesToTest, BinaryCodeClassProducer.CodePresenceStrategy.Any, featureConfigs, experimentConfigs, classifierConfigs);
    }
}