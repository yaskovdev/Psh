package org.spiderland.Psh;

import java.util.Random;

//
// All instructions 
//

/**
 * Abstract instruction class for instructions which operate on any of the
 * built-in stacks.
 */

abstract class StackInstruction extends Instruction {
    private static final long serialVersionUID = 1L;

    protected Stack stack;

    StackInstruction(Stack inStack) {
        stack = inStack;
    }
}

/**
 * Abstract instruction class for instructions which operate on one of the
 * standard ObjectStacks (code & exec).
 */

abstract class ObjectStackInstruction extends Instruction {
    private static final long serialVersionUID = 1L;

    protected ObjectStack stack;

    ObjectStackInstruction(ObjectStack inStack) {
        stack = inStack;
    }
}

class Quote extends Instruction {
    private static final long serialVersionUID = 1L;

    Quote() {
    }

    @Override
    public void Execute(Interpreter inI) {
        ObjectStack cstack = inI.codeStack();
        ObjectStack estack = inI.execStack();

        if (estack.size() > 0)
            cstack.push(estack.pop());
    }
}

class Pop extends StackInstruction {
    private static final long serialVersionUID = 1L;

    Pop(Stack inStack) {
        super(inStack);
    }

    @Override
    public void Execute(Interpreter inI) {
        if (stack.size() > 0)
            stack.popdiscard();
    }
}

class Flush extends StackInstruction {
    private static final long serialVersionUID = 1L;

    Flush(Stack inStack) {
        super(inStack);
    }

    @Override
    public void Execute(Interpreter inI) {
        stack.clear();
    }
}

class Dup extends StackInstruction {
    private static final long serialVersionUID = 1L;

    Dup(Stack inStack) {
        super(inStack);
    }

    @Override
    public void Execute(Interpreter inI) {
        stack.dup();
    }
}

class Rot extends StackInstruction {
    private static final long serialVersionUID = 1L;

    Rot(Stack inStack) {
        super(inStack);
    }

    @Override
    public void Execute(Interpreter inI) {
        if (stack.size() > 2)
            stack.rot();
    }
}

class Shove extends StackInstruction {
    private static final long serialVersionUID = 1L;

    Shove(Stack inStack) {
        super(inStack);
    }

    @Override
    public void Execute(Interpreter inI) {
        IntStack iStack = inI.intStack();

        if (iStack.size() > 0) {
            int index = iStack.pop();
            if (stack.size() > 0) {
                stack.shove(index);
            } else {
                iStack.push(index);
            }
        }
    }
}

class Swap extends StackInstruction {
    private static final long serialVersionUID = 1L;

    Swap(Stack inStack) {
        super(inStack);
    }

    @Override
    public void Execute(Interpreter inI) {
        if (stack.size() > 1)
            stack.swap();
    }
}

class Yank extends StackInstruction {
    private static final long serialVersionUID = 1L;

    Yank(Stack inStack) {
        super(inStack);
    }

    @Override
    public void Execute(Interpreter inI) {
        IntStack iStack = inI.intStack();

        if (iStack.size() > 0) {
            int index = iStack.pop();
            if (stack.size() > 0) {
                stack.yank(index);
            } else {
                iStack.push(index);
            }
        }
    }
}

class YankDup extends StackInstruction {
    private static final long serialVersionUID = 1L;

    YankDup(Stack inStack) {
        super(inStack);
    }

    @Override
    public void Execute(Interpreter inI) {
        IntStack iStack = inI.intStack();

        if (iStack.size() > 0) {
            int index = iStack.pop();
            if (stack.size() > 0) {
                stack.yankdup(index);
            } else {
                iStack.push(index);
            }
        }
    }
}

class Depth extends StackInstruction {
    private static final long serialVersionUID = 1L;

    Depth(Stack inStack) {
        super(inStack);
    }

    @Override
    public void Execute(Interpreter inI) {
        IntStack stack = inI.intStack();
        stack.push(this.stack.size());
    }
}

class IntegerConstant extends Instruction {
    private static final long serialVersionUID = 1L;

    int value;

    public IntegerConstant(int inValue) {
        value = inValue;
    }

    @Override
    public void Execute(Interpreter inI) {
        inI.intStack().push(value);
    }
}

class FloatConstant extends Instruction {
    private static final long serialVersionUID = 1L;

    float value;

    public FloatConstant(float inValue) {
        value = inValue;
    }

    @Override
    public void Execute(Interpreter inI) {
        inI.floatStack().push(value);
    }
}

class BooleanConstant extends Instruction {
    private static final long serialVersionUID = 1L;

    boolean value;

    public BooleanConstant(boolean inValue) {
        value = inValue;
    }

    @Override
    public void Execute(Interpreter inI) {
        inI.boolStack().push(value);
    }
}

class ObjectConstant extends ObjectStackInstruction {
    private static final long serialVersionUID = 1L;

    Object value;

    public ObjectConstant(ObjectStack inStack, Object inValue) {
        super(inStack);
        value = inValue;
    }

