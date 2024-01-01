package org.spiderland.Psh.TestCase;

import org.spiderland.Psh.ObjectPair;

public class FloatRegTestCases2 implements TestCaseGenerator {

    private static final int testCaseCount = 200;
    private float[] testCasesX = null;
    private float[] testCasesY = null;

    private static final float firstSample = -3;
    private static final float lastSample = 3;

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
                testCasesX[i] = xValue(i);
                testCasesY[i] = testCaseFunction(testCasesX[i]);
            }
        }

        return new ObjectPair(testCasesX[inIndex], testCasesY[inIndex]);
    }

    private float xValue(float i) {
        return firstSample
                + (((lastSample - firstSample) / (testCaseCount - 1)) * i);
    }

    private float testCaseFunction(float x) {
        return (float) (Math.exp(Math.abs(x)) * Math.sin(2.0 * Math.PI * x));
    }

}
