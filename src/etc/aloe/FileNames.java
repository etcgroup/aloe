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
package etc.aloe;

/**
 * Collection of input/output file name conventions shared by different controllers.
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public abstract class FileNames {

    public final static String DATA_SUFFIX = ".csv";
    public final static String EVALUATION_REPORT_SUFFIX = ".txt";
    public final static String MODEL_SUFFIX = ".model";
    public final static String FEATURE_SPEC_SUFFIX = ".spec";
    public final static String FEATURE_WEIGHTS_SUFFIX = ".csv";
    public final static String TOP_FEATURES_SUFFIX = ".txt";
    public final static String ROC_SUFFIX = ".csv";
    public final static String TEST_DATA_SUFFIX = ".csv";
    public final static String OUTPUT_CSV_NAME = "labeled" + DATA_SUFFIX;
    public final static String OUTPUT_EVALUTION_REPORT_NAME = "report" + EVALUATION_REPORT_SUFFIX;
    public final static String OUTPUT_FEATURE_SPEC_NAME = "features" + FEATURE_SPEC_SUFFIX;
    public final static String OUTPUT_MODEL_NAME = "model" + MODEL_SUFFIX;
    public final static String OUTPUT_TOP_FEATURES_NAME = "top_features" + TOP_FEATURES_SUFFIX;
    public final static String OUTPUT_FEATURE_WEIGHTS_NAME = "feature_weights" + FEATURE_WEIGHTS_SUFFIX;
    public final static String OUTPUT_TEST_DATA_COMBINED_NAME = "combined" + TEST_DATA_SUFFIX;
    public final static String OUTPUT_ROC_DIR_NAME = "rocs";
    public final static String OUTPUT_TESTS_DIR_NAME = "test_sets";
    public final static String OUTPUT_FEATURE_VALUES_NAME = "feature_values.csv";
    public final static String OUTPUT_COMMAND_FILE_NAME = "command.txt";
    
    
}
