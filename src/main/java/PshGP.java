import org.spiderland.Psh.GA;
import org.spiderland.Psh.Params;
import org.spiderland.Psh.Program;
import org.spiderland.Psh.PushGP;

import java.io.File;

/**
 * PshGP executes a genetic programming run using the given parameter file. More
 * information about parameter files can be found in the README.
 */
public class PshGP {
    public static void main(String[] args) throws Exception {

        if (args.length != 1 && args.length != 3) {
            System.out.println("Usage: PshGP paramfile|checkpointfile.gz [testprogram testcasenumber]");
            System.exit(0);
        }

        GA ga = null;
        if (args[0].endsWith(".gz"))
            ga = GA.GAWithCheckpoint(args[0]);
        else
            ga = GA.GAWithParameters(Params.ReadFromFile(new File(args[0])));

        if (args.length == 3) {
            // Execute a test program

            Program p = new Program(args[1]);
            ((PushGP) ga).RunTestProgram(p, Integer.parseInt(args[2]));
        } else {
            // Execute a GP run.

            ga.Run();
        }
    }
}
