/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package etc.aloe.wt2013;

import etc.aloe.data.ExampleSet;
import etc.aloe.data.Model;
import etc.aloe.processes.FeatureWeighting;
import java.util.*;
import java.util.Map.Entry;

/**
 *
 * @author Eli Rose <erose@oberlin.edu>
 */
public class HMMFeatureWeighting implements FeatureWeighting{

    @Override
    public List<String> getTopFeatures(ExampleSet trainingExamples, Model model, int topN) {
        return new ArrayList<String>();
    }

    @Override
    public List<Entry<String, Double>> getFeatureWeights(ExampleSet trainingExamples, Model model) {
        return new ArrayList<Entry<String, Double>>();
    }
    
}
