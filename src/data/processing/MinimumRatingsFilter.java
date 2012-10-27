/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data.processing;

import data.EntitySet;
import data.MultiRatedEntity;

/**
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class MinimumRatingsFilter extends EntitySetFilter {

    private int minimumRatings = 1;

    public MinimumRatingsFilter() {
    }

    public MinimumRatingsFilter(int minRatings) {
        this.minimumRatings = minRatings;
    }

    public int getMinimumRatings() {
        return minimumRatings;
    }

    public void setMinimumRatings(int minimumRatings) {
        this.minimumRatings = minimumRatings;
    }

    @Override
    public String getName() {
        return "min" + minimumRatings;
    }

    @Override
    protected MultiRatedEntity filterEntity(MultiRatedEntity entity, EntitySet entitySet) {
        if (entity.countRatings() >= minimumRatings) {
            return entity;
        } else {
            return null;
        }
    }
}
