package org.spiderland.Psh.TestCase;

import org.spiderland.Psh.ObjectPair;

/**
 * A class allowing for the runtime creation of custom test cases.
 * <p>
 * Each test case is a dictionary of HashMap< String, Object >. Each entry in
 * the dictionary corresponds to a problem input, except for the special token
 * "output", which is reserved for the problem output.
 */

public interface TestCaseGenerator {

    /**
     * @return The number of cases the generator will create.
     */
    int testCaseCount();

    /**
     * @return Test case at index n as an ObjectPair, where first is the input
     * and second is the output. The types of the objects may depend
     * on the problem type.
     */
    ObjectPair testCase(int inIndex);
}
