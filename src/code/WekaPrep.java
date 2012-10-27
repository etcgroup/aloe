/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package code;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import weka.core.Attribute;
import weka.core.AttributeStats;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;
import weka.filters.Filter;
import weka.filters.supervised.instance.Resample;
import weka.filters.unsupervised.attribute.NominalToString;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.attribute.Reorder;
import weka.filters.unsupervised.attribute.StringToWordVector;
import weka.filters.unsupervised.instance.Randomize;
import weka.filters.unsupervised.instance.RemovePercentage;

/**
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class WekaPrep {
    
    static class DataConfig {

        private String classAttribute;
        private ArrayList<String> groupDistinguishers;
        private Instances train;
        private Instances trainBalanced;
        private Instances trainInflated;
        private Instances test;
        private Instances full;

        public DataConfig(Instances full, Instances train, Instances test, String classAttribute, String... groupDistinguishers) {
            this.train = train;
            this.test = test;
            this.full = full;
            this.classAttribute = classAttribute;
            this.groupDistinguishers = new ArrayList(Arrays.asList(groupDistinguishers));
        }

        public DataConfig branch(Instances full, Instances train, Instances test, String... distinguishers) {
            DataConfig child = new DataConfig(full, train, test, this.classAttribute);
            child.groupDistinguishers.addAll(this.groupDistinguishers);
            child.groupDistinguishers.addAll(Arrays.asList(distinguishers));
            return child;
        }

        @Override
        public String toString() {
            String result = "";

            for (int i = 0; i < this.groupDistinguishers.size(); i++) {
                result += this.groupDistinguishers.get(i);
                if (i < this.groupDistinguishers.size() - 1) {
                    result += ".";
                }
            }

            return result;
        }
        
        Instances balanceData(Instances instances, boolean inflate) throws Exception {
            Resample resample = new Resample();
            resample.setBiasToUniformClass(1);
            resample.setRandomSeed(1);
            
            if (!inflate) {
                AttributeStats stats = instances.attributeStats(instances.classIndex());
                int countOfLeastCommonFeature = Integer.MAX_VALUE;
                for (int i = 0; i < stats.nominalCounts.length; i++) {
                    if (stats.nominalCounts[i] < countOfLeastCommonFeature) {
                        countOfLeastCommonFeature = stats.nominalCounts[i];
                    }
                }
                int newSampleSize = countOfLeastCommonFeature * stats.nominalCounts.length;
                
                resample.setSampleSizePercent(100 * newSampleSize / instances.size());
            }
            
            resample.setInputFormat(instances);
            
            return Filter.useFilter(instances, resample);
        }

        public void prepData() throws Exception {
            Attribute classAttr = train.attribute(classAttribute);
            if (classAttr != null) {
                Reorder reorder = new Reorder();
                int[] attrIndices = new int[train.numAttributes()];
                for (int i = 0; i < attrIndices.length - 1; i++) {
                    if (i < classAttr.index()) {
                        attrIndices[i] = i;
                    } else {
                        attrIndices[i] = i + 1;
                    }
                }
                attrIndices[attrIndices.length - 1] = classAttr.index();

                reorder.setAttributeIndicesArray(attrIndices);
                reorder.setInputFormat(train);
                train = Filter.useFilter(train, reorder);
                test = Filter.useFilter(test, reorder);
                full = Filter.useFilter(full, reorder);
                
                //Balance the training set
                train.setClassIndex(train.numAttributes() - 1);
                trainInflated = balanceData(train, true);
                trainBalanced = balanceData(train, false);
                
                String relation = toString();
                train.setRelationName(relation + ".unbalanced.train");
                trainInflated.setRelationName(relation + ".inflated.train");
                trainInflated.setRelationName(relation + ".balanced.train");
                test.setRelationName(relation + ".test");
                full.setRelationName(relation);
            } else {
                System.err.println("No class attribute " + classAttribute + " in " + toString());
            }
        }
    }
    
    static String inputDataRoot = "../1 pos neg vs details/csv_data/";
    static String outputDataRoot = "../1 pos neg vs details/weka_data/";
    public static List<BagOfWordsConfig> bagConfigs = new ArrayList<BagOfWordsConfig>();
    
    static ArrayList<DataConfig> getData(String filename, String classAttribute, String... groupDistinguishers) throws Exception {
        System.out.println("Reading " + filename);

        Instances instances = ConverterUtils.DataSource.read(inputDataRoot + filename);
        instances = removeAttribute(instances, "participant");

        DataConfig config = new DataConfig(null, null, null, classAttribute, groupDistinguishers);

        return createDataSets(config, instances, bagConfigs);
    }

    static ArrayList<DataConfig> createDataSets(DataConfig config, Instances instances, List<BagOfWordsConfig> bagConfigs) throws Exception {

        ArrayList<DataConfig> out = new ArrayList<DataConfig>();

        //Shuffle the data before splitting
        Randomize randomize = new Randomize();
        randomize.setRandomSeed(42);
        randomize.setInputFormat(instances);
        instances = Filter.useFilter(instances, randomize);
        config.full = instances;
        
        //Create the training set
        RemovePercentage split = new RemovePercentage();
        split.setPercentage(10);
        split.setInputFormat(instances);
        config.train = Filter.useFilter(instances, split);
        
        //Create the test set
        split = new RemovePercentage();
        split.setPercentage(10);
        split.setInvertSelection(true);
        split.setInputFormat(instances);
        config.test = Filter.useFilter(instances, split);

        for (BagOfWordsConfig bagConfig : bagConfigs) {
            out.add(toWordBags(config, "message", bagConfig));
        }
        
        return out;
    }
    
    static void putData(DataConfig config) throws Exception {

        config.prepData();

        String filename = outputDataRoot + config.train.relationName() + ".arff";
        ConverterUtils.DataSink.write(filename, config.train);

        System.out.println("Saved " + filename);
        
        filename = outputDataRoot + config.trainInflated.relationName() + ".arff";
        ConverterUtils.DataSink.write(filename, config.trainInflated);

        System.out.println("Saved " + filename);

        filename = outputDataRoot + "_t_" + config.test.relationName() + ".arff";
        ConverterUtils.DataSink.write(filename, config.test);

        System.out.println("Saved " + filename);
        
        filename = outputDataRoot + "_f_" + config.full.relationName() + ".arff";
        ConverterUtils.DataSink.write(filename, config.full);

        System.out.println("Saved " + filename);

    }

    static Instances removeAttribute(Instances instances, String attributeName) throws Exception {
        Attribute attr = instances.attribute(attributeName);
        if (attr != null) {
            Remove remove = new Remove();
            remove.setAttributeIndices(1 + attr.index() + "");
            remove.setInputFormat(instances);
            instances = Filter.useFilter(instances, remove);
        } else {
            System.err.println("No attribute " + attributeName + " in " + instances.relationName());
        }
        return instances;
    }

    private static DataConfig toWordBags(DataConfig config, String attributeName, BagOfWordsConfig bagConfig) throws Exception {
        Instances train = config.train;
        Instances test = config.test;
        Instances full = config.full;

        Attribute attr = train.attribute(attributeName);
        if (attr != null) {
            NominalToString stringify = new NominalToString();
            stringify.setAttributeIndexes(1 + attr.index() + "");
            stringify.setInputFormat(train);
            train = Filter.useFilter(train, stringify);
            test = Filter.useFilter(test, stringify);
            full = Filter.useFilter(full, stringify);
            
            StringToWordVector bagger = new StringToWordVector();
            bagger.setAttributeIndices(1 + attr.index() + "");
            bagger.setAttributeNamePrefix("_");

            bagConfig.configure(bagger);
            
            bagger.setInputFormat(train);

            train = Filter.useFilter(train, bagger);
            test = Filter.useFilter(test, bagger);
            
            //Reset the bagger on the full data set
            bagger = new StringToWordVector();
            bagger.setAttributeIndices(1 + attr.index() + "");
            bagger.setAttributeNamePrefix("_");
    
            bagConfig.configure(bagger);
            
            bagger.setInputFormat(full);
            full = Filter.useFilter(full, bagger);
            
        } else {
            System.err.println("No attribute " + attributeName + " in " + train.relationName());
        }

        DataConfig baggedConfig = config.branch(full, train, test, bagConfig.toString());

        return baggedConfig;
    }
}
