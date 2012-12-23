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

import etc.aloe.controllers.InteractiveController;
import etc.aloe.cscw2013.FeatureExtractionImpl;
import etc.aloe.cscw2013.LabelMappingImpl;
import etc.aloe.data.FeatureSpecification;
import etc.aloe.data.MessageSet;
import etc.aloe.cscw2013.WekaModel;
import java.io.File;
import java.text.SimpleDateFormat;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

/**
 * Controller for interactive mode.
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class AloeInteractive extends Aloe {

    @Argument(index = 0, usage = "output directory (contents may be overwritten)", required = true, metaVar = "OUTPUT_DIR")
    private void setOutputDir(File dir) {
        this.outputDir = dir;
        dir.mkdir();

        outputCSVFile = new File(dir, FileNames.OUTPUT_CSV_NAME);
    }
    private File outputDir;
    private File outputCSVFile;
    @Option(name = "--model", aliases = {"-m"}, usage = "use an existing model file", required = true, metaVar="MODEL_FILE")
    private File inputModelFile;
    @Option(name = "--features", aliases = {"-f"}, usage = "use an existing feature specification file", required = true, metaVar="FEATURES_FILE")
    private File inputFeatureSpecFile;

    @Override
    public void printUsage() {
        System.err.println("java -jar aloe.jar interactive OUTPUT_DIR -m MODEL_FILE -f FEATURES_FILE [options...]");
    }

    @Override
    public void run() {
        System.out.println("== Preparation ==");

        InteractiveController interactiveController = new InteractiveController();

        //Provide implementations for the controller
        interactiveController.setFeatureExtractionImpl(new FeatureExtractionImpl());
        interactiveController.setMappingImpl(new LabelMappingImpl());

        FeatureSpecification spec = this.loadFeatureSpecification(inputFeatureSpecFile);
        WekaModel model = this.loadModel(inputModelFile);

        interactiveController.setModel(model);
        interactiveController.setFeatureSpecification(spec);
        interactiveController.run();

        System.out.println();
        System.out.println("== Saving Output ==");

        MessageSet messages = interactiveController.getMessageSet();
        messages.setDateFormat(new SimpleDateFormat(dateFormatString));
        saveMessages(messages, outputCSVFile);
    }
}
