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
public class WekaPrep_1_V3 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {

        WekaPrep.inputDataRoot = "../1 pos neg vs details/csv_data/";
        WekaPrep.outputDataRoot = "../1 pos neg vs details/weka_data/";

        ArrayList<DataConfig> configurations = new ArrayList<DataConfig>();
        configurations.addAll(WekaPrep.getData("set3.aggregated.codes.csv", "code", "set3", "code"));
        configurations.addAll(WekaPrep.getData("set3.aggregated.sentiments.csv", "sentiment", "set3", "sentiment"));

        String[] codeList = new String[]{
            "annoyance", "apprehension", "confusion", "excitement", "frustration", "happiness", "relief", "serenity", "supportive"
        };
        for (int i = 0; i < codeList.length; i++) {
            String code = codeList[i];
            configurations.addAll(WekaPrep.getData("set4.aggregated.bycode." + code + ".csv", code, "set4", code));
        }

        configurations.addAll(WekaPrep.getData("set4.aggregated.bysent.neg.csv", "neg", "set4", "neg"));
        configurations.addAll(WekaPrep.getData("set4.aggregated.bysent.pos.csv", "pos", "set4", "pos"));

        for (DataConfig config : configurations) {
            WekaPrep.putData(config);
        }
    }
}
