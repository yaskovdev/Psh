package org.spiderland.Psh;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class ProgramTest {

    @Test
    public void testEquals() {
        // Equality testing of nested programs

        Program p = new Program("( 1.0 ( TEST 2 ( 3 ) ) )");
        Program q = new Program("( 1.0 ( TEST 2 ( 3 ) ) )");
        Program r = new Program("( 2.0 ( TEST 2 ( 3 ) ) )");

        assertNotEquals(p, r);
        assertEquals(p, q);
    }

    @Test
    public void testParse() {
        // Parse a program, and then reparse its string representation.
        // They should be equal.

        String program = "(1(2) (3) TEST TEST (2 TEST))";
        Program p = new Program(program);
        Program q = new Program(p.toString());

        assertEquals(p, q);
    }

    @Test
    public void testSubtreeFetch() {
        Program p = new Program("( 2.0 ( TEST 2 ( 3 ) ) )");
        assertEquals(new Program("(TEST 2 (3))"), p.subtree(1));
    }

    @Test
    public void testSubtreeReplace() {
        Program p = new Program("( 2.0 ( TEST 2 ( 3 ) ) )");

        p.replaceSubtree(0, 3);
        p.replaceSubtree(2, "TEST2");
        p.replaceSubtree(3, new Program("( X )"));

        Program q = new Program("( 3 ( TEST2 ( X ) ( 3 ) ) )");

        assertEquals(q, p);
    }
}
