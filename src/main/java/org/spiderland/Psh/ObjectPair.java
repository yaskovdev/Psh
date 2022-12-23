package org.spiderland.Psh;

/**
 * An abstract container for a pair of objects.
 */
public record ObjectPair(Object first, Object second) {

    public String toString() {
        return "<" + first.toString() + ", " + second.toString() + ">";
    }
}
