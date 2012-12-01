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
package etc.aloe.processes;

/**
 * Indicates a class that has configurable verbosity levels.
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public interface Loggable {

    public static enum Verbosity {

        Quiet,
        Normal
    }

    /**
     * Set the verbosity level for output produced inside the class.
     *
     * @param verbosityLevel
     */
    void setVerbosity(Verbosity verbosityLevel);
}
