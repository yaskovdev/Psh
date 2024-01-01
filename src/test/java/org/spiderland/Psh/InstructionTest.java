package org.spiderland.Psh;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InstructionTest {
    protected Interpreter interpreter = null;
    protected IntStack intStack = null;
    protected FloatStack floatStack = null;
    protected BooleanStack booleanStack = null;

    @BeforeEach
    public void setUp() {
        interpreter = new Interpreter();
        Program instructionList = new Program("( )");
        interpreter.setInstructions(instructionList);
        intStack = new IntStack();
        floatStack = new FloatStack();
        booleanStack = new BooleanStack();
    }

    @Test
    public void testNumberName() {
        Program p = new Program("( 1 false 1.0 0 0.0 x true )");
        interpreter.execute(p);
        assertEquals(2, interpreter.intStack().size());
        assertEquals(2, interpreter.floatStack().size());
        assertEquals(1, interpreter.nameStack().size());
        assertEquals(2, interpreter.boolStack().size());
        assertEquals(0, interpreter.intStack().pop());
        assertEquals(1, interpreter.intStack().pop());
        assertEquals(0.0, interpreter.floatStack().pop(), Float.MIN_VALUE);
        assertEquals(1.0, interpreter.floatStack().pop(), Float.MAX_VALUE);
        assertEquals("x", interpreter.nameStack().pop());
        assertTrue(interpreter.boolStack().pop());
        assertFalse(interpreter.boolStack().pop());

    }

    @Test
    public void testPop() {
        Program p = new Program("( 1 2 3 4.0 5.0 true false " +
                "boolean.pop integer.pop float.pop )");
        interpreter.execute(p);

        intStack.push(1);
        intStack.push(2);

        floatStack.push(4.0f);

        booleanStack.push(true);

        assertEquals(intStack, interpreter.intStack());
        assertEquals(floatStack, interpreter.floatStack());
        assertEquals(booleanStack, interpreter.boolStack());
    }

    @Test
    public void testDup() {
        Program p = new Program("( 1 2 3 4.0 5.0 true false " +
                "boolean.dup integer.dup float.dup )");
        interpreter.execute(p);

        intStack.push(1);
        intStack.push(2);
        intStack.push(3);
        intStack.push(3);

        floatStack.push(4.0f);
        floatStack.push(5.0f);
        floatStack.push(5.0f);

        booleanStack.push(true);
        booleanStack.push(false);
        booleanStack.push(false);

        assertEquals(intStack, interpreter.intStack());
        assertEquals(floatStack, interpreter.floatStack());
        assertEquals(booleanStack, interpreter.boolStack());
    }

    @Test
    public void testSwap() {
        Program p = new Program("( 1 2 3 4.0 5.0 true false " +
                "boolean.swap integer.swap float.swap )");
        interpreter.execute(p);

        intStack.push(1);
        intStack.push(3);
        intStack.push(2);

        floatStack.push(5.0f);
        floatStack.push(4.0f);

        booleanStack.push(false);
        booleanStack.push(true);

        assertEquals(intStack, interpreter.intStack());
        assertEquals(floatStack, interpreter.floatStack());
        assertEquals(booleanStack, interpreter.boolStack());
    }

    @Test
    public void testRot() {
        Program p = new Program("( 1 2 3 4.0 5.0 6.0 true false true " +
                "boolean.rot integer.rot float.rot )");
        interpreter.execute(p);

        intStack.push(2);
        intStack.push(3);
        intStack.push(1);

        floatStack.push(5.0f);
        floatStack.push(6.0f);
        floatStack.push(4.0f);

        booleanStack.push(false);
        booleanStack.push(true);
        booleanStack.push(true);

        assertEquals(intStack, interpreter.intStack());
        assertEquals(floatStack, interpreter.floatStack());
        assertEquals(booleanStack, interpreter.boolStack());
    }

    @Test
    public void testFlush() {
        Program p = new Program("( 1 2 3 4.0 5.0 true false " +
                "boolean.flush integer.flush float.flush )");
        interpreter.execute(p);

        assertEquals(0, interpreter.intStack().size());
        assertEquals(0, interpreter.floatStack().size());
        assertEquals(0, interpreter.boolStack().size());
    }

    @Test
    public void testStackDepth() {
        Program p = new Program("( 1 2 3 4.0 5.0 true false " +
                "boolean.stackdepth integer.stackdepth float.stackdepth )");
        interpreter.execute(p);

        intStack.push(1);
        intStack.push(2);
        intStack.push(3);
        intStack.push(2);
        intStack.push(4);
        intStack.push(2);

        assertEquals(intStack, interpreter.intStack());
    }

    @Test
    public void testAdd() {
        Program p = new Program("( 1 2 3 4.0 5.0 true false " +
                "integer.+ float.+ )");
        interpreter.execute(p);

        intStack.push(1);
        intStack.push(5);

        floatStack.push(9.0f);

        booleanStack.push(true);
        booleanStack.push(false);

        assertEquals(intStack, interpreter.intStack());
        assertEquals(floatStack, interpreter.floatStack());
        assertEquals(booleanStack, interpreter.boolStack());
    }

    @Test
    public void testSub() {
        Program p = new Program("( 1 2 3 4.0 5.0 true false " +
                "integer.- float.- )");
        interpreter.execute(p);

        intStack.push(1);
        intStack.push(-1);

        floatStack.push(-1.0f);

        booleanStack.push(true);
        booleanStack.push(false);

        assertEquals(intStack, interpreter.intStack());
        assertEquals(floatStack, interpreter.floatStack());
        assertEquals(booleanStack, interpreter.boolStack());
    }

    @Test
    public void testMul() {
        Program p = new Program("( 1 2 3 4.0 5.0 true false " +
                "integer.* float.* )");
        interpreter.execute(p);

        intStack.push(1);
        intStack.push(6);

        floatStack.push(20.0f);

        booleanStack.push(true);
        booleanStack.push(false);

        assertEquals(intStack, interpreter.intStack());
        assertEquals(floatStack, interpreter.floatStack());
        assertEquals(booleanStack, interpreter.boolStack());
    }

    @Test
    public void testDiv() {
        Program p = new Program("( 1 2 3 4.0 5.0 true false " +
                "integer./ float./ )");
        interpreter.execute(p);

        intStack.push(1);
        intStack.push(0);

        floatStack.push(4.0f / 5.0f);

        booleanStack.push(true);
        booleanStack.push(false);

        assertEquals(intStack, interpreter.intStack());
        assertEquals(floatStack, interpreter.floatStack());
        assertEquals(booleanStack, interpreter.boolStack());
    }

    @Test
    public void testMod() {
        Program p = new Program("( 1 5 3 7.0 5.0 true false " +
                "integer.% float.% )");
        interpreter.execute(p);

        intStack.push(1);
        intStack.push(2);

        floatStack.push(2.0f);

        booleanStack.push(true);
        booleanStack.push(false);

        assertEquals(intStack, interpreter.intStack());
        assertEquals(floatStack, interpreter.floatStack());
        assertEquals(booleanStack, interpreter.boolStack());
    }

    @Test
    public void testEq() {
        Program p = new Program("( 1 3 3 7.0 5.0 true false " +
                "integer.= float.= true false boolean.= false false boolean.=)");
        interpreter.execute(p);

        intStack.push(1);

        booleanStack.push(true);
        booleanStack.push(false);
        booleanStack.push(true);
        booleanStack.push(false);
        booleanStack.push(false);
        booleanStack.push(true);

        assertEquals(intStack, interpreter.intStack());
        assertEquals(floatStack, interpreter.floatStack());
        assertEquals(booleanStack, interpreter.boolStack());
    }

    @Test
    public void testLt() {
        Program p = new Program("( 1 3 3 5.0 6.0 true false " +
                "integer.< float.< )");
        interpreter.execute(p);

        intStack.push(1);

        booleanStack.push(true);
        booleanStack.push(false);
        booleanStack.push(false);
        booleanStack.push(true);

        assertEquals(intStack, interpreter.intStack());
        assertEquals(floatStack, interpreter.floatStack());
        assertEquals(booleanStack, interpreter.boolStack());
    }

    @Test
    public void testGt() {
        Program p = new Program("( 1 3 3 5.0 6.0 true false " +
                "integer.> float.> )");
        interpreter.execute(p);

        intStack.push(1);

        booleanStack.push(true);
        booleanStack.push(false);
        booleanStack.push(false);
        booleanStack.push(false);

        assertEquals(intStack, interpreter.intStack());
        assertEquals(floatStack, interpreter.floatStack());
        assertEquals(booleanStack, interpreter.boolStack());
    }

    @Test
    public void testBoolOps() {
        Program p = new Program("( true false boolean.or " +
                "true false boolean.and true false boolean.xor true boolean.not )");
        interpreter.execute(p);

        booleanStack.push(true);
        booleanStack.push(false);
        booleanStack.push(true);
        booleanStack.push(false);

        assertEquals(intStack, interpreter.intStack());
        assertEquals(floatStack, interpreter.floatStack());
        assertEquals(booleanStack, interpreter.boolStack());
    }

    @Test
    public void testInputIndex() {
        Program p = new Program("( 1 input.index 1 input.index 0 input.index " +
                "0 input.index 2 input.index 2 input.index 1000 input.index -1 input.index)");
        interpreter.inputStack().push(true);
        interpreter.inputStack().push(3);
        interpreter.inputStack().push(2.0f);

        interpreter.execute(p);

        intStack.push(3);
        intStack.push(3);

        floatStack.push(2.0f);
        floatStack.push(2.0f);
        floatStack.push(2.0f);

        booleanStack.push(true);
        booleanStack.push(true);
        booleanStack.push(true);

        ObjectStack inputs = new ObjectStack();
        inputs.push(true);
        inputs.push(3);
        inputs.push(2.0f);

        assertEquals(intStack, interpreter.intStack());
        assertEquals(floatStack, interpreter.floatStack());
        assertEquals(booleanStack, interpreter.boolStack());
        assertEquals(inputs, interpreter.inputStack());
    }

    @Test
    public void testInputStackDepth() {
        Program p = new Program("( input.stackdepth )");
        interpreter.inputStack().push(true);
        interpreter.inputStack().push(3);
        interpreter.inputStack().push(2.0f);
        interpreter.inputStack().push(1.0f);

        interpreter.execute(p);

        intStack.push(4);

        assertEquals(intStack, interpreter.intStack());
        assertEquals(floatStack, interpreter.floatStack());
        assertEquals(booleanStack, interpreter.boolStack());
    }

    @Test
    public void testInputInAll() {
        Program p = new Program("( input.inall )");
        interpreter.inputStack().push(true);
        interpreter.inputStack().push(3);
        interpreter.inputStack().push(2.0f);
        interpreter.inputStack().push(1.0f);

        interpreter.execute(p);

        intStack.push(3);

        floatStack.push(2.0f);
        floatStack.push(1.0f);

        booleanStack.push(true);

        assertEquals(intStack, interpreter.intStack());
        assertEquals(floatStack, interpreter.floatStack());
        assertEquals(booleanStack, interpreter.boolStack());
    }

    @Test
    public void testInputInAllRev() {
        Program p = new Program("( input.inallrev )");
        interpreter.inputStack().push(true);
        interpreter.inputStack().push(3);
        interpreter.inputStack().push(2.0f);
        interpreter.inputStack().push(1.0f);

        interpreter.execute(p);

        intStack.push(3);

        floatStack.push(1.0f);
        floatStack.push(2.0f);

        booleanStack.push(true);

        assertEquals(intStack, interpreter.intStack());
        assertEquals(floatStack, interpreter.floatStack());
        assertEquals(booleanStack, interpreter.boolStack());
    }

    @Test
    @Disabled
    public void testCodeQuote() {
        Program p = new Program("( 1 code.quote integer.pop code.quote code.quote)");
        interpreter.execute(p);

        intStack.push(1);

        assertEquals(intStack, interpreter.intStack());
        assertEquals(floatStack, interpreter.floatStack());
        assertEquals(booleanStack, interpreter.boolStack());
        assertEquals(interpreter.getInstruction("code.quote"), interpreter.codeStack().pop());
        assertEquals(interpreter.getInstruction("integer.pop"), interpreter.codeStack().pop());
    }

    @Test
    public void testCodeEquals() {
        Program p = new Program("( 1 " +
                "code.quote integer.pop code.quote integer.pop code.= " +
                "code.quote integer.pop code.quote integer.+ code.= )");
        interpreter.execute(p);

        intStack.push(1);

        booleanStack.push(true);
        booleanStack.push(false);

        assertEquals(intStack, interpreter.intStack());
        assertEquals(floatStack, interpreter.floatStack());
        assertEquals(booleanStack, interpreter.boolStack());
    }

    @Test
    public void testExecEquals() {
        Program p = new Program("( 1 " +
                "exec.= code.quote integer.pop " +
                "exec.= integer.pop integer.pop )");
        interpreter.execute(p);

        intStack.push(1);

        booleanStack.push(false);
        booleanStack.push(true);

        assertEquals(intStack, interpreter.intStack());
        assertEquals(floatStack, interpreter.floatStack());
        assertEquals(booleanStack, interpreter.boolStack());
    }

    @Test
    public void testCodeIf() {
        Program p = new Program("( 1 2 1.0 2.0 " +
                "code.quote integer.pop code.quote float.pop true code.if " +
                "code.quote integer.pop code.quote float.pop false code.if )");
        interpreter.execute(p);

        intStack.push(1);

        floatStack.push(1.0f);

        assertEquals(intStack, interpreter.intStack());
        assertEquals(floatStack, interpreter.floatStack());
        assertEquals(booleanStack, interpreter.boolStack());
    }

    @Test
    public void testExecIf() {
        Program p = new Program("( 1 2 1.0 2.0 " +
                "true exec.if integer.pop float.pop " +
                "false exec.if integer.pop float.pop )");
        interpreter.execute(p);

        intStack.push(1);

        floatStack.push(1.0f);

        assertEquals(intStack, interpreter.intStack());
        assertEquals(floatStack, interpreter.floatStack());
        assertEquals(booleanStack, interpreter.boolStack());
    }

    @Test
    public void testExecDoRange() {
        Program p = new Program("( 1 3 " +
                "exec.do*range 2.0 )");
        interpreter.execute(p);

        intStack.push(1);
        intStack.push(2);
        intStack.push(3);

        floatStack.push(2.0f);
        floatStack.push(2.0f);
        floatStack.push(2.0f);

        assertEquals(intStack, interpreter.intStack());
        assertEquals(floatStack, interpreter.floatStack());
        assertEquals(booleanStack, interpreter.boolStack());
    }

    @Test
    public void testExecDoTimes() {
        Program p = new Program("( 1 3 " +
                "exec.do*times 2.0 )");
        interpreter.execute(p);

        intStack.push(1);

        floatStack.push(2.0f);
        floatStack.push(2.0f);
        floatStack.push(2.0f);

        assertEquals(intStack, interpreter.intStack());
        assertEquals(floatStack, interpreter.floatStack());
        assertEquals(booleanStack, interpreter.boolStack());
    }

    @Test
    public void testExecDoCount() {
        Program p = new Program("( 1 3 " +
                "exec.do*count 2.0 )");
        interpreter.execute(p);

        intStack.push(1);
        intStack.push(0);
        intStack.push(1);
        intStack.push(2);

        floatStack.push(2.0f);
        floatStack.push(2.0f);
        floatStack.push(2.0f);

        assertEquals(intStack, interpreter.intStack());
        assertEquals(floatStack, interpreter.floatStack());
        assertEquals(booleanStack, interpreter.boolStack());
    }

    @Test
    public void testCodeDoRange() {
        Program p = new Program("( 1 3 " +
                "code.quote 2.0 code.do*range )");
        interpreter.execute(p);

        intStack.push(1);
        intStack.push(2);
        intStack.push(3);

        floatStack.push(2.0f);
        floatStack.push(2.0f);
        floatStack.push(2.0f);

        assertEquals(intStack, interpreter.intStack());
        assertEquals(floatStack, interpreter.floatStack());
        assertEquals(booleanStack, interpreter.boolStack());
    }

    @Test
    public void testCodeDoTimes() {
        Program p = new Program("( 1 3 " +
                "code.quote 2.0 code.do*times )");
        interpreter.execute(p);

        intStack.push(1);

        floatStack.push(2.0f);
        floatStack.push(2.0f);
        floatStack.push(2.0f);

        assertEquals(intStack, interpreter.intStack());
        assertEquals(floatStack, interpreter.floatStack());
        assertEquals(booleanStack, interpreter.boolStack());
    }

    @Test
    public void testCodeDoCount() {
        Program p = new Program("( 1 3 " +
                "code.quote 2.0 code.do*count )");
        interpreter.execute(p);

        intStack.push(1);
        intStack.push(0);
        intStack.push(1);
        intStack.push(2);

        floatStack.push(2.0f);
        floatStack.push(2.0f);
        floatStack.push(2.0f);

        assertEquals(intStack, interpreter.intStack());
        assertEquals(floatStack, interpreter.floatStack());
        assertEquals(booleanStack, interpreter.boolStack());
    }
}
