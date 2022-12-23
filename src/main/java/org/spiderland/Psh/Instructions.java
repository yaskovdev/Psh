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

    protected Stack _stack;

    StackInstruction(Stack inStack) {
        _stack = inStack;
    }
}

/**
 * Abstract instruction class for instructions which operate on one of the
 * standard ObjectStacks (code & exec).
 */

abstract class ObjectStackInstruction extends Instruction {

    protected ObjectStack _stack;

    ObjectStackInstruction(ObjectStack inStack) {
        _stack = inStack;
    }
}

class Quote extends Instruction {

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

    Pop(Stack inStack) {
        super(inStack);
    }

    @Override
    public void Execute(Interpreter inI) {
        if (_stack.size() > 0)
            _stack.popdiscard();
    }
}

class Flush extends StackInstruction {

    Flush(Stack inStack) {
        super(inStack);
    }

    @Override
    public void Execute(Interpreter inI) {
        _stack.clear();
    }
}

class Dup extends StackInstruction {

    Dup(Stack inStack) {
        super(inStack);
    }

    @Override
    public void Execute(Interpreter inI) {
        _stack.dup();
    }
}

class Rot extends StackInstruction {

    Rot(Stack inStack) {
        super(inStack);
    }

    @Override
    public void Execute(Interpreter inI) {
        if (_stack.size() > 2)
            _stack.rot();
    }
}

class Shove extends StackInstruction {

    Shove(Stack inStack) {
        super(inStack);
    }

    @Override
    public void Execute(Interpreter inI) {
        IntStack iStack = inI.intStack();

        if (iStack.size() > 0) {
            int index = iStack.pop();
            if (_stack.size() > 0) {
                _stack.shove(index);
            } else {
                iStack.push(index);
            }
        }
    }
}

class Swap extends StackInstruction {

    Swap(Stack inStack) {
        super(inStack);
    }

    @Override
    public void Execute(Interpreter inI) {
        if (_stack.size() > 1)
            _stack.swap();
    }
}

class Yank extends StackInstruction {

    Yank(Stack inStack) {
        super(inStack);
    }

    @Override
    public void Execute(Interpreter inI) {
        IntStack iStack = inI.intStack();

        if (iStack.size() > 0) {
            int index = iStack.pop();
            if (_stack.size() > 0) {
                _stack.yank(index);
            } else {
                iStack.push(index);
            }
        }
    }
}

class YankDup extends StackInstruction {

    YankDup(Stack inStack) {
        super(inStack);
    }

    @Override
    public void Execute(Interpreter inI) {
        IntStack iStack = inI.intStack();

        if (iStack.size() > 0) {
            int index = iStack.pop();
            if (_stack.size() > 0) {
                _stack.yankdup(index);
            } else {
                iStack.push(index);
            }
        }
    }
}

class Depth extends StackInstruction {

    Depth(Stack inStack) {
        super(inStack);
    }

    @Override
    public void Execute(Interpreter inI) {
        IntStack stack = inI.intStack();
        stack.push(_stack.size());
    }
}

class IntegerConstant extends Instruction {

    int _value;

    public IntegerConstant(int inValue) {
        _value = inValue;
    }

    @Override
    public void Execute(Interpreter inI) {
        inI.intStack().push(_value);
    }
}

class FloatConstant extends Instruction {

    float _value;

    public FloatConstant(float inValue) {
        _value = inValue;
    }

    @Override
    public void Execute(Interpreter inI) {
        inI.floatStack().push(_value);
    }
}

class BooleanConstant extends Instruction {

    boolean _value;

    public BooleanConstant(boolean inValue) {
        _value = inValue;
    }

    @Override
    public void Execute(Interpreter inI) {
        inI.boolStack().push(_value);
    }
}

class ObjectConstant extends ObjectStackInstruction {

    Object _value;

    public ObjectConstant(ObjectStack inStack, Object inValue) {
        super(inStack);
        _value = inValue;
    }

