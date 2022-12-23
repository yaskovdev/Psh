package org.spiderland.Psh;

/**
 * The Push stack type for integers.
 */

public class IntStack extends Stack {
    private static final long serialVersionUID = 1L;

    protected int[] _stack;

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final IntStack other = (IntStack) obj;
        if (_size != other._size)
            return false;
        for (int i = 0; i < _size; i++)
            if (_stack[i] != other._stack[i])
                return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        for (int i = 0; i < _size; i++)
            hash = 41 * hash + _stack[i];
        return hash;
    }

    void resize(int inSize) {
        int[] newstack = new int[inSize];

        if (_stack != null)
            System.arraycopy(_stack, 0, newstack, 0, _size);

        _stack = newstack;
        _maxsize = inSize;
    }

    public int top() {
        return peek(_size - 1);
    }

    public int peek(int inIndex) {
        if (inIndex >= 0 && inIndex < _size)
            return _stack[inIndex];

        return 0;
    }

    public int pop() {
        int result = 0;

        if (_size > 0) {
            result = _stack[_size - 1];
            _size--;
        }

        return result;
    }

    public void push(int inValue) {
        _stack[_size] = inValue;
        _size++;

        if (_size >= _maxsize)
            resize(_maxsize * 2);
    }

    public void dup() {
        if (_size > 0)
            push(_stack[_size - 1]);
    }

    public void shove(int inIndex) {
        if (_size > 0) {
            if (inIndex < 0) {
                inIndex = 0;
            }
            if (inIndex > _size - 1) {
                inIndex = _size - 1;
            }

            int toShove = top();
            int shovedIndex = _size - inIndex - 1;

            for (int i = _size - 1; i > shovedIndex; i--) {
                _stack[i] = _stack[i - 1];
            }
            _stack[shovedIndex] = toShove;
        }
    }

    public void swap() {
        if (_size > 1) {
            int tmp = _stack[_size - 1];
            _stack[_size - 1] = _stack[_size - 2];
            _stack[_size - 2] = tmp;
        }
    }

    public void rot() {
        if (_size > 2) {
            int tmp = _stack[_size - 3];
            _stack[_size - 3] = _stack[_size - 2];
            _stack[_size - 2] = _stack[_size - 1];
            _stack[_size - 1] = tmp;
        }
    }

    public void yank(int inIndex) {
        if (_size > 0) {
            if (inIndex < 0) {
                inIndex = 0;
            }
            if (inIndex > _size - 1) {
                inIndex = _size - 1;
            }

            int yankedIndex = _size - inIndex - 1;
            int toYank = peek(yankedIndex);

            for (int i = yankedIndex; i < _size - 1; i++) {
                _stack[i] = _stack[i + 1];
            }
            _stack[_size - 1] = toYank;
        }
    }

    public void yankdup(int inIndex) {
        if (_size > 0) {
            if (inIndex < 0) {
                inIndex = 0;
            }
            if (inIndex > _size - 1) {
                inIndex = _size - 1;
            }

            int yankedIndex = _size - inIndex - 1;
            push(peek(yankedIndex));
        }
    }

    public void set(int inIndex, int inValue) {
        if (inIndex >= 0 && inIndex < _size)
            _stack[inIndex] = inValue;
    }

    public String toString() {
        String result = "[";

        for (int n = _size - 1; n >= 0; n--) {
            if (n == _size - 1)
                result += _stack[n];
            else
                result += " " + _stack[n];
        }
        result += "]";

        return result;
    }

}
