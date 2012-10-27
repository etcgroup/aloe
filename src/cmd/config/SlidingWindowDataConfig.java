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
public class SlidingWindowDataConfig extends DefaultDataConfig {

    public SlidingWindowDataConfig(String name) {
        super(name);
    }

    public SlidingWindowDataConfig() {
    }

    private int maxWindowSize = 10;
    private double maxWindowDuration = 300; //5 minutes in seconds
    private boolean prescient = false;
    private int ratingsBasis = 1;
    
    public boolean isPrescient() {
        return prescient;
    }

    public void setPrescient(boolean prescient) {
        this.prescient = prescient;
    }

    public int getRatingsBasis() {
        return ratingsBasis;
    }

    public void setRatingsBasis(int ratingsBasis) {
        this.ratingsBasis = ratingsBasis;
    }
    
    
    
    
    @Override
    public DataSource getDataSource() {
        SlidingWindowDataSource src = new SlidingWindowDataSource(this.getName());
        src.setVerbose(false);
        src.setHost("localhost");
        src.setDatabaseSchema("chatdb");
        src.setUsername("root");
        src.setPassword("");
        src.setMaxWindowSize(getMaxWindowSize());
        src.setMaxWindowDuration(getMaxWindowDuration());
        src.setPrescient(isPrescient());
        src.setRatingsBasis(getRatingsBasis());
        return src;
    }

    public double getMaxWindowDuration() {
        return maxWindowDuration;
    }

    public void setMaxWindowDuration(double seconds) {
        this.maxWindowDuration = seconds;
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
