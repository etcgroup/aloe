package etc.aloe.cscw2013;

import etc.aloe.data.FeatureSpecification;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import weka.filters.Filter;

/**
 * Implementation of a feature specification. Contains data about bag of words
 * features, emoticon features, and other configuration.
 */
public class FeatureSpecificationImpl extends FeatureSpecification {

    private List<Filter> filters = new ArrayList<Filter>();

    @Override
    public boolean load(InputStream source) throws InvalidObjectException {
        try {
            ObjectInputStream in = new ObjectInputStream(source);
            filters = (List<Filter>) in.readObject();
            return true;
        } catch (ClassNotFoundException e) {
            throw new InvalidObjectException(e.getMessage());
        } catch (IOException e) {
            throw new InvalidObjectException(e.getMessage());
        }
    }

    @Override
    public boolean save(OutputStream destination) throws IOException {
        ObjectOutputStream out = new ObjectOutputStream(destination);
        out.writeObject(filters);
        return true;
    }

    void addFilter(Filter filter) {
        this.filters.add(filter);
    }

    List<Filter> getFilters() {
        return filters;
    }
}