    @Override
    public void Execute(Interpreter inI) {
        stack.push(value);
    }
}

//
//
// Binary integer instructions
//

abstract class BinaryIntegerInstruction extends Instruction {
    private static final long serialVersionUID = 1L;

    abstract int BinaryOperator(int inA, int inB);

    @Override
    public void Execute(Interpreter inI) {
        IntStack stack = inI.intStack();

        if (stack.size() > 1) {
            int a, b;
            a = stack.pop();
            b = stack.pop();
            stack.push(BinaryOperator(b, a));
        }
    }
}

class IntegerAdd extends BinaryIntegerInstruction {
    private static final long serialVersionUID = 1L;

    @Override
    int BinaryOperator(int inA, int inB) {
        // Test for overflow
        if ((Math.abs(inA) > Integer.MAX_VALUE / 10) ||
                (Math.abs(inB) > Integer.MAX_VALUE / 10)) {
            long lA = inA;
            long lB = inB;
            if (lA + lB != inA + inB) {
                if (inA > 0) {
                    return Integer.MAX_VALUE;
                } else {
                    return Integer.MIN_VALUE;
                }
            }
        }

        return inA + inB;
    }
}

class IntegerSub extends BinaryIntegerInstruction {
    private static final long serialVersionUID = 1L;

    @Override
    int BinaryOperator(int inA, int inB) {
        // Test for overflow
        if ((Math.abs(inA) > Integer.MAX_VALUE / 10) ||
                (Math.abs(inB) > Integer.MAX_VALUE / 10)) {
            long lA = inA;
            long lB = inB;
            if (lA - lB != inA - inB) {
                if (inA > 0) {
                    return Integer.MAX_VALUE;
                } else {
                    return Integer.MIN_VALUE;
                }
            }
        }

        return inA - inB;
    }
}

class IntegerDiv extends BinaryIntegerInstruction {
    private static final long serialVersionUID = 1L;

    @Override
    int BinaryOperator(int inA, int inB) {
        return inB != 0 ? (inA / inB) : 0;
    }
}

class IntegerMul extends BinaryIntegerInstruction {
    private static final long serialVersionUID = 1L;

    @Override
    int BinaryOperator(int inA, int inB) {
        // Test for overflow
        if ((Math.abs(inA) > Math.sqrt(Integer.MAX_VALUE - 1)) ||
                (Math.abs(inB) > Math.sqrt(Integer.MAX_VALUE - 1))) {
            long lA = inA;
            long lB = inB;
            if (lA * lB != (long) inA * inB) {
                if ((inA > 0 && inB > 0) || (inA < 0 && inB < 0)) {
                    return Integer.MAX_VALUE;
                } else {
                    return Integer.MIN_VALUE;
                }
            }
        }

        return inA * inB;
    }
}

class IntegerMod extends BinaryIntegerInstruction {
    private static final long serialVersionUID = 1L;

    @Override
    int BinaryOperator(int inA, int inB) {
        return inB != 0 ? (inA % inB) : 0;
    }
}

class IntegerPow extends BinaryIntegerInstruction {
    private static final long serialVersionUID = 1L;

    @Override
    int BinaryOperator(int inA, int inB) {
        // Test for overflow
        double result = Math.pow(inA, inB);
        if (Double.isInfinite(result) && result > 0) {
            return Integer.MAX_VALUE;
        }
        if (Double.isInfinite(result) && result < 0) {
            return Integer.MIN_VALUE;
        }
        if (Double.isNaN(result)) {
            return 0;
        }

        return (int) result;
    }
}

class IntegerLog extends BinaryIntegerInstruction {
    private static final long serialVersionUID = 1L;

    @Override
    int BinaryOperator(int inA, int inB) {
        // Test for overflow
        double result = Math.log(inB) / Math.log(inA);
        if (Double.isInfinite(result) && result > 0) {
            return Integer.MAX_VALUE;
        }
        if (Double.isInfinite(result) && result < 0) {
            return Integer.MIN_VALUE;
        }
        if (Double.isNaN(result)) {
            return 0;
        }

        return (int) result;
    }
}

class IntegerMin extends BinaryIntegerInstruction {
    private static final long serialVersionUID = 1L;

    @Override
    int BinaryOperator(int inA, int inB) {
        return Math.min(inA, inB);
    }
}

class IntegerMax extends BinaryIntegerInstruction {
    private static final long serialVersionUID = 1L;

    @Override
    int BinaryOperator(int inA, int inB) {
        return Math.max(inA, inB);
    }
}

//
//Unary int instructions
//

abstract class UnaryIntInstruction extends Instruction {
    private static final long serialVersionUID = 1L;

    abstract int UnaryOperator(int inValue);

    @Override
    public void Execute(Interpreter inI) {
        IntStack stack = inI.intStack();

        if (stack.size() > 0)
            stack.push(UnaryOperator(stack.pop()));
    }
}

class IntegerAbs extends UnaryIntInstruction {
    private static final long serialVersionUID = 1L;

