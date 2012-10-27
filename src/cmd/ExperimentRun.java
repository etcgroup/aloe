/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cmd;

import cmd.config.ClassifierConfig;
import daisy.io.CSV;
import data.DataSet;
import java.util.*;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.SMO;
import weka.core.AttributeStats;
import weka.core.Instances;

/**
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class ExperimentRun {

    private final DataSet dataSet;
    private final ClassifierConfig classifierConfig;
    private int folds = 10;
    private int runs = 1;
    private int randomSeed = 1;
    private CSV modelWriter;

    ExperimentRun(DataSet dataSet, ClassifierConfig classifierConfig) {
        this.dataSet = dataSet;
        this.classifierConfig = classifierConfig;
    }

    public void setFolds(int folds) {
        this.folds = folds;
    }

    public void setRuns(int runs) {
        this.runs = runs;
    }

    public void setRandomSeed(int randomSeed) {
        this.randomSeed = randomSeed;
    }

    public List<Evaluation> runTest() {
        List<Evaluation> resultList = new ArrayList<Evaluation>();

        Instances train = this.dataSet.getPartition(DataSet.Partition.Train);
        Instances test = this.dataSet.getPartition(DataSet.Partition.Test);

        Classifier classifier = classifierConfig.getConfiguredClassifier();
        if (classifier == null) {
            System.err.println("Terminating evaluation of classifier " + classifierConfig.getClassifierType());
            return resultList;
        }
        try {
            classifier.buildClassifier(train);

            Evaluation eval = new Evaluation(train);
            eval.evaluateModel(classifier, test);
            resultList.add(eval);

            System.out.println("error rate: " + eval.errorRate() + " f-measure: " + eval.fMeasure(1));

        } catch (Exception e) {
            System.err.println("Error evaluating classifier " + classifierConfig.getConfigString());
            e.printStackTrace();
        }
        return resultList;
    }

    public List<Evaluation> run() {
        //Do several runs of cross validation
        List<Evaluation> resultList = new ArrayList<Evaluation>();
        for (int r = 0; r < runs; r++) {
            System.out.print("Run " + r + "... ");

            int seed = randomSeed + r;
            Random rand = new Random(seed);

            Instances train = this.dataSet.getPartition(DataSet.Partition.Train);
            Classifier classifier = classifierConfig.getConfiguredClassifier();
            if (classifier == null) {
                System.err.println("Terminating evaluation of classifier " + classifierConfig.getClassifierType());
                return resultList;
            }

            try {
                Evaluation eval = new Evaluation(train);
                eval.crossValidateModel(classifier, train, folds, rand);
                resultList.add(eval);

                System.out.println("error rate: " + eval.errorRate() + " f-measure: " + eval.fMeasure(1));

                if (classifier instanceof SMO && modelWriter != null) {
                    SMO fullCl = (SMO) classifierConfig.getConfiguredClassifier();
                    fullCl.buildClassifier(train);

                    double[] sparseWeights = fullCl.sparseWeights()[0][1];
                    int[] sparseIndices = fullCl.sparseIndices()[0][1];

                    final double[] attrWeightsSq = new double[train.numAttributes()];
                    double[] attrWeights = new double[train.numAttributes()];
                    Integer[] attrRanking = new Integer[train.numAttributes()];

                    for (int i = 0; i < sparseWeights.length; i++) {
                        int index = sparseIndices[i];
                        double weight = sparseWeights[i];
                        attrWeightsSq[index] = weight * weight;
                        attrWeights[index] = weight;
                    }
                    for (int i = 0; i < attrRanking.length; i++) {
                        attrRanking[i] = i;
                    }
                    Arrays.sort(attrRanking, new Comparator<Integer>() {

                        @Override
                        public int compare(Integer o1, Integer o2) {
                            return Double.compare(attrWeightsSq[o1], attrWeightsSq[o2]);
                        }
                    });

                    //Print out the top 10
                    modelWriter.println("Top 10 attributes for " + train.classAttribute().name());
                    modelWriter.println("Rank", "Feature", "Weight", "Weight^2");
                    for (int i = 0; i < 10; i++) {
                        int idx = attrRanking.length - 1 - i;
                        int attrIndex = attrRanking[idx];
                        String name = train.attribute(attrIndex).name();
                        double weightSq = attrWeightsSq[attrIndex];
                        double weight = attrWeights[attrIndex];
                        if (weightSq != weight * weight) {
                            throw new IllegalStateException("???");
                        }
                        modelWriter.println("" + idx, name, "" + weight, "" + weightSq);
                    }
                }


            } catch (Exception e) {
                System.err.println("Error evaluating classifier " + classifierConfig.getConfigString());
                e.printStackTrace();
            }
        }
        return resultList;
    }

    public void setModelWriter(CSV csv) {
        this.modelWriter = csv;
    }

    public List<Evaluation> runNonRandom() {
        //Do several runs of cross validation
        List<Evaluation> resultList = new ArrayList<Evaluation>();
        for (int r = 0; r < runs; r++) {
            System.out.print("Run " + r + "... ");

            int seed = randomSeed + r;
            Random rand = new Random(seed);

            Instances full = new Instances(this.dataSet.getPartition(DataSet.Partition.Train));
            Classifier classifier = classifierConfig.getConfiguredClassifier();
            if (classifier == null) {
                System.err.println("Terminating evaluation of classifier " + classifierConfig.getClassifierType());
                return resultList;
            }

            try {
                full.stratify(folds);

                Evaluation eval = new Evaluation(full);
                for (int i = 0; i < folds; i++) {
                    Instances train = full.trainCV(folds, i, rand);
                    Instances test = full.testCV(folds, i);

                    AttributeStats trainStats = train.attributeStats(train.classIndex());
                    AttributeStats testStats = train.attributeStats(test.classIndex());
                    double trainPercentPos = (double) trainStats.nominalCounts[1] / trainStats.totalCount;
                    double testPercentPos = (double) testStats.nominalCounts[1] / testStats.totalCount;
                    System.out.println("  Train % pos: " + trainPercentPos + " Test % pos: " + testPercentPos);

                    Classifier copiedClassifier = AbstractClassifier.makeCopy(classifier);
                    copiedClassifier.buildClassifier(train);
                    eval.evaluateModel(copiedClassifier, test);


                }
                resultList.add(eval);
                System.out.println("error rate: " + eval.errorRate() + " f-measure: " + eval.fMeasure(1));
            } catch (Exception e) {
                System.err.println("Error conducting evaluation fold");
                e.printStackTrace();
            }
        }
        return resultList;
    }
}
