/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data.analysis;

import daisy.io.CSV;

/**
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public interface AnalysisResult {
    public String getAsString(boolean humanFriendly);
    public void writeToCSV(boolean humanFriendly, CSV csv);
    public String getExplanation();
}
