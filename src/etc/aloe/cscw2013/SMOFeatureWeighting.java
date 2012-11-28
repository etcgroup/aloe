/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package etc.aloe.cscw2013;

import etc.aloe.processes.FeatureWeighting;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import weka.classifiers.Classifier;
import weka.classifiers.functions.SMO;
import weka.classifiers.meta.CostSensitiveClassifier;
import weka.core.Instances;

/**
 *
 * @author michael
 */
public class SMOFeatureWeighting implements FeatureWeighting {

    @Override
    public List<String> getTopFeatures(Instances dataFormat, Classifier classifier, int topN) {
        List<Map.Entry<String, Double>> weights = getFeatureWeights(dataFormat, classifier);

        Collections.sort(weights, new Comparator<Map.Entry<String, Double>>() {
            @Override
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                return -Double.compare(o1.getValue() * o1.getValue(), o2.getValue() * o2.getValue());
            }
        });

        List<String> result = new ArrayList<String>();
        for (int i = 0; i < topN && i < weights.size(); i++) {
            Map.Entry<String, Double> entry = weights.get(i);

            result.add(entry.getKey());
        }

        return result;
    }

    @Override
    public List<Map.Entry<String, Double>> getFeatureWeights(Instances dataFormat, Classifier classifier) {
        SMO smo = getSMO(classifier);

        double[] sparseWeights = smo.sparseWeights()[0][1];
        int[] sparseIndices = smo.sparseIndices()[0][1];

        Map<String, Double> weights = new HashMap<String, Double>();
        for (int i = 0; i < sparseWeights.length; i++) {
            int index = sparseIndices[i];
            double weight = sparseWeights[i];
            String name = dataFormat.attribute(index).name();
            weights.put(name, weight);
        }

        List<Map.Entry<String, Double>> entries = new ArrayList<Map.Entry<String, Double>>(weights.entrySet());

        Collections.sort(entries, new Comparator<Map.Entry<String, Double>>() {
            @Override
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                return o1.getKey().compareTo(o2.getKey());
            }
        });

        return entries;
    }

    private SMO getSMO(Classifier classifier) {
        if (classifier instanceof CostSensitiveClassifier) {
            classifier = ((CostSensitiveClassifier) classifier).getClassifier();
        }

        SMO smo = null;
        if (classifier instanceof SMO) {
            smo = (SMO) classifier;
        } else {
            throw new IllegalArgumentException("Classifier was neither SMO or CostSensitiveClassifier(SMO)");
        }

        return smo;
    }
}
