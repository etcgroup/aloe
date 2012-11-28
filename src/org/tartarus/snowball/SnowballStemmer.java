
package org.tartarus.snowball;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

public abstract class SnowballStemmer extends SnowballProgram implements Serializable {
    public abstract boolean stem();
};
