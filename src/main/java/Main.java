import org.spiderland.Psh.GA;
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
        // _input will allow easy initialization of the interpreter.
        InspectorInput _input = new InspectorInput(args[0]);
        Interpreter _interpreter = _input.getInterpreter();

        int _executionLimit = _input.getExecutionLimit();
        int executed = 0;
        int stepsTaken = 1;
        String stepPrint = "";

        // Print registered instructions
        System.out.println("Registered Instructions: "
                + _interpreter.getRegisteredInstructionsString() + "\n");

        // Run the Psh Inspector
        System.out.println("====== State after " + executed + " steps ======");
        _interpreter.printStacks();

        while (executed < _executionLimit && stepsTaken == 1) {
            executed += 1;

            // Create output string
            if (executed == 1)
                stepPrint = "====== State after " + executed + " step ";
            else
                stepPrint = "====== State after " + executed + " steps ";

            stepPrint += "(last step: ";
            Object execTop = _interpreter.execStack().top();

            if (execTop instanceof Program)
                stepPrint += "(...)";
            else
                stepPrint += execTop;

            stepPrint += ") ======";

            // Execute 1 instruction
            stepsTaken = _interpreter.Step(1);

            if (stepsTaken == 1) {
                System.out.println(stepPrint);
                _interpreter.printStacks();
            }
        }
    }

    /**
     * Executes a genetic programming run using the given parameter file. More
     * information about parameter files can be found in the README.
     */
    private static void runGeneticProgramming(final String[] args) throws Exception {
        GA ga = null;
        if (args[0].endsWith(".gz"))
            ga = GA.gaWithCheckpoint(args[0]);
        else
            ga = GA.gaWithParameters(Params.readFromFile(new File(args[0])));

        if (args.length == 3) {
            // Execute a test program

            Program p = new Program(args[1]);
            ((PushGP) ga).runTestProgram(p, Integer.parseInt(args[2]));
        } else {
            // Execute a GP run.

            ga.run();
        }
    }
}
