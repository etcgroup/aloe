/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data.processing.attrs;

import data.EntityMetaData;
import data.MultiRatedEntity;
import java.util.List;
import weka.core.Attribute;
import weka.core.Instances;

/**
 * Produces an attribute from an entity. Used in converting from EntitySet to DataSet.
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public interface AttributeProducer {
    public List<Attribute> getAttributes();
    public void putValues(MultiRatedEntity entity, EntityMetaData meta, List<Object> values);
    public boolean isClassProvider();
    public void setClassAttribute(Instances format);
}
