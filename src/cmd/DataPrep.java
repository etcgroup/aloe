/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cmd;

import cmd.config.exp.ExperimentConfig;
import cmd.config.exp.DefaultExperimentConfig;
import cmd.config.*;
import data.DataSet;
import data.EntitySet;
import data.analysis.Analysis;
import data.analysis.AnalysisResult;
import data.indexes.CodeNames;
import data.io.DataSaver;
import data.io.DataSource;
import data.io.FileDataSaver;
import data.processing.EntitiesToDataSet;
import data.processing.EntityConverter;
import data.processing.EntitySetFilter;
import data.processing.Facet;
import data.processing.attrs.AttributeProducer;
import data.processing.attrs.BinaryCodeClassProducer;
import data.transforms.DataSetTransform;
import data.transforms.FilterDataSetTransform;
import java.util.ArrayList;
import java.util.List;
import weka.filters.Filter;
import weka.filters.unsupervised.instance.Randomize;
import weka.filters.unsupervised.instance.RemovePercentage;

/**
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class DataPrep {

    public static void main(String[] args) {
        DataPrep pipeline = new DataPrep(new DefaultDataConfig(), new DefaultAnalysisConfig(), new DefaultFeatureConfig(), new DefaultExperimentConfig(DefaultExperimentConfig.BalancingStrategy.Downsample));
        pipeline.run();
    }
    
    private final FeatureConfig featureConfig;
    private final AnalysisConfig analysisConfig;
    private final DataConfig dataConfig;
    private final ExperimentConfig expConfig;
    
    public DataPrep(DataConfig dataConfig, AnalysisConfig analysisConfig, FeatureConfig featureConfig, ExperimentConfig expConfig) {
        this.dataConfig = dataConfig;
        this.analysisConfig = analysisConfig;
        this.featureConfig = featureConfig;
        this.expConfig = expConfig;
    }

    public void run() {
        DataSource source = dataConfig.getDataSource();
        dataConfig.configureDataSource(source);
        
        try {
            source.initialize();
        } catch (Exception e) {
            System.err.println("Error initializing data source.");
            e.printStackTrace();
            System.exit(1);
        }
        
        source.loadIndexes();
        
        EntitySet entities = source.getData();

        
        DataSaver saver = getDataSaver();
        
        List<Analysis<EntitySet>> preFilterAnalyses = analysisConfig.getPreFilterAnalyses();
        for (int i = 0; i < preFilterAnalyses.size(); i++) {
            Analysis<EntitySet> analysis = preFilterAnalyses.get(i);
            System.out.println("Running pre-filter analysis: " + analysis.getName());
            AnalysisResult result = analysis.analyze(entities);
            saver.saveAnalysis(result, entities.getName(), analysis.getName(), analysisConfig.isReportHumanReadable());
        }
        
        List<EntitySetFilter> entitySetFilters = dataConfig.getEntitySetFilters();
        for (EntitySetFilter filter : entitySetFilters) {
            entities = filter.filter(entities);
            System.out.println("Filtered to " + entities.size() + " entities.");
        }
        
        List<Analysis<EntitySet>> postFilterAnalyses = analysisConfig.getPostFilterAnalyses();
        for (int i = 0; i < postFilterAnalyses.size(); i++) {
            Analysis<EntitySet> analysis = postFilterAnalyses.get(i);
            System.out.println("Running post-filter analysis: " + analysis.getName());
            AnalysisResult result = analysis.analyze(entities);
            saver.saveAnalysis(result, entities.getName(), analysis.getName(), analysisConfig.isReportHumanReadable());
        }

        List<Integer> codeIds = analysisConfig.getCodeIds();

        for (int i = 0; i < codeIds.size(); i++) {
            int codeId = codeIds.get(i);
            String codeName = CodeNames.instance.get(codeId);

            Facet facet = new Facet(analysisConfig.getCodePresenceStrategy().toString().toLowerCase(), codeId);

            System.out.println("Creating facet " + (i + 1) + " of " + codeIds.size() + ": " + facet.getName());
            EntitySet faceted = facet.facetEntitySet(entities);

            EntitiesToDataSet converter = getDataSetConverter(codeName, codeId);

            System.out.println("Converting to data set...");
            DataSet dataSet = converter.toDataSet(faceted);

            dataSet.validate();

            DataSetTransform balancer = expConfig.getBalancingTransform();
            if (balancer != null) {
                try {
                    dataSet = balancer.transform(dataSet);
                } catch (Exception ex) {
                    System.err.println("Balancing transform failed.");
                    ex.printStackTrace();
                    System.exit(1);
                }
            }
            
            System.out.println("Saving data set for " + facet.getName());
            saver.saveData(dataSet);
        }
    }

    private EntitiesToDataSet getDataSetConverter(String codeName, int codeId) {
        EntityConverter converter = new EntityConverter(codeName);

        BinaryCodeClassProducer classifier = new BinaryCodeClassProducer();
        classifier.setCodeId(codeId);
        classifier.setCodeName(codeName);
        classifier.setCodePresenceStrategy(analysisConfig.getCodePresenceStrategy());
        converter.pushProducer(classifier);

        List<AttributeProducer> producers = featureConfig.getPrimaryAttributeProducers();
        for(AttributeProducer producer : producers) {
            converter.pushProducer(producer);
        }        

        List<DataSetTransform> transforms = getTransforms();
        for (DataSetTransform transform : transforms) {
            converter.pushTransform(transform);
        }

        return converter;
    }

    private List<DataSetTransform> getTransforms() {
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

        int splitPercent = expConfig.getTestSetPercent();
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

    private DataSaver getDataSaver() {
        DataSaver saver = new FileDataSaver(dataConfig.getDestination());
        saver.initialize();
        return saver;
    }

    
}
