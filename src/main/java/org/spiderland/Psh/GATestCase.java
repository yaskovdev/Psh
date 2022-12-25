package org.spiderland.Psh;

import java.io.Serializable;

/**
 * An abstract container for a GATestCase containing an input and output object.
 */

public record GATestCase(Object input, Object output) implements Serializable {
    private static final long serialVersionUID = 1L;
}
