package org.spiderland.Psh;

import java.io.Serializable;

/**
 * Abstract class for implementing stacks.
 */
abstract class Stack implements Serializable {
    private static final long serialVersionUID = 1L;

    protected int size;
    protected int maxsize;

    Stack() {
        size = 0;
        resize(8);
    }

    abstract void resize(int inSize);

    abstract void dup();

    abstract void rot();

    abstract void shove(int inIndex);

    abstract void swap();

    abstract void yank(int inIndex);

    abstract void yankdup(int inIndex);

    public void clear() {
        size = 0;
    }

    public int size() {
        return size;
    }

    public void popdiscard() {
        if (size > 0)
            size--;
    }
}
