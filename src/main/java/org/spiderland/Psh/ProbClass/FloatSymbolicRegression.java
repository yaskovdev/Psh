package org.spiderland.Psh.ProbClass;

import org.spiderland.Psh.FloatStack;
import org.spiderland.Psh.GAIndividual;
import org.spiderland.Psh.GATestCase;
import org.spiderland.Psh.Interpreter;
import org.spiderland.Psh.ObjectPair;
import org.spiderland.Psh.Program;
import org.spiderland.Psh.PushGP;
import org.spiderland.Psh.PushGPIndividual;
import org.spiderland.Psh.TestCase.TestCaseGenerator;

/**
 * This problem class implements symbolic regression for floating point numbers.
 * See also IntSymbolicRegression for integer symbolic regression.
 */
public class FloatSymbolicRegression extends PushGP {
    private static final long serialVersionUID = 1L;

    private final float noResultPenalty = 10000;

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

                Float in = (Float) testCase.first();
                Float out = (Float) testCase.second();

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
                    throw new Exception(
                            "Not enough elements for fitness case \"" + p
                                    + "\"");

                Float in = Float.valueOf(p.peek(0).toString());
                Float out = Float.valueOf(p.peek(1).toString());

                print(";; Fitness case #" + i + " input: " + in + " output: " + out + "\n");

                this.testCases.add(new GATestCase(in, out));
            }
        }

    }

    protected void initInterpreter(Interpreter inInterpreter) {
    }

    public float evaluateTestCase(GAIndividual inIndividual, Object inInput,
            Object inOutput) {
        interpreter.clearStacks();

        float currentInput = (Float) inInput;

        FloatStack stack = interpreter.floatStack();

        stack.push(currentInput);

        // Must be included in order to use the input stack.
        interpreter.inputStack().push(currentInput);

        interpreter.execute(((PushGPIndividual) inIndividual).program,
                executionLimit);

        float result = stack.top();

        // Penalize individual if there is no result on the stack.
        if (stack.size() == 0) {
            return noResultPenalty;
        }

        return result - ((Float) inOutput);
    }

    public float GetIndividualTestCaseResult(GAIndividual inIndividual, GATestCase inTestCase) {
        interpreter.clearStacks();

        float currentInput = (Float) inTestCase.input();

        FloatStack stack = interpreter.floatStack();

        stack.push(currentInput);

        // Must be included in order to use the input stack.
        interpreter.inputStack().push(currentInput);

        interpreter.execute(((PushGPIndividual) inIndividual).program,
                executionLimit);

        float result = stack.top();

        // If no result, return 0
        if (stack.size() == 0) {
            return 0;
        }

        return result;
    }

    protected boolean success() {
        return bestMeanFitness <= 0.1;
    }

}
