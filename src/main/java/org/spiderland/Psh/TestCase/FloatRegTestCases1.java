package org.spiderland.Psh.TestCase;

import org.spiderland.Psh.ObjectPair;

public class FloatRegTestCases1 implements TestCaseGenerator {

    private static final int testCaseCount = 200;
    private static final float firstSample = -10;
    private static final float lastSample = 10;

    private float[] testCasesX = null;
    private float[] testCasesY = null;

    @Override
    public int testCaseCount() {
        return testCaseCount;
    }

    @Override
    public ObjectPair testCase(int inIndex) {
        if (testCasesX == null) {
            testCasesX = new float[testCaseCount];
            testCasesY = new float[testCaseCount];

            for (int i = 0; i < testCaseCount; i++) {
                testCasesX[i] = XValue(i);
                testCasesY[i] = TestCaseFunction(testCasesX[i]);
            }
        }

        return new ObjectPair(testCasesX[inIndex], testCasesY[inIndex]);
    }

    private float XValue(float i) {
        return firstSample
                + (((lastSample - firstSample) / (testCaseCount - 1)) * i);
    }

    private float TestCaseFunction(float x) {
        // y = 3x^2 + 4x
        return (3 * x * x) + (4 * x);
    }

}
