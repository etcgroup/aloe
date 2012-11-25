/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package etc.aloe.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

/**
 *
 * @author michael
 */
public class SegmentSet {

    private List<Segment> segments = new ArrayList<Segment>();

    /**
     * Add a segment to the set.
     *
     * @param segment
     */
    public void add(Segment segment) {
        this.segments.add(segment);
    }

    /**
     * Get the size of the segment set.
     *
     * @return
     */
    public int size() {
        return this.segments.size();
    }

    public List<Segment> getSegments() {
        return segments;
    }

    public void setSegments(List<Segment> segments) {
        this.segments = segments;
    }

    public ExampleSet getBasicExamples() {
        ArrayList<Attribute> attributes = new ArrayList<Attribute>();

        attributes.add(new Attribute(ExampleSet.ID_ATTR_NAME));
        attributes.add(new Attribute(ExampleSet.MESSAGE_ATTR_NAME, (List<String>) null));
        attributes.add(new Attribute(ExampleSet.LABEL_ATTR_NAME, Arrays.asList(new String[]{"false", "true"})));

        Instances instances = new Instances("BasicExamples", attributes, 0);
        instances.setClassIndex(2);

        Attribute idAttr = instances.attribute(ExampleSet.ID_ATTR_NAME);
        Attribute messageAttr = instances.attribute(ExampleSet.MESSAGE_ATTR_NAME);
        Attribute labelAttr = instances.attribute(ExampleSet.LABEL_ATTR_NAME);

        for (int i = 0; i < size(); i++) {
            Segment segment = get(i);
            Instance instance = new DenseInstance(3);

            instance.setValue(idAttr, segment.getId());
            instance.setValue(messageAttr, segment.concatMessages());
            if (segment.hasTrueLabel()) {
                instance.setValue(labelAttr, segment.getTrueLabel() ? "true" : "false");
            }

            instances.add(instance);
        }

        return new ExampleSet(instances);
    }

    public Segment get(int i) {
        return this.segments.get(i);
    }
}