    @Override
    int UnaryOperator(int inValue) {
        return Math.abs(inValue);
    }
}

class IntegerNeg extends UnaryIntInstruction {
    private static final long serialVersionUID = 1L;

    @Override
    int UnaryOperator(int inValue) {
        // Test for overflow
        if (inValue == Integer.MIN_VALUE)
            return Integer.MAX_VALUE;

        return -inValue;
    }
}

class IntegerLn extends UnaryIntInstruction {
    private static final long serialVersionUID = 1L;

    @Override
    int UnaryOperator(int inA) {
        // Test for overflow
        double result = Math.log(inA);
        if (Double.isInfinite(result) && result > 0) {
            return Integer.MAX_VALUE;
        }
        if (Double.isInfinite(result) && result < 0) {
            return Integer.MIN_VALUE;
        }
        if (Double.isNaN(result)) {
            return 0;
        }

        return (int) result;
    }
}

class IntegerRand extends Instruction {
    private static final long serialVersionUID = 1L;

    Random random;

    IntegerRand() {
        random = new Random();
    }

    @Override
    public void Execute(Interpreter inI) {
        int range = (inI.maxRandomInt - inI.minRandomInt)
                / inI.randomIntResolution;
        int randInt = (random.nextInt(range) * inI.randomIntResolution)
                + inI.minRandomInt;
        inI.intStack().push(randInt);
    }
}

//
// Conversion instructions to integer
//

class IntegerFromFloat extends Instruction {
    private static final long serialVersionUID = 1L;

    @Override
    public void Execute(Interpreter inI) {
        IntStack iStack = inI.intStack();
        FloatStack fStack = inI.floatStack();

        if (fStack.size() > 0) {
            iStack.push((int) fStack.pop());
        }
    }
}

class IntegerFromBoolean extends Instruction {
    private static final long serialVersionUID = 1L;

    @Override
    public void Execute(Interpreter inI) {
        BooleanStack bStack = inI.boolStack();
        IntStack iStack = inI.intStack();

        if (bStack.size() > 0) {
            if (bStack.pop()) {
                iStack.push(1);
            } else {
                iStack.push(0);
            }
        }
    }
}

//
// Integer instructions with boolean output
//

abstract class BinaryIntegerBoolInstruction extends Instruction {
    private static final long serialVersionUID = 1L;

    abstract boolean BinaryOperator(int inA, int inB);

    @Override
    public void Execute(Interpreter inI) {
        IntStack istack = inI.intStack();
        BooleanStack bstack = inI.boolStack();

        if (istack.size() > 1) {
            int a, b;
            a = istack.pop();
            b = istack.pop();
            bstack.push(BinaryOperator(b, a));
        }
    }
}

class IntegerGreaterThan extends BinaryIntegerBoolInstruction {
    private static final long serialVersionUID = 1L;

    @Override
    boolean BinaryOperator(int inA, int inB) {
        return inA > inB;
    }
}

class IntegerLessThan extends BinaryIntegerBoolInstruction {
    private static final long serialVersionUID = 1L;

    @Override
    boolean BinaryOperator(int inA, int inB) {
        return inA < inB;
    }
}

class IntegerEquals extends BinaryIntegerBoolInstruction {
    private static final long serialVersionUID = 1L;

    @Override
    boolean BinaryOperator(int inA, int inB) {
        return inA == inB;
    }
}

//
// Binary float instructions with float output
//

abstract class BinaryFloatInstruction extends Instruction {
    private static final long serialVersionUID = 1L;

    abstract float BinaryOperator(float inA, float inB);

    @Override
    public void Execute(Interpreter inI) {
        FloatStack stack = inI.floatStack();

        if (stack.size() > 1) {
            float a, b;
            a = stack.pop();
            b = stack.pop();
            stack.push(BinaryOperator(b, a));
        }
    }
}

class FloatAdd extends BinaryFloatInstruction {
    private static final long serialVersionUID = 1L;

    @Override
    float BinaryOperator(float inA, float inB) {
        // Test for overflow
        float result = inA + inB;
        if (Float.isInfinite(result) && result > 0) {
            return Float.MAX_VALUE;
        }
        if (Float.isInfinite(result) && result < 0) {
            return (1.0f - Float.MAX_VALUE);
        }

        return result;
    }
}

class FloatSub extends BinaryFloatInstruction {
    private static final long serialVersionUID = 1L;

    @Override
    float BinaryOperator(float inA, float inB) {
        // Test for overflow
        float result = inA - inB;
        if (Float.isInfinite(result) && result > 0) {
            return Float.MAX_VALUE;
        }
        if (Float.isInfinite(result) && result < 0) {
            return (1.0f - Float.MAX_VALUE);
        }

        return inA - inB;
    }
}

class FloatMul extends BinaryFloatInstruction {
    private static final long serialVersionUID = 1L;

    @Override
    float BinaryOperator(float inA, float inB) {
        // Test for overflow
        float result = inA * inB;
        if (Float.isInfinite(result) && result > 0) {
            return Float.MAX_VALUE;
        }
        if (Float.isInfinite(result) && result < 0) {
            return (1.0f - Float.MAX_VALUE);
        }
        if (Float.isNaN(result)) {
            return 0.0f;
        }

        return inA * inB;
    }
}

