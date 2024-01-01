package org.spiderland.Psh.TestCase;

import org.spiderland.Psh.ObjectPair;

public class IntRegTestCases1 implements TestCaseGenerator {

    private static final int testCaseCount = 200;
    private int[] testCasesX = null;
    private int[] testCasesY = null;

    private static final int firstSample = -99;
    private static final int stepSize = 1;

    @Override
    public int testCaseCount() {
        return testCaseCount;
    }

    @Override
    public ObjectPair testCase(int inIndex) {
        if (testCasesX == null) {
            testCasesX = new int[testCaseCount];
            testCasesY = new int[testCaseCount];

            for (int i = 0; i < testCaseCount; i++) {
                testCasesX[i] = XValue(i);
                testCasesY[i] = TestCaseFunction(testCasesX[i]);
            }
        }

        return new ObjectPair(testCasesX[inIndex], testCasesY[inIndex]);
    }

    private int XValue(int i) {
        return firstSample + (stepSize * i);
    }

    private int TestCaseFunction(int x) {
        // y = 9x^3 + 24x^2 - 3x + 10
        return (9 * x * x * x) + (24 * x * x)
                - (3 * x) + 10;
    }

}
