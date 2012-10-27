/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cmd.config;

import data.EntitySet;
import data.analysis.Analysis;
import data.processing.attrs.BinaryCodeClassProducer.CodePresenceStrategy;
import java.util.List;

/**
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public interface AnalysisConfig {
    
    public List<Integer> getCodeIds();
    
    public List<Analysis<EntitySet>> getPreFilterAnalyses();
    
    public List<Analysis<EntitySet>> getPostFilterAnalyses();
    
    public CodePresenceStrategy getCodePresenceStrategy();

    public boolean isReportHumanReadable();
}