class FloatDiv extends BinaryFloatInstruction {
    private static final long serialVersionUID = 1L;

    @Override
    float BinaryOperator(float inA, float inB) {
        // Test for overflow
        float result = inA / inB;
        if (Float.isInfinite(result) && result > 0) {
            return Float.MAX_VALUE;
        }
        if (Float.isInfinite(result) && result < 0) {
            return (1.0f - Float.MAX_VALUE);
        }
        if (Float.isNaN(result)) {
            return 0.0f;
        }

        return result;
    }
}

class FloatMod extends BinaryFloatInstruction {
    private static final long serialVersionUID = 1L;

    @Override
    float BinaryOperator(float inA, float inB) {
        return inB != 0.0f ? (inA % inB) : 0.0f;
    }
}

class FloatPow extends BinaryFloatInstruction {
    private static final long serialVersionUID = 1L;

    @Override
    float BinaryOperator(float inA, float inB) {
        // Test for overflow
        float result = (float) Math.pow(inA, inB);
        if (Float.isInfinite(result) && result > 0) {
            return Float.MAX_VALUE;
        }
        if (Float.isInfinite(result) && result < 0) {
            return (1.0f - Float.MAX_VALUE);
        }
        if (Float.isNaN(result)) {
            return 0.0f;
        }

        return result;
    }
}

class FloatLog extends BinaryFloatInstruction {
    private static final long serialVersionUID = 1L;

    @Override
    float BinaryOperator(float inA, float inB) {
        // Test for overflow
        float result = (float) (Math.log(inB) / Math.log(inA));
        if (Double.isInfinite(result) && result > 0) {
            return Float.MAX_VALUE;
        }
        if (Double.isInfinite(result) && result < 0) {
            return (1.0f - Float.MAX_VALUE);
        }
        if (Double.isNaN(result)) {
            return 0.0f;
        }

        return result;
    }
}

class FloatMin extends BinaryFloatInstruction {
    private static final long serialVersionUID = 1L;

    @Override
    float BinaryOperator(float inA, float inB) {
        return Math.min(inA, inB);
    }
}

class FloatMax extends BinaryFloatInstruction {
    private static final long serialVersionUID = 1L;

    @Override
    float BinaryOperator(float inA, float inB) {
        return Math.max(inA, inB);
    }
}


//
// Unary float instructions
//

abstract class UnaryFloatInstruction extends Instruction {
    private static final long serialVersionUID = 1L;

    abstract float UnaryOperator(float inValue);

    @Override
    public void Execute(Interpreter inI) {
        FloatStack stack = inI.floatStack();

        if (stack.size() > 0)
            stack.push(UnaryOperator(stack.pop()));
    }
}

class FloatSin extends UnaryFloatInstruction {
    private static final long serialVersionUID = 1L;

    @Override
    float UnaryOperator(float inValue) {
        return (float) Math.sin(inValue);
    }
}

class FloatCos extends UnaryFloatInstruction {
    private static final long serialVersionUID = 1L;

    @Override
    float UnaryOperator(float inValue) {
        return (float) Math.cos(inValue);
    }
}

class FloatTan extends UnaryFloatInstruction {
    private static final long serialVersionUID = 1L;

    @Override
    float UnaryOperator(float inValue) {
        // Test for overflow
        float result = (float) Math.tan(inValue);
        if (Float.isInfinite(result) && result > 0) {
            return Float.MAX_VALUE;
        }
        if (Float.isInfinite(result) && result < 0) {
            return (1.0f - Float.MAX_VALUE);
        }
        if (Float.isNaN(result)) {
            return 0.0f;
        }

        return result;
    }
}

class FloatExp extends UnaryFloatInstruction {
    private static final long serialVersionUID = 1L;

    @Override
    float UnaryOperator(float inValue) {
        // Test for overflow
        float result = (float) Math.exp(inValue);
        if (Float.isInfinite(result) && result > 0) {
            return Float.MAX_VALUE;
        }
        if (Float.isInfinite(result) && result < 0) {
            return (1.0f - Float.MAX_VALUE);
        }
        if (Float.isNaN(result)) {
            return 0.0f;
        }

        return result;
    }
}

class FloatAbs extends UnaryFloatInstruction {
    private static final long serialVersionUID = 1L;

    @Override
    float UnaryOperator(float inValue) {
        return Math.abs(inValue);
    }
}

class FloatNeg extends UnaryFloatInstruction {
    private static final long serialVersionUID = 1L;

    @Override
    float UnaryOperator(float inValue) {
        return -inValue;
    }
}

class FloatLn extends UnaryFloatInstruction {
    private static final long serialVersionUID = 1L;

