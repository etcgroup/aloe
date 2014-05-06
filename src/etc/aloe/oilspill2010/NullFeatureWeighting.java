/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package etc.aloe.oilspill2010;

import etc.aloe.data.ExampleSet;
import etc.aloe.data.Model;
import etc.aloe.processes.FeatureWeighting;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author mjbrooks
 */
public class NullFeatureWeighting implements FeatureWeighting {

    @Override
    public List<String> getTopFeatures(ExampleSet trainingExamples, Model model, int topN) {
        return new ArrayList<String>();
    }

    @Override
    public List<Map.Entry<String, Double>> getFeatureWeights(ExampleSet trainingExamples, Model model) {
        return new ArrayList<Map.Entry<String, Double>>();
    }
    
}
