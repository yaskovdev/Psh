package org.spiderland.Psh.Coevolution;

import org.spiderland.Psh.GAIndividual;
import org.spiderland.Psh.PushGPIndividual;

import java.util.ArrayList;
import java.util.List;

/**
 * An abstract CEPredictorGA individual class for developing co-evolved
 * predictors.
 */

public abstract class PredictionGAIndividual extends GAIndividual {
    private static final long serialVersionUID = 1L;

    /**
     * Predicts the fitness of the input PushGPIndividual
     *
     * @param individual to predict the fitness of
     * @return predicted fitness
     */
    public abstract float PredictSolutionFitness(PushGPIndividual individual);

    /**
     * Computes the absolute-average-of-errors fitness from an error vector.
     *
     * @return the average error value for the vector.
     */
    protected float AbsoluteAverageOfErrors(List<Float> inArray) {
        float total = 0.0f;

        for (int n = 0; n < inArray.size(); n++)
            total += Math.abs(inArray.get(n));

        if (Float.isInfinite(total))
            return Float.MAX_VALUE;

        return (total / inArray.size());
    }
}
