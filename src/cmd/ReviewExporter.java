/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cmd;

import cmd.config.*;
import cmd.config.exp.DefaultExperimentConfig;
import cmd.config.exp.ExperimentConfig;
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
import java.io.IOException;
import java.util.*;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.RemoveByName;
import weka.filters.unsupervised.instance.Randomize;
import weka.filters.unsupervised.instance.RemovePercentage;

/**
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class ReviewExporter {

    private final CodePresenceStrategy codePresenceStrategy;
    private int codeId;
    private DataConfig dataConfig;
    private DataSaver dataSaver;
    private FeatureConfig featureConfig;
    private ExperimentConfig experimentConfig;
    private ClassifierConfig classifierConfig;

    public ClassifierConfig getClassifierConfig() {
        return classifierConfig;
    }

    public void setClassifierConfig(ClassifierConfig classifierConfig) {
        this.classifierConfig = classifierConfig;
    }

    public int getCodeId() {
        return codeId;
    }

    public void setCodeId(int codeId) {
        this.codeId = codeId;
    }

    public DataConfig getDataConfig() {
        return dataConfig;
    }

    public void setDataConfig(DataConfig dataConfig) {
        this.dataConfig = dataConfig;
    }

    public DataSaver getDataSaver() {
        return dataSaver;
    }

    public void setDataSaver(DataSaver dataSaver) {
        this.dataSaver = dataSaver;
    }

    public ExperimentConfig getExperimentConfig() {
        return experimentConfig;
    }

    public void setExperimentConfig(ExperimentConfig experimentConfig) {
        this.experimentConfig = experimentConfig;
    }

    public FeatureConfig getFeatureConfig() {
        return featureConfig;
    }

    public void setFeatureConfig(FeatureConfig featureConfig) {
        this.featureConfig = featureConfig;
    }

    public static void exportClassifications(
            String studyName,
            DataConfig dataConfig,
            int codeId,
            BinaryCodeClassProducer.CodePresenceStrategy presenceStrategy,
            FeatureConfig featureConfig,
            ExperimentConfig expConfig,
            ClassifierConfig classifierConfig) {

        System.out.println("Exporting results from study " + studyName);

        ReviewExporter exporter = new ReviewExporter(dataConfig, codeId, presenceStrategy);

        exporter.setFeatureConfig(featureConfig);
        exporter.setExperimentConfig(expConfig);
        exporter.setClassifierConfig(classifierConfig);

        DataSaver saver = new FileDataSaver("report_data/" + studyName);
        exporter.setDataSaver(saver);

        exporter.run();
    }

    private ReviewExporter(DataConfig dataConfig, int codeId, CodePresenceStrategy presenceStrategy) {
        this.dataConfig = dataConfig;
        this.codeId = codeId;
        this.codePresenceStrategy = presenceStrategy;
    }

    public void run() {
        String dataName = dataConfig.getName();
        DataPreparer preparer = new DataPreparer(dataConfig);

        EntitySet baseEntities = preparer.getData();

        Facet facet = new Facet(codePresenceStrategy.toString().toLowerCase(), codeId);

        System.out.println("Creating facet: " + facet.getName());

        EntitySet codeFacet = facet.facetEntitySet(baseEntities);

        EntitiesToDataSet converter = getDataSetConverter(codeId, experimentConfig, featureConfig);
        DataSet dataSet = converter.toDataSet(codeFacet);

        dataSet.appendName(featureConfig.getName());

        dataSet.validate();

        DataSetTransform balancer = experimentConfig.getBalancingTransform();
        if (balancer != null) {
            try {
                dataSet = balancer.transform(dataSet);
            } catch (Exception ex) {
                System.err.println("Balancing transform failed.");
                ex.printStackTrace();
            }
        }

        if (dataSaver != null) {
            dataSaver.saveData(dataSet);
        }

        //Toss out the data we don't need, to save memory
        dataSet.setPartition(DataSet.Partition.Full, null);
        dataSet.setPartition(DataSet.Partition.Test, null);
        System.gc();
        System.out.println("Data prepared");


        Instances data = dataSet.getPartition(DataSet.Partition.Train);
        Classifier classifier = classifierConfig.getConfiguredClassifier();
        if (classifier == null) {
            System.err.println("Terminating evaluation of classifier " + classifierConfig.getClassifierType());
            System.exit(1);
        }

        int numFolds = 10;

        //Randomize and stratify
        Random random = new Random(544);
        data = new Instances(data);
        data.randomize(random);
        if (data.classAttribute().isNominal()) {
            data.stratify(numFolds);
        }

        //Then remove keys
        Instances anon = removeKeyAttributes(data);

        random = new Random(867);
        Instances train = data.trainCV(numFolds, 0, random);

        Random randomAnon = new Random(867);
        Instances trainAnon = anon.trainCV(numFolds, 0, randomAnon);

        int idAttrIndex = train.attribute("*id").index();

        ArrayList<Integer> falsePositives = new ArrayList<Integer>();
        ArrayList<Integer> falseNegatives = new ArrayList<Integer>();
        ArrayList<Integer> truePredictions = new ArrayList<Integer>();

        try {
            Date startTime = new Date();
            classifier.buildClassifier(trainAnon);
            Date stopTime = new Date();

            Instances test = data.testCV(numFolds, 0);
            Instances testAnon = anon.testCV(numFolds, 0);

            for (int i = 0; i < testAnon.numInstances(); i++) {
                Instance anonInstance = testAnon.get(i);
                int prediction = (int) classifier.classifyInstance(anonInstance);
                int actual = (int) anonInstance.classValue();

                Instance instance = test.get(i);
                int id = (int) instance.value(idAttrIndex);
                if (prediction == actual) {
                    truePredictions.add(id);
                } else if (actual == 1) {
                    falseNegatives.add(id);
                } else {
                    falsePositives.add(id);
                }
            }

        } catch (Exception e) {
            System.err.println("Error training classifier");
            e.printStackTrace();
        }

        dataSaver.recordReport(dataName, CodeNames.instance.get(codeId), featureConfig, classifierConfig, "false-positives", falsePositives, 1, codeFacet, data);
        dataSaver.recordReport(dataName, CodeNames.instance.get(codeId), featureConfig, classifierConfig, "false-negatives", falseNegatives, 0, codeFacet, data);
        dataSaver.recordReport(dataName, CodeNames.instance.get(codeId), featureConfig, classifierConfig, "true-predictions", truePredictions, -1, codeFacet, data);
    }

    private Instances removeKeyAttributes(Instances dataSet) {
        try {
            RemoveByName filter = new RemoveByName();
            filter.setExpression("^\\*.*$");
            filter.setInputFormat(dataSet);
            return Filter.useFilter(dataSet, filter);
        } catch (Exception e) {
            System.err.println("Error removing key attrs");
            e.printStackTrace();
            System.exit(1);
        }
        return null;
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

        {
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
            RemovePercentage trainSplit = new RemovePercentage();//Create the training set
            trainSplit.setPercentage(splitPercent);
            trainPartitioner.addFilter(trainSplit);
            transforms.add(trainPartitioner);
        }

        {
            FilterDataSetTransform testPartitioner = new FilterDataSetTransform();
            testPartitioner.setInputPartition(DataSet.Partition.Full);
            testPartitioner.setOutputPartitions(DataSet.Partition.Test);
            RemovePercentage testSplit = new RemovePercentage(); //Create the test set
            testSplit.setPercentage(splitPercent);
            testSplit.setInvertSelection(true);
            testPartitioner.addFilter(testSplit);
            transforms.add(testPartitioner);
        }

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

    public static void main(String[] args) {
        DefaultDataConfig dataConfig = new DefaultDataConfig("seg-6-human");
        dataConfig.setSegmentationId(6);
        dataConfig.setRemoveSystemMessages(true);
        
        String studyName = "seg-6-human";
        //int codeId = 73; //annoyance
        int codeId = 81; //amusement
        
        ParameterizedFeatureConfig featureConfig = new ParameterizedFeatureConfig("the-works-unstemmed-unstopped");
        featureConfig.setFeatureSets(EnumSet.allOf(ParameterizedFeatureConfig.FeatureSet.class));
        featureConfig.setEmoticonSettings(EnumSet.of(ParameterizedFeatureConfig.EmoticonSettings.GlobalBasis, ParameterizedFeatureConfig.EmoticonSettings.WordCounts));
        featureConfig.setBagOfWordsSettings(EnumSet.of(ParameterizedFeatureConfig.BagOfWordsSettings.GlobalBasis, ParameterizedFeatureConfig.BagOfWordsSettings.LargeMinFrequency, ParameterizedFeatureConfig.BagOfWordsSettings.LowerCase, ParameterizedFeatureConfig.BagOfWordsSettings.ExcludeNonsense, ParameterizedFeatureConfig.BagOfWordsSettings.Stemming, ParameterizedFeatureConfig.BagOfWordsSettings.LowerCase, ParameterizedFeatureConfig.BagOfWordsSettings.ImposeHardLimit));
        
        ClassifierConfig classifierConfig = new WekaClassifierConfig("weka.classifiers.functions.SMO", "-C 1.0 -L 0.0010 -P 1.0E-12 -N 0 -V -1 -W 1 -K \"weka.classifiers.functions.supportVector.PolyKernel -C 250007 -E 1.0\"");
        ExperimentConfig expConfig = new DefaultExperimentConfig(DefaultExperimentConfig.BalancingStrategy.Downsample);
        
        exportClassifications(
            studyName, dataConfig, codeId,
            CodePresenceStrategy.Any,
            featureConfig, expConfig, classifierConfig);
        
    }
}
