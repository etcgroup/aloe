/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data.transforms;

import data.DataSet;
import java.util.Arrays;
import weka.core.AttributeStats;
import weka.core.Instance;
import weka.core.Instances;

/**
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class ReweightBalancingFilter extends AbstractDataSetTransform {

    @Override
    public DataSet transform(DataSet dataSet) throws Exception {
        DataSet copy = super.transform(dataSet);
        copy.appendName("weightbalanced");
        
        Instances train = copy.getPartition(DataSet.Partition.Train);
        int classAttrIndex = train.classIndex();
        if (classAttrIndex < 0) {
            throw new IllegalStateException("The class attribute is not defined.");
        }
        
        AttributeStats stats = train.attributeStats(classAttrIndex);
        int instances = train.size();
        int values = stats.distinctCount;
        int[] counts = stats.nominalCounts;
        
        double[] weights = new double[values];
        for (int i = 0; i < values; i++) {
            weights[i] = (double)instances / (values * counts[i]);
        }
        
        System.out.println("Balancing data set with weights: " + Arrays.toString(weights));
        
        for (int i = 0; i < instances; i++) {
            Instance instance = train.instance(i);
            
            int classValue = (int)instance.classValue();
            instance.setWeight(weights[classValue]);
        }
        
        return copy;
    }
    
}
