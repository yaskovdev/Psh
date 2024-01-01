package org.spiderland.Psh;

/**
 * The Push stack type for object-based data (Strings, Programs, etc.)
 */
public class ObjectStack extends Stack {
    private static final long serialVersionUID = 1L;

    protected Object[] stack;
    final static int blocksize = 16;

    public void pushAllReverse(ObjectStack inOther) {
        for (int n = size - 1; n >= 0; n--)
            inOther.push(stack[n]);
    }

    public boolean equals(Object inOther) {
        if (this == inOther)
            return true;

        if (!(inOther instanceof ObjectStack))
            return false;

        return ((ObjectStack) inOther).compareStack(stack, size);
    }

    boolean compareStack(Object[] inOther, int inOtherSize) {
        if (inOtherSize != size)
            return false;

        for (int n = 0; n < size; n++) {
            if (!stack[n].equals(inOther[n]))
                return false;
        }

        return true;
    }

    void resize(int inSize) {
        Object[] newstack = new Object[inSize];

        if (stack != null)
            System.arraycopy(stack, 0, newstack, 0, size);

        stack = newstack;
        maxsize = inSize;
    }

    public Object peek(int inIndex) {
        return inIndex >= 0 && inIndex < size ? stack[inIndex] : null;
    }

    public Object top() {
        return peek(size - 1);
    }

    public Object pop() {
        Object result = null;

        if (size > 0) {
            result = stack[size - 1];
            size--;
        }

        return result;
    }

    public void push(Object inValue) {
        if (inValue instanceof Program)
            inValue = new Program((Program) inValue);

        stack[size] = inValue;
        size++;

        if (size >= maxsize)
            resize(maxsize + blocksize);
    }

    public void dup() {
        if (size > 0)
            push(stack[size - 1]);
    }

    public void shove(Object obj, int n) {
        if (n > size)
            n = size;

        // n = 0 is the same as push, so
        // the position in the array we insert at is
        // size - n.

        n = size - n;

        for (int i = size; i > n; i--)
            stack[i] = stack[i - 1];
        stack[n] = obj;
        size++;
        if (size >= maxsize)
            resize(maxsize + blocksize);
    }

    public void shove(int inIndex) {
        if (size > 0) {
            if (inIndex < 0) {
                inIndex = 0;
            }
            if (inIndex > size - 1) {
                inIndex = size - 1;
            }

            Object toShove = top();
            int shovedIndex = size - inIndex - 1;

            for (int i = size - 1; i > shovedIndex; i--) {
                stack[i] = stack[i - 1];
            }
            stack[shovedIndex] = toShove;
        }
    }

    public void swap() {
        if (size > 1) {
            Object tmp = stack[size - 2];
            stack[size - 2] = stack[size - 1];
            stack[size - 1] = tmp;
        }
    }

    public void rot() {
        if (size > 2) {
            Object tmp = stack[size - 3];
            stack[size - 3] = stack[size - 2];
            stack[size - 2] = stack[size - 1];
            stack[size - 1] = tmp;
        }
    }

    public void yank(int inIndex) {
        if (size > 0) {
            if (inIndex < 0) {
                inIndex = 0;
            }
            if (inIndex > size - 1) {
                inIndex = size - 1;
            }

            int yankedIndex = size - inIndex - 1;
            Object toYank = peek(yankedIndex);

            for (int i = yankedIndex; i < size - 1; i++) {
                stack[i] = stack[i + 1];
            }
            stack[size - 1] = toYank;
        }
    }

    public void yankdup(int inIndex) {
        if (size > 0) {
            if (inIndex < 0) {
                inIndex = 0;
            }
            if (inIndex > size - 1) {
                inIndex = size - 1;
            }

            int yankedIndex = size - inIndex - 1;
            push(peek(yankedIndex));
        }
    }

    public String toString() {
        String result = "[";

        for (int n = size - 1; n >= 0; n--) {

            if (n == size - 1)
                result += stack[n];
            else
                result += " " + stack[n];
        }
        result += "]";

        return result;
    }
}
