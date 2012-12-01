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

import etc.aloe.RandomProvider;
import etc.aloe.data.Segment;
import etc.aloe.data.SegmentSet;
import etc.aloe.processes.Balancing;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Applies an upsampling algorithm to balance a data set.
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class UpsampleBalancing implements Balancing {

    private final double falsePositiveCost;
    private final double falseNegativeCost;

    public UpsampleBalancing() {
        this.falsePositiveCost = 1;
        this.falseNegativeCost = 1;
    }

    public UpsampleBalancing(double falsePositiveCost, double falseNegativeCost) {
        this.falsePositiveCost = falsePositiveCost;
        this.falseNegativeCost = falseNegativeCost;
    }

    @Override
    public SegmentSet balance(SegmentSet segmentSet) {
        SegmentSet balanced = new SegmentSet();

        List<Segment> allSegments = segmentSet.getSegments();
        List<Segment> resultSegments = new ArrayList<Segment>();

        List<Segment> positive = new ArrayList<Segment>();
        List<Segment> negative = new ArrayList<Segment>();
        List<Segment> unlabeled = new ArrayList<Segment>();

        for (Segment segment : allSegments) {
            if (!segment.hasTrueLabel()) {
                unlabeled.add(segment);
            } else if (segment.getTrueLabel() == true) {
                positive.add(segment);
            } else if (segment.getTrueLabel() == false) {
                negative.add(segment);
            }
        }

        if (!unlabeled.isEmpty()) {
            throw new IllegalArgumentException("Data set contains " + unlabeled.size() + " unlabeled examples.");
        }

        double currentPositiveNegativeRatio = (double) positive.size() / negative.size();
        double desiredPositiveNegativeRatio = (double) falseNegativeCost / falsePositiveCost;

        //Will we be upsampling positive or negative examples?
        if (currentPositiveNegativeRatio < desiredPositiveNegativeRatio) {
            //We are adding positive examples
            resultSegments.addAll(negative);

            int desiredPositiveExamples = computeFinalExamples(negative.size(), desiredPositiveNegativeRatio);
            sampleInto(resultSegments, positive, desiredPositiveExamples);
        } else if (currentPositiveNegativeRatio > desiredPositiveNegativeRatio) {
            //We are adding negative examples
            resultSegments.addAll(positive);

            int desiredNegativeExamples = computeFinalExamples(positive.size(), 1.0 / desiredPositiveNegativeRatio);
            sampleInto(resultSegments, negative, desiredNegativeExamples);
        } else {
            //We are not doing any downsampling
            resultSegments.addAll(positive);
            resultSegments.addAll(negative);
        }
        balanced.setSegments(resultSegments);

        System.out.println("Balanced (" + positive.size() + ", " + negative.size() + ") to (" + balanced.getCountWithTrueLabel(true) + ", " + balanced.getCountWithTrueLabel(false) + ")");

        return balanced;
    }

    /**
     * Include all of from, and then sample with replacement.
     *
     * @param target
     * @param from
     * @param number
     */
    private void sampleInto(List<Segment> target, List<Segment> from, int number) {
        ArrayList<Segment> selected = new ArrayList<Segment>(from);

        Random random = RandomProvider.getRandom();
        for (int i = selected.size(); i < number; i++) {
            int index = random.nextInt(from.size());
            Segment segment = from.get(index);
            selected.add(segment);
        }

        target.addAll(selected);
    }

    /**
     * Computes the number of a particular class that should be remaining, given
     * the number currently of the opposite class and the desired ratio of the
     * downsampled class to the opposite class.
     *
     * @param numOppositeClass
     * @param ratioDownsampledToOpposite
     * @return
     */
    private int computeFinalExamples(int numOppositeClass, double ratioDownsampledToOpposite) {
        return (int) Math.floor(numOppositeClass * ratioDownsampledToOpposite);
    }
}
