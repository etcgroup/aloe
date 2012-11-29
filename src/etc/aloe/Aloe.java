package etc.aloe;

import com.csvreader.CsvWriter;
import etc.aloe.data.EvaluationReport;
import etc.aloe.data.FeatureSpecification;
import etc.aloe.data.MessageSet;
import etc.aloe.data.Model;
import etc.aloe.filters.StringToDictionaryVector;
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
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.kohsuke.args4j.Option;

/**
 * Main Aloe controller superclass
 *
 * @version 1.0 - using CSCW2013 implementations
 */
public abstract class Aloe {

    /**
     * True if segmentation should separate messages by participant.
     */
    @Option(name = "--ignore-participants", usage = "ignore participants during segmentation")
    protected boolean ignoreParticipants = false;
    @Option(name = "--threshold", aliases = {"-t"}, usage = "segmentation threshold in seconds (default 30)")
    protected int segmentationThresholdSeconds = 30;
    @Option(name = "--dateformat", aliases = {"-d"}, usage = "date format string (default 'yyyy-MM-dd'T'HH:mm:ss')")
    protected String dateFormatString = "yyyy-MM-dd'T'HH:mm:ss";
    @Option(name = "--no-segmentation", usage = "disable segmentation (each message is in its own segment)")
    protected boolean disableSegmentation = false;

    @Option(name = "--random", aliases = {"-r"}, usage = "random seed")
    void setRandomSeed(int randomSeed) {
        RandomProvider.setRandom(new Random(randomSeed));
    }

    protected MessageSet loadMessages(String dateFormatString, File inputCSVFile) {
        MessageSet messages = new MessageSet();
        messages.setDateFormat(new SimpleDateFormat(dateFormatString));

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
        Model model = new Model();
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

    protected List<String> loadTermList(File emoticonFile) {
        try {
            return StringToDictionaryVector.readDictionaryFile(emoticonFile);
        } catch (FileNotFoundException ex) {
            System.err.println("Unable to read emoticon dictionary file " + emoticonFile);
            System.err.println("\t" + ex.getMessage());
            System.exit(1);
        }
        return null;
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

    public abstract void printUsage();

    public abstract void run();
}
