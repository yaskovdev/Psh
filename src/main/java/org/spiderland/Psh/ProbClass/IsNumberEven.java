package org.spiderland.Psh.ProbClass;

import org.spiderland.Psh.BooleanStack;
import org.spiderland.Psh.GAIndividual;
import org.spiderland.Psh.GATestCase;
import org.spiderland.Psh.IntStack;
import org.spiderland.Psh.Interpreter;
import org.spiderland.Psh.PushGP;
import org.spiderland.Psh.PushGPIndividual;

public class IsNumberEven extends PushGP {

    @Override
    protected void InitInterpreter(final Interpreter interpreter) {
    }

    protected void InitFromParameters() throws Exception {
        super.InitFromParameters();
        for (int i = 0; i < 20; i++) {
            _testCases.add(new GATestCase(i, i % 2 == 0));
        }
    }

    @Override
    public float EvaluateTestCase(final GAIndividual individual, final Object input, final Object output) {
        _interpreter.clearStacks();
        final IntStack intStack = _interpreter.intStack();
        intStack.push((Integer) input);
        _interpreter.Execute(((PushGPIndividual) individual)._program, _executionLimit);
        final BooleanStack booleanStack = _interpreter.boolStack();
        final boolean expected = (boolean) output;
        final boolean actual = booleanStack.pop();
        return expected == actual ? 0 : 1000;
    }
}
