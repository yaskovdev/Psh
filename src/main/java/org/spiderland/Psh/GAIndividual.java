package org.spiderland.Psh;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

/**
 * An abstract GP individual class containing a fitness value. The fitness value
 * represents an individual's <i>error</i> values, such that <i>lower</i>
 * fitness values are better and such that a fitness value of 0 indicates a
 * perfect solution.
 */

public abstract class GAIndividual implements Serializable {

    private static final long serialVersionUID = 1L;

    private float fitness;
    private ArrayList<Float> errors;
    protected boolean fitnessSet;

    public float getFitness() {
        return fitness;
    }

    public void setFitness(float inFitness) {
        fitness = inFitness;
        fitnessSet = true;
    }

    public ArrayList<Float> getErrors() {
        return errors;
    }

    public void setErrors(ArrayList<Float> errors) {
        this.errors = errors;
    }

    public boolean isFitnessSet() {
        return fitnessSet;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GAIndividual that = (GAIndividual) o;
        return Float.compare(that.fitness, fitness) == 0 && fitnessSet == that.fitnessSet && Objects.equals(errors, that.errors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fitness, errors, fitnessSet);
    }

    public abstract GAIndividual clone();
}
