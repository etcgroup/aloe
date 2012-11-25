package etc.aloe;

import etc.aloe.controllers.CrossValidationController;
import etc.aloe.controllers.LabelingController;
import etc.aloe.controllers.TrainingController;
import etc.aloe.cscw2013.CrossValidationPrepImpl;
import etc.aloe.cscw2013.CrossValidationSplitImpl;
import etc.aloe.cscw2013.EvaluationImpl;
import etc.aloe.cscw2013.FeatureExtractionImpl;
import etc.aloe.cscw2013.FeatureGenerationImpl;
import etc.aloe.cscw2013.LabelMappingImpl;
import etc.aloe.cscw2013.ResolutionImpl;
import etc.aloe.cscw2013.ThresholdSegmentation;
import etc.aloe.cscw2013.TrainingImpl;
import etc.aloe.data.EvaluationReport;
import etc.aloe.data.FeatureSpecification;
import etc.aloe.data.MessageSet;
import etc.aloe.data.Model;
import etc.aloe.data.Segment;
import etc.aloe.data.SegmentSet;
import etc.aloe.filters.StringToDictionaryVector;
import etc.aloe.processes.Segmentation;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidObjectException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Random;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.Option;

/**
 * Main Aloe controller
 *
 * @version 1.0 - using CSCW2013 implementations
 */
public class Aloe {

    private final static String DATA_SUFFIX = ".csv";
    private final static String EVALUATION_REPORT_SUFFIX = ".eval";
    private final static String MODEL_SUFFIX = ".model";
    private final static String FEATURE_SPEC_SUFFIX = ".spec";
    private final static String OUTPUT_CSV_NAME = "labeled" + DATA_SUFFIX;
    private final static String OUTPUT_EVALUTION_REPORT_NAME = "report" + EVALUATION_REPORT_SUFFIX;
    private final static String OUTPUT_FEATURE_SPEC_NAME = "features" + FEATURE_SPEC_SUFFIX;
    private final static String OUTPUT_MODEL_NAME = "model" + MODEL_SUFFIX;
    //TRAINING: java -jar aloe.jar input.csv -t 30
    /**
     * True if segmentation should separate messages by participant.
     */
    private boolean segmentationByParticipant = true;
    @Argument(index = 0, usage = "input CSV file containing messages", required = true, metaVar = "INPUT_CSV")
    private File inputCSVFile;
    private List<String> termList;

    @Argument(index = 1, usage = "output directory (contents may be overwritten)", required = true, metaVar = "OUTPUT_DIR")
    private void setOutputDir(File dir) {
        this.outputDir = dir;
        dir.mkdir();

        outputCSVFile = new File(dir, OUTPUT_CSV_NAME);
        outputEvaluationReportFile = new File(dir, OUTPUT_EVALUTION_REPORT_NAME);
        outputFeatureSpecFile = new File(dir, OUTPUT_FEATURE_SPEC_NAME);
        outputModelFile = new File(dir, OUTPUT_MODEL_NAME);
    }
    private File outputDir;
    private File outputCSVFile;
    private File outputEvaluationReportFile;
    private File outputFeatureSpecFile;
    private File outputModelFile;
    @Option(name = "-m", usage = "use an existing model file (requires -f option)")
    private File inputModelFile;
    @Option(name = "-f", usage = "use an existing feature specification file (requires -m option)")
    private File inputFeatureSpecFile;
    @Option(name = "-t", usage = "segmentation threshold in seconds (default 30)")
    private int segmentationThresholdSeconds = 30;
    @Option(name = "-k", usage = "number of cross-validation folds (default 10)")
    private int crossValidationFolds = 10;
    @Option(name = "-d", usage = "date format string (default 'yyyy-MM-dd'T'HH:mm:ss')")
    private String dateFormatString = "yyyy-MM-dd'T'HH:mm:ss";
    @Option(name = "-e", usage = "emoticon dictionary file (default emoticons.txt)")
    private File emoticonFile = new File("emoticons.txt");

