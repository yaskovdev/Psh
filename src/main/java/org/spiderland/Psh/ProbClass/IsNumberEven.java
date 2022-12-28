package org.spiderland.Psh.ProbClass;

import org.spiderland.Psh.BooleanStack;
import org.spiderland.Psh.GAIndividual;
import org.spiderland.Psh.GATestCase;
import org.spiderland.Psh.IntStack;
import org.spiderland.Psh.Interpreter;
import org.spiderland.Psh.PushGP;
import org.spiderland.Psh.PushGPIndividual;

import static java.util.stream.IntStream.range;

public class IsNumberEven extends PushGP {

    public static final int MAX_ERROR = 1000;

    @Override
    protected void initInterpreter(final Interpreter interpreter) {
    }

    protected void initFromParameters() throws Exception {
        super.initFromParameters();
        testCases = range(0, 100)
                .mapToObj(it -> new GATestCase(it, it % 2 == 0))
                .toList();
    }

    @Override
    public float evaluateTestCase(final GAIndividual individual, final Object input, final Object output) {
        interpreter.clearStacks();
        final IntStack intStack = interpreter.intStack();
        intStack.push((Integer) input);
        interpreter.execute(((PushGPIndividual) individual)._program, executionLimit);
        final BooleanStack booleanStack = interpreter.boolStack();
        if (booleanStack.size() == 0) {
            return MAX_ERROR;
        } else {
            final boolean expected = (boolean) output;
            final boolean actual = booleanStack.pop();
            return expected == actual ? 0 : MAX_ERROR;
        }
    }
}
