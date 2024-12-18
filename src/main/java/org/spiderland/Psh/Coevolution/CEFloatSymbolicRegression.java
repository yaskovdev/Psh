package org.spiderland.Psh.Coevolution;

import org.spiderland.Psh.FloatStack;
import org.spiderland.Psh.GAIndividual;
import org.spiderland.Psh.GATestCase;
import org.spiderland.Psh.Interpreter;
import org.spiderland.Psh.ObjectPair;
import org.spiderland.Psh.Program;
import org.spiderland.Psh.PushGP;
import org.spiderland.Psh.PushGPIndividual;
import org.spiderland.Psh.TestCase.TestCaseGenerator;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This problem class implements symbolic regression for floating point numbers
 * using co-evolved prediction. The class must keep track of the amount of
 * effort it takes compared to the effort of the co-evolving predictor
 * population, and use about 95% of the effort. Effort based on the number of
 * evaluation executions thus far, which is tracked by the interpreter.
 */
public class CEFloatSymbolicRegression extends PushGP {
    private static final long serialVersionUID = 1L;

    protected float currentInput;

    protected long effort;
    protected float predictorEffortPercent;
    protected PredictionGeneticAlgorithm predictorGeneticAlgorithm;

    private boolean success;

    private static final float NO_RESULT_PENALTY = 1000f;

    protected void initFromParameters() throws Exception {
        super.initFromParameters();

        effort = 0;

        String cases = getParam("test-cases", true);
        String casesClass = getParam("test-case-class", true);
        if (cases == null && casesClass == null) {
            throw new Exception("No acceptable test-case parameter.");
        }

        if (casesClass != null) {
            // Get test cases from the TestCasesClass.
            Class<?> iclass = Class.forName(casesClass);
            Object iObject = iclass.newInstance();
            if (!(iObject instanceof TestCaseGenerator testCaseGenerator)) {
                throw (new Exception(
                        "test-case-class must inherit from class TestCaseGenerator"));
            }

            int numTestCases = testCaseGenerator.testCaseCount();

            for (int i = 0; i < numTestCases; i++) {
                ObjectPair testCase = testCaseGenerator.testCase(i);

                Float in = (Float) testCase.first();
                Float out = (Float) testCase.second();

                print(";; Fitness case #" + i + " input: " + in + " output: "
                        + out + "\n");

                testCases.add(new GATestCase(in, out));
            }
        } else {
            // Get test cases from test-cases.
            Program testCases = new Program(cases);

            for (int i = 0; i < testCases.size(); i++) {
                Program p = (Program) testCases.peek(i);

                if (p.size() < 2)
                    throw new Exception(
                            "Not enough elements for fitness case \"" + p
                                    + "\"");

                Float in = Float.valueOf(p.peek(0).toString());
                Float out = Float.valueOf(p.peek(1).toString());

                print(";; Fitness case #" + i + " input: " + in + " output: " + out + "\n");

                this.testCases.add(new GATestCase(in, out));
            }
        }

        // Create and initialize predictors
        predictorEffortPercent = getFloatParam("PREDICTOR-effort-percent",
                true);
        predictorGeneticAlgorithm = PredictionGeneticAlgorithm.PredictionGAWithParameters(this,
                GetPredictorParameters(parameters));

    }

    protected void initInterpreter(Interpreter inInterpreter) {
    }

    @Override
    protected void beginGeneration() throws Exception {
        //trh Temporary solution, needs to actually use effort info
        if (generationCount % 2 == 1) {
            predictorGeneticAlgorithm.run(1);
        }
    }

    /**
     * Evaluates a solution individual using the best predictor so far.
     */
    protected void PredictIndividual(GAIndividual inIndividual, boolean duringSimplify) {

        FloatRegFitPredictionIndividual predictor = (FloatRegFitPredictionIndividual) predictorGeneticAlgorithm.getBestPredictor();
        float fitness = predictor.PredictSolutionFitness((PushGPIndividual) inIndividual);

        inIndividual.setFitness(fitness);
        inIndividual.setErrors(new ArrayList<>());
    }

    public float evaluateTestCase(GAIndividual inIndividual, Object inInput,
            Object inOutput) {
        effort++;

        interpreter.clearStacks();

        currentInput = (Float) inInput;

        FloatStack fstack = interpreter.floatStack();

        fstack.push(currentInput);

        // Must be included in order to use the input stack.
        interpreter.inputStack().push(currentInput);

        interpreter.execute(((PushGPIndividual) inIndividual).program,
                executionLimit);

        float result = fstack.top();

        // System.out.println(interpreter + " " + result);

        //trh
        /*
         * System.out.println("\nevaluations according to interpreter " +
         * Interpreter.GetEvaluationExecutions());
         * System.out.println("evaluations according to effort " + effort);
         */

        // Penalize individual if there is no result on the stack.
        if (fstack.size() == 0) {
            return NO_RESULT_PENALTY;
        }

        return result - ((Float) inOutput);
    }

