package org.spiderland.Psh.TestCase;

import org.spiderland.Psh.ObjectPair;

/**
 * A class allowing for the runtime creation of custom test cases.
 * 
 * Each test case is a dictionary of HashMap< String, Object >. Each entry in
 * the dictionary corresponds to a problem input, except for the special token
 * "output", which is reserved for the problem output.
 */

abstract public class TestCaseGenerator {

	/**
	 * @returns The number of cases the generator will create.
	 */
	abstract public int TestCaseCount();

	/**
	 * @returns Test case at index n as an ObjectPair, where _first is the input
	 *          and _second is the output. The types of the objects may depend
	 *          on the problem type.
	 */
	abstract public ObjectPair TestCase(int inIndex);

}
