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
import weka.core.Instance;
import weka.core.Instances;

/**
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class BasicFeaturesProducer extends AbstractAttributeProducer {

    @Override
    public List<Attribute> getAttributes() {
        List<Attribute> attrs = new ArrayList<Attribute>();
        
        attrs.add(new Attribute("message", (List<String>)null));
        attrs.add(new Attribute("duration"));
        attrs.add(new Attribute("length"));
        attrs.add(new Attribute("cps"));
        attrs.add(new Attribute("rate"));
        
        return attrs;
    }

    @Override
    public void putValues(MultiRatedEntity entity, EntityMetaData meta, List<Object> values) {
        //Build the combined string
        String combinedString = meta.concatMessages();
        double duration = meta.getDurationInSeconds();
        double length = meta.size();
        
        //If the length is 1, then we correct the duration.
        //Assume average typing speed (35 words per minute, 5 char/word)
        if (length <= 1) {
            double averageCharPerSecond = 35.0 * 5.0 / 60.0;
            //[seconds] = [chars] / ([chars]/[seconds])
            duration = combinedString.length() / averageCharPerSecond;
        }
        
        if (duration > 100000) {
            System.err.println("id: " + entity.getEntityId() + " duration: " + duration);
        }
        
        double cps = combinedString.length() / duration;
        double rate = meta.size() / duration;
        
        values.add(combinedString);
        values.add(duration);
        values.add(length);
        values.add(cps);
        values.add(rate);
    }    
    
}
