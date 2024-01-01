package org.spiderland.Psh;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * An abstract GP individual class containing a fitness value. The fitness value
 * represents an individual's <i>error</i> values, such that <i>lower</i>
 * fitness values are better and such that a fitness value of 0 indicates a
 * perfect solution.
 */

@Data
public abstract class GAIndividual implements Serializable {
    private static final long serialVersionUID = 1L;

    private float fitness;
    private ArrayList<Float> errors;
    protected boolean fitnessSet;

    public void setFitness(float inFitness) {
        fitness = inFitness;
        fitnessSet = true;
    }

    public abstract GAIndividual clone();
}
