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
package etc.aloe.options;

import etc.aloe.RandomProvider;
import java.util.Random;
import org.kohsuke.args4j.Option;

/**
 * Defines options shared among all modes.
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public abstract class ModeOptions {

    @Option(name = "--dateformat", aliases = {"-d"}, usage = "date format string (default 'yyyy-MM-dd HH:mm:ss')", metaVar = "DATE_FORMAT")
    public String dateFormatString = "yyyy-MM-dd HH:mm:ss";

    @Option(name = "--random", aliases = {"-r"}, usage = "random seed")
    void setRandomSeed(int randomSeed) {
        RandomProvider.setRandom(new Random(randomSeed));
    }
    
    public abstract void printUsage();
}
