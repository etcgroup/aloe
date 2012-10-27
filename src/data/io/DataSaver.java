package data.io;

import cmd.config.ClassifierConfig;
import cmd.config.FeatureConfig;
import data.DataSet;
import data.EntitySet;
import data.analysis.AnalysisResult;
import java.util.ArrayList;
import weka.core.Instances;

/**
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public interface DataSaver {

    void initialize();

    void saveData(DataSet dataSet);

    void setDestination(String destination);

    void saveAnalysis(
            AnalysisResult analysis,
            String dataName,
            String analysisName,
            boolean isHumanReadable);

    void recordReport(
            String dataName,
            String codeName,
            FeatureConfig featureConfig,
            ClassifierConfig classifierConfig,
            String resultSet,
            ArrayList<Integer> ids,
            int prediction,
            EntitySet codeFacet,
            Instances train);
}
