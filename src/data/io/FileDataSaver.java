/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data.io;

import cmd.config.ClassifierConfig;
import cmd.config.FeatureConfig;
import daisy.io.CSV;
import data.DataSet;
import data.EntityMetaData;
import data.EntitySet;
import data.MultiRatedEntity;
import data.analysis.AnalysisResult;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

/**
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class FileDataSaver implements DataSaver {

    private String destination;
    private String fullPrefix = "_full_";
    private String testPrefix = "_test_";
    private String trainPrefix = "_trai_";
    private DateFormat fileDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    public FileDataSaver(String destination) {
        String now = fileDateFormat.format(new Date());
        this.destination = destination + "/" + now;
    }

    public FileDataSaver() {
    }

    @Override
    public void initialize() {
        File destFile = new File(destination);
        if (!destFile.exists()) {
            if (!destFile.mkdirs()) {
                throw new IllegalArgumentException("Could not create destination directory!");
            }
        } else if (!destFile.isDirectory()) {
            throw new IllegalArgumentException("Destination is not a directory!");
        }
    }

    @Override
    public void saveData(DataSet dataSet) {
        String name = dataSet.getName();

        Instances full = dataSet.getPartition(DataSet.Partition.Full);
        if (full != null) {
            saveInstances(fullPrefix, name, full);
        }

        Instances train = dataSet.getPartition(DataSet.Partition.Train);
        if (train != null) {
            saveInstances(trainPrefix, name, train);
        }

        Instances test = dataSet.getPartition(DataSet.Partition.Test);
        if (test != null) {
            saveInstances(testPrefix, name, test);
        }
    }

    @Override
    public void setDestination(String destination) {
        this.destination = destination;
    }

    private void saveInstances(String prefix, String name, Instances instances) {
        String filename = prefix + name;
        instances.setRelationName(filename);

        filename = destination + "/" + filename + ".arff";
        try {
            ConverterUtils.DataSink.write(filename, instances);
        } catch (Exception ex) {
            System.err.println("Unable to save " + filename);
            ex.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void saveAnalysis(AnalysisResult analysis, String dataName, String analysisName, boolean isHumanReadable) {
        String filename = destination + "/" + dataName + "." + analysisName + ".csv";
        try {
            File file = new File(filename);
            if (file.exists()) {
                file.delete();
            }

            CSV csv = new CSV(filename, "DataSet:", dataName, "Analaysis:", analysisName);
            csv.println(analysis.getExplanation());
            analysis.writeToCSV(isHumanReadable, csv);

            csv.close();
        } catch (IOException ex) {
            System.err.println("Unable to save " + filename);
            ex.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void recordReport(String dataName, String codeName, FeatureConfig featureConfig, ClassifierConfig classifierConfig, String resultSet, ArrayList<Integer> ids, int prediction, EntitySet codeFacet, Instances train) {
        String classifierType = classifierConfig.getClassifierType().replaceAll("\\w*\\.", "");
        String filename = destination + "/" + dataName + "." + codeName + "." + featureConfig.getName() + "." + classifierType + "." + resultSet + ".csv";
        String featuresFilename = destination + "/features." + dataName + "." + codeName + "." + featureConfig.getName() + "." + classifierType + "." + resultSet + ".csv";
        String predictionString = prediction == 1 ? "yes" : "no";
        try {
            File file = new File(filename);
            if (file.exists()) {
                file.delete();
            }

            CSV friendlyCsv = new CSV(filename, "id", "start_time", "stop_time", "participants", "actual", "predicted", "message");
            ArrayList<String> attrNames = new ArrayList<String>();
            for (int i = 0; i < train.numAttributes(); i++) {
                Attribute attr = train.attribute(i);
                attrNames.add(attr.name());
            }
            CSV featuresCsv = new CSV(featuresFilename, attrNames);

            HashSet<Integer> idSet = new HashSet<Integer>(ids);
            HashMap<Integer, Instance> instances = new HashMap<Integer, Instance>();

            int idAttrIndex = train.attribute("*id").index();
            for (int i = 0; i < train.size(); i++) {
                Instance inst = train.instance(i);
                int id = (int) inst.value(idAttrIndex);
                if (idSet.contains(id)) {
                    instances.put(id, inst);
                }
            }

            for (int i = 0; i < codeFacet.size(); i++) {
                MultiRatedEntity entity = codeFacet.get(i);
                int id = entity.getEntityId();
                if (idSet.contains(id)) {
                    EntityMetaData meta = codeFacet.getMetaData(id);

                    String startTime = "T: " + meta.getStartTime().toString();
                    String stopTime = "T: " + meta.getStartTime().toString();

                    String participants = meta.getParticipantNames();
                    Instance inst = instances.get(id);
                    int actual = (int) inst.classValue();
                    String message = meta.concatMessages(" | ");
                    String actualString = actual == 1 ? "yes" : "no";
                    if (prediction == -1) {
                        predictionString = actualString;
                    }
                    friendlyCsv.println(Integer.toString(id), startTime, stopTime, participants, actualString, predictionString, message);


                    ArrayList<String> attrValues = new ArrayList<String>();
                    for (int j = 0; j < train.numAttributes(); j++) {
                        Attribute attr = train.attribute(j);
                        String value = null;
                        if (attr.isString()) {
                            value = inst.stringValue(j);
                        } else {
                            double dv = inst.value(j);
                            value = Double.toString(dv);
                        }
                        attrValues.add(value);
                    }
                    featuresCsv.println(attrValues.toArray(new String[]{}));
                }
            }

            friendlyCsv.close();
        } catch (IOException ex) {
            System.err.println("Unable to save " + filename);
            ex.printStackTrace();
            System.exit(1);
        }
    }
}
