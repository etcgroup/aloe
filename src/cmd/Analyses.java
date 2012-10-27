package cmd;

import cmd.config.AnalysisConfig;
import cmd.config.DataConfig;
import cmd.config.DefaultAnalysisConfig;
import cmd.config.DefaultDataConfig;
import data.EntitySet;
import data.analysis.Analysis;
import data.analysis.AnalysisResult;
import data.io.DataSaver;
import data.io.DataSource;
import data.io.FileDataSaver;
import data.processing.EntitySetFilter;
import java.util.List;

/**
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class Analyses {

    private final AnalysisConfig analysisConfig;
    private final DataConfig dataConfig;
    private final DataSaver dataSaver;

    public Analyses(DataConfig dataConfig, AnalysisConfig analysisConfig, DataSaver saver) {
        this.dataConfig = dataConfig;
        this.analysisConfig = analysisConfig;
        this.dataSaver = saver;
    }

    public void run() {
        DataSource source = dataConfig.getDataSource();
        dataConfig.configureDataSource(source);

        try {
            source.initialize();
        } catch (Exception e) {
            System.err.println("Error initializing data source.");
            e.printStackTrace();
            System.exit(1);
        }

        source.loadIndexes();

        EntitySet entities = source.getData();

        List<Analysis<EntitySet>> preFilterAnalyses = analysisConfig.getPreFilterAnalyses();
        for (int i = 0; i < preFilterAnalyses.size(); i++) {
            Analysis<EntitySet> analysis = preFilterAnalyses.get(i);
            System.out.println("Running pre-filter analysis: " + analysis.getName());
            AnalysisResult result = analysis.analyze(entities);
            dataSaver.saveAnalysis(result, entities.getName(), analysis.getName(), analysisConfig.isReportHumanReadable());
        }

        List<EntitySetFilter> entitySetFilters = dataConfig.getEntitySetFilters();
        for (EntitySetFilter filter : entitySetFilters) {
            entities = filter.filter(entities);
            System.out.println("Filtered to " + entities.size() + " entities.");
        }

        List<Analysis<EntitySet>> postFilterAnalyses = analysisConfig.getPostFilterAnalyses();
        for (int i = 0; i < postFilterAnalyses.size(); i++) {
            Analysis<EntitySet> analysis = postFilterAnalyses.get(i);
            System.out.println("Running post-filter analysis: " + analysis.getName());
            AnalysisResult result = analysis.analyze(entities);
            dataSaver.saveAnalysis(result, entities.getName(), analysis.getName(), analysisConfig.isReportHumanReadable());
        }
    }

    public static void main(String[] args) {
        DataSaver saver = new FileDataSaver("frequencies test");
        saver.initialize();

//        {
//            SlidingWindowDataConfig regularConfig = new SlidingWindowDataConfig("regular");
//            regularConfig.setRemoveSystemMessages(true);
//            regularConfig.setPrescient(false);
//            regularConfig.setMaxWindowSize(5);
//            regularConfig.setMaxWindowDuration(30);
//            regularConfig.setRatingsBasis(1);
//
//            AnalysisConfig analysisConfig = new DefaultAnalysisConfig();
//            Analyses analyses = new Analyses(regularConfig, analysisConfig, saver);
//            analyses.run();
//        }
//        
//        {
//            SlidingWindowDataConfig prescientConfig = new SlidingWindowDataConfig("prescient");
//            prescientConfig.setRemoveSystemMessages(true);
//            prescientConfig.setPrescient(true);
//            prescientConfig.setMaxWindowSize(5);
//            prescientConfig.setMaxWindowDuration(30);
//            prescientConfig.setRatingsBasis(1);
//
//            AnalysisConfig analysisConfig = new DefaultAnalysisConfig();
//            Analyses analyses = new Analyses(prescientConfig, analysisConfig, saver);
//            analyses.run();
//        }
//        
//        {
//            SlidingWindowDataConfig prescientConfig = new SlidingWindowDataConfig("prescient_plus");
//            prescientConfig.setRemoveSystemMessages(true);
//            prescientConfig.setPrescient(true);
//            prescientConfig.setMaxWindowSize(5);
//            prescientConfig.setMaxWindowDuration(30);
//            prescientConfig.setRatingsBasis(5);
//
//            AnalysisConfig analysisConfig = new DefaultAnalysisConfig();
//            Analyses analyses = new Analyses(prescientConfig, analysisConfig, saver);
//            analyses.run();
//        }

//        {
//            SlidingWindowDataConfig dataConfig = new SlidingWindowDataConfig("wide-5-1-mid");
//            dataConfig.setMaxWindowDuration(60);//1 minute
//            dataConfig.setMaxWindowSize(5);
//            dataConfig.setRemoveSystemMessages(true);
//            dataConfig.setRatingsBasis(3);
//            dataConfig.setPrescient(true);
//
//            AnalysisConfig analysisConfig = new DefaultAnalysisConfig();
//            Analyses analyses = new Analyses(dataConfig, analysisConfig, saver);
//            analyses.run();
//        }
//
//        {
//            SlidingWindowDataConfig dataConfig = new SlidingWindowDataConfig("wide-3-1-mid");
//            dataConfig.setMaxWindowDuration(60);//1 minute
//            dataConfig.setMaxWindowSize(3);
//            dataConfig.setRemoveSystemMessages(true);
//            dataConfig.setRatingsBasis(3);
//            dataConfig.setPrescient(true);
//
//            AnalysisConfig analysisConfig = new DefaultAnalysisConfig();
//            Analyses analyses = new Analyses(dataConfig, analysisConfig, saver);
//            analyses.run();
//        }
//
//        {
//            SlidingWindowDataConfig dataConfig = new SlidingWindowDataConfig("window-3-1");
//            dataConfig.setRemoveSystemMessages(true);
//            dataConfig.setMaxWindowSize(3);
//            dataConfig.setMaxWindowDuration(60);
//
//            AnalysisConfig analysisConfig = new DefaultAnalysisConfig();
//            Analyses analyses = new Analyses(dataConfig, analysisConfig, saver);
//            analyses.run();
//        }
//
        {
            DefaultDataConfig dataConfig = new DefaultDataConfig("no-seg");
            dataConfig.setSegmentationId(0);
            dataConfig.setRemoveSystemMessages(true);

            AnalysisConfig analysisConfig = new DefaultAnalysisConfig();
            Analyses analyses = new Analyses(dataConfig, analysisConfig, saver);
            analyses.run();
        }
//
        {
            DefaultDataConfig dataConfig = new DefaultDataConfig("seg-6");
            dataConfig.setSegmentationId(6);
            dataConfig.setRemoveSystemMessages(true);

            AnalysisConfig analysisConfig = new DefaultAnalysisConfig();
            Analyses analyses = new Analyses(dataConfig, analysisConfig, saver);
            analyses.run();
        }
    }
}
