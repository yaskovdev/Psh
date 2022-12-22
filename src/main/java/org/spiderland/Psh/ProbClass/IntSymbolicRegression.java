package org.spiderland.Psh.ProbClass;

import org.spiderland.Psh.GAIndividual;
import org.spiderland.Psh.GATestCase;
import org.spiderland.Psh.Interpreter;
import org.spiderland.Psh.ObjectPair;
import org.spiderland.Psh.Program;
import org.spiderland.Psh.PushGP;
import org.spiderland.Psh.PushGPIndividual;
import org.spiderland.Psh.IntStack;
import org.spiderland.Psh.TestCase.TestCaseGenerator;

/**
 * This problem class implements symbolic regression for integers. See also
 * IntSymbolicRegression for integer symbolic regression.
 */
public class IntSymbolicRegression extends PushGP {
	private static final long serialVersionUID = 1L;
	
	protected float _noResultPenalty = 1000;

	protected void InitFromParameters() throws Exception {
		super.InitFromParameters();

		String cases = GetParam("test-cases", true);
		String casesClass = GetParam("test-case-class", true);
		if (cases == null && casesClass == null) {
			throw new Exception("No acceptable test-case parameter.");
		}

		if (casesClass != null) {
			// Get test cases from the TestCasesClass.
			Class<?> iclass = Class.forName(casesClass);
			Object iObject = iclass.newInstance();
			if (!(iObject instanceof TestCaseGenerator testCaseGenerator)) {
				throw (new Exception(
						"test-case-class must inherit from class TestCaseGenerator"));
			}

            int numTestCases = testCaseGenerator.TestCaseCount();

			for (int i = 0; i < numTestCases; i++) {
				ObjectPair testCase = testCaseGenerator.TestCase(i);

				Integer in = (Integer) testCase._first;
				Integer out = (Integer) testCase._second;

				Print(";; Fitness case #" + i + " input: " + in + " output: "
						+ out + "\n");

				_testCases.add(new GATestCase(in, out));
			}
		} else {
			// Get test cases from test-cases.
			Program caselist = new Program(_interpreter, cases);
	
			for (int i = 0; i < caselist.size(); i++) {
				Program p = (Program) caselist.peek(i);
	
				if (p.size() < 2)
					throw new Exception("Not enough elements for fitness case \""
							+ p + "\"");
	
				Integer in = Integer.valueOf(p.peek(0).toString());
				Integer out = Integer.valueOf(p.peek(1).toString());
	
				Print(";; Fitness case #" + i + " input: " + in + " output: " + out
						+ "\n");
	
				_testCases.add(new GATestCase(in, out));
			}
		}
		
	}

	protected void InitInterpreter(Interpreter inInterpreter) {
	}

	public float EvaluateTestCase(GAIndividual inIndividual, Object inInput,
			Object inOutput) {
		_interpreter.clearStacks();

		int currentInput = (Integer) inInput;

		IntStack stack = _interpreter.intStack();

		stack.push(currentInput);

		// Must be included in order to use the input stack.
		_interpreter.inputStack().push(currentInput);

		_interpreter.Execute(((PushGPIndividual) inIndividual)._program,
				_executionLimit);

		int result = stack.top();
		// System.out.println( _interpreter + " " + result );

		// Penalize individual if there is no result on the stack.
		if(stack.size() == 0){
			return _noResultPenalty;
		}
		
		return result - ((Integer) inOutput);
	}

}