    @Override
    float UnaryOperator(float inA) {
        // Test for overflow
        float result = (float) Math.log(inA);
        if (Double.isInfinite(result) && result > 0) {
            return Float.MAX_VALUE;
        }
        if (Double.isInfinite(result) && result < 0) {
            return (1.0f - Float.MAX_VALUE);
        }
        if (Double.isNaN(result)) {
            return 0.0f;
        }

        return result;
    }
}

class FloatRand extends Instruction {
    private static final long serialVersionUID = 1L;

    Random random;

    FloatRand() {
        random = new Random();
    }

    @Override
    public void Execute(Interpreter inI) {


        float range = (inI.maxRandomFloat - inI.minRandomFloat)
                / inI.randomFloatResolution;
        float randFloat = (random.nextFloat() * range * inI.randomFloatResolution)
                + inI.minRandomFloat;
        inI.floatStack().push(randFloat);
    }
}

//
// Conversion instructions to float
//

class FloatFromInteger extends Instruction {
    private static final long serialVersionUID = 1L;

    @Override
    public void Execute(Interpreter inI) {
        IntStack iStack = inI.intStack();
        FloatStack fStack = inI.floatStack();

        if (iStack.size() > 0) {
            fStack.push(iStack.pop());
        }
    }
}

class FloatFromBoolean extends Instruction {
    private static final long serialVersionUID = 1L;

    @Override
    public void Execute(Interpreter inI) {
        BooleanStack bStack = inI.boolStack();
        FloatStack fStack = inI.floatStack();

        if (bStack.size() > 0) {
            if (bStack.pop()) {
                fStack.push(1);
            } else {
                fStack.push(0);
            }
        }
    }
}

//
// Binary float instructions with boolean output
//

abstract class BinaryFloatBoolInstruction extends Instruction {
    private static final long serialVersionUID = 1L;

    abstract boolean BinaryOperator(float inA, float inB);

    @Override
    public void Execute(Interpreter inI) {
        FloatStack fstack = inI.floatStack();
        BooleanStack bstack = inI.boolStack();

        if (fstack.size() > 1) {
            float a, b;
            b = fstack.pop();
            a = fstack.pop();
            bstack.push(BinaryOperator(a, b));
        }
    }
}

class FloatGreaterThan extends BinaryFloatBoolInstruction {
    private static final long serialVersionUID = 1L;

    @Override
    boolean BinaryOperator(float inA, float inB) {
        return inA > inB;
    }
}

class FloatLessThan extends BinaryFloatBoolInstruction {
    private static final long serialVersionUID = 1L;

    @Override
    boolean BinaryOperator(float inA, float inB) {
        return inA < inB;
    }
}

class FloatEquals extends BinaryFloatBoolInstruction {
    private static final long serialVersionUID = 1L;

    @Override
    boolean BinaryOperator(float inA, float inB) {
        return inA == inB;
    }
}


//
//Binary bool instructions with bool output
//

abstract class BinaryBoolInstruction extends Instruction {
    private static final long serialVersionUID = 1L;

    abstract boolean BinaryOperator(boolean inA, boolean inB);

    @Override
    public void Execute(Interpreter inI) {
        BooleanStack stack = inI.boolStack();

        if (stack.size() > 1) {
            boolean a, b;
            a = stack.pop();
            b = stack.pop();
            stack.push(BinaryOperator(b, a));
        }
    }
}

class BoolEquals extends BinaryBoolInstruction {
    private static final long serialVersionUID = 1L;

    @Override
    boolean BinaryOperator(boolean inA, boolean inB) {
        return inA == inB;
    }
}

class BoolAnd extends BinaryBoolInstruction {
    private static final long serialVersionUID = 1L;

    @Override
    boolean BinaryOperator(boolean inA, boolean inB) {
        return inA & inB;
    }
}

class BoolOr extends BinaryBoolInstruction {
    private static final long serialVersionUID = 1L;

    @Override
    boolean BinaryOperator(boolean inA, boolean inB) {
        return inA | inB;
    }
}

class BoolXor extends BinaryBoolInstruction {
    private static final long serialVersionUID = 1L;

    @Override
    boolean BinaryOperator(boolean inA, boolean inB) {
        return inA ^ inB;
    }
}

class BoolNot extends Instruction {
    private static final long serialVersionUID = 1L;

    @Override
    public void Execute(Interpreter inI) {
        if (inI.boolStack().size() > 0)
            inI.boolStack().push(!inI.boolStack().pop());
    }
}

class BoolRand extends Instruction {
    private static final long serialVersionUID = 1L;

    Random random;

    BoolRand() {
        random = new Random();
    }

    @Override
    public void Execute(Interpreter inI) {
        inI.boolStack().push(random.nextBoolean());
    }
}

//
// Conversion instructions to boolean
//

class BooleanFromInteger extends Instruction {
    private static final long serialVersionUID = 1L;

    @Override
    public void Execute(Interpreter inI) {
        BooleanStack bStack = inI.boolStack();
        IntStack iStack = inI.intStack();

        if (iStack.size() > 0) {
            bStack.push(iStack.pop() != 0);
        }
    }
}

class BooleanFromFloat extends Instruction {
    private static final long serialVersionUID = 1L;

