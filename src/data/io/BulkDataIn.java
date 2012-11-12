package data.io;

import data.Expression;
import data.ExpressionSet;
import java.util.Iterator;

/**
 *
 * @author kuksenok
 */
public abstract class BulkDataIn extends DataIn {

    private ExpressionSet es;
    Iterator<Expression> it;

    private void init() {
        if (es == null || it == null) {
            es = all();
            it = es.iterator();
        }
    }

    @Override
    public boolean hasNext() {
        init();
        return it.hasNext();
    }

    @Override
    public Expression next() {
        init();
        return it.next();
    }

    @Override
    public void remove() {
        init();
        it.remove();
    }
}
