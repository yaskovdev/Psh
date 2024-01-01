package org.spiderland.Psh;

import lombok.EqualsAndHashCode;

/**
 * A PushGA individual class which is a simple wrapper around a Push Program
 * object.
 */
@EqualsAndHashCode
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

    public String toString() {
        return program.toString();
    }

    public GAIndividual clone() {
        return new PushGPIndividual(program);
    }
}
