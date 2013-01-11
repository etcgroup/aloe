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
 * Defines options for all training runs, regardless of pipeline implementation.
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class TrainOptions extends ModeOptions {

    @Argument(index = 0, usage = "input CSV file containing messages", required = true, metaVar = "INPUT_CSV")
    public File inputCSVFile;

    @Argument(index = 1, usage = "output directory (contents may be overwritten)", required = true, metaVar = "OUTPUT_DIR")
    private void setOutputDir(File dir) {
        this.outputDir = dir;
        dir.mkdirs();

        outputROCDir = new File(dir, FileNames.OUTPUT_ROC_DIR_NAME);
        outputEvaluationReportFile = new File(dir, FileNames.OUTPUT_EVALUTION_REPORT_NAME);
        outputFeatureSpecFile = new File(dir, FileNames.OUTPUT_FEATURE_SPEC_NAME);
        outputModelFile = new File(dir, FileNames.OUTPUT_MODEL_NAME);
        outputTopFeaturesFile = new File(dir, FileNames.OUTPUT_TOP_FEATURES_NAME);
        outputFeatureWeightsFile = new File(dir, FileNames.OUTPUT_FEATURE_WEIGHTS_NAME);
    }
    public File outputDir;
    public File outputROCDir;
    public File outputEvaluationReportFile;
    public File outputFeatureSpecFile;
    public File outputModelFile;
    public File outputTopFeaturesFile;
    public File outputFeatureWeightsFile;

    @Option(name="--roc", usage="Export data for ROC curves")
    public boolean makeROC;

    @Override
    public void printUsage() {
        System.err.println("java -jar aloe.jar PIPELINE_CLASS train INPUT_CSV OUTPUT_DIR [options...]");
    }
}
