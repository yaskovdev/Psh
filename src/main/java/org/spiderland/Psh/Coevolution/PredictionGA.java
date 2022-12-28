package org.spiderland.Psh.Coevolution;

import org.spiderland.Psh.GA;
import org.spiderland.Psh.GAIndividual;
import org.spiderland.Psh.PushGP;
import org.spiderland.Psh.PushGPIndividual;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * An abstract class for a population of co-evolving predictors of fitness,
 * rank, or something similar. In this class, "fitness" or "predicted fitness"
 * of a solution individual may be referring to the actual fitness of the
 * individual, or it may be referring to something similar, such as the
 * individual's rank.
 */
public abstract class PredictionGA extends GA {
    private static final long serialVersionUID = 1L;

    // Note: Oldest trainer has the lowest index; newest trainer has the highest
    // index.
    protected ArrayList<PushGPIndividual> _trainerPopulation;
    protected int _generationsBetweenTrainers;
    protected int _trainerPopulationSize;

    // The solution population and genetic algorithm.
    protected PushGP _solutionGA;

    /**
     * Customizes GA.GAWithParameters to allow the inclusion of the solution GA,
     * which is required for the initialization of the prediction GA.
     *
     * @param ceFloatSymbolicRegression
     * @param getPredictorParameters
     * @return
     * @throws Exception
     */
    public static PredictionGA PredictionGAWithParameters(PushGP inSolutionGA, HashMap<String, String> inParams) throws Exception {

        Class<?> cls = Class.forName(inParams.get("problem-class"));
        Object gaObject = cls.newInstance();
        if (!(gaObject instanceof PredictionGA ga))
            throw (new Exception("Predictor problem-class must inherit from"
                    + " class PredictorGA"));

        // Must set the solution GA before InitFromParameters, since the latter
        // uses _solutionGA while creating the predictor population.
        ga.SetSolutionGA(inSolutionGA);
        ga.setParams(inParams);
        ga.initFromParameters();

        return ga;
    }

    @Override
    protected void initFromParameters() throws Exception {
        _generationsBetweenTrainers = (int) getFloatParam("generations-between-trainers");
        _trainerPopulationSize = (int) getFloatParam("trainer-population-size");

        InitTrainerPopulation();

        super.initFromParameters();
    }

    /**
     * Runs a single generation.
     *
     * @throws Exception
     */
    public void RunGeneration() throws Exception {
        run(1);
    }

    @Override
    protected void beginGeneration() {
        if (generationCount % _generationsBetweenTrainers == _generationsBetweenTrainers - 1) {
            // Time to add a new trainer
            PushGPIndividual newTrainer = (PushGPIndividual) ChooseNewTrainer().clone();
            EvaluateSolutionIndividual(newTrainer);

            _trainerPopulation.remove(0);
            _trainerPopulation.add(newTrainer);

            EvaluateTrainerFitnesses();

        }
    }

    @Override
    public boolean terminate() {
        return false;
    }

    @Override
    protected boolean success() {
        return false;
    }

    /**
     * Chooses a new trainer from the solution population to add to the trainer
     * population. The solution individual is chosen with the highest variance
     * of the predictions from the current predictor population.
     */
    protected PushGPIndividual ChooseNewTrainer() {
        ArrayList<Float> individualVariances = new ArrayList<>();

        for (int i = 0; i < _solutionGA.GetPopulationSize(); i++) {
            PushGPIndividual individual = (PushGPIndividual) _solutionGA
                    .getIndividualFromPopulation(i);

            ArrayList<Float> predictions = new ArrayList<>();
            for (int j = 0; j < populations[currentPopulation].length; j++) {
                PredictionGAIndividual predictor = (PredictionGAIndividual) populations[currentPopulation][j];
                predictions.add(predictor.PredictSolutionFitness(individual));
            }

            individualVariances.add(Variance(predictions));
        }

        // Find individual with the highest variance
        int highestVarianceIndividual = 0;
        float highestVariance = individualVariances.get(0);

        for (int i = 0; i < _solutionGA.GetPopulationSize(); i++) {
            if (highestVariance < individualVariances.get(i)) {
                highestVarianceIndividual = i;
                highestVariance = individualVariances.get(i);
            }
        }

        return (PushGPIndividual) _solutionGA
                .getIndividualFromPopulation(highestVarianceIndividual);
    }

