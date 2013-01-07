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

import etc.aloe.FileNames;
import java.io.File;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

/**
 * Defines options required for all interactive-mode runs, independent of
 * pipeline implementation.
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class InteractiveOptions extends ModeOptions {

    @Argument(index = 0, usage = "output directory (contents may be overwritten)", required = true, metaVar = "OUTPUT_DIR")
    private void setOutputDir(File dir) {
        this.outputDir = dir;
        dir.mkdirs();

        outputCSVFile = new File(dir, FileNames.OUTPUT_CSV_NAME);
    }
    public File outputDir;
    public File outputCSVFile;
    @Option(name = "--model", aliases = {"-m"}, usage = "use an existing model file", required = true, metaVar = "MODEL_FILE")
    public File inputModelFile;
    @Option(name = "--features", aliases = {"-f"}, usage = "use an existing feature specification file", required = true, metaVar = "FEATURES_FILE")
    public File inputFeatureSpecFile;

    @Override
    public void printUsage() {
        System.err.println("java -jar aloe.jar PIPELINE_CLASS interactive OUTPUT_DIR -m MODEL_FILE -f FEATURES_FILE [options...]");
    }
}