    @Override
    public void Execute(Interpreter inI) {
        BooleanStack bStack = inI.boolStack();
        FloatStack fStack = inI.floatStack();

        if (fStack.size() > 0) {
            bStack.push(fStack.pop() != 0.0);
        }
    }
}

//
// Instructions for input stack
//

class InputInN extends Instruction {
    private static final long serialVersionUID = 1L;

    protected int index;

    InputInN(int inIndex) {
        index = inIndex;
    }

    @Override
    public void Execute(Interpreter inI) {
        inI.getInputPusher().pushInput(inI, index);
    }
}

class InputInAll extends ObjectStackInstruction {
    private static final long serialVersionUID = 1L;

    InputInAll(ObjectStack inStack) {
        super(inStack);
    }

    @Override
    public void Execute(Interpreter inI) {

        if (stack.size() > 0) {
            for (int index = 0; index < stack.size(); index++) {
                inI.getInputPusher().pushInput(inI, index);
            }
        }
    }
}

class InputInRev extends ObjectStackInstruction {
    private static final long serialVersionUID = 1L;

    InputInRev(ObjectStack inStack) {
        super(inStack);
    }

    @Override
    public void Execute(Interpreter inI) {

        if (stack.size() > 0) {
            for (int index = stack.size() - 1; index >= 0; index--) {
                inI.getInputPusher().pushInput(inI, index);
            }
        }
    }
}

class InputIndex extends ObjectStackInstruction {
    private static final long serialVersionUID = 1L;

    InputIndex(ObjectStack inStack) {
        super(inStack);
    }

    @Override
    public void Execute(Interpreter inI) {
        IntStack istack = inI.intStack();

        if (istack.size() > 0 && stack.size() > 0) {
            int index = istack.pop();

            if (index < 0)
                index = 0;
            if (index >= stack.size())
                index = stack.size() - 1;

            inI.getInputPusher().pushInput(inI, index);
        }
    }
}

//
// Instructions for code and exec stack
//

// trh//All code and exec stack iteration fuctions have been fixed to match the
// specifications of Push 3.0

// Begin code iteration functions
class CodeDoRange extends ObjectStackInstruction {
    private static final long serialVersionUID = 1L;

    CodeDoRange(Interpreter inI) {
        super(inI.codeStack());
    }

    @Override
    public void Execute(Interpreter inI) {
        IntStack istack = inI.intStack();
        ObjectStack estack = inI.execStack();

        if (stack.size() > 0 && istack.size() > 1) {
            int stop = istack.pop();
            int start = istack.pop();
            Object code = stack.pop();

            if (start == stop) {
                istack.push(start);
                estack.push(code);
            } else {
                istack.push(start);
                start = (start < stop) ? (start + 1) : (start - 1);

                try {
                    Program recursiveCallProgram = new Program();
                    recursiveCallProgram.push(Integer.valueOf(start));
                    recursiveCallProgram.push(Integer.valueOf(stop));
                    recursiveCallProgram.push("code.quote");
                    recursiveCallProgram.push(code);
                    recursiveCallProgram.push("code.do*range");
                    estack.push(recursiveCallProgram);
                } catch (Exception e) {
                    System.err.println("Error while initializing a program.");
                }

                estack.push(code);
            }
        }
    }
}

class CodeDoTimes extends ObjectStackInstruction {
    private static final long serialVersionUID = 1L;

    CodeDoTimes(Interpreter inI) {
        super(inI.codeStack());
    }

    @Override
    public void Execute(Interpreter inI) {
        IntStack istack = inI.intStack();
        ObjectStack estack = inI.execStack();

        if (stack.size() > 0 && istack.size() > 0) {
            if (istack.top() > 0) {
                Object bodyObj = stack.pop();

                if (bodyObj instanceof Program) {
                    // insert integer.pop in front of program
                    ((Program) bodyObj).shove("integer.pop", ((Program) bodyObj).size);
                } else {
                    // create a new program with integer.pop in front of
                    // the popped object
                    Program newProgram = new Program();
                    newProgram.push("integer.pop");
                    newProgram.push(bodyObj);
                    bodyObj = newProgram;
                }

                int stop = istack.pop() - 1;

                try {
                    Program doRangeMacroProgram = new Program();
                    doRangeMacroProgram.push(Integer.valueOf(0));
                    doRangeMacroProgram.push(Integer.valueOf(stop));
                    doRangeMacroProgram.push("code.quote");
                    doRangeMacroProgram.push(bodyObj);
                    doRangeMacroProgram.push("code.do*range");
                    estack.push(doRangeMacroProgram);
                } catch (Exception e) {
                    System.err.println("Error while initializing a program.");
                }

            }
        }

    }
}

class CodeDoCount extends ObjectStackInstruction {
    private static final long serialVersionUID = 1L;

    CodeDoCount(Interpreter inI) {
        super(inI.codeStack());
    }

