/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data.transforms.filters;

import weka.core.Attribute;
import weka.core.Instances;
import weka.filters.unsupervised.instance.RemoveWithValues;

/**
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class RemoveTestSet extends RemoveWithValues {

    String attributeName;

    @Override
    public boolean setInputFormat(Instances instanceInfo) throws Exception {
        if (attributeName == null) {
            throw new IllegalStateException("String attribute name was not set");
        }

        Attribute attr = instanceInfo.attribute(attributeName);
        if (attr == null) {
            throw new IllegalStateException("Attribute " + attributeName + " does not exist");
        }

        this.setAttributeIndex(Integer.toString(1 + attr.index()));
        this.setNominalIndicesArr(new int[]{1});
        
        return super.setInputFormat(instanceInfo);
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attrName) {
        this.attributeName = attrName;
    }
}
