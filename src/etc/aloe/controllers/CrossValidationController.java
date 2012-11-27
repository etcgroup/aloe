package etc.aloe.controllers;

import etc.aloe.data.EvaluationReport;
import etc.aloe.data.ExampleSet;
import etc.aloe.data.FeatureSpecification;
import etc.aloe.data.Model;
import etc.aloe.data.Segment;
import etc.aloe.data.SegmentSet;
import etc.aloe.processes.Balancing;
import etc.aloe.processes.CrossValidationPrep;
import etc.aloe.processes.CrossValidationSplit;
import etc.aloe.processes.Evaluation;
import etc.aloe.processes.FeatureExtraction;
import etc.aloe.processes.FeatureGeneration;
import etc.aloe.processes.Training;
import java.util.List;

/**
 * Class that performs cross validation and produces results.
 */
public class CrossValidationController {

    private final int folds;
    private EvaluationReport evaluationReport;
    private SegmentSet segmentSet;
    private FeatureGeneration featureGenerationImpl;
    private FeatureExtraction featureExtractionImpl;
    private Training trainingImpl;
    private Evaluation evaluationImpl;
    private CrossValidationPrep<Segment> crossValidationPrepImpl;
    private CrossValidationSplit<Segment> crossValidationSplitImpl;
    private Balancing balancingImpl;

    public EvaluationReport getEvaluationReport() {
        return evaluationReport;
    }

    public void setSegmentSet(SegmentSet segments) {
        this.segmentSet = segments;
    }

    public CrossValidationController(int folds) {
        this.folds = folds;
    }

    public void run() {

        System.out.println("== " + this.folds + "-Fold Cross Validation ==");

        segmentSet = segmentSet.onlyLabeled();

        //Prepare for cross validation
        System.out.println("Randomizing and stratifying segments.");
        CrossValidationPrep<Segment> validationPrep = this.getCrossValidationPrepImpl();
        validationPrep.randomize(segmentSet.getSegments());
        segmentSet.setSegments(validationPrep.stratify(segmentSet.getSegments(), folds));

        evaluationReport = new EvaluationReport();
        for (int foldIndex = 0; foldIndex < this.folds; foldIndex++) {
            System.out.println("- Starting fold " + (foldIndex + 1));
            //Split the data
            CrossValidationSplit split = this.getCrossValidationSplitImpl();

            SegmentSet trainingSegments = new SegmentSet();
            trainingSegments.setSegments(split.getTrainingForFold(segmentSet.getSegments(), foldIndex, this.folds));
            if (getBalancingImpl() != null) {
                trainingSegments = getBalancingImpl().balance(trainingSegments);
            }

            SegmentSet testingSegments = new SegmentSet();
            testingSegments.setSegments(split.getTestingForFold(segmentSet.getSegments(), foldIndex, this.folds));

            ExampleSet basicTrainingExamples = trainingSegments.getBasicExamples();
            ExampleSet basicTestingExamples = testingSegments.getBasicExamples();

            FeatureGeneration generation = getFeatureGenerationImpl();
            FeatureSpecification spec = generation.generateFeatures(basicTrainingExamples);

            FeatureExtraction extraction = getFeatureExtractionImpl();
            ExampleSet trainingSet = extraction.extractFeatures(basicTrainingExamples, spec);
            ExampleSet testingSet = extraction.extractFeatures(basicTestingExamples, spec);

            Training training = getTrainingImpl();
            Model model = training.train(trainingSet);

            List<Boolean> predictions = model.getPredictedLabels(testingSet);
            Evaluation evaluation = getEvaluationImpl();
            EvaluationReport report = evaluation.evaluate(predictions, testingSet);

            evaluationReport.addPartial(report);
            int numCorrect = report.getTrueNegativeCount() + report.getTruePositiveCount();
            System.out.println("- Fold " + (foldIndex + 1) + " completed (" + numCorrect + "/" + testingSet.size() + " correct).");
        }

        System.out.println("Aggregated cross-validation report:");
        System.out.println(evaluationReport);
        System.out.println("---------");
    }

    public FeatureGeneration getFeatureGenerationImpl() {
        return this.featureGenerationImpl;
    }

    public void setFeatureGenerationImpl(FeatureGeneration featureGenerator) {
        this.featureGenerationImpl = featureGenerator;
    }

    public FeatureExtraction getFeatureExtractionImpl() {
        return this.featureExtractionImpl;
    }

    public void setFeatureExtractionImpl(FeatureExtraction featureExtractor) {
        this.featureExtractionImpl = featureExtractor;
    }

    public Training getTrainingImpl() {
        return this.trainingImpl;
    }

    public void setTrainingImpl(Training training) {
        this.trainingImpl = training;
    }

    public Evaluation getEvaluationImpl() {
        return this.evaluationImpl;
    }

    public void setEvaluationImpl(Evaluation evaluation) {
        this.evaluationImpl = evaluation;
    }

    public CrossValidationPrep<Segment> getCrossValidationPrepImpl() {
        return this.crossValidationPrepImpl;
    }

    public void setCrossValidationPrepImpl(CrossValidationPrep<Segment> crossValidationPrep) {
        this.crossValidationPrepImpl = crossValidationPrep;
    }

    public CrossValidationSplit<Segment> getCrossValidationSplitImpl() {
        return this.crossValidationSplitImpl;
    }

    public void setCrossValidationSplitImpl(CrossValidationSplit<Segment> crossValidationSplit) {
        this.crossValidationSplitImpl = crossValidationSplit;
    }

    public Balancing getBalancingImpl() {
        return this.balancingImpl;
    }

    public void setBalancingImpl(Balancing balancing) {
        this.balancingImpl = balancing;
    }
}
