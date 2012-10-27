/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import weka.core.Instance;
import weka.core.Instances;

/**
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class DataSet {

    public void validate() {
        System.out.println("Validating data set " + getName());
        
        Instances train = partitions.get(Partition.Train);
        Instances test = partitions.get(Partition.Test);
        Instances full = partitions.get(Partition.Full);
        
        int idAttrIndex = train.attribute("*id").index();
        
        HashSet<Integer> trainIds = new HashSet<Integer>();
        for (int i = 0; i < train.size(); i++) {
            Instance instance = train.instance(i);
            int id = (int)instance.value(idAttrIndex);
            
            //Check for duplicates
            if (trainIds.contains(id)) {
                throw new IllegalStateException("The training set contains duplicates");
            }
            
            trainIds.add(id);
        }
        
        HashSet<Integer> testIds = new HashSet<Integer>();
        for (int i = 0; i < test.size(); i++) {
            Instance instance = test.instance(i);
            int id = (int)instance.value(idAttrIndex);
            
            //Make sure the train and test sets don't overlap
            if (trainIds.contains(id)) {
                throw new IllegalStateException("Entity id " + id + " present in both train and test.");
            }
            
            //Check for duplicates
            if (testIds.contains(id)) {
                throw new IllegalStateException("The test set contains duplicates");
            }
        }
    }

    public enum Partition {
        Full,
        Train,
        Test
    }
    
    private HashMap<Partition, Instances> partitions = new HashMap<Partition, Instances>();
    
    private String name;

    public DataSet(DataSet toCopy) {
        for (Map.Entry<Partition, Instances> entry : toCopy.partitions.entrySet()) {
            this.partitions.put(entry.getKey(), new Instances(entry.getValue()));
        }
        this.name = toCopy.name;
    }

    public DataSet(String name, Instances full) {
        this.name = name;
        this.partitions.put(Partition.Full, full);
    }

    public Instances getPartition(Partition partition) {
        return partitions.get(partition);
    }
    
    public void setPartition(Partition partition, Instances instances) {
        this.partitions.put(partition, instances);
    }
    
    public void appendName(String name) {
        this.name += "." + name;
    }
    
    
    public String getName() {
        return this.name;
    }
}
