/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data.processing.attrs;

import data.EntityMetaData;
import data.MultiRatedEntity;
import java.util.ArrayList;
import java.util.List;
import weka.core.Attribute;
import weka.core.Instances;

/**
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public abstract class AbstractAttributeProducer implements AttributeProducer {

    @Override
    public List<Attribute> getAttributes() {
        return new ArrayList<Attribute>();
    }

    @Override
    public boolean isClassProvider() {
        return false;
    }

    @Override
    public void setClassAttribute(Instances format) {
        throw new UnsupportedOperationException("Attribute producer is not a class attribute provider");
    }

    
}