    @Override
    public void Execute(Interpreter inI) {
        _stack.push(_value);
    }
}

//
//
// Binary integer instructions
//

abstract class BinaryIntegerInstruction extends Instruction {

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

    @Override
    int BinaryOperator(int inA, int inB) {
        return inB != 0 ? (inA / inB) : 0;
    }
}

class IntegerMul extends BinaryIntegerInstruction {

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

    @Override
    int BinaryOperator(int inA, int inB) {
        return inB != 0 ? (inA % inB) : 0;
    }
}

class IntegerPow extends BinaryIntegerInstruction {

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

    @Override
    int BinaryOperator(int inA, int inB) {
        return Math.min(inA, inB);
    }
}

class IntegerMax extends BinaryIntegerInstruction {

    @Override
    int BinaryOperator(int inA, int inB) {
        return Math.max(inA, inB);
    }
}

//
//Unary int instructions
//

abstract class UnaryIntInstruction extends Instruction {

    abstract int UnaryOperator(int inValue);

    @Override
    public void Execute(Interpreter inI) {
        IntStack stack = inI.intStack();

        if (stack.size() > 0)
            stack.push(UnaryOperator(stack.pop()));
    }
}

class IntegerAbs extends UnaryIntInstruction {

    @Override
    int UnaryOperator(int inValue) {
        return Math.abs(inValue);
    }
}

class IntegerNeg extends UnaryIntInstruction {

    @Override
    int UnaryOperator(int inValue) {
        // Test for overflow
        if (inValue == Integer.MIN_VALUE)
            return Integer.MAX_VALUE;

        return -inValue;
    }
}

class IntegerLn extends UnaryIntInstruction {

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

    Random _RNG;

    IntegerRand() {
        _RNG = new Random();
    }

    @Override
    public void Execute(Interpreter inI) {
        int range = (inI._maxRandomInt - inI._minRandomInt)
                / inI._randomIntResolution;
        int randInt = (_RNG.nextInt(range) * inI._randomIntResolution)
                + inI._minRandomInt;
        inI.intStack().push(randInt);
    }
}

//
// Conversion instructions to integer
//

class IntegerFromFloat extends Instruction {

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

    @Override
    boolean BinaryOperator(int inA, int inB) {
        return inA > inB;
    }
}

class IntegerLessThan extends BinaryIntegerBoolInstruction {

    @Override
    boolean BinaryOperator(int inA, int inB) {
        return inA < inB;
    }
}

class IntegerEquals extends BinaryIntegerBoolInstruction {

    @Override
    boolean BinaryOperator(int inA, int inB) {
        return inA == inB;
    }
}

//
// Binary float instructions with float output
//

abstract class BinaryFloatInstruction extends Instruction {

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

    @Override
    float BinaryOperator(float inA, float inB) {
        return inB != 0.0f ? (inA % inB) : 0.0f;
    }
}

class FloatPow extends BinaryFloatInstruction {

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

    @Override
    float BinaryOperator(float inA, float inB) {
        return Math.min(inA, inB);
    }
}

class FloatMax extends BinaryFloatInstruction {

    @Override
    float BinaryOperator(float inA, float inB) {
        return Math.max(inA, inB);
    }
}


//
// Unary float instructions
//

abstract class UnaryFloatInstruction extends Instruction {

    abstract float UnaryOperator(float inValue);

    @Override
    public void Execute(Interpreter inI) {
        FloatStack stack = inI.floatStack();

        if (stack.size() > 0)
            stack.push(UnaryOperator(stack.pop()));
    }
}

class FloatSin extends UnaryFloatInstruction {

    @Override
    float UnaryOperator(float inValue) {
        return (float) Math.sin(inValue);
    }
}

class FloatCos extends UnaryFloatInstruction {

    @Override
    float UnaryOperator(float inValue) {
        return (float) Math.cos(inValue);
    }
}

class FloatTan extends UnaryFloatInstruction {

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

