package org.spiderland.Psh.ProbClass;

import org.spiderland.Psh.FloatStack;
import org.spiderland.Psh.GAIndividual;
import org.spiderland.Psh.GATestCase;
import org.spiderland.Psh.Interpreter;
import org.spiderland.Psh.Program;
import org.spiderland.Psh.PushGP;
import org.spiderland.Psh.PushGPIndividual;

public class FloatClassification extends PushGP {
    private static final long serialVersionUID = 1L;

    float currentInput;
    int inputCount;

    protected void initFromParameters() throws Exception {
        super.initFromParameters();

        String cases = getParam("test-cases");

        Program testCases = new Program(cases);

        inputCount = ((Program) testCases.peek(0)).size() - 1;

        for (int i = 0; i < testCases.size(); i++) {
            Program p = (Program) testCases.peek(i);

            if (p.size() < 2)
                throw new Exception("Not enough entries for fitness case \""
                        + p + "\"");

            if (p.size() != inputCount + 1)
                throw new Exception(
                        "Wrong number of inputs for fitness case \"" + p + "\"");

            Float in = Float.valueOf(p.peek(0).toString());
            Float out = Float.valueOf(p.peek(1).toString());

            print(";; Fitness case #" + i + " input: " + in + " output: " + out + "\n");

            this.testCases.add(new GATestCase(in, out));
        }
    }

    protected void initInterpreter(Interpreter inInterpreter) {
    }

    public float evaluateTestCase(GAIndividual inIndividual, Object inInput, Object inOutput) {
        interpreter.clearStacks();

        currentInput = (Float) inInput;

        FloatStack stack = interpreter.floatStack();

        stack.push(currentInput);

        interpreter.execute(((PushGPIndividual) inIndividual).program, executionLimit);

        float result = stack.top();
        return result - ((Float) inOutput);
    }

}
