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
public abstract class EntitySetFilter {
    public EntitySet filter(EntitySet input) {
        EntitySet output = constructOutputSet(input);
        
        for (MultiRatedEntity entity : input) {
            entity = filterEntity(entity, input);
            if (entity != null) {
                output.add(entity);
            }
        }
        
        return output;
    }

    protected EntitySet constructOutputSet(EntitySet input) {
        EntitySet output = new EntitySet(input.getName() + "." + getName());
        return output;
    }

    public String getName() {
        return "all";
    }

    protected abstract MultiRatedEntity filterEntity(MultiRatedEntity entity, EntitySet entitySet);
}
