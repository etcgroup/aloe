/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package etc.aloe.processes;

import java.util.List;
import java.util.Map;
import weka.classifiers.Classifier;
import weka.core.Instances;

/**
 *
 * @author michael
 */
public interface FeatureWeighting {

    List<String> getTopFeatures(Instances dataFormat, Classifier classifier, int topN);

    List<Map.Entry<String, Double>> getFeatureWeights(Instances dataFormat, Classifier classifier);

}