    protected boolean success() {
        if (success) {
            return true;
        }

        GAIndividual best = populations[currentPopulation][bestIndividual];
        float predictedFitness = best.getFitness();

        predictorGeneticAlgorithm.evaluateSolutionIndividual((PushGPIndividual) best);

        bestMeanFitness = best.getFitness();

        if (bestMeanFitness <= 0.1) {
            success = true;
            return true;
        }

        best.setFitness(predictedFitness);
        return false;
    }

    protected String report() {
        success(); // Finds the real fitness of the best individual

        return super.report();
    }

    private HashMap<String, String> GetPredictorParameters(
            HashMap<String, String> parameters) throws Exception {

        HashMap<String, String> predictorParameters = new HashMap<String, String>();

        predictorParameters.put("max-generations", Integer
                .toString(Integer.MAX_VALUE));

        predictorParameters.put("problem-class",
                getParam("PREDICTOR-problem-class"));
        predictorParameters.put("individual-class",
                getParam("PREDICTOR-individual-class"));
        predictorParameters.put("population-size",
                getParam("PREDICTOR-population-size"));
        predictorParameters.put("mutation-percent",
                getParam("PREDICTOR-mutation-percent"));
        predictorParameters.put("crossover-percent",
                getParam("PREDICTOR-crossover-percent"));
        predictorParameters.put("tournament-size",
                getParam("PREDICTOR-tournament-size"));
        predictorParameters.put("trivial-geography-radius",
                getParam("PREDICTOR-trivial-geography-radius"));
        predictorParameters.put("generations-between-trainers",
                getParam("PREDICTOR-generations-between-trainers"));
        predictorParameters.put("trainer-population-size",
                getParam("PREDICTOR-trainer-population-size"));

        return predictorParameters;
    }

    /**
     * NOTE: This is entirely copied from PushGP, except EvaluateIndividual
     * was changed to PredictIndividual, as noted below.
     */
    protected void evaluate() {
        float totalFitness = 0;
        bestMeanFitness = Float.MAX_VALUE;

        for (int n = 0; n < populations[currentPopulation].length; n++) {
            GAIndividual i = populations[currentPopulation][n];

            PredictIndividual(i, false);

            totalFitness += i.getFitness();

            if (i.getFitness() < bestMeanFitness) {
                bestMeanFitness = i.getFitness();
                bestIndividual = n;
                bestSize = ((PushGPIndividual) i).program.programSize();
                bestErrors = i.getErrors();
            }
        }

        populationMeanFitness = totalFitness / populations[currentPopulation].length;
    }

    /**
     * NOTE: This is entirely copied from PushGP, except EvaluateIndividual
     * was changed to PredictIndividual, as noted below (twice).
     */
    protected PushGPIndividual autosimplify(PushGPIndividual inIndividual,
            int steps) {

        PushGPIndividual simplest = (PushGPIndividual) inIndividual.clone();
        PushGPIndividual trial = (PushGPIndividual) inIndividual.clone();
        PredictIndividual(simplest, true); // Changed from EvaluateIndividual
        float bestError = simplest.getFitness();

        boolean madeSimpler = false;

        for (int i = 0; i < steps; i++) {
            madeSimpler = false;
            float method = random.nextInt(100);

            if (trial.program.programSize() <= 0)
                break;
            if (method < simplifyFlattenPercent) {
                // Flatten random thing
                int pointIndex = random.nextInt(trial.program.programSize());
                Object point = trial.program.subtree(pointIndex);

                if (point instanceof Program) {
                    trial.program.flatten(pointIndex);
                    madeSimpler = true;
                }
            } else {
                // Remove small number of random things
                int numberToRemove = random.nextInt(3) + 1;

                for (int j = 0; j < numberToRemove; j++) {
                    int trialSize = trial.program.programSize();

                    if (trialSize > 0) {
                        int pointIndex = random.nextInt(trialSize);
                        trial.program.replaceSubtree(pointIndex, new Program());
                        trial.program.flatten(pointIndex);
                        madeSimpler = true;
                    }
                }
            }

            if (madeSimpler) {
                PredictIndividual(trial, true); // Changed from EvaluateIndividual

                if (trial.getFitness() <= bestError) {
                    simplest = (PushGPIndividual) trial.clone();
                    bestError = trial.getFitness();
                }
            }

            trial = (PushGPIndividual) simplest.clone();
        }

        return simplest;
    }

}
