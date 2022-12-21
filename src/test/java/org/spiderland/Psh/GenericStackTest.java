/*
 * Copyright 2009-2010 Jon Klein and Robert Baruch
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.spiderland.Psh;

import org.junit.Assert;
import org.junit.Test;

import java.util.Vector;

public class GenericStackTest {
    @Test
    public void testPushPop() throws Exception
    {
        GenericStack<String> stringStack = new GenericStack<String>();
        GenericStack<Vector<String>> stringVectorStack = new GenericStack<Vector<String>>();

        Vector<String> vect = new Vector<String>();
        vect.add("a string in a vector 1");
        vect.add("another string 2");

        stringStack.push("value 1");
        stringVectorStack.push(vect);

        stringStack.push("value 2");
        stringVectorStack.push(null);

        Assert.assertEquals(2, stringStack.size());
        Assert.assertEquals(2, stringVectorStack.size());

        Assert.assertEquals("value 2", stringStack.pop());
        Assert.assertEquals(1, stringStack.size());
        Assert.assertEquals("value 1", stringStack.pop());
        Assert.assertEquals(0, stringStack.size());

        Assert.assertNull(stringVectorStack.pop());
        Assert.assertEquals(vect, stringVectorStack.pop());

        Assert.assertNull(stringStack.pop());
        Assert.assertEquals(0, stringStack.size());
    }

    @Test
    public void testPushAllReverse() throws Exception
    {
        GenericStack<String> stringStack = new GenericStack<String>();

        stringStack.push("value 1");
        stringStack.push("value 2");

        GenericStack<String> stringStack2 = new GenericStack<String>();

        stringStack.PushAllReverse(stringStack2);

        Assert.assertEquals(2, stringStack.size());
        Assert.assertEquals(2, stringStack2.size());
        Assert.assertEquals("value 1", stringStack2.pop());
        Assert.assertEquals("value 2", stringStack2.pop());
    }

    @Test
    public void testEquals() throws Exception
    {
        GenericStack<String> stringStack = new GenericStack<String>();
        GenericStack<String> stringStack2 = new GenericStack<String>();
        GenericStack<Vector<String>> stringVectorStack = new GenericStack<Vector<String>>();

        System.out.println("StringStack type is " + stringStack.getClass());
        Assert.assertTrue(stringStack.equals(stringVectorStack)); // see note in equals
        Assert.assertTrue(stringStack.equals(stringStack2));

        Assert.assertEquals(stringStack.hashCode(), stringStack2.hashCode());
        Assert.assertEquals(stringStack.hashCode(), stringVectorStack.hashCode()); // see note in equals

        stringStack.push("value 1");
        Assert.assertFalse(stringStack.equals(stringStack2));
        Assert.assertFalse(stringStack.equals(stringVectorStack));
        Assert.assertFalse(stringStack.hashCode() == stringStack2.hashCode());

        stringStack2.push("value 1");
        Assert.assertTrue(stringStack.equals(stringStack2));

        Assert.assertEquals(stringStack.hashCode(), stringStack2.hashCode());
    }

    @Test
    public void testPeek() throws Exception
    {
        GenericStack<String> stringStack = new GenericStack<String>();

        stringStack.push("value 1");
        stringStack.push("value 2");

        Assert.assertEquals("value 1", stringStack.peek(0)); // deepest stack
        Assert.assertEquals(2, stringStack.size());
        Assert.assertEquals("value 2", stringStack.top());
        Assert.assertEquals(2, stringStack.size());
        Assert.assertEquals("value 2", stringStack.peek(1));
    }

    @Test
    public void testDup() throws Exception
    {
        GenericStack<String> stringStack = new GenericStack<String>();

        stringStack.dup();
        Assert.assertEquals(0, stringStack.size());

        stringStack.push("value 1");
        stringStack.push("value 2");
        stringStack.dup();

        Assert.assertEquals(3, stringStack.size());
        Assert.assertEquals("value 2", stringStack.peek(2));
        Assert.assertEquals("value 2", stringStack.peek(1));
        Assert.assertEquals("value 1", stringStack.peek(0));
    }

    @Test
    public void testSwap() throws Exception
    {
        GenericStack<String> stringStack = new GenericStack<String>();

        stringStack.push("value 1");
        stringStack.swap();
        Assert.assertEquals(1, stringStack.size());
        Assert.assertEquals("value 1", stringStack.peek(0));

        stringStack.push("value 2");
        stringStack.swap();

        Assert.assertEquals(2, stringStack.size());
        Assert.assertEquals("value 1", stringStack.peek(1));
        Assert.assertEquals("value 2", stringStack.peek(0));
    }

    @Test
    public void testRot() throws Exception
    {
        GenericStack<String> stringStack = new GenericStack<String>();

        stringStack.push("value 1");
        stringStack.push("value 2");
        stringStack.rot();

        Assert.assertEquals(2, stringStack.size());
        Assert.assertEquals("value 2", stringStack.peek(1));
        Assert.assertEquals("value 1", stringStack.peek(0));

        stringStack.push("value 3");
        stringStack.push("value 4");
        stringStack.rot();
        Assert.assertEquals(4, stringStack.size());
        Assert.assertEquals("value 2", stringStack.peek(3));
        Assert.assertEquals("value 4", stringStack.peek(2));
        Assert.assertEquals("value 3", stringStack.peek(1));
        Assert.assertEquals("value 1", stringStack.peek(0));
   }

    @Test
    public void testShove() throws Exception
    {
        GenericStack<String> stringStack = new GenericStack<String>();

        stringStack.shove("value 1", 0);
        Assert.assertEquals(1, stringStack.size());
        Assert.assertEquals("value 1", stringStack.peek(0));

        stringStack.shove("value 2", 1);
        Assert.assertEquals(2, stringStack.size());
        Assert.assertEquals("value 2", stringStack.peek(0));
        Assert.assertEquals("value 1", stringStack.peek(1));

        stringStack.shove("value 3", 1);
        Assert.assertEquals(3, stringStack.size());
        Assert.assertEquals("value 2", stringStack.peek(0));
        Assert.assertEquals("value 3", stringStack.peek(1));
        Assert.assertEquals("value 1", stringStack.peek(2));

    }
}
