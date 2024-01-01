package org.spiderland.Psh;

import java.io.Serializable;

/**
 * An abstract container for a pair of objects.
 */
public record ObjectPair(Object first, Object second) implements Serializable {

    private static final long serialVersionUID = 1L;

    public String toString() {
        return "<" + first.toString() + ", " + second.toString() + ">";
    }
}
