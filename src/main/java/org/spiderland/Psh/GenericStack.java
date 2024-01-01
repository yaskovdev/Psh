package org.spiderland.Psh;

import java.util.Arrays;

/**
 * The Push stack type for generic data (Strings, Programs, etc.)
 */

public class GenericStack<T> extends Stack {
    private static final long serialVersionUID = 1L;

    protected T[] stack;
    final static int blocksize = 16;

    public void PushAllReverse(GenericStack<T> inOther) {
        for (int n = size - 1; n >= 0; n--)
            inOther.push(stack[n]);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object inOther) {
        if (this == inOther)
            return true;

        // Sadly, because generics are implemented using type erasure,
        // a GenericStack<A> will be the same class as a GenericStack<B>,
        // this being GenericStack. So the best we can do here is be assured
        // that inOther is at least a GenericStack.
        //
        // This just means that every empty stack is the same as every other
        // empty stack.

        if (inOther.getClass() != getClass())
            return false;

        return ((GenericStack<T>) inOther).comparestack(stack, size);
    }

    @Override
    public int hashCode() {
        int hash = getClass().hashCode();
        hash = 37 * hash + Arrays.deepHashCode(this.stack);
        return hash;
    }

    boolean comparestack(T[] inOther, int inOtherSize) {
        if (inOtherSize != size)
            return false;

        for (int n = 0; n < size; n++) {
            if (!stack[n].equals(inOther[n]))
                return false;
        }

        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    void resize(int inSize) {
        T[] newstack = (T[]) new Object[inSize];

        if (stack != null)
            System.arraycopy(stack, 0, newstack, 0, size);

        stack = newstack;
        maxsize = inSize;
    }

    public T peek(int inIndex) {
        if (inIndex >= 0 && inIndex < size)
            return stack[inIndex];

        return null;
    }

    public T top() {
        return peek(size - 1);
    }

    public T pop() {
        T result = null;

        if (size > 0) {
            result = stack[size - 1];
            size--;
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    public void push(T inValue) {
        if (inValue instanceof Program)
            inValue = (T) new Program((Program) inValue);

        stack[size] = inValue;
        size++;

        if (size >= maxsize)
            resize(maxsize + blocksize);
    }

    @Override
    public void dup() {
        if (size > 0)
            push(stack[size - 1]);
    }

    public void shove(T obj, int n) {
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

    @Override
    public void shove(int inIndex) {
        if (size > 0) {
            if (inIndex < 0) {
                inIndex = 0;
            }
            if (inIndex > size - 1) {
                inIndex = size - 1;
            }

            T toShove = top();
            int shovedIndex = size - inIndex - 1;

            for (int i = size - 1; i > shovedIndex; i--) {
                stack[i] = stack[i - 1];
            }
            stack[shovedIndex] = toShove;
        }
    }

    @Override
    public void swap() {
        if (size > 1) {
            T tmp = stack[size - 2];
            stack[size - 2] = stack[size - 1];
            stack[size - 1] = tmp;
        }
    }

    @Override
    public void rot() {
        if (size > 2) {
            T tmp = stack[size - 3];
            stack[size - 3] = stack[size - 2];
            stack[size - 2] = stack[size - 1];
            stack[size - 1] = tmp;
        }
    }

    @Override
    public void yank(int inIndex) {
        if (size > 0) {
            if (inIndex < 0) {
                inIndex = 0;
            }
            if (inIndex > size - 1) {
                inIndex = size - 1;
            }

            int yankedIndex = size - inIndex - 1;
            T toYank = peek(yankedIndex);

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

    @Override
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
