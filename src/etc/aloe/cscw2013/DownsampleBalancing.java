/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package etc.aloe.cscw2013;

import etc.aloe.RandomProvider;
import etc.aloe.data.Segment;
import etc.aloe.data.SegmentSet;
import etc.aloe.processes.Balancing;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

/**
 *
 * @author michael
 */
public class DownsampleBalancing implements Balancing {
    private final double falsePositiveCost;
    private final double falseNegativeCost;

    public DownsampleBalancing() {
        this.falsePositiveCost = 1;
        this.falseNegativeCost = 1;
    }

    public DownsampleBalancing(double falsePositiveCost, double falseNegativeCost) {
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

        if (positive.size() < negative.size()) {
            resultSegments.addAll(positive);
            sampleInto(resultSegments, negative, positive.size());
            balanced.setSegments(resultSegments);
        } else if (negative.size() < positive.size()) {
            resultSegments.addAll(negative);
            sampleInto(resultSegments, negative, positive.size());
            balanced.setSegments(resultSegments);
        } else {
            resultSegments.addAll(negative);
            resultSegments.addAll(positive);
            balanced.setSegments(resultSegments);
        }

        return balanced;
    }

    private void sampleInto(List<Segment> target, List<Segment> from, int number) {
        HashSet<Segment> selected = new HashSet<Segment>();

        Random random = RandomProvider.getRandom();
        for (int i = 0; i < number; i++) {
            boolean added = false;
            while (!added) {
                int index = random.nextInt(from.size());
                Segment segment = from.get(index);
                if (!selected.contains(segment)) {
                    selected.add(segment);
                    added = true;
                }
            }
        }

        target.addAll(selected);
    }
}
