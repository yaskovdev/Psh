package org.spiderland.Psh;

import java.util.Objects;

/**
 * A PushGA individual class which is a simple wrapper around a Push Program object.
 */
public class PushGPIndividual extends GAIndividual {
    private static final long serialVersionUID = 1L;

    public Program program;

    public PushGPIndividual() {
    }

    PushGPIndividual(Program inProgram) {
        setProgram(inProgram);
        fitnessSet = false;
    }

    void setProgram(Program inProgram) {
        if (inProgram != null) {
            program = new Program(inProgram);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PushGPIndividual that = (PushGPIndividual) o;
        return program.equals(that.program);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), program);
    }

    public String toString() {
        return program.toString();
    }

    public GAIndividual clone() {
        return new PushGPIndividual(program);
    }
}
