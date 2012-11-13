package etc.aloe;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

/**
 *
 * @author kuksenok
 */
public class Main {

    public static void main(String[] args) {

        args = new String[] {
            "-t", "30",
            "-i", "example.csv"
        };

        Aloe aloe = new Aloe();

        //Parse the command line arguments
        CmdLineParser parser = new CmdLineParser(aloe);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            System.err.println("java -jar aloe.jar [options...] arguments...");
            parser.printUsage(System.err);
            return;
        }

        //And go!
        aloe.run();
    }
}
