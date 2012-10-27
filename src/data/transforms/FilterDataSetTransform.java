/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data.transforms;

import data.DataSet;
import data.DataSet.Partition;
import java.util.ArrayList;
import java.util.List;
import weka.core.Instances;
import weka.filters.Filter;

/**
 * Applies a Weka filter to the input partition once for each output partition.
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class FilterDataSetTransform extends AbstractDataSetTransform {

    private List<Filter> filters = new ArrayList<Filter>();
    private Partition inputPartition;
    private Partition[] outputPartitions;
    private String name;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<Filter> getFilters() {
        return filters;
    }

    public void setFilters(List<Filter> filters) {
        this.filters = filters;
    }

    public void addFilter(Filter filter) {
        this.filters.add(filter);
    }

    public Partition getInputPartition() {
        return inputPartition;
    }

    public void setInputPartition(Partition inputPartition) {
        this.inputPartition = inputPartition;
    }

    public Partition[] getOutputPartitions() {
        return outputPartitions;
    }

    public void setOutputPartitions(Partition... outputPartitions) {
        this.outputPartitions = outputPartitions;
    }

    @Override
    public DataSet transform(DataSet dataSet) throws Exception {
        DataSet copied = super.transform(dataSet);
        if (getName() != null) {
            copied.appendName(getName());
        }

        for (int f = 0; f < filters.size(); f++) {
            Filter filter = filters.get(f);
            //Initialize the filter
            if (!filter.isFirstBatchDone()) {
                filter.setInputFormat(copied.getPartition(inputPartition));
            }

            for (int i = 0; i < outputPartitions.length; i++) {
                Instances output = Filter.useFilter(copied.getPartition(inputPartition), filter);
                Partition outputPartition = outputPartitions[i];
                copied.setPartition(outputPartition, output);
            }
        }
        return copied;
    }
}
