/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cmd.config;

import data.processing.attrs.AttributeProducer;
import data.transforms.DataSetTransform;
import java.util.List;
import weka.filters.Filter;

/**
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public interface FeatureConfig {

    public List<AttributeProducer> getPrimaryAttributeProducers();

    public List<DataSetTransform> getPreSplitTransforms();

    public List<Filter> getFinalFilters();
    
    public String[] describeFeatures();
    
    public String[] describeFeaturesHeader();
    
    public String getName();
    
}
