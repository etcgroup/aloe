/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data.processing;

import data.DataSet;
import data.EntityMetaData;
import data.EntitySet;
import data.MultiRatedEntity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;

/**
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class BaseEntitiesToDataSet implements EntitiesToDataSet {

    private final String conversionName;
    private final String keyPrefix = "*";

    public BaseEntitiesToDataSet(String conversionName) {
        this.conversionName = conversionName;
    }

    @Override
    public final DataSet toDataSet(EntitySet entities) {

        //Construct the full instance set
        Instances full = createOutputFormat(entities);
        for (MultiRatedEntity entity : entities) {
            int entityId = entity.getEntityId();
            EntityMetaData meta = entities.getMetaData(entityId);

            insertInstance(entity, meta, full);
        }

        DataSet dataSet = new DataSet(entities.getName() + "." + getConversionName(), full);

        dataSet = postprocess(dataSet);

        return dataSet;
    }

    public String getConversionName() {
        return conversionName;
    }

    public final String getKeyPrefix() {
        return keyPrefix;
    }

    private Instances createOutputFormat(EntitySet entities) {
        ArrayList<Attribute> attributes = new ArrayList<Attribute>();

        //Add the key information
        attributes.add(new Attribute(getKeyPrefix() + "id"));
        attributes.add(new Attribute(getKeyPrefix() + "test", Arrays.asList("no", "yes")));

        //Add the data attributes
        attributes.addAll(this.getAttributes());

        Instances outputFormat = new Instances("none-yet", attributes, 0);

        //Add an entry to any string attrs
        for (int i = 0; i < outputFormat.numAttributes(); i++) {
            Attribute attr = outputFormat.attribute(i);
            if (attr.isString()) {
                attr.addStringValue("Hack for sparse instance");
            }
        }


        setClassAttribute(outputFormat);

        return outputFormat;
    }

    protected List<Attribute> getAttributes() {
        return new ArrayList<Attribute>();
    }

    private void insertInstance(MultiRatedEntity entity, EntityMetaData meta, Instances output) {
        Instance instance = new SparseInstance(output.numAttributes());

        //Add the key value
        instance.setValue(output.attribute(getKeyPrefix() + "id"), entity.getEntityId());
        instance.setValue(output.attribute(getKeyPrefix() + "test"), entity.isTestSet() ? 1 : 0);

        int attrIndex = 2;

        //Add all the other values
        List<Object> otherValues = getValues(entity, meta);
        for (int i = 0; i < otherValues.size(); i++) {
            Object val = otherValues.get(i);
            if (val instanceof String) {
                String strVal = (String) val;
                instance.setValue(output.attribute(attrIndex + i), strVal);
            } else {
                double dblVal = (Double) val;
                instance.setValue(output.attribute(attrIndex + i), dblVal);
            }
        }

        output.add(instance);
    }

    protected List<Object> getValues(MultiRatedEntity entity, EntityMetaData meta) {
        return new ArrayList<Object>();
    }

    protected DataSet postprocess(DataSet output) {
        return output;
    }

    protected void setClassAttribute(Instances outputFormat) {
        //no class attribute
    }

    protected List<EntitySet> preprocess(EntitySet entities) {
        ArrayList<EntitySet> entitySets = new ArrayList<EntitySet>();
        entitySets.add(entities);
        return entitySets;
    }
}