    @Override
    public void Execute(Interpreter inI) {
        IntStack istack = inI.intStack();
        ObjectStack estack = inI.execStack();

        if (stack.size() > 0 && istack.size() > 0) {
            if (istack.top() > 0) {
                int stop = istack.pop() - 1;
                Object bodyObj = stack.pop();

                try {
                    Program doRangeMacroProgram = new Program();
                    doRangeMacroProgram.push(Integer.valueOf(0));
                    doRangeMacroProgram.push(Integer.valueOf(stop));
                    doRangeMacroProgram.push("code.quote");
                    doRangeMacroProgram.push(bodyObj);
                    doRangeMacroProgram.push("code.do*range");
                    estack.push(doRangeMacroProgram);
                } catch (Exception e) {
                    System.err.println("Error while initializing a program.");
                }

            }
        }

    }
}

// End code iteration functions


//
// Conversion instructions to code
//

class CodeFromBoolean extends Instruction {
    private static final long serialVersionUID = 1L;

    @Override
    public void Execute(Interpreter inI) {
        ObjectStack codeStack = inI.codeStack();
        BooleanStack bStack = inI.boolStack();

        if (bStack.size() > 0) {
            codeStack.push(bStack.pop());
        }
    }
}

class CodeFromInteger extends Instruction {
    private static final long serialVersionUID = 1L;

    @Override
    public void Execute(Interpreter inI) {
        ObjectStack codeStack = inI.codeStack();
        IntStack iStack = inI.intStack();

        if (iStack.size() > 0) {
            codeStack.push(iStack.pop());
        }
    }
}

class CodeFromFloat extends Instruction {
    private static final long serialVersionUID = 1L;

    @Override
    public void Execute(Interpreter inI) {
        ObjectStack codeStack = inI.codeStack();
        FloatStack fStack = inI.floatStack();

        if (fStack.size() > 0) {
            codeStack.push(fStack.pop());
        }
    }
}

// Begin exec iteration functions

class ExecDoRange extends ObjectStackInstruction {
    private static final long serialVersionUID = 1L;

    ExecDoRange(Interpreter inI) {
        super(inI.execStack());
    }

    @Override
    public void Execute(Interpreter inI) {
        IntStack istack = inI.intStack();
        ObjectStack estack = inI.execStack();

        if (stack.size() > 0 && istack.size() > 1) {
            int stop = istack.pop();
            int start = istack.pop();
            Object code = stack.pop();

            if (start == stop) {
                istack.push(start);
                estack.push(code);
            } else {
                istack.push(start);
                start = (start < stop) ? (start + 1) : (start - 1);

                // trh//Made changes to correct errors with code.do*range

                try {
                    Program recursiveCallProgram = new Program();
                    recursiveCallProgram.push(Integer.valueOf(start));
                    recursiveCallProgram.push(Integer.valueOf(stop));
                    recursiveCallProgram.push("exec.do*range");
                    recursiveCallProgram.push(code);
                    estack.push(recursiveCallProgram);
                } catch (Exception e) {
                    System.err.println("Error while initializing a program.");
                }

                estack.push(code);
            }
        }
    }
}

class ExecDoTimes extends ObjectStackInstruction {
    private static final long serialVersionUID = 1L;

    ExecDoTimes(Interpreter inI) {
        super(inI.execStack());
    }

    @Override
    public void Execute(Interpreter inI) {
        IntStack istack = inI.intStack();
        ObjectStack estack = inI.execStack();

        if (stack.size() > 0 && istack.size() > 0) {
            if (istack.top() > 0) {
                Object bodyObj = stack.pop();

                if (bodyObj instanceof Program) {
                    // insert integer.pop in front of program
                    ((Program) bodyObj).shove("integer.pop", ((Program) bodyObj).size);
                } else {
                    // create a new program with integer.pop in front of
                    // the popped object
                    Program newProgram = new Program();
                    newProgram.push("integer.pop");
                    newProgram.push(bodyObj);
                    bodyObj = newProgram;
                }

                int stop = istack.pop() - 1;

                try {
                    Program doRangeMacroProgram = new Program();
                    doRangeMacroProgram.push(Integer.valueOf(0));
                    doRangeMacroProgram.push(Integer.valueOf(stop));
                    doRangeMacroProgram.push("exec.do*range");
                    doRangeMacroProgram.push(bodyObj);
                    estack.push(doRangeMacroProgram);
                } catch (Exception e) {
                    System.err.println("Error while initializing a program.");
                }

            }
        }

    }
}

class ExecDoCount extends ObjectStackInstruction {
    private static final long serialVersionUID = 1L;

    ExecDoCount(Interpreter inI) {
        super(inI.execStack());
    }

    @Override
    public void Execute(Interpreter inI) {
        IntStack istack = inI.intStack();
        ObjectStack estack = inI.execStack();

        if (stack.size() > 0 && istack.size() > 0) {
            if (istack.top() > 0) {
                int stop = istack.pop() - 1;
                Object bodyObj = stack.pop();

                try {
                    Program doRangeMacroProgram = new Program();
                    doRangeMacroProgram.push(Integer.valueOf(0));
                    doRangeMacroProgram.push(Integer.valueOf(stop));
                    doRangeMacroProgram.push("exec.do*range");
                    doRangeMacroProgram.push(bodyObj);
                    estack.push(doRangeMacroProgram);
                } catch (Exception e) {
                    System.err.println("Error while initializing a program.");
                }

            }
        }
    }
}

