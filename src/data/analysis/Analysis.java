/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data.analysis;

/**
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public interface Analysis<T> {
    
    public AnalysisResult analyze(T dataSet);

    public String getName();
}
