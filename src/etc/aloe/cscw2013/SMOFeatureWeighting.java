/*
 * This file is part of ALOE.
 *
 * ALOE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * ALOE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with ALOE.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2012 SCCL, University of Washington (http://depts.washington.edu/sccl)
 */
package etc.aloe.cscw2013;

import etc.aloe.processes.FeatureWeighting;
import java.util.ArrayList;
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
 * Extracts top features and feature weights from a linear support vector
 * machine (SMO) classifier.
 *
 * Also works with a CostSensitiveClassifier wrapping an SMO.
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
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

    /**
     * Given a classifier, attempts to cast it to an SMO or get the contained
     * SMO.
     *
     * @param classifier
     * @return
     */
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
