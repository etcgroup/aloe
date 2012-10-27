/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cmd.config;

import data.io.ChatPrismDataSource;
import data.io.DataSource;
import data.io.SlidingWindowDataSource;
import java.util.Arrays;

/**
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class WindowDataConfig extends DefaultDataConfig {

    public WindowDataConfig(String name) {
        super(name);
    }

    public WindowDataConfig() {
    }
    private int maxWindowSize = 2;

    @Override
    public DataSource getDataSource() {
        SlidingWindowDataSource src = new SlidingWindowDataSource(this.getName());
        src.setVerbose(false);
        src.setHost("localhost");
        src.setDatabaseSchema("chatdb");
        src.setUsername("root");
        src.setPassword("");
        src.setMaxWindowSize(getMaxWindowSize());
        return src;
    }

    public int getMaxWindowSize() {
        return maxWindowSize;
    }

    public void setMaxWindowSize(int maxWindowSize) {
        this.maxWindowSize = maxWindowSize;
    }

    @Override
    public void configureDataSource(DataSource src) {
        src.setCodeSchemaId(2);

        src.setInstanceFilter("code_id NOT IN (117)"); //no frencch
        src.setTestSetDates(Arrays.asList("2006-06-04", "2006-11-10", "2007-05-23", "2005-07-04", "2005-03-25"));
        if (this.isRemoveSystemMessages()) {
            src.setMessageFilter("participant_id NOT IN (1, 2) AND type = 0");
        }
    }
}