    protected PredictionGAIndividual GetBestPredictor() {
        float bestFitness = Float.MAX_VALUE;
        GAIndividual bestPredictor = populations[currentPopulation][0];

        for (GAIndividual ind : populations[currentPopulation]) {
            if (ind.GetFitness() < bestFitness) {
                bestPredictor = ind;
                bestFitness = ind.GetFitness();
            }
        }

        return (PredictionGAIndividual) bestPredictor;
    }

    /**
     * Calculates and sets the exact fitness from any individual of the
     * _solutionGA population. This includes trainers.
     *
     * @param inIndividual
     */
    protected void EvaluateSolutionIndividual(PushGPIndividual inIndividual) {
        _solutionGA.evaluateIndividual(inIndividual);
    }

    protected void SetSolutionGA(PushGP inGA) {
        _solutionGA = inGA;
    }

    /**
     * This must be private, since there must be a _solutionGA set before this
     * method is invoked. Use SetGAandTrainers() instead.
     */
    private void InitTrainerPopulation() {
        _trainerPopulation = new ArrayList<PushGPIndividual>();

        PushGPIndividual individual = new PushGPIndividual();

        for (int i = 0; i < _trainerPopulationSize; i++) {
            _trainerPopulation.add((PushGPIndividual) individual.clone());
            _solutionGA.initIndividual(_trainerPopulation.get(i));
        }

        EvaluateTrainerFitnesses();
    }

    protected String report() {
        String report = super.report();
        report = report.replace('-', '#');
        report = report.replaceFirst("Report for", " Predictor");

        report += ";; Best Predictor: "
                + populations[currentPopulation][bestIndividual] + "\n";
        report += ";; Best Predictor Fitness: " + bestMeanFitness + "\n\n";

        report += ";; Mean Predictor Fitness: " + populationMeanFitness + "\n";

        report += ";;########################################################;;\n\n";

        return report;
    }

    protected String finalReport() {
        return "";
    }

    private Float Variance(ArrayList<Float> list) {
        float sampleMean = SampleMean(list);
        float sum = 0;

        for (float element : list) {
            sum += (element - sampleMean) * (element - sampleMean);
        }

        return (sum / (list.size() - 1));
    }

    private float SampleMean(ArrayList<Float> list) {
        float total = 0;
        for (float element : list) {
            total += element;
        }
        return (total / list.size());
    }

    /**
     * Initiates inIndividual as a random predictor individual.
     */
    @Override
    protected abstract void initIndividual(GAIndividual inIndividual);

    /**
     * Evaluates a PredictionGAIndividual's fitness, based on the difference
     * between the predictions of the fitnesses of the trainers and the actual
     * fitnesses of the trainers.
     */
    @Override
    protected abstract void evaluateIndividual(GAIndividual inIndividual);

    /**
     * Determines the predictor's fitness on a trainer, where the trainer is the
     * inInput, and the trainer's actual fitness (or rank, whatever is to be
     * predicted) is inOutput.
     *
     * @return Predictor's fitness (or rank, etc.) for the given trainer.
     */
    @Override
    public abstract float evaluateTestCase(GAIndividual inIndividual,
            Object inInput, Object inOutput);

    /**
     * Actual fitnesses of trainers will always be stored as part of the
     * PushGPIndividual object. Some predictor types, such as rank predictors,
     * will also need a separate storage of data, such as a method of storing
     * the ranking of the predictors. Others, such as fitness predictors, may
     * just need the fitness information directly from the trainers. This
     * function may be used to make sure fitnesses or ranks are updated, i.e. to
     * recalculate rank order with the addition of a new trainer.
     */
    protected abstract void EvaluateTrainerFitnesses();

    @Override
    protected abstract GAIndividual reproduceByMutation(int inIndex);

    @Override
    protected abstract GAIndividual reproduceByCrossover(int inIndex);

}
