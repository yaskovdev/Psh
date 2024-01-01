package org.spiderland.Psh.Coevolution;

import org.spiderland.Psh.GAIndividual;
import org.spiderland.Psh.Program;
import org.spiderland.Psh.PushGP;
import org.spiderland.Psh.PushGPIndividual;

public class GenericPredictionIndividual extends PredictionGAIndividual {
    private static final long serialVersionUID = 1L;

    protected Program program;

    protected PushGP solutionGeneticAlgorithm;

    public GenericPredictionIndividual() {
        solutionGeneticAlgorithm = null;
    }

    public GenericPredictionIndividual(Program inProgram, PushGP inSolutionGA) {
        program = inProgram;
        solutionGeneticAlgorithm = inSolutionGA;
    }

    @Override
    public float PredictSolutionFitness(PushGPIndividual pgpIndividual) {
        //TODO implement program being run to predict fitness


        return -2999;
    }

    @Override
    public GAIndividual clone() {
        return new GenericPredictionIndividual(program, solutionGeneticAlgorithm);
    }

    void SetProgram(Program inProgram) {
        if (inProgram != null)
            program = new Program(inProgram);
    }

    public String toString() {
        return program.toString();
    }

}
