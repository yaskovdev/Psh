package org.spiderland.Psh;

/**
 * A PushGA individual class which is a simple wrapper around a Push Program
 * object.
 */
public class PushGPIndividual extends GAIndividual {

    public Program _program;

    public PushGPIndividual() {
    }

    PushGPIndividual(Program inProgram) {
        SetProgram(inProgram);
        _fitnessSet = false;
    }

    void SetProgram(Program inProgram) {
        if (inProgram != null)
            _program = new Program(inProgram);
    }

    public String toString() {
        return _program.toString();
    }

    public GAIndividual clone() {
        return new PushGPIndividual(_program);
    }
}
