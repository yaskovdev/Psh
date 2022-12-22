package org.spiderland.Psh;

import org.junit.jupiter.api.Test;

import java.util.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class GenericStackTest {
    @Test
    public void testPushPop() {
        GenericStack<String> stringStack = new GenericStack<>();
        GenericStack<Vector<String>> stringVectorStack = new GenericStack<>();

        Vector<String> vect = new Vector<>();
        vect.add("a string in a vector 1");
        vect.add("another string 2");

        stringStack.push("value 1");
        stringVectorStack.push(vect);

        stringStack.push("value 2");
        stringVectorStack.push(null);

        assertEquals(2, stringStack.size());
        assertEquals(2, stringVectorStack.size());

        assertEquals("value 2", stringStack.pop());
        assertEquals(1, stringStack.size());
        assertEquals("value 1", stringStack.pop());
        assertEquals(0, stringStack.size());

        assertNull(stringVectorStack.pop());
        assertEquals(vect, stringVectorStack.pop());

        assertNull(stringStack.pop());
        assertEquals(0, stringStack.size());
    }

    @Test
    public void testPushAllReverse() {
        GenericStack<String> stringStack = new GenericStack<>();

        stringStack.push("value 1");
        stringStack.push("value 2");

        GenericStack<String> stringStack2 = new GenericStack<>();

        stringStack.PushAllReverse(stringStack2);

        assertEquals(2, stringStack.size());
        assertEquals(2, stringStack2.size());
        assertEquals("value 1", stringStack2.pop());
        assertEquals("value 2", stringStack2.pop());
    }

    @Test
    public void testEquals() {
        GenericStack<String> stringStack = new GenericStack<>();
        GenericStack<String> stringStack2 = new GenericStack<>();
        GenericStack<Vector<String>> stringVectorStack = new GenericStack<>();

        System.out.println("StringStack type is " + stringStack.getClass());
        assertEquals(stringStack, stringVectorStack); // see note in equals
        assertEquals(stringStack, stringStack2);

        assertEquals(stringStack.hashCode(), stringStack2.hashCode());
        assertEquals(stringStack.hashCode(), stringVectorStack.hashCode()); // see note in equals

        stringStack.push("value 1");
        assertNotEquals(stringStack, stringStack2);
        assertNotEquals(stringStack, stringVectorStack);
        assertNotEquals(stringStack.hashCode(), stringStack2.hashCode());

        stringStack2.push("value 1");
        assertEquals(stringStack, stringStack2);

        assertEquals(stringStack.hashCode(), stringStack2.hashCode());
    }

    @Test
    public void testPeek() {
        GenericStack<String> stringStack = new GenericStack<>();

        stringStack.push("value 1");
        stringStack.push("value 2");

        assertEquals("value 1", stringStack.peek(0)); // deepest stack
        assertEquals(2, stringStack.size());
        assertEquals("value 2", stringStack.top());
        assertEquals(2, stringStack.size());
        assertEquals("value 2", stringStack.peek(1));
    }

    @Test
    public void testDup() {
        GenericStack<String> stringStack = new GenericStack<>();

        stringStack.dup();
        assertEquals(0, stringStack.size());

        stringStack.push("value 1");
        stringStack.push("value 2");
        stringStack.dup();

        assertEquals(3, stringStack.size());
        assertEquals("value 2", stringStack.peek(2));
        assertEquals("value 2", stringStack.peek(1));
        assertEquals("value 1", stringStack.peek(0));
    }

    @Test
    public void testSwap() {
        GenericStack<String> stringStack = new GenericStack<>();

        stringStack.push("value 1");
        stringStack.swap();
        assertEquals(1, stringStack.size());
        assertEquals("value 1", stringStack.peek(0));

        stringStack.push("value 2");
        stringStack.swap();

        assertEquals(2, stringStack.size());
        assertEquals("value 1", stringStack.peek(1));
        assertEquals("value 2", stringStack.peek(0));
    }

    @Test
    public void testRot() {
        GenericStack<String> stringStack = new GenericStack<>();

        stringStack.push("value 1");
        stringStack.push("value 2");
        stringStack.rot();

        assertEquals(2, stringStack.size());
        assertEquals("value 2", stringStack.peek(1));
        assertEquals("value 1", stringStack.peek(0));

        stringStack.push("value 3");
        stringStack.push("value 4");
        stringStack.rot();
        assertEquals(4, stringStack.size());
        assertEquals("value 2", stringStack.peek(3));
        assertEquals("value 4", stringStack.peek(2));
        assertEquals("value 3", stringStack.peek(1));
        assertEquals("value 1", stringStack.peek(0));
   }

    @Test
    public void testShove() {
        GenericStack<String> stringStack = new GenericStack<String>();

        stringStack.shove("value 1", 0);
        assertEquals(1, stringStack.size());
        assertEquals("value 1", stringStack.peek(0));

        stringStack.shove("value 2", 1);
        assertEquals(2, stringStack.size());
        assertEquals("value 2", stringStack.peek(0));
        assertEquals("value 1", stringStack.peek(1));

        stringStack.shove("value 3", 1);
        assertEquals(3, stringStack.size());
        assertEquals("value 2", stringStack.peek(0));
        assertEquals("value 3", stringStack.peek(1));
        assertEquals("value 1", stringStack.peek(2));

    }
}
