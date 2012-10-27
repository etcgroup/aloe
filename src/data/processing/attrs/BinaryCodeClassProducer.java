/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data.processing.attrs;

import data.EntityMetaData;
import data.MultiRatedEntity;
import java.util.ArrayList;
import java.util.List;
import weka.core.Attribute;
import weka.core.Instances;

/**
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class BinaryCodeClassProducer extends AbstractAttributeProducer {
    private String codeName;
    private int codeId;

    public enum CodePresenceStrategy {
        /**
         * A code is considered to apply to the entity if any rater applied it.
         */
        Any,
        /**
         * A code is considered to apply to the entity if the majority of raters applied it.
         */
        Majority,
        /**
         * A code is considered to apply to the entity only if all raters applied it.
         */
        All
    }
    
    private CodePresenceStrategy codePresenceStrategy;

    public BinaryCodeClassProducer() {
        
    }
    
    public BinaryCodeClassProducer(CodePresenceStrategy codePresenceStrategy, int codeId, String codeName) {
        this.codeId = codeId;
        this.codeName = codeName;
        this.codePresenceStrategy = codePresenceStrategy;
    }

    public CodePresenceStrategy getCodePresenceStrategy() {
        return codePresenceStrategy;
    }

    public void setCodePresenceStrategy(CodePresenceStrategy codePresenceStrategy) {
        this.codePresenceStrategy = codePresenceStrategy;
    }

    public int getCodeId() {
        return codeId;
    }

    public void setCodeId(int codeId) {
        this.codeId = codeId;
    }

    public String getCodeName() {
        return codeName;
    }
    
    public void setCodeName(String codeName) {
        this.codeName = codeName;
    }
    
    
    @Override
    public List<Attribute> getAttributes() {
        List<Attribute> attrs = new ArrayList<Attribute>();
        
        ArrayList<String> values = new ArrayList<String>();
        values.add("false");
        values.add("true");
        attrs.add(new Attribute(getCodeName(), values));
        
        return attrs;
    }

    @Override
    public boolean isClassProvider() {
        return true;
    }

    @Override
    public void setClassAttribute(Instances format) {
        format.setClass(format.attribute(getCodeName()));
    }

    @Override
    public void putValues(MultiRatedEntity entity, EntityMetaData meta, List<Object> values) {
        boolean hasCode = false;
        
        switch (getCodePresenceStrategy()) {
            case Any:
                hasCode = anyAppliedCode(entity, codeId);
                break;
            case Majority:
                hasCode = majorityAppliedCode(entity, codeId);
                break;
            case All:
                hasCode = allAppliedCode(entity, codeId);
                break;
        }
        
        if (hasCode) {
            values.add("true");
        } else {
            values.add("false");
        }
    }
    
    private boolean anyAppliedCode(MultiRatedEntity entity, int codeId) {
        int numApplied = entity.countUsersByCode(codeId);
        
        return numApplied > 0;
    }
    
    private boolean majorityAppliedCode(MultiRatedEntity entity, int codeId) {
        int numApplied = entity.countUsersByCode(codeId);
        int totalRaters = entity.countDistinctUsers();
        return numApplied > 0 && numApplied > totalRaters / 2;
    }
    
    private boolean allAppliedCode(MultiRatedEntity entity, int codeId) {
        int numApplied = entity.countUsersByCode(codeId);
        int totalRaters = entity.countDistinctUsers();
        return numApplied > 0 && numApplied == totalRaters;
    }
    
}
