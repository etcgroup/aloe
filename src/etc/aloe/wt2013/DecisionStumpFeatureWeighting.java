/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package etc.aloe.wt2013;

import etc.aloe.data.ExampleSet;
import etc.aloe.data.Model;
import etc.aloe.processes.FeatureWeighting;
import java.util.List;
import java.util.Map.Entry;
import java.util.*;

/**
 *
 * @author erose
 */
public class DecisionStumpFeatureWeighting implements FeatureWeighting{

    @Override
    public List<String> getTopFeatures(ExampleSet trainingExamples, Model model, int topN) {
        return new ArrayList<String>();
    }

    @Override
    public List<Entry<String, Double>> getFeatureWeights(ExampleSet trainingExamples, Model model) {
        return new ArrayList<Entry<String, Double>>();
    }
    
}
