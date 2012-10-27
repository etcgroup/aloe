/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cmd.config.exp;

import data.DataSet;
import data.transforms.DataSetTransform;
import data.transforms.FilterDataSetTransform;
import data.transforms.NullDataSetTransform;
import data.transforms.ReweightBalancingFilter;
import weka.filters.supervised.instance.SpreadSubsample;

/**
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class DefaultExperimentConfig implements ExperimentConfig {
    private boolean runNonRandom;
    private boolean useTestSet;

    @Override
    public boolean isUseTestSet() {
        return useTestSet;
    }

    
    @Override
    public String describeBalancingStrategy() {
        return getBalancingStrategy().toString();
    }

    @Override
    public int getNumRuns() {
        return 1;
    }

    @Override
    public int getNumFolds() {
        return 10;
    }

    @Override
    public boolean getRunNonRandom() {
        return runNonRandom;
    }

    public void setRunNonRandom(boolean runNonRandom) {
        this.runNonRandom = runNonRandom;
    }

    public void setUseTestSet(boolean useTestSet) {
        this.useTestSet = useTestSet;
    }

    
    
    public enum BalancingStrategy {

        Reweight,
        Downsample,
        None
    }
    BalancingStrategy balancingStrategy = BalancingStrategy.None;

    public DefaultExperimentConfig() {
    }

    public DefaultExperimentConfig(BalancingStrategy balancingStrategy) {
        this.balancingStrategy = balancingStrategy;
    }

    @Override
    public int getTestSetPercent() {
        return 0;
    }

    public BalancingStrategy getBalancingStrategy() {
        return balancingStrategy;
    }

    public void setBalancingStrategy(BalancingStrategy balancingStrategy) {
        this.balancingStrategy = balancingStrategy;
    }

    @Override
    public DataSetTransform getBalancingTransform() {
        switch (balancingStrategy) {
            case Reweight:
                return new ReweightBalancingFilter();
            case Downsample:
                FilterDataSetTransform balancer = new FilterDataSetTransform();
                balancer.setName("downsample");
                balancer.setInputPartition(DataSet.Partition.Train);
                balancer.setOutputPartitions(DataSet.Partition.Train);

                SpreadSubsample sampler = new SpreadSubsample();
                sampler.setDistributionSpread(1);
                sampler.setRandomSeed(57);

                balancer.addFilter(sampler);
                return balancer;
            default:
                return new NullDataSetTransform("unbalanced");
        }
    }
}
