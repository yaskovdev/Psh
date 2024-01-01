package org.spiderland.Psh.ProbClass;

import org.spiderland.Psh.GAIndividual;
import org.spiderland.Psh.GATestCase;
import org.spiderland.Psh.IntStack;
import org.spiderland.Psh.Interpreter;
import org.spiderland.Psh.ObjectPair;
import org.spiderland.Psh.Program;
import org.spiderland.Psh.PushGP;
import org.spiderland.Psh.PushGPIndividual;
import org.spiderland.Psh.TestCase.TestCaseGenerator;

/**
 * This problem class implements symbolic regression for integers. See also
 * IntSymbolicRegression for integer symbolic regression.
 */
public class IntSymbolicRegression extends PushGP {
    private static final long serialVersionUID = 1L;

    protected float _noResultPenalty = 1000;

    protected void initFromParameters() throws Exception {
        super.initFromParameters();

        String cases = getParam("test-cases", true);
        String casesClass = getParam("test-case-class", true);
        if (cases == null && casesClass == null) {
            throw new Exception("No acceptable test-case parameter.");
        }

        if (casesClass != null) {
            // Get test cases from the TestCasesClass.
            Class<?> iclass = Class.forName(casesClass);
            Object iObject = iclass.newInstance();
            if (!(iObject instanceof TestCaseGenerator testCaseGenerator)) {
                throw (new Exception(
                        "test-case-class must inherit from class TestCaseGenerator"));
            }

            int numTestCases = testCaseGenerator.testCaseCount();

            for (int i = 0; i < numTestCases; i++) {
                ObjectPair testCase = testCaseGenerator.testCase(i);

                Integer in = (Integer) testCase.first();
                Integer out = (Integer) testCase.second();

                print(";; Fitness case #" + i + " input: " + in + " output: "
                        + out + "\n");

                testCases.add(new GATestCase(in, out));
            }
        } else {
            // Get test cases from test-cases.
            Program testCases = new Program(cases);

            for (int i = 0; i < testCases.size(); i++) {
                Program p = (Program) testCases.peek(i);

                if (p.size() < 2)
                    throw new Exception("Not enough elements for fitness case \""
                            + p + "\"");

                Integer in = Integer.valueOf(p.peek(0).toString());
                Integer out = Integer.valueOf(p.peek(1).toString());

                print(";; Fitness case #" + i + " input: " + in + " output: " + out
                        + "\n");

                this.testCases.add(new GATestCase(in, out));
            }
        }

    }

    protected void initInterpreter(Interpreter inInterpreter) {
    }

    public float evaluateTestCase(GAIndividual inIndividual, Object inInput,
            Object inOutput) {
        interpreter.clearStacks();

        int currentInput = (Integer) inInput;

        IntStack stack = interpreter.intStack();

        stack.push(currentInput);

        // Must be included in order to use the input stack.
        interpreter.inputStack().push(currentInput);

        interpreter.execute(((PushGPIndividual) inIndividual).program, executionLimit);

        int result = stack.top();
        // System.out.println( _interpreter + " " + result );

        // Penalize individual if there is no result on the stack.
        if (stack.size() == 0) {
            return _noResultPenalty;
        }

        return result - ((Integer) inOutput);
    }

}