    @Option(name = "-r", usage = "random seed")
    void setRandomSeed(int randomSeed) {
        RandomProvider.setRandom(new Random(randomSeed));
    }

    void printUsage() {
        System.err.println("java -jar aloe.jar [options...] INPUT_CSV OUTPUT_DIR");
    }

    void run() throws CmdLineException {

        if (inputModelFile != null) {
            if (inputFeatureSpecFile != null) {
                //We're using an existing model!
                runTestingMode();
            } else {
                //We're in an invalid mode
                throw new CmdLineException("Model file provided without feature spec file!");
            }
        } else if (inputFeatureSpecFile != null) {
            //We're in an invalid mode
            throw new CmdLineException("Feature spec file provided without model file!");
        } else {
            runTrainingMode();
        }
    }

    private void runTrainingMode() {

        termList = loadTermList();

        // This sets up the components of the abstract pipeline with specific
        // implementations.

        CrossValidationController crossValidationController = new CrossValidationController(this.crossValidationFolds);
        crossValidationController.setCrossValidationPrepImpl(new CrossValidationPrepImpl<Segment>());
        crossValidationController.setCrossValidationSplitImpl(new CrossValidationSplitImpl<Segment>());
        crossValidationController.setFeatureGenerationImpl(new FeatureGenerationImpl(termList));
        crossValidationController.setFeatureExtractionImpl(new FeatureExtractionImpl());
        crossValidationController.setTrainingImpl(new TrainingImpl());
        crossValidationController.setEvaluationImpl(new EvaluationImpl());

        TrainingController trainingController = new TrainingController();
        trainingController.setFeatureGenerationImpl(new FeatureGenerationImpl(termList));
        trainingController.setFeatureExtractionImpl(new FeatureExtractionImpl());
        trainingController.setTrainingImpl(new TrainingImpl());

        //Get and preprocess the data
        MessageSet messages = this.loadMessages();
        Segmentation segmentation = new ThresholdSegmentation(this.segmentationThresholdSeconds, segmentationByParticipant);
        segmentation.setSegmentResolution(new ResolutionImpl());
        SegmentSet segments = segmentation.segment(messages);

        //Run cross validation
        crossValidationController.setSegmentSet(segments);
        crossValidationController.run();

        //Run the full training
        trainingController.setSegmentSet(segments);
        trainingController.run();

        //Get the fruits
        EvaluationReport evalReport = crossValidationController.getEvaluationReport();
        FeatureSpecification spec = trainingController.getFeatureSpecification();
        Model model = trainingController.getModel();

        //Save the fruits
        saveEvaluationReport(evalReport);
        saveFeatureSpecification(spec);
        saveModel(model);
    }

    private void runTestingMode() {
        Segmentation segmentation = new ThresholdSegmentation(this.segmentationThresholdSeconds, segmentationByParticipant);
        segmentation.setSegmentResolution(new ResolutionImpl());
        
        LabelingController labelingController = new LabelingController();
        labelingController.setFeatureExtractionImpl(new FeatureExtractionImpl());
        labelingController.setEvaluationImpl(new EvaluationImpl());
        labelingController.setMappingImpl(new LabelMappingImpl());

        MessageSet messages = this.loadMessages();
        FeatureSpecification spec = this.loadFeatureSpecification();
        Model model = this.loadModel();

        SegmentSet segments = segmentation.segment(messages);

        labelingController.setModel(model);
        labelingController.setSegmentSet(segments);
        labelingController.setFeatureSpecification(spec);
        labelingController.run();

        EvaluationReport evalReport = labelingController.getEvaluationReport();

        saveEvaluationReport(evalReport);
        saveMessages(messages);
    }

