/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cmd;

import cmd.config.ClassifierConfig;
import cmd.config.DataConfig;
import cmd.config.FeatureConfig;
import cmd.config.exp.ExperimentConfig;
import daisy.io.CSV;
import data.DataSet;
import data.EntitySet;
import data.indexes.CodeNames;
import data.io.DataSaver;
import data.io.FileDataSaver;
import data.processing.EntitiesToDataSet;
import data.processing.EntityConverter;
import data.processing.Facet;
import data.processing.attrs.AttributeProducer;
import data.processing.attrs.BinaryCodeClassProducer;
import data.processing.attrs.BinaryCodeClassProducer.CodePresenceStrategy;
import data.transforms.DataSetTransform;
import data.transforms.FilterDataSetTransform;
import data.transforms.filters.RemoveTestSet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import weka.classifiers.Evaluation;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.RemoveByName;
import weka.filters.unsupervised.instance.Randomize;
import weka.filters.unsupervised.instance.RemovePercentage;

/**
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class ExperimentManager {

    private final CodePresenceStrategy codePresenceStrategy;
    private List<Integer> codesToTest;
    private List<DataConfig> dataConfigs;
    private DataSaver dataSaver;
    private List<FeatureConfig> featureConfigs = new ArrayList<FeatureConfig>();
    private List<ExperimentConfig> experimentConfigs = new ArrayList<ExperimentConfig>();
    private List<ClassifierConfig> classifierConfigs = new ArrayList<ClassifierConfig>();

    public ExperimentManager(List<DataConfig> dataConfigs, List<Integer> codesToTest, CodePresenceStrategy codePresence) {
        this.codesToTest = codesToTest;
        this.dataConfigs = dataConfigs;
        this.codePresenceStrategy = codePresence;
    }

    public void setDataSaver(DataSaver dataSaver) {
        this.dataSaver = dataSaver;
    }

    public void setExperimentConfigs(List<ExperimentConfig> expConfigs) {
        this.experimentConfigs = expConfigs;
    }

    public void setFeatureConfigs(List<FeatureConfig> featureConfigs) {
        this.featureConfigs = featureConfigs;
    }

    public void setClassifierConfigs(List<ClassifierConfig> classifierConfigs) {
        this.classifierConfigs = classifierConfigs;
    }

    public void run(ResultRecorder recorder) {

        for (int d = 0; d < dataConfigs.size(); d++) {
            DataConfig dataConfig = dataConfigs.get(d);
            String dataName = dataConfig.getName();
            DataPreparer preparer = new DataPreparer(dataConfig);

            EntitySet baseEntities = preparer.getData();
            CSV modelWriter = null;
            try {
                modelWriter = new CSV(recorder.getDestinationDir() + "/models." + System.currentTimeMillis() + ".csv", "Top 10 Features");
            } catch (IOException ex) {
                System.err.println("Unable to open model writer");
            }
            for (int i = 0; i < codesToTest.size(); i++) {
                int codeId = codesToTest.get(i);

                Facet facet = new Facet(codePresenceStrategy.toString().toLowerCase(), codeId);

                System.out.println("Creating facet " + (i + 1) + " of " + codesToTest.size() + ": " + facet.getName());

                EntitySet codeFacet = facet.facetEntitySet(baseEntities);

                //Now try each experiment config
                for (int e = 0; e < experimentConfigs.size(); e++) {
                    ExperimentConfig experimentConfig = experimentConfigs.get(e);
                    System.out.println("|  Starting experiment config " + e);

                    //Now try each feature config
                    for (int f = 0; f < featureConfigs.size(); f++) {
                        FeatureConfig featureConfig = featureConfigs.get(f);
                        System.out.println("   |  Testing feature set " + f);

                        //Prepare the data
                        EntitiesToDataSet converter = getDataSetConverter(codeId, experimentConfig, featureConfig);
                        DataSet dataSet = converter.toDataSet(codeFacet);

                        if (experimentConfig.getRunNonRandom()) {
                            Instances train = dataSet.getPartition(DataSet.Partition.Train);
                            train.sort(train.attribute("*id"));
                        }

                        dataSet.appendName(featureConfig.getName());

                        dataSet.validate();

                        if (dataSaver != null) {
                            dataSaver.saveData(dataSet);
                        }

                        dataSet = removeKeyAttributes(dataSet);

                        System.out.println("Training data has " + dataSet.getPartition(DataSet.Partition.Train).numAttributes() + " attributes");

                        //Toss out the data we don't need, to save memory
//                        dataSet.setPartition(DataSet.Partition.Full, null);
//                        dataSet.setPartition(DataSet.Partition.Test, null);
//                        System.gc();
                        System.out.println("        -Data prepared");

                        //Now try each classifier config
                        for (int c = 0; c < classifierConfigs.size(); c++) {
                            ClassifierConfig classifierConfig = classifierConfigs.get(c);
                            System.out.println("      |  Testing classifier " + c);

                            ExperimentRun runner = new ExperimentRun(dataSet, classifierConfig);
                            runner.setRuns(experimentConfig.getNumRuns());
                            runner.setFolds(experimentConfig.getNumFolds());
                            runner.setModelWriter(modelWriter);
                            Date startTime = new Date();

                            List<Evaluation> evaluations = null;
                            if (!experimentConfig.isUseTestSet()) {
                                if (experimentConfig.getRunNonRandom()) {
                                    evaluations = runner.runNonRandom();
                                } else {
                                    evaluations = runner.run();
                                }
                            } else {
                                evaluations = runner.runTest();
                            }

                            Date stopTime = new Date();

                            recorder.recordResult(dataName, startTime, stopTime, codeId, experimentConfig, featureConfig, classifierConfig, evaluations);

                        }
                    }
                }
            }
        }
    }

    private EntitiesToDataSet getDataSetConverter(int codeId, ExperimentConfig experimentConfig, FeatureConfig featureConfig) {
        String codeName = CodeNames.instance.get(codeId);

        EntityConverter converter = new EntityConverter(codeName);

        BinaryCodeClassProducer classifier = new BinaryCodeClassProducer();
        classifier.setCodeId(codeId);
        classifier.setCodeName(codeName);
        classifier.setCodePresenceStrategy(codePresenceStrategy);
        converter.pushProducer(classifier);

        List<AttributeProducer> producers = featureConfig.getPrimaryAttributeProducers();
        for (AttributeProducer producer : producers) {
            converter.pushProducer(producer);
        }

        List<DataSetTransform> transforms = getTransforms(experimentConfig, featureConfig);
        for (DataSetTransform transform : transforms) {
            converter.pushTransform(transform);
        }

        return converter;
    }

    private List<DataSetTransform> getTransforms(ExperimentConfig experimentConfig, FeatureConfig featureConfig) {
        ArrayList<DataSetTransform> transforms = new ArrayList<DataSetTransform>();

        transforms.addAll(featureConfig.getPreSplitTransforms());

        if (!experimentConfig.getRunNonRandom()) {
            //Shuffle the data before splitting
            FilterDataSetTransform randomizer = new FilterDataSetTransform();
            randomizer.setInputPartition(DataSet.Partition.Full);
            randomizer.setOutputPartitions(DataSet.Partition.Full);
            Randomize randomize = new Randomize();
            randomize.setRandomSeed(42);
            randomizer.addFilter(randomize);
            transforms.add(randomizer);
        }

        int splitPercent = experimentConfig.getTestSetPercent();

        {
            FilterDataSetTransform trainPartitioner = new FilterDataSetTransform();
            trainPartitioner.setInputPartition(DataSet.Partition.Full);
            trainPartitioner.setOutputPartitions(DataSet.Partition.Train);
            if (splitPercent >= 0) {
                RemovePercentage trainSplit = new RemovePercentage();//Create the training set
                trainSplit.setPercentage(splitPercent);
                trainPartitioner.addFilter(trainSplit);
            } else {
                RemoveTestSet trainSplit = new RemoveTestSet();
                trainSplit.setAttributeName("*test");
                trainPartitioner.addFilter(trainSplit);
            }
            transforms.add(trainPartitioner);
        }

        {
            FilterDataSetTransform testPartitioner = new FilterDataSetTransform();
            testPartitioner.setInputPartition(DataSet.Partition.Full);
            testPartitioner.setOutputPartitions(DataSet.Partition.Test);
            if (splitPercent >= 0) {
                RemovePercentage testSplit = new RemovePercentage(); //Create the test set
                testSplit.setPercentage(splitPercent);
                testSplit.setInvertSelection(true);
                testPartitioner.addFilter(testSplit);
            } else {
                RemoveTestSet trainSplit = new RemoveTestSet();
                trainSplit.setAttributeName("*test");
                trainSplit.setInvertSelection(true);
                testPartitioner.addFilter(trainSplit);
            }
            transforms.add(testPartitioner);
        }

        DataSetTransform balancer = experimentConfig.getBalancingTransform();
        transforms.add(balancer);

        {
            //Final features for full
            List<Filter> filters = featureConfig.getFinalFilters();

            FilterDataSetTransform finalFull = new FilterDataSetTransform();
            finalFull.setInputPartition(DataSet.Partition.Full);
            finalFull.setOutputPartitions(DataSet.Partition.Full);
            finalFull.setFilters(filters);
            transforms.add(finalFull);
        }

        {
            //Final features for train and test
            List<Filter> filters = featureConfig.getFinalFilters();

            FilterDataSetTransform trainLast = new FilterDataSetTransform();
            trainLast.setInputPartition(DataSet.Partition.Train);
            trainLast.setOutputPartitions(DataSet.Partition.Train);
            trainLast.setFilters(filters);
            transforms.add(trainLast);

            //Final features for test, using the same filters!
            FilterDataSetTransform testLast = new FilterDataSetTransform();
            testLast.setInputPartition(DataSet.Partition.Test);
            testLast.setOutputPartitions(DataSet.Partition.Test);
            testLast.setFilters(filters);
            transforms.add(testLast);
        }
        return transforms;
    }

    private DataSet removeKeyAttributes(DataSet dataSet) {
        if (dataSet.getPartition(DataSet.Partition.Train) != null) {
            FilterDataSetTransform remover = new FilterDataSetTransform();
            remover.setInputPartition(DataSet.Partition.Train);
            remover.setOutputPartitions(DataSet.Partition.Train);
            RemoveByName filter = new RemoveByName();
            filter.setExpression("^\\*.*$");
            remover.addFilter(filter);
            try {
                dataSet = remover.transform(dataSet);
            } catch (Exception ex) {
                System.err.println("Error removing key attributes");
            }
        }
        if (dataSet.getPartition(DataSet.Partition.Test) != null) {
            FilterDataSetTransform remover = new FilterDataSetTransform();
            remover.setInputPartition(DataSet.Partition.Test);
            remover.setOutputPartitions(DataSet.Partition.Test);
            RemoveByName filter = new RemoveByName();
            filter.setExpression("^\\*.*$");
            remover.addFilter(filter);
            try {
                dataSet = remover.transform(dataSet);
            } catch (Exception ex) {
                System.err.println("Error removing key attributes");
            }
        }
        return dataSet;
    }

    public static void runStudy(String studyName,
            List<DataConfig> dataConfigs,
            List<Integer> codesToTest,
            CodePresenceStrategy presenceStrategy,
            List<FeatureConfig> featureConfigs,
            List<ExperimentConfig> expConfigs,
            List<ClassifierConfig> classifierConfigs) {

        System.out.println("Running study " + studyName);

        ExperimentManager manager = new ExperimentManager(dataConfigs, codesToTest, presenceStrategy);

        manager.setFeatureConfigs(featureConfigs);
        manager.setExperimentConfigs(expConfigs);
        manager.setClassifierConfigs(classifierConfigs);

        DataSaver saver = new FileDataSaver("experiment_data/" + studyName);
        manager.setDataSaver(saver);

        ResultRecorder recorder = new ResultRecorder("experiment_results/" + studyName);
        try {
            recorder.open();
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
        manager.run(recorder);
    }
}
