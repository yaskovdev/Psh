package org.spiderland.Psh.Coevolution;

import org.spiderland.Psh.GAIndividual;
import org.spiderland.Psh.PushGPIndividual;

import java.util.ArrayList;
import java.util.List;

public class FloatRegFitPrediction extends PredictionGeneticAlgorithm {
    private static final long serialVersionUID = 1L;

    @Override
    protected void initIndividual(GAIndividual inIndividual) {
        FloatRegFitPredictionIndividual i = (FloatRegFitPredictionIndividual) inIndividual;

        int[] samples = new int[FloatRegFitPredictionIndividual.sampleSize];
        for (int j = 0; j < samples.length; j++) {
            samples[j] = random.nextInt(solutionGeneticAlgorithm.testCases.size());
        }
        i.SetSampleIndicesAndSolutionGA(solutionGeneticAlgorithm, samples);
    }

    @Override
    protected void evaluateIndividual(GAIndividual inIndividual) {

        FloatRegFitPredictionIndividual predictor = (FloatRegFitPredictionIndividual) inIndividual;
        List<Float> errors = new ArrayList<>();

        for (int i = 0; i < trainerPopulationSize; i++) {
            float predictedError = predictor.PredictSolutionFitness(trainerPopulation.get(i));

            // Error is difference between predictedError and the actual fitness
            // of the trainer.
            float error = Math.abs(predictedError) - Math.abs(trainerPopulation.get(i).getFitness());
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
    protected void evaluateTrainerFitnesses() {
        for (PushGPIndividual trainer : trainerPopulation) {
            if (!trainer.isFitnessSet()) {
                evaluateSolutionIndividual(trainer);
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
                            .nextInt(FloatRegFitPredictionIndividual.sampleSize);
                    ((FloatRegFitPredictionIndividual) next).SetSampleIndex(
                            index, random.nextInt(solutionGeneticAlgorithm.testCases.size()));
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

        int index = random.nextInt(FloatRegFitPredictionIndividual.sampleSize);
        i.SetSampleIndex(index, random.nextInt(solutionGeneticAlgorithm.testCases.size()));

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
                .nextInt(FloatRegFitPredictionIndividual.sampleSize - 1) + 1;
        for (int i = crossoverPoint; i < FloatRegFitPredictionIndividual.sampleSize; i++) {
            a.SetSampleIndex(i, b.GetSampleIndex(i));
        }

        return a;
    }


}
