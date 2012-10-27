/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data.processing;

import data.DataSet;
import data.EntityMetaData;
import data.MultiRatedEntity;
import data.processing.attrs.AttributeProducer;
import data.transforms.DataSetTransform;
import java.util.ArrayList;
import java.util.List;
import weka.core.Attribute;
import weka.core.Instances;

/**
 * Converts an entity set to instances and applies a set of filters to generate
 * features.
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class EntityConverter extends BaseEntitiesToDataSet {
    
    private final List<DataSetTransform> postprocessing = new ArrayList<DataSetTransform>();
    private final List<AttributeProducer> producers = new ArrayList<AttributeProducer>();

    public EntityConverter(String conversionName) {
        super(conversionName);
    }
    
    public void pushTransform(DataSetTransform transform) {
        postprocessing.add(transform);
    }

    public void pushProducer(AttributeProducer producer) {
        producers.add(producer);
    }

    @Override
    protected List<Attribute> getAttributes() {
        List<Attribute> attrs = super.getAttributes();
        for (AttributeProducer producer : producers) {
            attrs.addAll(producer.getAttributes());
        }
        return attrs;
    }

    @Override
    protected void setClassAttribute(Instances outputFormat) {
        boolean classAttrSet = false;
        for (AttributeProducer producer : producers) {
            if (producer.isClassProvider()) {
                if (classAttrSet) {
                    throw new IllegalStateException("Multiple class providers!");
                }

                producer.setClassAttribute(outputFormat);
                classAttrSet = true;
            }
        }
    }

    @Override
    protected List<Object> getValues(MultiRatedEntity entity, EntityMetaData meta) {
        List<Object> values = super.getValues(entity, meta);
        
        for (AttributeProducer producer : producers) {
            producer.putValues(entity, meta, values);
        }
        
        return values;
    }

    @Override
    protected DataSet postprocess(DataSet dataSet) {
        dataSet = super.postprocess(dataSet);

        //Apply the postprocessing transforms
        for (int i = 0; i < postprocessing.size(); i++) {
            DataSetTransform transform = postprocessing.get(i);
            try {
                dataSet = transform.transform(dataSet);
            } catch (Exception ex) {
                System.err.println("Error applying transform " + i);
                ex.printStackTrace();
                System.exit(1);
            }
        }

        return dataSet;
    }
}