    private MessageSet loadMessages() {
        MessageSet messages = new MessageSet();
        messages.setDateFormat(new SimpleDateFormat(dateFormatString));

        try {
            InputStream inputCSV = new FileInputStream(inputCSVFile);
            messages.load(inputCSV);
            inputCSV.close();
        } catch (FileNotFoundException e) {
            System.err.println("Input CSV file " + this.inputCSVFile + " not found.");
            System.exit(1);
        } catch (InvalidObjectException e) {
            System.err.println("Incorrect format in input CSV file " + this.inputCSVFile);
            System.err.println("\t" + e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.err.println("File read error for " + this.inputCSVFile);
            System.err.println("\t" + e.getMessage());
            System.exit(1);
        }

        return messages;
    }

    private Model loadModel() {
        Model model = new Model();
        try {
            InputStream inputModel = new FileInputStream(inputModelFile);
            model.load(inputModel);
            inputModel.close();
        } catch (FileNotFoundException e) {
            System.err.println("Model file " + this.inputModelFile + " not found.");
            System.exit(1);
        } catch (InvalidObjectException e) {
            System.err.println("Incorrect format in model file " + this.inputModelFile);
            System.err.println("\t" + e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.err.println("File read error for " + this.inputModelFile);
            System.err.println("\t" + e.getMessage());
            System.exit(1);
        }
        return model;
    }

    private FeatureSpecification loadFeatureSpecification() {
        FeatureSpecification spec = new FeatureSpecification();

        try {
            InputStream inputFeatureSpec = new FileInputStream(inputFeatureSpecFile);
            spec.load(inputFeatureSpec);
            inputFeatureSpec.close();
        } catch (FileNotFoundException e) {
            System.err.println("Feature specification file " + this.inputFeatureSpecFile + " not found.");
            System.exit(1);
        } catch (InvalidObjectException e) {
            System.err.println("Incorrect format for feature specification file " + this.inputFeatureSpecFile);
            System.err.println("\t" + e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.err.println("File read error for " + this.inputFeatureSpecFile);
            System.err.println("\t" + e.getMessage());
            System.exit(1);
        }

        return spec;
    }

    private void saveMessages(MessageSet messages) {
        try {
            OutputStream outputCSV = new FileOutputStream(outputCSVFile);
            messages.save(outputCSV);
            outputCSV.close();
            System.out.println("Saved labeled data to " + outputCSVFile);
        } catch (IOException e) {
            System.err.println("Error saving messages to " + this.outputCSVFile);
            System.err.println("\t" + e.getMessage());
        }
    }

    private void saveEvaluationReport(EvaluationReport evalReport) {
        try {
            OutputStream outputEval = new FileOutputStream(outputEvaluationReportFile);
            evalReport.save(outputEval);
            outputEval.close();
            System.out.println("Saved evaluation to " + this.outputEvaluationReportFile);
            System.out.println("Evaluation:");
            System.out.println(evalReport.toString());
        } catch (IOException e) {
            System.err.println("Error saving evaluation report to " + this.outputEvaluationReportFile);
            System.err.println("\t" + e.getMessage());
        }
    }

    private void saveFeatureSpecification(FeatureSpecification spec) {
        try {
            OutputStream outputFeatureSpec = new FileOutputStream(outputFeatureSpecFile);
            spec.save(outputFeatureSpec);
            outputFeatureSpec.close();
            System.out.println("Saved feature spec to " + this.outputFeatureSpecFile);
        } catch (IOException e) {
            System.err.println("Error saving feature spec to " + this.outputFeatureSpecFile);
            System.err.println("\t" + e.getMessage());
        }
    }

    private void saveModel(Model model) {
        try {
            OutputStream outputModel = new FileOutputStream(outputModelFile);
            model.save(outputModel);
            outputModel.close();
            System.out.println("Saved model to " + this.outputModelFile);
        } catch (IOException e) {
            System.err.println("Error saving model to " + this.outputModelFile);
            System.err.println("\t" + e.getMessage());
        }
    }

    private List<String> loadTermList() {
        try {
            return StringToDictionaryVector.readDictionaryFile(emoticonFile);
        } catch (FileNotFoundException ex) {
            System.err.println("Unable to read emoticon dictionary file " + emoticonFile);
            System.err.println("\t" + ex.getMessage());
            System.exit(1);
        }
        return null;
    }
}
