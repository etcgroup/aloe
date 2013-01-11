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
import java.io.OutputStream;

/**
 * A Model has the ability to learn from examples and label unlabeled examples.
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public interface Model extends Loading, Saving {

    /**
     * Attempt to label each example in the example set according to the model.
     *
     * Returns the list of generated labels and confidences. Order corresponds to the order of
     * examples.
     *
     * @param examples
     * @return
     */
    Predictions getPredictions(ExampleSet examples);

    boolean load(InputStream source) throws InvalidObjectException;

    boolean save(OutputStream destination) throws IOException;

}
