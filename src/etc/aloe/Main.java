package etc.aloe;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

/**
 *
 * @author kuksenok
 */
public class Main {

    public static void main(String[] args) {

        Aloe aloe = new Aloe();

        //Parse the command line arguments
        CmdLineParser parser = new CmdLineParser(aloe);

        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            aloe.printUsage();
            parser.printUsage(System.err);
            return;
        }

        //And go!
        try {
            aloe.run();
        } catch (CmdLineException e) {
            aloe.printUsage();
            parser.printUsage(System.err);
        }
    }
}
