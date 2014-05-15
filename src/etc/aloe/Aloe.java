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

import com.csvreader.CsvWriter;
import etc.aloe.data.EvaluationReport;
import etc.aloe.data.FeatureSpecification;
import etc.aloe.data.MessageSet;
import etc.aloe.data.Model;
import etc.aloe.data.ROC;
import etc.aloe.factories.PipelineFactory;
import etc.aloe.options.ModeOptions;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidObjectException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import weka.core.Instances;
import weka.core.converters.CSVSaver;

/**
 * Main Aloe controller superclass. Provides many useful IO methods used by the
 * subcontrollers.
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public abstract class Aloe {
    
    private String[] cmdLineArgs = new String[0];

    public void setCmdLineArgs(String[] cmdLineArgs) {
        this.cmdLineArgs = cmdLineArgs;
    }
    
    protected PipelineFactory factory = null;

    protected void setPipeline(String className) {
        className = "etc.aloe.factories." + className;
        try {
            this.factory = (PipelineFactory) Class.forName(className).newInstance();
        } catch (InstantiationException ex) {
            System.err.println("Error instantiating pipeline class " + className);
            System.err.println(ex.getMessage());
            System.exit(1);
        } catch (IllegalAccessException ex) {
            System.err.println("Cannot access constructor for class " + className);
            System.err.println(ex.getMessage());
            System.exit(1);
        } catch (ClassNotFoundException ex) {
            System.err.println("Cannot find pipeline class " + className);
            System.err.println(ex.getMessage());
            System.exit(1);
        }
    }

    protected MessageSet loadMessages(File inputCSVFile) {
        MessageSet messages = new MessageSet();
        messages.setDateFormat(factory.constructDateFormat());

        try {
            System.out.println("Reading messages from " + inputCSVFile);
            InputStream inputCSV = new FileInputStream(inputCSVFile);
            messages.load(inputCSV);
            inputCSV.close();
        } catch (FileNotFoundException e) {
            System.err.println("Input CSV file " + inputCSVFile + " not found.");
            System.exit(1);
        } catch (InvalidObjectException e) {
            System.err.println("Incorrect format in input CSV file " + inputCSVFile);
            System.err.println("\t" + e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.err.println("File read error for " + inputCSVFile);
            System.err.println("\t" + e.getMessage());
            System.exit(1);
        }

        return messages;
    }

    protected Model loadModel(File inputModelFile) {
        Model model = factory.constructModel();
        try {
            System.out.println("Reading model from " + inputModelFile);
            InputStream inputModel = new FileInputStream(inputModelFile);
            model.load(inputModel);
            inputModel.close();
        } catch (FileNotFoundException e) {
            System.err.println("Model file " + inputModelFile + " not found.");
            System.exit(1);
        } catch (InvalidObjectException e) {
            System.err.println("Incorrect format in model file " + inputModelFile);
            System.err.println("\t" + e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.err.println("File read error for " + inputModelFile);
            System.err.println("\t" + e.getMessage());
            System.exit(1);
        }
        return model;
    }

    protected FeatureSpecification loadFeatureSpecification(File inputFeatureSpecFile) {
        FeatureSpecification spec = new FeatureSpecification();

        try {
            System.out.println("Reading feature spec from " + inputFeatureSpecFile);
            InputStream inputFeatureSpec = new FileInputStream(inputFeatureSpecFile);
            spec.load(inputFeatureSpec);
            inputFeatureSpec.close();
        } catch (FileNotFoundException e) {
            System.err.println("Feature specification file " + inputFeatureSpecFile + " not found.");
            System.exit(1);
        } catch (InvalidObjectException e) {
            System.err.println("Incorrect format for feature specification file " + inputFeatureSpecFile);
            System.err.println("\t" + e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.err.println("File read error for " + inputFeatureSpecFile);
            System.err.println("\t" + e.getMessage());
            System.exit(1);
        }

        return spec;
    }

    protected void saveMessages(MessageSet messages, File outputCSVFile) {
        try {
            OutputStream outputCSV = new FileOutputStream(outputCSVFile);
            messages.save(outputCSV);
            outputCSV.close();
            System.out.println("Saved labeled data to " + outputCSVFile);
        } catch (IOException e) {
            System.err.println("Error saving messages to " + outputCSVFile);
            System.err.println("\t" + e.getMessage());
        }
    }

    protected void saveEvaluationReport(EvaluationReport evalReport, File outputEvaluationReportFile) {
        try {
            OutputStream outputEval = new FileOutputStream(outputEvaluationReportFile);
            evalReport.save(outputEval);
            outputEval.close();
            System.out.println("Saved evaluation to " + outputEvaluationReportFile);
        } catch (IOException e) {
            System.err.println("Error saving evaluation report to " + outputEvaluationReportFile);
            System.err.println("\t" + e.getMessage());
        }
    }

    protected void saveROC(ROC roc, File outputROCFile) {
        try {
            OutputStream outputRoc = new FileOutputStream(outputROCFile);
            roc.save(outputRoc);
            outputRoc.close();
            System.out.println("Saved ROC " + roc.getName() + " to " + outputROCFile);
        } catch (IOException e) {
            System.err.println("Error saving ROC " + roc.getName() + " to " + outputROCFile);
            System.err.println("\t" + e.getMessage());
        }
    }

    protected void saveFeatureSpecification(FeatureSpecification spec, File outputFeatureSpecFile) {
        try {
            OutputStream outputFeatureSpec = new FileOutputStream(outputFeatureSpecFile);
            spec.save(outputFeatureSpec);
            outputFeatureSpec.close();
            System.out.println("Saved feature spec to " + outputFeatureSpecFile);
        } catch (IOException e) {
            System.err.println("Error saving feature spec to " + outputFeatureSpecFile);
            System.err.println("\t" + e.getMessage());
        }
    }

    protected void saveModel(Model model, File outputModelFile) {
        try {
            OutputStream outputModel = new FileOutputStream(outputModelFile);
            model.save(outputModel);
            outputModel.close();
            System.out.println("Saved model to " + outputModelFile);
        } catch (IOException e) {
            System.err.println("Error saving model to " + outputModelFile);
            System.err.println("\t" + e.getMessage());
        }
    }

    protected void saveTopFeatures(List<String> topFeatures, File outputTopFeaturesFile) {
        try {
            PrintStream output = new PrintStream(outputTopFeaturesFile);
            for (String feature : topFeatures) {
                output.println(feature);
            }
            output.close();
            System.out.println("Saved top features to " + outputTopFeaturesFile);
        } catch (FileNotFoundException e) {
            System.err.println("Top features file not found:" + outputTopFeaturesFile);
            System.err.println("\t" + e.getMessage());
        }
    }

    protected void saveFeatureWeights(List<Map.Entry<String, Double>> featureWeights, File outputFeatureWeightsFile) {
        try {
            CsvWriter writer = new CsvWriter(new FileWriter(outputFeatureWeightsFile), ',');

            writer.write("Feature");
            writer.write("Weight");
            writer.write("WeightSquared");
            writer.endRecord();

            for (Map.Entry<String, Double> entry : featureWeights) {
                writer.write(entry.getKey());
                writer.write(entry.getValue() + "");
                writer.write(entry.getValue() * entry.getValue() + "");
                writer.endRecord();
            }
            writer.close();
            System.out.println("Saved feature weights to " + outputFeatureWeightsFile);
        } catch (IOException e) {
            System.err.println("Error writing feature weights to " + outputFeatureWeightsFile);
            System.err.println("\t" + e.getMessage());
        }
    }

    protected void saveInstances(Instances instances, File outputFile) {
        try {
            CSVSaver saver = new CSVSaver();
            saver.setFile(outputFile);
            saver.setInstances(instances);
            saver.writeBatch();
            System.out.println("Saved instances to " + outputFile);
        } catch (IOException e) {
            System.err.println("Error writing instances to " + outputFile);
            System.err.println("\t" + e.getMessage());
        }
    }

    protected void saveCommand(File outputFile) {
        try {
            PrintStream output = new PrintStream(outputFile);

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm z");
            String now = df.format(new Date());
            output.println("# ALOE arguments (" + now + ")");

            //Based on http://java.dzone.com/articles/programmatically-restart-java
            //Based on http://stackoverflow.com/questions/13958318/is-it-possible-to-get-the-command-used-to-launch-the-jvm-in-java
//            RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
//            List<String> args = bean.getInputArguments();
//
//            for (int i = 0; i < args.size(); i++) {
//                output.print(args.get(i) + " ");
//            }
            // print the non-JVM command line arguments using args
            // name of the main class
//            output.println("java " + System.getProperty("sun.java.command"));
            output.println(getJavaCommand());
            output.close();

            System.out.println("Saved command to " + outputFile);
        } catch (IOException e) {
            System.err.println("Error writing command to " + outputFile);
            System.err.println("\t" + e.getMessage());
        }
    }

    private String getJavaCommand() {
        // java binary
        String java = "java";

        // vm arguments
        List<String> vmArguments = ManagementFactory.getRuntimeMXBean().getInputArguments();
        StringBuffer vmArgsOneLine = new StringBuffer();
        for (String arg : vmArguments) {
                // if it's the agent argument : we ignore it otherwise the
            // address of the old application and the new one will be in conflict
            if (!arg.contains("-agentlib")) {
                vmArgsOneLine.append(arg);
                vmArgsOneLine.append(" ");
            }
        }
        // init the command to execute, add the vm args
        final StringBuffer cmd = new StringBuffer(java + " " + vmArgsOneLine);

        // program main and program arguments
        String[] mainCommand = System.getProperty("sun.java.command").split(" ");
        // program main is a jar
        if (mainCommand[0].endsWith(".jar")) {
            // if it's a jar, add -jar mainJar
            cmd.append("-jar ")
                    .append(new File(mainCommand[0]).getPath());
        } else {
            // else it's a .class, add the classpath and mainClass
            cmd.append("-cp \"")
                    .append(System.getProperty("java.class.path"))
                    .append("\" ")
                    .append(mainCommand[0]);
        }
        // finally add program arguments
        for (int i = 0; i < cmdLineArgs.length; i++) {
            cmd.append(" ");

            cmdLineArgs[i] = cmdLineArgs[i].replaceAll("\"", "\\\"");
            
            if (cmdLineArgs[i].contains(" ")) {
                cmd.append("\"")
                        .append(cmdLineArgs[i])
                        .append("\"");
            } else {
                cmd.append(cmdLineArgs[i]);
            }
        }

        return cmd.toString();
    }

    public abstract void run(ModeOptions options);
}
