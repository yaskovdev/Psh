package org.spiderland.Psh;

/**
 * The Push stack type for object-based data (Strings, Programs, etc.)
 */
public class FloatStack extends Stack {
    private static final long serialVersionUID = 1L;

    protected float[] stack;

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final FloatStack other = (FloatStack) obj;
        if (size != other.size)
            return false;
        for (int i = 0; i < size; i++)
            if (stack[i] != other.stack[i])
                return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        for (int i = 0; i < size; i++)
            hash = 41 * hash + Float.valueOf(stack[i]).hashCode();
        return hash;
    }

    void resize(int inSize) {
        float[] newstack = new float[inSize];

        if (stack != null)
            System.arraycopy(stack, 0, newstack, 0, size);

        stack = newstack;
        maxsize = inSize;
    }

    public float accumulate() {
        float f = 0;

        for (int n = 0; n < size; n++) {
            f += stack[n];
        }

        return f;
    }

    public float top() {
        return peek(size - 1);
    }

    public float peek(int inIndex) {
        if (inIndex >= 0 && inIndex < size)
            return stack[inIndex];

        return 0.0f;
    }

    public float pop() {
        float result = 0.0f;

        if (size > 0) {
            result = stack[size - 1];
            size--;
        }

        return result;
    }

    public void push(float inValue) {
        stack[size] = inValue;
        size++;

        if (size >= maxsize)
            resize(maxsize * 2);
    }

    public void dup() {
        if (size > 0)
            push(stack[size - 1]);
    }

    public void shove(int inIndex) {
        if (size > 0) {
            if (inIndex < 0) {
                inIndex = 0;
            }
            if (inIndex > size - 1) {
                inIndex = size - 1;
            }

            float toShove = top();
            int shovedIndex = size - inIndex - 1;

            for (int i = size - 1; i > shovedIndex; i--) {
                stack[i] = stack[i - 1];
            }
            stack[shovedIndex] = toShove;
        }
    }

    public void swap() {
        if (size > 1) {
            float tmp = stack[size - 1];
            stack[size - 1] = stack[size - 2];
            stack[size - 2] = tmp;
        }
    }

    public void rot() {
        if (size > 2) {
            float tmp = stack[size - 3];
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
            float toYank = peek(yankedIndex);

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

    public void set(int inIndex, float inValue) {
        if (inIndex >= 0 && inIndex < size)
            stack[inIndex] = inValue;
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
