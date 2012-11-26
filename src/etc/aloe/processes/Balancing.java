/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package etc.aloe.processes;

import etc.aloe.data.SegmentSet;

/**
 *
 * @author michael
 */
public interface Balancing {

    /**
     * Return a balanced segment set.
     * @param segments
     * @return
     */
    SegmentSet balance(SegmentSet segments);
}
