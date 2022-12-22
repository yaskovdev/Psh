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

    float _currentInput;
    int _inputCount;

    protected void InitFromParameters() throws Exception {
        super.InitFromParameters();

        String cases = GetParam("test-cases");

        Program caselist = new Program(_interpreter, cases);

        _inputCount = ((Program) caselist.peek(0)).size() - 1;

        for (int i = 0; i < caselist.size(); i++) {
            Program p = (Program) caselist.peek(i);

            if (p.size() < 2)
                throw new Exception("Not enough entries for fitness case \""
                        + p + "\"");

            if (p.size() != _inputCount + 1)
                throw new Exception(
                        "Wrong number of inputs for fitness case \"" + p + "\"");

            Float in = Float.valueOf(p.peek(0).toString());
            Float out = Float.valueOf(p.peek(1).toString());

            Print(";; Fitness case #" + i + " input: " + in + " output: " + out + "\n");

            _testCases.add(new GATestCase(in, out));
        }
    }

    protected void InitInterpreter(Interpreter inInterpreter) {
    }

    public float EvaluateTestCase(GAIndividual inIndividual, Object inInput, Object inOutput) {
        _interpreter.clearStacks();

        _currentInput = (Float) inInput;

        FloatStack stack = _interpreter.floatStack();

        stack.push(_currentInput);

        _interpreter.Execute(((PushGPIndividual) inIndividual)._program, _executionLimit);

        float result = stack.top();
        return result - ((Float) inOutput);
    }

}
