package data.io;

import data.ExpressionSet;

/**
 *
 * @author kuksenok
 */
public abstract class PagedDataIn extends DataIn{

    @Override
    public final ExpressionSet all() {
        ExpressionSet es = new ExpressionSet();
        while(hasNext()){
            es.add(next());
        }
        return es;
    }

}
