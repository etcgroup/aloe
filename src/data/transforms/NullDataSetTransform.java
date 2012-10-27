/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data.transforms;

import data.DataSet;

/**
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class NullDataSetTransform extends AbstractDataSetTransform {

    String name;

    public NullDataSetTransform(String name) {
        this.name = name;
    }

    public NullDataSetTransform() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public DataSet transform(DataSet dataSet) throws Exception {
        DataSet out = super.transform(dataSet);
        if (getName() != null) {
            out.appendName(getName());
        }
        return out;
    }
}
