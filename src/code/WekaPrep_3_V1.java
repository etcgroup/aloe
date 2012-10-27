/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package code;

import code.WekaPrep.DataConfig;
import java.util.ArrayList;

/**
 *
 * @author katie
 */
public class WekaPrep_3_V1 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {

        WekaPrep.inputDataRoot = "../3 complete 5.19.2012/csv_data/";
        WekaPrep.outputDataRoot = "../3 complete 5.19.2012/weka_data/";

        //Basic config
        WekaPrep.bagConfigs.add(new BagOfWordsConfig(false, false, 1, false, false, false));
        
        //One change each
        WekaPrep.bagConfigs.add(new BagOfWordsConfig(true, false, 1, false, false, false));
        WekaPrep.bagConfigs.add(new BagOfWordsConfig(false, true, 1, false, false, false));
        WekaPrep.bagConfigs.add(new BagOfWordsConfig(false, false, 4, false, false, false));
        WekaPrep.bagConfigs.add(new BagOfWordsConfig(false, false, 10, false, false, false)); //a higher frequency
        WekaPrep.bagConfigs.add(new BagOfWordsConfig(false, false, 1, true, false, false));
        WekaPrep.bagConfigs.add(new BagOfWordsConfig(false, false, 1, false, true, false));
        WekaPrep.bagConfigs.add(new BagOfWordsConfig(false, false, 1, false, true, true));
        
        //Remove one at a time
        //Throw everything at it (except stopwords)
        WekaPrep.bagConfigs.add(new BagOfWordsConfig(true, false, 4, true, true, true));
        //Everything except normalizing
        WekaPrep.bagConfigs.add(new BagOfWordsConfig(true, false, 4, true, true, false));
        //Everything except tfidf
        WekaPrep.bagConfigs.add(new BagOfWordsConfig(true, false, 4, true, false, true));
        //Everything except counts
        WekaPrep.bagConfigs.add(new BagOfWordsConfig(true, false, 4, false, true, true));
        //Everything except min frequency
        WekaPrep.bagConfigs.add(new BagOfWordsConfig(true, false, 1, true, true, true));
        //Everything except stemming
        WekaPrep.bagConfigs.add(new BagOfWordsConfig(false, true, 4, true, true, true));
        
        ArrayList<DataConfig> configurations = new ArrayList<DataConfig>();
        
        String prefix = "set3_1";
        
        String[] codeList = new String[]{
            "annoyance", "apprehension", "confusion", "excitement", "frustration", "happiness", "relief", "serenity", "supportive"
        };
        for (int i = 0; i < codeList.length; i++) {
            String code = codeList[i];
            configurations.addAll(WekaPrep.getData(prefix + ".aggregated.bycode." + code + ".csv", code, prefix, code));
        }

        configurations.addAll(WekaPrep.getData(prefix + ".aggregated.bysent.neg.csv", "neg", prefix, "neg"));
        configurations.addAll(WekaPrep.getData(prefix + ".aggregated.bysent.pos.csv", "pos", prefix, "pos"));

        for (DataConfig config : configurations) {
            WekaPrep.putData(config);
        }
    }
}
