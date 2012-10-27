/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cmd;

import cmd.config.DataConfig;
import data.EntitySet;
import data.io.DataSource;
import data.processing.EntitySetFilter;
import java.util.List;

/**
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class DataPreparer {
    private final DataConfig dataConfig;

    public DataPreparer(DataConfig dataConfig) {
        this.dataConfig = dataConfig;
    }
    
    public EntitySet getData() {
        DataSource source = dataConfig.getDataSource();
        dataConfig.configureDataSource(source);
        
        try {
            source.initialize();
        } catch (Exception e) {
            System.err.println("Error initializing data source.");
            e.printStackTrace();
            System.exit(1);
        }
        
        source.loadIndexes();
        
        EntitySet entities = source.getData();
        
        List<EntitySetFilter> entitySetFilters = dataConfig.getEntitySetFilters();
        for (EntitySetFilter filter : entitySetFilters) {
            entities = filter.filter(entities);
            System.out.println("Filtered to " + entities.size() + " entities.");
        }
        
        return entities;
    }
    
    
}
