/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data.processing;

import data.EntitySet;
import data.MultiRatedEntity;
import data.Rating;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class Facet {

    private Set<Integer> codeIds = new HashSet<Integer>();
    private String name = "all";
    
    public Facet() {
    }

    public Facet(String name, int codeId) {
        this.name = name;
        codeIds.add(codeId);
    }

    public Facet(String name, Set<Integer> codeIds) {
        this.name = name;
        this.codeIds.addAll(codeIds);
    }

    public Set<Integer> getCodeIds() {
        return codeIds;
    }
    
    public void addCodeId(int codeId) {
        codeIds.add(codeId);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Facet by a set of codes.
     *
     * @param entities
     * @return
     */
    public EntitySet facetEntitySet(EntitySet entities) {
        EntitySet result = new EntitySet(entities.getName() + "." + getName());

        for (MultiRatedEntity entity : entities) {
            MultiRatedEntity facetedEntity = facetEntity(entity, this.getCodeIds());
            result.add(facetedEntity);
        }

        return result;
    }

    private MultiRatedEntity facetEntity(MultiRatedEntity entity, Set<Integer> codeIds) {
        MultiRatedEntity result = new MultiRatedEntity(entity);
        for (Rating rating : entity.getRatings()) {
            if (codeIds.contains(rating.getCodeId())) {
                result.addRating(rating);
            }
        }
        return result;
    }
}
