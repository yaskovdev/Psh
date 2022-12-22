package org.spiderland.Psh.TestCase;

import org.spiderland.Psh.ObjectPair;

public class FloatRegTestCases1 implements TestCaseGenerator {

    private static final int _testCaseCount = 200;
    private static final float _firstSample = -10;
    private static final float _lastSample = 10;

    private float[] _testCasesX = null;
    private float[] _testCasesY = null;

    @Override
    public int testCaseCount() {
        return _testCaseCount;
    }

    @Override
    public ObjectPair testCase(int inIndex) {
        if (_testCasesX == null) {
            _testCasesX = new float[_testCaseCount];
            _testCasesY = new float[_testCaseCount];

            for (int i = 0; i < _testCaseCount; i++) {
                _testCasesX[i] = XValue(i);
                _testCasesY[i] = TestCaseFunction(_testCasesX[i]);
            }
        }

        return new ObjectPair(_testCasesX[inIndex], _testCasesY[inIndex]);
    }

    private float XValue(float i) {
        return _firstSample
                + (((_lastSample - _firstSample) / (_testCaseCount - 1)) * i);
    }

    private float TestCaseFunction(float x) {
        // y = 3x^2 + 4x
        return (3 * x * x) + (4 * x);
    }

}
