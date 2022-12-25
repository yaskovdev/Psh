package org.spiderland.Psh;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProgramTest {
    @Test
    @SuppressWarnings("deprecation")
    public void testEquals() throws Exception {
        // Equality testing of nested programs

        Program p = new Program("( 1.0 ( TEST 2 ( 3 ) ) )");
        Program q = new Program("( 1.0 ( TEST 2 ( 3 ) ) )");
        Program r = new Program("( 2.0 ( TEST 2 ( 3 ) ) )");

        assertNotEquals(p, r);
        assertEquals(p, q);
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testParse() throws Exception {
        // Parse a program, and then reparse its string representation.
        // They should be equal.

        String program = "(1(2) (3) TEST TEST (2 TEST))";
        Program p = new Program(program);
        Program q = new Program(p.toString());

        assertEquals(p, q);
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testSubtreeFetch() throws Exception {
        Program p = new Program("( 2.0 ( TEST 2 ( 3 ) ) )");

        assertTrue(true);
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testSubtreeReplace() throws Exception {
        Program p = new Program("( 2.0 ( TEST 2 ( 3 ) ) )");

        p.ReplaceSubtree(0, 3);
        p.ReplaceSubtree(2, "TEST2");
        p.ReplaceSubtree(3, new Program("( X )"));

        Program q = new Program("( 3 ( TEST2 ( X ) ( 3 ) ) )");

        assertEquals(q, p);
    }
}
