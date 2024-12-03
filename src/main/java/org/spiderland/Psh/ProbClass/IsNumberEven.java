package org.spiderland.Psh.ProbClass;

import org.spiderland.Psh.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IsNumberEven extends PushGP {

    public static final int MAX_ERROR = 1000;

    @Override
    protected void initInterpreter(final Interpreter interpreter) {
    }

    protected void initFromParameters() throws Exception {
        super.initFromParameters();
        testCases = sampleTestCases(-1000000000, 1000000000, 50);
    }

    @Override
    public float evaluateTestCase(final GAIndividual individual, final Object input, final Object output) {
        interpreter.clearStacks();
        final IntStack intStack = interpreter.intStack();
        intStack.push((Integer) input);
        interpreter.execute(((PushGPIndividual) individual).program, executionLimit);
        final BooleanStack booleanStack = interpreter.boolStack();
        if (booleanStack.size() == 0) {
            return MAX_ERROR;
        } else {
            final boolean expected = (boolean) output;
            final boolean actual = booleanStack.pop();
            return expected == actual ? 0 : MAX_ERROR;
        }
    }

    @SuppressWarnings("SameParameterValue")
    private List<GATestCase> sampleTestCases(final int min, final int max, final int numberOfSamples) {
        final Set<Integer> integers = new HashSet<>();
        while (integers.size() < numberOfSamples) {
            integers.add(random.nextInt(max - min) + min);
        }
        return integers
                .stream()
                .map(it -> new GATestCase(it, it % 2 == 0))
                .toList();
    }
}
