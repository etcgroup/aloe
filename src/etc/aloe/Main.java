package etc.aloe;

import java.io.File;
import java.util.Arrays;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

/**
 *
 * @author kuksenok
 */
public class Main {

    private void printUsage() {
        System.err.println("For Help: java -jar aloe.jar MODE_NAME --help");
        System.err.println("List of modes: ");
        for (ModeName cmd : ModeName.values()) {
            System.err.println("\t" + cmd.name());
        }
    }

    private void run(String[] args) {

        Aloe aloe = null;
        switch (mode) {
            case Train:
                aloe = new AloeTrain();
                break;
            case Label:
                aloe = new AloeLabel();
                break;
            case Interactive:
                aloe = new AloeInteractive();
                break;
        }

        CmdLineParser parser = new CmdLineParser(aloe);

        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            aloe.printUsage();
            parser.printUsage(System.err);
            return;
        }

        aloe.run();
    }

    private static enum ModeName {

        Train,
        Label,
        Interactive
    }
    @Argument(index = 0, usage = "mode", required = true, metaVar = "MODE_NAME")
    private ModeName mode;

    public static void main(String[] args) {

        String[] restOfArgs = Arrays.copyOfRange(args, 1, args.length);
        String[] firstArgs = Arrays.copyOfRange(args, 0, 1);

        //Parse the command line arguments
        Main main = new Main();
        CmdLineParser parser = new CmdLineParser(main);

        try {
            parser.parseArgument(firstArgs);
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            main.printUsage();
            return;
        }

        //And go!

        main.run(restOfArgs);
    }
}
