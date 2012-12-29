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

import etc.aloe.options.ModeOptions;
import java.util.Arrays;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

/**
 * Main controller for ALOE. Does parsing of primary mode option and then
 * delegates to one of the mode controllers.
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 * @version 1.0 - using CSCW2013 implementations
 */
public class Main {

    private void printUsage() {
        System.err.println("For specific usage: java -jar aloe.jar PIPELINE_CLASS MODE");
        System.err.println("List of modes: ");
        for (ModeName cmd : ModeName.values()) {
            System.err.println("\t" + cmd.name());
        }
    }

    private void run(String[] args) {

        Aloe aloe = null;
        ModeOptions options = null;
        switch (mode) {
            case train:
                aloe = new AloeTrain();
                aloe.setPipeline(pipelineClassName);
                options = aloe.factory.constructTrainOptions();
                break;
            case label:
                aloe = new AloeLabel();
                aloe.setPipeline(pipelineClassName);
                options = aloe.factory.constructLabelOptions();
                break;
            case interactive:
                aloe = new AloeInteractive();
                aloe.setPipeline(pipelineClassName);
                options = aloe.factory.constructInteractiveOptions();
                break;
            case single:
                aloe = new AloeSingle();
                aloe.setPipeline(pipelineClassName);
                options = aloe.factory.constructSingleOptions();
                break;
        }

        CmdLineParser parser = new CmdLineParser(options);

        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            options.printUsage();
            parser.printUsage(System.err);
            return;
        }

        aloe.factory.setOptions(options);
        aloe.factory.initialize();
        aloe.run(options);
    }

    private static enum ModeName {

        train,
        label,
        interactive,
        single
    }
    @Argument(index = 1, usage = "mode", required = true, metaVar = "MODE")
    private ModeName mode;
    @Argument(index = 0, usage = "name of pipeline class (default 'CSCW2013')", required = true, metaVar = "PIPELINE_CLASS")
    private String pipelineClassName;

    public static void main(String[] args) {

        //Parse the command line arguments
        Main main = new Main();
        CmdLineParser parser = new CmdLineParser(main);


        if (args.length < 1) {
            System.err.println("PIPELINE_CLASS is required.");
            main.printUsage();
            return;
        }

        if (args.length < 2) {
            System.err.println("MODE is required.");
            main.printUsage();
            return;
        }

        //Separate the first argument from the rest of the arguments
        String[] restOfArgs = Arrays.copyOfRange(args, 2, args.length);
        String[] firstArgs = Arrays.copyOfRange(args, 0, 2);

        try {
            parser.parseArgument(firstArgs);
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            main.printUsage();
            return;
        }

        //And go!
        main.run(restOfArgs);
    }
}
