/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cmd.config;

import data.EntitySet;
import data.MultiRatedEntity;
import data.io.ChatPrismDataSource;
import data.io.DataSource;
import data.processing.EntitySetFilter;
import data.processing.MinimumRatingsFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class DefaultDataConfig implements DataConfig {

    private String name;
    private boolean removeSystemMessages = false;

    public DefaultDataConfig() {
        this.name = "default";
    }

    public DefaultDataConfig(String name) {
        this.name = name;
    }

    @Override
    public DataSource getDataSource() {
        ChatPrismDataSource src = new ChatPrismDataSource(this.getName());
        src.setVerbose(false);
        src.setHost("localhost");
        src.setDatabaseSchema("chatdb");
        src.setUsername("root");
        src.setPassword("");
        return src;
    }
    int segmentationId = 0;

    public void setSegmentationId(int id) {
        this.segmentationId = id;
    }

    public boolean isRemoveSystemMessages() {
        return removeSystemMessages;
    }

    public void setRemoveSystemMessages(boolean removeSystemMessages) {
        this.removeSystemMessages = removeSystemMessages;
    }

    @Override
    public void configureDataSource(DataSource src) {
        src.setCodeSchemaId(2);
        src.setSegmentationId(segmentationId);
        
        src.setInstanceFilter("code_id NOT IN (117)"); //no frencch
        
        src.setTestSetDates(Arrays.asList("2006-06-04", "2006-11-10", "2007-05-23", "2005-07-04", "2005-03-25"));
        
//        src.setSegmentFilter("code_count > 1");
//        src.setCodeFilter("code_id != 113");
        if (this.isRemoveSystemMessages()) {
            src.setMessageFilter("participant_id NOT IN (1, 2) AND type = 0");
        }
//        src.setMessageFilter("participant_id = 7 AND type = 0");
    }

    @Override
    public String getDestination() {
        return "output";
    }

    @Override
    public List<EntitySetFilter> getEntitySetFilters() {
        List<EntitySetFilter> filters = new ArrayList<EntitySetFilter>();
        filters.add(new MinimumRatingsFilter(1));
        return filters;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
