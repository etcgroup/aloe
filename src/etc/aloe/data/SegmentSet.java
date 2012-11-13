/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package etc.aloe.data;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author michael
 */
public class SegmentSet {

    private List<Segment> segments = new ArrayList<Segment>();

    public SegmentSet getTrainingForFold(int foldIndex) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public SegmentSet getTestingForFold(int foldIndex) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void prepareForCrossValidation(int folds) {
        throw new UnsupportedOperationException("Not yet implemented");
    }


}
