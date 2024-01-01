package org.spiderland.Psh;

import lombok.Data;

import java.io.Serializable;

/**
 * An abstract container for a pair of objects.
 */

@Data
public class ObjectPair implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Object first;
    private final Object second;

    public String toString() {
        return "<" + first.toString() + ", " + second.toString() + ">";
    }

}
