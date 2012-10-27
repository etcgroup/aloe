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
public class WekaPrep_2_V1 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {

        WekaPrep.inputDataRoot = "../2 update 5.8.2012/csv_data/";
        WekaPrep.outputDataRoot = "../2 update 5.8.2012/weka_data/";

        ArrayList<DataConfig> configurations = new ArrayList<DataConfig>();
        
        String[] codeList = new String[]{
            "annoyance", "apprehension", "confusion", "excitement", "frustration", "happiness", "relief", "serenity", "supportive"
        };
        for (int i = 0; i < codeList.length; i++) {
            String code = codeList[i];
            configurations.addAll(WekaPrep.getData("set2_1.aggregated.bycode." + code + ".csv", code, "set2_1", code));
        }

        configurations.addAll(WekaPrep.getData("set2_1.aggregated.bysent.neg.csv", "neg", "set2_1", "neg"));
        configurations.addAll(WekaPrep.getData("set2_1.aggregated.bysent.pos.csv", "pos", "set2_1", "pos"));

        for (DataConfig config : configurations) {
            WekaPrep.putData(config);
        }
    }
}