    @Override
    float UnaryOperator(float inValue) {
        return Math.abs(inValue);
    }
}

class FloatNeg extends UnaryFloatInstruction {

    @Override
    float UnaryOperator(float inValue) {
        return -inValue;
    }
}

class FloatLn extends UnaryFloatInstruction {

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

    Random _RNG;

    FloatRand() {
        _RNG = new Random();
    }

    @Override
    public void Execute(Interpreter inI) {


        float range = (inI._maxRandomFloat - inI._minRandomFloat)
                / inI._randomFloatResolution;
        float randFloat = (_RNG.nextFloat() * range * inI._randomFloatResolution)
                + inI._minRandomFloat;
        inI.floatStack().push(randFloat);
    }
}

//
// Conversion instructions to float
//

class FloatFromInteger extends Instruction {

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

    @Override
    boolean BinaryOperator(float inA, float inB) {
        return inA > inB;
    }
}

class FloatLessThan extends BinaryFloatBoolInstruction {

    @Override
    boolean BinaryOperator(float inA, float inB) {
        return inA < inB;
    }
}

class FloatEquals extends BinaryFloatBoolInstruction {

    @Override
    boolean BinaryOperator(float inA, float inB) {
        return inA == inB;
    }
}


//
//Binary bool instructions with bool output
//

abstract class BinaryBoolInstruction extends Instruction {

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

    @Override
    boolean BinaryOperator(boolean inA, boolean inB) {
        return inA == inB;
    }
}

class BoolAnd extends BinaryBoolInstruction {

    @Override
    boolean BinaryOperator(boolean inA, boolean inB) {
        return inA & inB;
    }
}

class BoolOr extends BinaryBoolInstruction {

    @Override
    boolean BinaryOperator(boolean inA, boolean inB) {
        return inA | inB;
    }
}

class BoolXor extends BinaryBoolInstruction {

    @Override
    boolean BinaryOperator(boolean inA, boolean inB) {
        return inA ^ inB;
    }
}

class BoolNot extends Instruction {

    @Override
    public void Execute(Interpreter inI) {
        if (inI.boolStack().size() > 0)
            inI.boolStack().push(!inI.boolStack().pop());
    }
}

class BoolRand extends Instruction {

    Random _RNG;

    BoolRand() {
        _RNG = new Random();
    }

    @Override
    public void Execute(Interpreter inI) {
        inI.boolStack().push(_RNG.nextBoolean());
    }
}

//
// Conversion instructions to boolean
//

class BooleanFromInteger extends Instruction {

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

    InputInAll(ObjectStack inStack) {
        super(inStack);
    }

    @Override
    public void Execute(Interpreter inI) {

        if (_stack.size() > 0) {
            for (int index = 0; index < _stack.size(); index++) {
                inI.getInputPusher().pushInput(inI, index);
            }
        }
    }
}

class InputInRev extends ObjectStackInstruction {

    InputInRev(ObjectStack inStack) {
        super(inStack);
    }

    @Override
    public void Execute(Interpreter inI) {

        if (_stack.size() > 0) {
            for (int index = _stack.size() - 1; index >= 0; index--) {
                inI.getInputPusher().pushInput(inI, index);
            }
        }
    }
}

class InputIndex extends ObjectStackInstruction {

    InputIndex(ObjectStack inStack) {
        super(inStack);
    }

