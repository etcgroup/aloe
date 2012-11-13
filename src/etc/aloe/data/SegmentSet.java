/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package etc.aloe.data;

import java.util.ArrayList;
import java.util.List;
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
}
