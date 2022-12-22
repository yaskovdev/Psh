package org.spiderland.Psh;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InstructionTest {
    protected Interpreter interpreter = null;
    protected IntStack istack = null;
    protected FloatStack fstack = null;
    protected BooleanStack bstack = null;

    @BeforeEach
    public void setUp() throws Exception {
        interpreter = new Interpreter();
        Program instructionList = new Program(interpreter, "( )");
        interpreter.SetInstructions(instructionList);
        istack = new IntStack();
        fstack = new FloatStack();
        bstack = new BooleanStack();
    }

    @Test
    public void testNumberName() throws Exception {
        Program p = new Program(interpreter, "( 1 false 1.0 0 0.0 x true )");
        interpreter.Execute(p);
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
    public void testPop() throws Exception {
        Program p = new Program(interpreter, "( 1 2 3 4.0 5.0 true false " +
                "boolean.pop integer.pop float.pop )");
        interpreter.Execute(p);

        istack.push(1);
        istack.push(2);

        fstack.push(4.0f);

        bstack.push(true);

        assertEquals(istack, interpreter.intStack());
        assertEquals(fstack, interpreter.floatStack());
        assertEquals(bstack, interpreter.boolStack());
    }

    @Test
    public void testDup() throws Exception {
        Program p = new Program(interpreter, "( 1 2 3 4.0 5.0 true false " +
                "boolean.dup integer.dup float.dup )");
        interpreter.Execute(p);

        istack.push(1);
        istack.push(2);
        istack.push(3);
        istack.push(3);

        fstack.push(4.0f);
        fstack.push(5.0f);
        fstack.push(5.0f);

        bstack.push(true);
        bstack.push(false);
        bstack.push(false);

        assertEquals(istack, interpreter.intStack());
        assertEquals(fstack, interpreter.floatStack());
        assertEquals(bstack, interpreter.boolStack());
    }

    @Test
    public void testSwap() throws Exception {
        Program p = new Program(interpreter, "( 1 2 3 4.0 5.0 true false " +
                "boolean.swap integer.swap float.swap )");
        interpreter.Execute(p);

        istack.push(1);
        istack.push(3);
        istack.push(2);

        fstack.push(5.0f);
        fstack.push(4.0f);

        bstack.push(false);
        bstack.push(true);

        assertEquals(istack, interpreter.intStack());
        assertEquals(fstack, interpreter.floatStack());
        assertEquals(bstack, interpreter.boolStack());
    }

    @Test
    public void testRot() throws Exception {
        Program p = new Program(interpreter, "( 1 2 3 4.0 5.0 6.0 true false true " +
                "boolean.rot integer.rot float.rot )");
        interpreter.Execute(p);

        istack.push(2);
        istack.push(3);
        istack.push(1);

        fstack.push(5.0f);
        fstack.push(6.0f);
        fstack.push(4.0f);

        bstack.push(false);
        bstack.push(true);
        bstack.push(true);

        assertEquals(istack, interpreter.intStack());
        assertEquals(fstack, interpreter.floatStack());
        assertEquals(bstack, interpreter.boolStack());
    }

    @Test
    public void testFlush() throws Exception {
        Program p = new Program(interpreter, "( 1 2 3 4.0 5.0 true false " +
                "boolean.flush integer.flush float.flush )");
        interpreter.Execute(p);

        assertEquals(0, interpreter.intStack().size());
        assertEquals(0, interpreter.floatStack().size());
        assertEquals(0, interpreter.boolStack().size());
    }

    @Test
    public void testStackDepth() throws Exception {
        Program p = new Program(interpreter, "( 1 2 3 4.0 5.0 true false " +
                "boolean.stackdepth integer.stackdepth float.stackdepth )");
        interpreter.Execute(p);

        istack.push(1);
        istack.push(2);
        istack.push(3);
        istack.push(2);
        istack.push(4);
        istack.push(2);

        assertEquals(istack, interpreter.intStack());
    }

    @Test
    public void testAdd() throws Exception {
        Program p = new Program(interpreter, "( 1 2 3 4.0 5.0 true false " +
                "integer.+ float.+ )");
        interpreter.Execute(p);

        istack.push(1);
        istack.push(5);

        fstack.push(9.0f);

        bstack.push(true);
        bstack.push(false);

        assertEquals(istack, interpreter.intStack());
        assertEquals(fstack, interpreter.floatStack());
        assertEquals(bstack, interpreter.boolStack());
    }

    @Test
    public void testSub() throws Exception {
        Program p = new Program(interpreter, "( 1 2 3 4.0 5.0 true false " +
                "integer.- float.- )");
        interpreter.Execute(p);

        istack.push(1);
        istack.push(-1);

        fstack.push(-1.0f);

        bstack.push(true);
        bstack.push(false);

        assertEquals(istack, interpreter.intStack());
        assertEquals(fstack, interpreter.floatStack());
        assertEquals(bstack, interpreter.boolStack());
    }

    @Test
    public void testMul() throws Exception {
        Program p = new Program(interpreter, "( 1 2 3 4.0 5.0 true false " +
                "integer.* float.* )");
        interpreter.Execute(p);

        istack.push(1);
        istack.push(6);

        fstack.push(20.0f);

        bstack.push(true);
        bstack.push(false);

        assertEquals(istack, interpreter.intStack());
        assertEquals(fstack, interpreter.floatStack());
        assertEquals(bstack, interpreter.boolStack());
    }

    @Test
    public void testDiv() throws Exception {
        Program p = new Program(interpreter, "( 1 2 3 4.0 5.0 true false " +
                "integer./ float./ )");
        interpreter.Execute(p);

        istack.push(1);
        istack.push(0);

        fstack.push(4.0f / 5.0f);

        bstack.push(true);
        bstack.push(false);

        assertEquals(istack, interpreter.intStack());
        assertEquals(fstack, interpreter.floatStack());
        assertEquals(bstack, interpreter.boolStack());
    }

    @Test
    public void testMod() throws Exception {
        Program p = new Program(interpreter, "( 1 5 3 7.0 5.0 true false " +
                "integer.% float.% )");
        interpreter.Execute(p);

        istack.push(1);
        istack.push(2);

        fstack.push(2.0f);

        bstack.push(true);
        bstack.push(false);

        assertEquals(istack, interpreter.intStack());
        assertEquals(fstack, interpreter.floatStack());
        assertEquals(bstack, interpreter.boolStack());
    }

    @Test
    public void testEq() throws Exception {
        Program p = new Program(interpreter, "( 1 3 3 7.0 5.0 true false " +
                "integer.= float.= true false boolean.= false false boolean.=)");
        interpreter.Execute(p);

        istack.push(1);

        bstack.push(true);
        bstack.push(false);
        bstack.push(true);
        bstack.push(false);
        bstack.push(false);
        bstack.push(true);

        assertEquals(istack, interpreter.intStack());
        assertEquals(fstack, interpreter.floatStack());
        assertEquals(bstack, interpreter.boolStack());
    }

    @Test
    public void testLt() throws Exception {
        Program p = new Program(interpreter, "( 1 3 3 5.0 6.0 true false " +
                "integer.< float.< )");
        interpreter.Execute(p);

        istack.push(1);

        bstack.push(true);
        bstack.push(false);
        bstack.push(false);
        bstack.push(true);

        assertEquals(istack, interpreter.intStack());
        assertEquals(fstack, interpreter.floatStack());
        assertEquals(bstack, interpreter.boolStack());
    }

    @Test
    public void testGt() throws Exception {
        Program p = new Program(interpreter, "( 1 3 3 5.0 6.0 true false " +
                "integer.> float.> )");
        interpreter.Execute(p);

        istack.push(1);

        bstack.push(true);
        bstack.push(false);
        bstack.push(false);
        bstack.push(false);

        assertEquals(istack, interpreter.intStack());
        assertEquals(fstack, interpreter.floatStack());
        assertEquals(bstack, interpreter.boolStack());
    }

    @Test
    public void testBoolOps() throws Exception {
        Program p = new Program(interpreter, "( true false boolean.or " +
                "true false boolean.and true false boolean.xor true boolean.not )");
        interpreter.Execute(p);

        bstack.push(true);
        bstack.push(false);
        bstack.push(true);
        bstack.push(false);

        assertEquals(istack, interpreter.intStack());
        assertEquals(fstack, interpreter.floatStack());
        assertEquals(bstack, interpreter.boolStack());
    }

    @Test
    public void testInputIndex() throws Exception {
        Program p = new Program(interpreter, "( 1 input.index 1 input.index 0 input.index " +
                "0 input.index 2 input.index 2 input.index 1000 input.index -1 input.index)");
        interpreter.inputStack().push(true);
        interpreter.inputStack().push(3);
        interpreter.inputStack().push(2.0f);

        interpreter.Execute(p);

        istack.push(3);
        istack.push(3);

        fstack.push(2.0f);
        fstack.push(2.0f);
        fstack.push(2.0f);

        bstack.push(true);
        bstack.push(true);
        bstack.push(true);

        ObjectStack inputs = new ObjectStack();
        inputs.push(true);
        inputs.push(3);
        inputs.push(2.0f);

        assertEquals(istack, interpreter.intStack());
        assertEquals(fstack, interpreter.floatStack());
        assertEquals(bstack, interpreter.boolStack());
        assertEquals(inputs, interpreter.inputStack());
    }

    @Test
    public void testInputStackDepth() throws Exception {
        Program p = new Program(interpreter, "( input.stackdepth )");
        interpreter.inputStack().push(true);
        interpreter.inputStack().push(3);
        interpreter.inputStack().push(2.0f);
        interpreter.inputStack().push(1.0f);

        interpreter.Execute(p);

        istack.push(4);

        assertEquals(istack, interpreter.intStack());
        assertEquals(fstack, interpreter.floatStack());
        assertEquals(bstack, interpreter.boolStack());
    }

    @Test
    public void testInputInAll() throws Exception {
        Program p = new Program(interpreter, "( input.inall )");
        interpreter.inputStack().push(true);
        interpreter.inputStack().push(3);
        interpreter.inputStack().push(2.0f);
        interpreter.inputStack().push(1.0f);

        interpreter.Execute(p);

        istack.push(3);

        fstack.push(2.0f);
        fstack.push(1.0f);

        bstack.push(true);

        assertEquals(istack, interpreter.intStack());
        assertEquals(fstack, interpreter.floatStack());
        assertEquals(bstack, interpreter.boolStack());
    }

    @Test
    public void testInputInAllRev() throws Exception {
        Program p = new Program(interpreter, "( input.inallrev )");
        interpreter.inputStack().push(true);
        interpreter.inputStack().push(3);
        interpreter.inputStack().push(2.0f);
        interpreter.inputStack().push(1.0f);

        interpreter.Execute(p);

        istack.push(3);

        fstack.push(1.0f);
        fstack.push(2.0f);

        bstack.push(true);

        assertEquals(istack, interpreter.intStack());
        assertEquals(fstack, interpreter.floatStack());
        assertEquals(bstack, interpreter.boolStack());
    }

    @Test
    @Disabled
    public void testCodeQuote() throws Exception {
        Program p = new Program(interpreter, "( 1 code.quote integer.pop code.quote code.quote)");
        interpreter.Execute(p);

        istack.push(1);

        assertEquals(istack, interpreter.intStack());
        assertEquals(fstack, interpreter.floatStack());
        assertEquals(bstack, interpreter.boolStack());
        assertEquals(interpreter.GetInstruction("code.quote"), interpreter.codeStack().pop());
        assertEquals(interpreter.GetInstruction("integer.pop"), interpreter.codeStack().pop());
    }

    @Test
    public void testCodeEquals() throws Exception {
        Program p = new Program(interpreter, "( 1 " +
                "code.quote integer.pop code.quote integer.pop code.= " +
                "code.quote integer.pop code.quote integer.+ code.= )");
        interpreter.Execute(p);

        istack.push(1);

        bstack.push(true);
        bstack.push(false);

        assertEquals(istack, interpreter.intStack());
        assertEquals(fstack, interpreter.floatStack());
        assertEquals(bstack, interpreter.boolStack());
    }

    @Test
    public void testExecEquals() throws Exception {
        Program p = new Program(interpreter, "( 1 " +
                "exec.= code.quote integer.pop " +
                "exec.= integer.pop integer.pop )");
        interpreter.Execute(p);

        istack.push(1);

        bstack.push(false);
        bstack.push(true);

        assertEquals(istack, interpreter.intStack());
        assertEquals(fstack, interpreter.floatStack());
        assertEquals(bstack, interpreter.boolStack());
    }

    @Test
    public void testCodeIf() throws Exception {
        Program p = new Program(interpreter, "( 1 2 1.0 2.0 " +
                "code.quote integer.pop code.quote float.pop true code.if " +
                "code.quote integer.pop code.quote float.pop false code.if )");
        interpreter.Execute(p);

        istack.push(1);

        fstack.push(1.0f);

        assertEquals(istack, interpreter.intStack());
        assertEquals(fstack, interpreter.floatStack());
        assertEquals(bstack, interpreter.boolStack());
    }

    @Test
    public void testExecIf() throws Exception {
        Program p = new Program(interpreter, "( 1 2 1.0 2.0 " +
                "true exec.if integer.pop float.pop " +
                "false exec.if integer.pop float.pop )");
        interpreter.Execute(p);

        istack.push(1);

        fstack.push(1.0f);

        assertEquals(istack, interpreter.intStack());
        assertEquals(fstack, interpreter.floatStack());
        assertEquals(bstack, interpreter.boolStack());
    }

    @Test
    public void testExecDoRange() throws Exception {
        Program p = new Program(interpreter, "( 1 3 " +
                "exec.do*range 2.0 )");
        interpreter.Execute(p);

        istack.push(1);
        istack.push(2);
        istack.push(3);

        fstack.push(2.0f);
        fstack.push(2.0f);
        fstack.push(2.0f);

        assertEquals(istack, interpreter.intStack());
        assertEquals(fstack, interpreter.floatStack());
        assertEquals(bstack, interpreter.boolStack());
    }

    @Test
    public void testExecDoTimes() throws Exception {
        Program p = new Program(interpreter, "( 1 3 " +
                "exec.do*times 2.0 )");
        interpreter.Execute(p);

        istack.push(1);

        fstack.push(2.0f);
        fstack.push(2.0f);
        fstack.push(2.0f);

        assertEquals(istack, interpreter.intStack());
        assertEquals(fstack, interpreter.floatStack());
        assertEquals(bstack, interpreter.boolStack());
    }

    @Test
    public void testExecDoCount() throws Exception {
        Program p = new Program(interpreter, "( 1 3 " +
                "exec.do*count 2.0 )");
        interpreter.Execute(p);

        istack.push(1);
        istack.push(0);
        istack.push(1);
        istack.push(2);

        fstack.push(2.0f);
        fstack.push(2.0f);
        fstack.push(2.0f);

        assertEquals(istack, interpreter.intStack());
        assertEquals(fstack, interpreter.floatStack());
        assertEquals(bstack, interpreter.boolStack());
    }

    @Test
    public void testCodeDoRange() throws Exception {
        Program p = new Program(interpreter, "( 1 3 " +
                "code.quote 2.0 code.do*range )");
        interpreter.Execute(p);

        istack.push(1);
        istack.push(2);
        istack.push(3);

        fstack.push(2.0f);
        fstack.push(2.0f);
        fstack.push(2.0f);

        assertEquals(istack, interpreter.intStack());
        assertEquals(fstack, interpreter.floatStack());
        assertEquals(bstack, interpreter.boolStack());
    }

    @Test
    public void testCodeDoTimes() throws Exception {
        Program p = new Program(interpreter, "( 1 3 " +
                "code.quote 2.0 code.do*times )");
        interpreter.Execute(p);

        istack.push(1);

        fstack.push(2.0f);
        fstack.push(2.0f);
        fstack.push(2.0f);

        assertEquals(istack, interpreter.intStack());
        assertEquals(fstack, interpreter.floatStack());
        assertEquals(bstack, interpreter.boolStack());
    }

    @Test
    public void testCodeDoCount() throws Exception {
        Program p = new Program(interpreter, "( 1 3 " +
                "code.quote 2.0 code.do*count )");
        interpreter.Execute(p);

        istack.push(1);
        istack.push(0);
        istack.push(1);
        istack.push(2);

        fstack.push(2.0f);
        fstack.push(2.0f);
        fstack.push(2.0f);

        assertEquals(istack, interpreter.intStack());
        assertEquals(fstack, interpreter.floatStack());
        assertEquals(bstack, interpreter.boolStack());
    }
}
