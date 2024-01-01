package org.spiderland.Psh.Coevolution;

import org.spiderland.Psh.GAIndividual;
import org.spiderland.Psh.PushGPIndividual;

import java.util.ArrayList;

public class FloatRegFitPrediction extends PredictionGeneticAlgorithm {
    private static final long serialVersionUID = 1L;

    @Override
    protected void initIndividual(GAIndividual inIndividual) {
        FloatRegFitPredictionIndividual i = (FloatRegFitPredictionIndividual) inIndividual;

        int[] samples = new int[FloatRegFitPredictionIndividual._sampleSize];
        for (int j = 0; j < samples.length; j++) {
            samples[j] = random.nextInt(_solutionGA.testCases.size());
        }
        i.SetSampleIndicesAndSolutionGA(_solutionGA, samples);
    }

    @Override
    protected void evaluateIndividual(GAIndividual inIndividual) {

        FloatRegFitPredictionIndividual predictor = (FloatRegFitPredictionIndividual) inIndividual;
        ArrayList<Float> errors = new ArrayList<>();

        for (int i = 0; i < _trainerPopulationSize; i++) {
            float predictedError = predictor.PredictSolutionFitness(_trainerPopulation.get(i));

            // Error is difference between predictedError and the actual fitness
            // of the trainer.
            float error = Math.abs(predictedError) - Math.abs(_trainerPopulation.get(i).getFitness());
            errors.add(error);
        }

        predictor.setFitness(absoluteAverageOfErrors(errors));
        predictor.setErrors(errors);
    }

    /**
     * Determines the predictor's fitness on a trainer, where the trainer is the
     * inInput, and the trainer's actual fitness is inOutput. The fitness of
     * the predictor is the absolute error between the prediction and the
     * trainer's actual fitness.
     *
     * @return Predictor's fitness (i.e. error) for the given trainer.
     * @throws Exception
     */
    @Override
    public float evaluateTestCase(GAIndividual inIndividual, Object inInput,
            Object inOutput) {

        PushGPIndividual trainer = (PushGPIndividual) inInput;
        float trainerFitness = (Float) inOutput;

        float predictedTrainerFitness = ((PredictionGAIndividual) inIndividual)
                .PredictSolutionFitness(trainer);

        return Math.abs(predictedTrainerFitness - trainerFitness);
    }

    @Override
    protected void EvaluateTrainerFitnesses() {
        for (PushGPIndividual trainer : _trainerPopulation) {
            if (!trainer.isFitnessSet()) {
                EvaluateSolutionIndividual(trainer);
            }
        }
    }

    protected void reproduce() {
        int nextPopulation = currentPopulation == 0 ? 1 : 0;

        for (int n = 0; n < populations[currentPopulation].length; n++) {
            float method = random.nextInt(100);
            GAIndividual next;

            if (method < mutationPercent) {
                next = this.reproduceByMutation(n);
            } else if (method < crossoverPercent + mutationPercent) {
                next = this.reproduceByCrossover(n);
            } else {
                next = reproduceByClone(n);
            }

            // Make sure next isn't already in the population, so that all
            // predictors are unique.
            for (int k = 0; k < n; k++) {
                if (((FloatRegFitPredictionIndividual) next)
                        .equalPredictors(populations[nextPopulation][k])) {
                    int index = random
                            .nextInt(FloatRegFitPredictionIndividual._sampleSize);
                    ((FloatRegFitPredictionIndividual) next).SetSampleIndex(
                            index, random.nextInt(_solutionGA.testCases.size()));
                }
            }

            populations[nextPopulation][n] = next;

        }
    }

    /**
     * Mutates an individual by choosing an index at random and randomizing
     * its training point among possible individuals.
     */
    @Override
    protected GAIndividual reproduceByMutation(int inIndex) {
        FloatRegFitPredictionIndividual i = (FloatRegFitPredictionIndividual) reproduceByClone(inIndex);

        int index = random.nextInt(FloatRegFitPredictionIndividual._sampleSize);
        i.SetSampleIndex(index, random.nextInt(_solutionGA.testCases.size()));

        return i;
    }

    @Override
    protected GAIndividual reproduceByCrossover(int inIndex) {
        FloatRegFitPredictionIndividual a = (FloatRegFitPredictionIndividual) reproduceByClone(inIndex);
        FloatRegFitPredictionIndividual b = (FloatRegFitPredictionIndividual) tournamentSelect(
            tournamentSize, inIndex);

        // crossoverPoint is the first index of a that will be changed to the
        // gene from b.
        int crossoverPoint = random
                .nextInt(FloatRegFitPredictionIndividual._sampleSize - 1) + 1;
        for (int i = crossoverPoint; i < FloatRegFitPredictionIndividual._sampleSize; i++) {
            a.SetSampleIndex(i, b.GetSampleIndex(i));
        }

        return a;
    }


}
