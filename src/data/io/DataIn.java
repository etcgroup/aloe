package data.io;

import data.Expression;
import data.ExpressionSet;
import java.util.Iterator;

/**
 *
 * @author kuksenok
 */
abstract class DataIn implements Iterator<Expression>{

    protected ExpressionSet all;
    /**
     * Get all rows as a collection.
     * @return
     */
    public abstract ExpressionSet all();
}
