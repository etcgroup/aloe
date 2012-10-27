/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cmd.studies;

import cmd.ExperimentManager;
import cmd.config.*;
import cmd.config.ParameterizedFeatureConfig.BagOfWordsSettings;
import cmd.config.ParameterizedFeatureConfig.EmoticonSettings;
import cmd.config.exp.DefaultExperimentConfig;
import cmd.config.exp.ExperimentConfig;
import data.processing.attrs.BinaryCodeClassProducer.CodePresenceStrategy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import weka.core.stemmers.SnowballStemmer;
import weka.core.stemmers.Stemmer;

/**
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class RegexLengthsStudy {
    //A study of bag of words

    public static void main(String[] args) {
        String studyName = "RegexLengths-human";

        //Three common codes and two less common codes
        List<Integer> codesToTest = Arrays.asList(77, 81, 73, 89, 94);

        List<FeatureConfig> featureConfigs = new ArrayList<FeatureConfig>();

        {//Base: optimal bag of words and everything else
            ParameterizedFeatureConfig features = new ParameterizedFeatureConfig("base");
            features.setFeatureSets(EnumSet.allOf(ParameterizedFeatureConfig.FeatureSet.class));
            features.setEmoticonSettings(EnumSet.of(EmoticonSettings.GlobalBasis, EmoticonSettings.LargeMinFrequency, EmoticonSettings.WordCounts));
            features.setBagOfWordsSettings(EnumSet.of(BagOfWordsSettings.GlobalBasis, BagOfWordsSettings.LargeMinFrequency, BagOfWordsSettings.LowerCase, BagOfWordsSettings.Stemming, BagOfWordsSettings.ExcludeNonsense, BagOfWordsSettings.ImposeHardLimit, BagOfWordsSettings.TFIDFTransform, BagOfWordsSettings.WordCounts));
            featureConfigs.add(features);
        }

        {//Try switching on regex length features
            ParameterizedFeatureConfig features = new ParameterizedFeatureConfig("with-lengths");
            features.setFeatureSets(EnumSet.allOf(ParameterizedFeatureConfig.FeatureSet.class));
            features.countRegexLengths(true);
            features.setEmoticonSettings(EnumSet.of(EmoticonSettings.GlobalBasis, EmoticonSettings.LargeMinFrequency, EmoticonSettings.WordCounts));
            features.setBagOfWordsSettings(EnumSet.of(BagOfWordsSettings.GlobalBasis, BagOfWordsSettings.LargeMinFrequency, BagOfWordsSettings.LowerCase, BagOfWordsSettings.Stemming, BagOfWordsSettings.ExcludeNonsense, BagOfWordsSettings.ImposeHardLimit, BagOfWordsSettings.TFIDFTransform, BagOfWordsSettings.WordCounts));
            featureConfigs.add(features);
        }

        List<ExperimentConfig> experimentConfigs = new ArrayList<ExperimentConfig>();
        experimentConfigs.add(new DefaultExperimentConfig(DefaultExperimentConfig.BalancingStrategy.Downsample));

        List<ClassifierConfig> classifierConfigs = new ArrayList<ClassifierConfig>();
        classifierConfigs.add(new WekaClassifierConfig("weka.classifiers.functions.SMO", "-C 1.0 -L 0.0010 -P 1.0E-12 -N 0 -V -1 -W 1 -K \"weka.classifiers.functions.supportVector.PolyKernel -C 250007 -E 1.0\""));
//        classifierConfigs.add(new WekaClassifierConfig("weka.classifiers.bayes.NaiveBayes", ""));
//        classifierConfigs.add(new WekaClassifierConfig("weka.classifiers.trees.J48", "-C 0.25 -M 2"));
//        classifierConfigs.add(new WekaClassifierConfig("weka.classifiers.function.Logistic", "-R 1.0 -M -1"));

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
            SlidingWindowDataConfig dataConfig = new SlidingWindowDataConfig("sliding-5-1-human");
            dataConfig.setMaxWindowDuration(60);
            dataConfig.setMaxWindowSize(5);
            dataConfig.setRemoveSystemMessages(true);
            dataConfigs.add(dataConfig);
        }
        ExperimentManager.runStudy(studyName, dataConfigs, codesToTest, CodePresenceStrategy.Any, featureConfigs, experimentConfigs, classifierConfigs);
    }
}