    @Override
    public void Execute(Interpreter inI) {
        IntStack istack = inI.intStack();

        if (istack.size() > 0 && _stack.size() > 0) {
            int index = istack.pop();

            if (index < 0)
                index = 0;
            if (index >= _stack.size())
                index = _stack.size() - 1;

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

    CodeDoRange(Interpreter inI) {
        super(inI.codeStack());
    }

    @Override
    public void Execute(Interpreter inI) {
        IntStack istack = inI.intStack();
        ObjectStack estack = inI.execStack();

        if (_stack.size() > 0 && istack.size() > 1) {
            int stop = istack.pop();
            int start = istack.pop();
            Object code = _stack.pop();

            if (start == stop) {
                istack.push(start);
                estack.push(code);
            } else {
                istack.push(start);
                start = (start < stop) ? (start + 1) : (start - 1);

                try {
                    Program recursiveCallProgram = new Program(inI);
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

    CodeDoTimes(Interpreter inI) {
        super(inI.codeStack());
    }

    @Override
    public void Execute(Interpreter inI) {
        IntStack istack = inI.intStack();
        ObjectStack estack = inI.execStack();

        if (_stack.size() > 0 && istack.size() > 0) {
            if (istack.top() > 0) {
                Object bodyObj = _stack.pop();

                if (bodyObj instanceof Program) {
                    // insert integer.pop in front of program
                    ((Program) bodyObj).shove("integer.pop", ((Program) bodyObj)._size);
                } else {
                    // create a new program with integer.pop in front of
                    // the popped object
                    Program newProgram = new Program(inI);
                    newProgram.push("integer.pop");
                    newProgram.push(bodyObj);
                    bodyObj = newProgram;
                }

                int stop = istack.pop() - 1;

                try {
                    Program doRangeMacroProgram = new Program(inI);
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

    CodeDoCount(Interpreter inI) {
        super(inI.codeStack());
    }

    @Override
    public void Execute(Interpreter inI) {
        IntStack istack = inI.intStack();
        ObjectStack estack = inI.execStack();

        if (_stack.size() > 0 && istack.size() > 0) {
            if (istack.top() > 0) {
                int stop = istack.pop() - 1;
                Object bodyObj = _stack.pop();

                try {
                    Program doRangeMacroProgram = new Program(inI);
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

    ExecDoRange(Interpreter inI) {
        super(inI.execStack());
    }

    @Override
    public void Execute(Interpreter inI) {
        IntStack istack = inI.intStack();
        ObjectStack estack = inI.execStack();

        if (_stack.size() > 0 && istack.size() > 1) {
            int stop = istack.pop();
            int start = istack.pop();
            Object code = _stack.pop();

            if (start == stop) {
                istack.push(start);
                estack.push(code);
            } else {
                istack.push(start);
                start = (start < stop) ? (start + 1) : (start - 1);

                // trh//Made changes to correct errors with code.do*range

                try {
                    Program recursiveCallProgram = new Program(inI);
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

    ExecDoTimes(Interpreter inI) {
        super(inI.execStack());
    }

    @Override
    public void Execute(Interpreter inI) {
        IntStack istack = inI.intStack();
        ObjectStack estack = inI.execStack();

        if (_stack.size() > 0 && istack.size() > 0) {
            if (istack.top() > 0) {
                Object bodyObj = _stack.pop();

                if (bodyObj instanceof Program) {
                    // insert integer.pop in front of program
                    ((Program) bodyObj).shove("integer.pop", ((Program) bodyObj)._size);
                } else {
                    // create a new program with integer.pop in front of
                    // the popped object
                    Program newProgram = new Program(inI);
                    newProgram.push("integer.pop");
                    newProgram.push(bodyObj);
                    bodyObj = newProgram;
                }

                int stop = istack.pop() - 1;

                try {
                    Program doRangeMacroProgram = new Program(inI);
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

    ExecDoCount(Interpreter inI) {
        super(inI.execStack());
    }

    @Override
    public void Execute(Interpreter inI) {
        IntStack istack = inI.intStack();
        ObjectStack estack = inI.execStack();

        if (_stack.size() > 0 && istack.size() > 0) {
            if (istack.top() > 0) {
                int stop = istack.pop() - 1;
                Object bodyObj = _stack.pop();

                try {
                    Program doRangeMacroProgram = new Program(inI);
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

    ExecK(ObjectStack inStack) {
        super(inStack);
    }

    @Override
    public void Execute(Interpreter inI) {
        // Removes the second item on the stack
        if (_stack.size() > 1) {
            _stack.swap();
            _stack.popdiscard();
        }
    }
}

class ExecS extends ObjectStackInstruction {

    int _maxPointsInProgram;

    ExecS(ObjectStack inStack, int inMaxPointsInProgram) {
        super(inStack);
        _maxPointsInProgram = inMaxPointsInProgram;
    }

    @Override
    public void Execute(Interpreter inI) {
        // Removes the second item on the stack
        if (_stack.size() > 2) {
            Object a = _stack.pop();
            Object b = _stack.pop();
            Object c = _stack.pop();
            Program listBC = new Program(inI);

            listBC.push(b);
            listBC.push(c);

            if (listBC.programsize() > _maxPointsInProgram) {
                // If the new list is too large, turn into a noop by re-pushing
                // the popped instructions
                _stack.push(c);
                _stack.push(b);
                _stack.push(a);
            } else {
                // If not too big, continue as planned
                _stack.push(listBC);
                _stack.push(c);
                _stack.push(a);
            }
        }
    }
}

class ExecY extends ObjectStackInstruction {

    ExecY(ObjectStack inStack) {
        super(inStack);
    }

    @Override
    public void Execute(Interpreter inI) {
        // Removes the second item on the stack
        if (_stack.size() > 0) {
            Object a = _stack.pop();
            Program listExecYA = new Program(inI);

            listExecYA.push("exec.y");
            listExecYA.push(a);

            _stack.push(listExecYA);
            _stack.push(a);
        }
    }
}

class ExecNoop extends Instruction {

    @Override
    public void Execute(Interpreter inI) {
        // Does Nothing
    }
}

class RandomPushCode extends ObjectStackInstruction {

    Random _RNG;

    RandomPushCode(ObjectStack inStack) {
        super(inStack);
        _RNG = new Random();
    }

    @Override
    public void Execute(Interpreter inI) {
        int randCodeMaxPoints = 0;

        if (inI.intStack().size() > 0) {
            randCodeMaxPoints = inI.intStack().pop();
            randCodeMaxPoints = Math.min(Math.abs(randCodeMaxPoints),
                    inI._maxRandomCodeSize);

            int randomCodeSize;
            if (randCodeMaxPoints > 0) {
                randomCodeSize = _RNG.nextInt(randCodeMaxPoints) + 2;
            } else {
                randomCodeSize = 2;
            }
            Program p = inI.RandomCode(randomCodeSize);

            _stack.push(p);
        }
    }
}


class ObjectEquals extends ObjectStackInstruction {

    ObjectEquals(ObjectStack inStack) {
        super(inStack);
    }

    @Override
    public void Execute(Interpreter inI) {
        BooleanStack bstack = inI.boolStack();

        if (_stack.size() > 1) {
            Object o1 = _stack.pop();
            Object o2 = _stack.pop();

            bstack.push(o1.equals(o2));
        }
    }
}

class If extends ObjectStackInstruction {

    If(ObjectStack inStack) {
        super(inStack);
    }

    @Override
    public void Execute(Interpreter inI) {
        BooleanStack bstack = inI.boolStack();
        ObjectStack estack = inI.execStack();

        if (_stack.size() > 1 && bstack.size() > 0) {
            boolean istrue = bstack.pop();

            Object iftrue = _stack.pop();
            Object iffalse = _stack.pop();

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

    PopFrame() {
    }

    @Override
    public void Execute(Interpreter inI) {
        // floatStack fstack = inI.floatStack();
        // float total = fstack.accumulate();

        inI.PopFrame();

        // do the activation, and push the result on to the end of the previous
        // frame
        // fstack = inI.floatStack();
        // fstack.push( 1.0f / ( 1.0f + (float)Math.exp( -10.0f * ( total - .5 )
        // ) ) );
    }
}

class PushFrame extends Instruction {

    PushFrame() {
    }

    @Override
    public void Execute(Interpreter inI) {
        inI.PushFrame();
    }
}
