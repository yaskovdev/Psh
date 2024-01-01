package org.spiderland.Psh;

import java.io.Serializable;
import java.util.Objects;

/**
 * An abstract container for a GATestCase containing an input and output object.
 */
public record GATestCase(Object input, Object output) implements Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GATestCase that = (GATestCase) o;
        return input.equals(that.input) && Objects.equals(output, that.output);
    }

    @Override
    public int hashCode() {
        return Objects.hash(input, output);
    }
}
