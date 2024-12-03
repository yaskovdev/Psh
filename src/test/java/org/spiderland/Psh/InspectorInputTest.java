package org.spiderland.Psh;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class InspectorInputTest {

    @Test
    public void shouldReadMultiLinePushProgram() throws Exception {
        var pushProgramFile = TestUtil.getFileFromResource("MultiLineProgram.push");
        var instanceUnderTest = new InspectorInput(pushProgramFile);
        assertThat(instanceUnderTest.executionLimit, is(10000000));
        assertThat(instanceUnderTest.interpreter.intStack.peek(0), is(-7));
        instanceUnderTest.interpreter.Step(instanceUnderTest.executionLimit);
        assertThat(instanceUnderTest.interpreter.boolStack.peek(0), is(false));
    }
}