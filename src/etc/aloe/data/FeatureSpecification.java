/*
 * This file is part of ALOE.
 *
 * ALOE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * ALOE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with ALOE.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2012 SCCL, University of Washington (http://depts.washington.edu/sccl)
 */
package etc.aloe.data;

import etc.aloe.processes.Loading;
import etc.aloe.processes.Saving;
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
 * A FeatureSpecification contains information sufficient to know how to extract
 * features for any single data point.
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class FeatureSpecification implements Loading, Saving {

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

    /**
     * Add a filter to the feature spec.
     *
     * @param filter
     */
    public void addFilter(Filter filter) {
        this.filters.add(filter);
    }

    /**
     * Get the list of filters in this spec.
     *
     * @return
     */
    public List<Filter> getFilters() {
        return filters;
    }
}