// End exec iteration functions.

class ExecK extends ObjectStackInstruction {
    private static final long serialVersionUID = 1L;

    ExecK(ObjectStack inStack) {
        super(inStack);
    }

    @Override
    public void Execute(Interpreter inI) {
        // Removes the second item on the stack
        if (stack.size() > 1) {
            stack.swap();
            stack.popdiscard();
        }
    }
}

class ExecS extends ObjectStackInstruction {
    private static final long serialVersionUID = 1L;

    int maxPointsInProgram;

    ExecS(ObjectStack inStack, int inMaxPointsInProgram) {
        super(inStack);
        maxPointsInProgram = inMaxPointsInProgram;
    }

    @Override
    public void Execute(Interpreter inI) {
        // Removes the second item on the stack
        if (stack.size() > 2) {
            Object a = stack.pop();
            Object b = stack.pop();
            Object c = stack.pop();
            Program listBC = new Program();

            listBC.push(b);
            listBC.push(c);

            if (listBC.programSize() > maxPointsInProgram) {
                // If the new list is too large, turn into a noop by re-pushing
                // the popped instructions
                stack.push(c);
                stack.push(b);
                stack.push(a);
            } else {
                // If not too big, continue as planned
                stack.push(listBC);
                stack.push(c);
                stack.push(a);
            }
        }
    }
}

class ExecY extends ObjectStackInstruction {
    private static final long serialVersionUID = 1L;

    ExecY(ObjectStack inStack) {
        super(inStack);
    }

    @Override
    public void Execute(Interpreter inI) {
        // Removes the second item on the stack
        if (stack.size() > 0) {
            Object a = stack.pop();
            Program listExecYA = new Program();

            listExecYA.push("exec.y");
            listExecYA.push(a);

            stack.push(listExecYA);
            stack.push(a);
        }
    }
}

class ExecNoop extends Instruction {
    private static final long serialVersionUID = 1L;

    @Override
    public void Execute(Interpreter inI) {
        // Does Nothing
    }
}

class RandomPushCode extends ObjectStackInstruction {
    private static final long serialVersionUID = 1L;

    Random random;

    RandomPushCode(ObjectStack inStack) {
        super(inStack);
        random = new Random();
    }

    @Override
    public void Execute(Interpreter inI) {
        int randCodeMaxPoints = 0;

        if (inI.intStack().size() > 0) {
            randCodeMaxPoints = inI.intStack().pop();
            randCodeMaxPoints = Math.min(Math.abs(randCodeMaxPoints),
                    inI.maxRandomCodeSize);

            int randomCodeSize;
            if (randCodeMaxPoints > 0) {
                randomCodeSize = random.nextInt(randCodeMaxPoints) + 2;
            } else {
                randomCodeSize = 2;
            }
            Program p = inI.randomCode(randomCodeSize);

            stack.push(p);
        }
    }
}


class ObjectEquals extends ObjectStackInstruction {
    private static final long serialVersionUID = 1L;

    ObjectEquals(ObjectStack inStack) {
        super(inStack);
    }

    @Override
    public void Execute(Interpreter inI) {
        BooleanStack bstack = inI.boolStack();

        if (stack.size() > 1) {
            Object o1 = stack.pop();
            Object o2 = stack.pop();

            bstack.push(o1.equals(o2));
        }
    }
}

class If extends ObjectStackInstruction {
    private static final long serialVersionUID = 1L;

    If(ObjectStack inStack) {
        super(inStack);
    }

    @Override
    public void Execute(Interpreter inI) {
        BooleanStack bstack = inI.boolStack();
        ObjectStack estack = inI.execStack();

        if (stack.size() > 1 && bstack.size() > 0) {
            boolean istrue = bstack.pop();

            Object iftrue = stack.pop();
            Object iffalse = stack.pop();

            if (istrue)
                estack.push(iftrue);
            else
                estack.push(iffalse);
        }
    }
}

//
// Instructions for the activation stack
//

class PopFrame extends Instruction {
    private static final long serialVersionUID = 1L;

    PopFrame() {
    }

    @Override
    public void Execute(Interpreter inI) {
        // floatStack fstack = inI.floatStack();
        // float total = fstack.accumulate();

        inI.popFrame();

        // do the activation, and push the result on to the end of the previous
        // frame
        // fstack = inI.floatStack();
        // fstack.push( 1.0f / ( 1.0f + (float)Math.exp( -10.0f * ( total - .5 )
        // ) ) );
    }
}

class PushFrame extends Instruction {
    private static final long serialVersionUID = 1L;

    PushFrame() {
    }

    @Override
    public void Execute(Interpreter inI) {
        inI.pushFrame();
    }
}
