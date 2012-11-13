package etc.aloe;

import etc.aloe.controllers.CrossValidationController;
import etc.aloe.controllers.LabelingController;
import etc.aloe.controllers.TrainingController;
import etc.aloe.cscw2013.EvaluationImpl;
import etc.aloe.cscw2013.FeatureExtractionImpl;
import etc.aloe.cscw2013.FeatureGenerationImpl;
import etc.aloe.cscw2013.FeatureSpecificationImpl;
import etc.aloe.cscw2013.PredictionImpl;
import etc.aloe.cscw2013.ThresholdSegmentation;
import etc.aloe.cscw2013.TrainingImpl;
import etc.aloe.data.EvaluationReport;
import etc.aloe.data.FeatureSpecification;
import etc.aloe.data.MessageSet;
import etc.aloe.data.Model;
import etc.aloe.data.SegmentSet;
import etc.aloe.processes.Segmentation;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

/**
 * Main Aloe controller
 *
 * @version 1.0 - using CSCW2013 implementations
 */
public class Aloe {

    /**
     * True if segmentation should separate messages by participant.
     */
    private boolean segmentationByParticipant = true;
    @Option(name = "-t", usage = "segmentation threshold in seconds")
    private int segmentationThresholdSeconds = 30;
    @Option(name = "-k", usage = "number of cross-validation folds")
    private int crossValidationFolds = 10;
    @Option(name = "-i", usage = "input CSV file containing messages")
    private File inputCSVFile;
    @Option(name = "-o", usage = "output CSV file for labeled messages")
    private File outputCSVFile;
    @Option(name = "-e", usage = "evaluation report file")
    private File evaluationReportFile;
    @Option(name = "-m", usage = "model file")
    private File modelFile;
    @Option(name = "-f", usage = "feature specification file")
    private File featureSpecificationFile;
    @Option(name = "-d", usage = "date format string")
    private String dateFormatString = "yyyy-MM-dd'T'HH:mm:ss";
    /**
     * Any remaining arguments to the program
     */
    @Argument
    private List<String> arguments = new ArrayList<String>();

    void run() {
        runTrainingMode();
        runTestingMode();
    }

    private void runTrainingMode() {

        // This sets up the components of the abstract pipeline with specific
        // implementations.

        CrossValidationController crossValidationController = new CrossValidationController(this.crossValidationFolds);
        crossValidationController.setFeatureGenerationImpl(new FeatureGenerationImpl());
        crossValidationController.setFeatureExtractionImpl(new FeatureExtractionImpl());
        crossValidationController.setTrainingImpl(new TrainingImpl());
        crossValidationController.setEvaluationImpl(new EvaluationImpl());

        TrainingController trainingController = new TrainingController();
        trainingController.setFeatureExtractionImpl(new FeatureExtractionImpl());
        trainingController.setTrainingImpl(new TrainingImpl());

        //Get and preprocess the data
        MessageSet messages = this.loadMessages();
        Segmentation segmentation = new ThresholdSegmentation(this.segmentationThresholdSeconds, segmentationByParticipant);
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
        LabelingController labelingController = new LabelingController();
        labelingController.setFeatureExtractionImpl(new FeatureExtractionImpl());
        labelingController.setEvaluationImpl(new EvaluationImpl());
        labelingController.setPredictionImpl(new PredictionImpl());

        MessageSet messages = this.loadMessages();
        FeatureSpecification spec = this.loadFeatureSpecification();
        Model model = this.loadModel();

        SegmentSet segments = segmentation.segment(messages);

        labelingController.setModel(model);
        labelingController.setSegmentSet(segments);
        labelingController.setFeatureSpecification(spec);
        labelingController.run();

        MessageSet labeledMessages = labelingController.getLabeledMessages();
        EvaluationReport evalReport = labelingController.getEvaluationReport();

        saveEvaluationReport(evalReport);
        saveMessages(labeledMessages);
    }

    private MessageSet loadMessages() {
        MessageSet messages = new MessageSet();
        messages.setDateFormat(new SimpleDateFormat(dateFormatString));

        try {
            messages.load(this.inputCSVFile);
        } catch (FileNotFoundException e) {
            System.err.println("Input CSV file " + this.inputCSVFile + " not found.");
            System.exit(1);
        } catch (InvalidObjectException e) {
            System.err.println("Incorrect format in input CSV file " + this.inputCSVFile);
            System.err.println("\t" + e.getMessage());
            System.exit(1);
        }

        return messages;
    }

    private Model loadModel() {
        Model model = new Model();
        try {
            model.load(this.modelFile);
        } catch (FileNotFoundException e) {
            System.err.println("Model file " + this.modelFile + " not found.");
            System.exit(1);
        } catch (InvalidObjectException e) {
            System.err.println("Incorrect format in model file " + this.modelFile);
            System.err.println("\t" + e.getMessage());
            System.exit(1);
        }
        return model;
    }

    private FeatureSpecification loadFeatureSpecification() {
        FeatureSpecification spec = new FeatureSpecificationImpl();

        try {
            spec.load(this.featureSpecificationFile);
        } catch (FileNotFoundException e) {
            System.err.println("Feature specification file " + this.featureSpecificationFile + " not found.");
            System.exit(1);
        } catch (InvalidObjectException e) {
            System.err.println("Incorrect format for feature specification file " + this.featureSpecificationFile);
            System.err.println("\t" + e.getMessage());
            System.exit(1);
        }

        return spec;
    }

    private void saveMessages(MessageSet messages) {
        try {
            messages.save(this.outputCSVFile);
        } catch (IOException e) {
            System.err.println("Error saving messages to " + this.outputCSVFile);
            System.err.println("\t" + e.getMessage());
        }
    }

    private void saveEvaluationReport(EvaluationReport evalReport) {
        try {
            evalReport.save(this.evaluationReportFile);
        } catch (IOException e) {
            System.err.println("Error saving evaluation report to " + this.evaluationReportFile);
            System.err.println("\t" + e.getMessage());
        }
    }

    private void saveFeatureSpecification(FeatureSpecification spec) {
        try {
            spec.save(this.featureSpecificationFile);
        } catch (IOException e) {
            System.err.println("Error saving feature spec to " + this.featureSpecificationFile);
            System.err.println("\t" + e.getMessage());
        }
    }

    private void saveModel(Model model) {
        try {
            model.save(this.modelFile);
        } catch (IOException e) {
            System.err.println("Error saving model to " + this.modelFile);
            System.err.println("\t" + e.getMessage());
        }
    }
}
