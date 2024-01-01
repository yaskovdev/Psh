import org.spiderland.Psh.GeneticAlgorithm;
import org.spiderland.Psh.InspectorInput;
import org.spiderland.Psh.Interpreter;
import org.spiderland.Psh.Params;
import org.spiderland.Psh.Program;
import org.spiderland.Psh.PushGP;

import java.io.File;

public class Main {

    public static void main(final String[] args) throws Exception {
        if (args.length == 0) {
            printUsage();
        } else if (args[0].endsWith("push")) {
            interpretPushProgram(args);
        } else {
            runGeneticProgramming(args);
        }
    }

    private static void printUsage() {
        System.out.println("Usage:");
        System.out.println("java -jar psh.jar IsNumberEven.push");
        System.out.println("java -jar psh.jar IsNumberEven.pushgp [testprogram testcasenumber]");
        System.out.println("java -jar psh.jar IsNumberEven3.gz [testprogram testcasenumber]");
    }

    /**
     * PshInspector pshFile
     * <p>
     * PshInspector can be used to inspect the execution of a Psh program.
     * After every step of the program, the stacks of the interpreter
     * are displayed. The Psh program is given to PshInspector through
     * the file pshFile. pshFile is an input file containing the
     * following, separated by new lines:
     * <p>
     * - Program: The psh program to run
     * - ExecutionLimit: Maximum execution steps
     * - Input(optional): Any inputs to be pushed before execution,
     * separated by spaces. Note: Only int, float, and
     * boolean inputs are accepted.
     */
    private static void interpretPushProgram(final String[] args) throws Exception {
        // input will allow easy initialization of the interpreter.
        InspectorInput input = new InspectorInput(args[0]);
        Interpreter interpreter = input.getInterpreter();

        int executionLimit = input.getExecutionLimit();
        int executed = 0;
        int stepsTaken = 1;
        String stepPrint = "";

        // Print registered instructions
        System.out.println("Registered Instructions: "
                + interpreter.getRegisteredInstructionsString() + "\n");

        // Run the Psh Inspector
        System.out.println("====== State after " + executed + " steps ======");
        interpreter.printStacks();

        while (executed < executionLimit && stepsTaken == 1) {
            executed += 1;

            // Create output string
            if (executed == 1)
                stepPrint = "====== State after " + executed + " step ";
            else
                stepPrint = "====== State after " + executed + " steps ";

            stepPrint += "(last step: ";
            Object execTop = interpreter.execStack().top();

            if (execTop instanceof Program)
                stepPrint += "(...)";
            else
                stepPrint += execTop;

            stepPrint += ") ======";

            // Execute 1 instruction
            stepsTaken = interpreter.Step(1);

            if (stepsTaken == 1) {
                System.out.println(stepPrint);
                interpreter.printStacks();
            }
        }
    }

    /**
     * Executes a genetic programming run using the given parameter file. More
     * information about parameter files can be found in the README.
     */
    private static void runGeneticProgramming(final String[] args) throws Exception {
        GeneticAlgorithm geneticAlgorithm = args[0].endsWith(".gz") ? GeneticAlgorithm.gaWithCheckpoint(args[0]) : GeneticAlgorithm.gaWithParameters(Params.readFromFile(new File(args[0])));

        if (args.length == 3) {
            // Execute a test program

            Program p = new Program(args[1]);
            ((PushGP) geneticAlgorithm).runTestProgram(p, Integer.parseInt(args[2]));
        } else {
            // Execute a GP run.

            geneticAlgorithm.run();
        }
    }
}
