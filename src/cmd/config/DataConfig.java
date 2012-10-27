/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cmd.config;

import data.io.DataSource;
import data.processing.EntitySetFilter;
import java.util.List;

/**
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public interface DataConfig {

    public void configureDataSource(DataSource source);
    
    public DataSource getDataSource();
    
    public String getDestination();
    
    public List<EntitySetFilter> getEntitySetFilters();
    
    public String getName();
}
