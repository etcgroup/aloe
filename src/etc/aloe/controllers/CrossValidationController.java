package etc.aloe.controllers;

import etc.aloe.data.EvaluationReport;
import etc.aloe.data.SegmentSet;

/**
 * Class that performs cross validation and produces results.
 */
public class CrossValidationController {

    private final int folds;
    private EvaluationReport evaluationReport;
    private SegmentSet segmentSet;

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

        segmentSet.randomize();
        segmentSet.stratify();

    }
}
