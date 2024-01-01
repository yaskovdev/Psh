package org.spiderland.Psh.ProbClass;

import org.spiderland.Psh.BooleanStack;
import org.spiderland.Psh.FloatStack;
import org.spiderland.Psh.GAIndividual;
import org.spiderland.Psh.GATestCase;
import org.spiderland.Psh.Interpreter;
import org.spiderland.Psh.ObjectPair;
import org.spiderland.Psh.ObjectStack;
import org.spiderland.Psh.Program;
import org.spiderland.Psh.PushGP;
import org.spiderland.Psh.PushGPIndividual;

/**
 * A sample problem class for testing the cart centering problem. This solves
 * the cart centering problem as described in John Koza's Genetic Programming,
 * chapter 7.1. In this problem, a cart is placed on a 1-dimensional,
 * frictionless track. At every time, the cart has a position and velocity on
 * the track. The problem is to stop the cart at the origin (within reasonable
 * approximations) by applying a fixed-magnitude force to accelerate the cart
 * in the forward or backward direction.
 * <p>
 * Note: Cart centering does not yet support test case generators.
 */
public class CartCentering extends PushGP {
    private static final long serialVersionUID = 1L;

    protected void initFromParameters() throws Exception {
        super.initFromParameters();

        String cases = getParam("test-cases");

        Program testCases = new Program(cases);

        for (int i = 0; i < testCases.size(); i++) {
            Program singleCase = (Program) testCases.peek(i);

            if (singleCase.size() < 2)
                throw new Exception("Not enough elements for fitness case \""
                        + singleCase + "\"");

            Float x = Float.valueOf(singleCase.peek(0).toString());
            Float v = Float.valueOf(singleCase.peek(1).toString());

            print(";; Fitness case #" + i + " position: " + x + " velocity: " + v
                    + "\n");

            ObjectPair xv = new ObjectPair(x, v);

            this.testCases.add(new GATestCase(xv, null));
        }
    }

    protected void initInterpreter(Interpreter inInterpreter) {
    }

    public float evaluateTestCase(GAIndividual inIndividual, Object inInput,
            Object inOutput) {

        int timeSteps = 1000;
        float timeDiscritized = 0.01f;
        float maxTime = timeSteps * timeDiscritized;

        float captureRadius = 0.01f;

        ObjectPair xv = (ObjectPair) inInput;
        float position = (Float) xv.first();
        float velocity = (Float) xv.second();

        for (int step = 1; step <= timeSteps; step++) {
            interpreter.clearStacks();

            FloatStack fStack = interpreter.floatStack();
            BooleanStack bStack = interpreter.boolStack();
            ObjectStack iStack = interpreter.inputStack();

            // Position will be on the top of the stack, and velocity will be
            // second.
            fStack.push(position);
            fStack.push(velocity);

            // Must be included in order to use the input stack. Uses same order
            // as inputs on Float stack.
            iStack.push(position);
            iStack.push(velocity);

            interpreter.execute(((PushGPIndividual) inIndividual).program,
                    executionLimit);

            // If there is no boolean on the stack, the program has failed to
            // return a reasonable output. So, return a penalty fitness of
            // twice the maximum time.
            if (bStack.size() == 0) {
                return 2 * maxTime;
            }

            // If there is a boolean, use it to compute the next position and
            // velocity. Then, check for termination conditions.
            // NOTE: If result == True, we will apply the force in the positive
            // direction, and if result == False, we will apply the force in
            // the negative direction.
            boolean positiveForce = bStack.top();
            float acceleration;

            if (positiveForce) {
                acceleration = 0.5f;
            } else {
                acceleration = -0.5f;
            }

            velocity = velocity + (timeDiscritized * acceleration);
            position = position + (timeDiscritized * velocity);

            // Check for termination conditions
            if (position <= captureRadius && position >= -captureRadius &&
                    velocity <= captureRadius && velocity >= -captureRadius) {
                //Cart is centered, so return time it took.
                return step * timeDiscritized;
            }

        }

        // If here, the cart failed to come to rest in the allotted time. So,
        // return the failed error of maxTime.

        return maxTime;
    }

    protected boolean success() {
        return generationCount >= maxGenerations;
    }

}
