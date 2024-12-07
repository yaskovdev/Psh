package org.spiderland.Psh;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

class IntStackTest {

    @Test
    void shoveShiftsElementsTowardsTopOfStack() {
        var instanceUnderTest = new IntStack();
        instanceUnderTest.push(0);
        instanceUnderTest.push(-1);
        instanceUnderTest.shove(1);

        assertThat(instanceUnderTest.pop(), is(0));
        assertThat(instanceUnderTest.pop(), is(-1));
    }
}
