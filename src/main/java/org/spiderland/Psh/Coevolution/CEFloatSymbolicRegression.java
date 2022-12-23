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

    protected float _currentInput;

    protected long _effort;
    protected float _predictorEffortPercent;
    protected PredictionGA _predictorGA;

    private boolean _success;

    private static final float NO_RESULT_PENALTY = 1000f;

    protected void InitFromParameters() throws Exception {
        super.InitFromParameters();

        _effort = 0;

        String cases = GetParam("test-cases", true);
        String casesClass = GetParam("test-case-class", true);
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

                Print(";; Fitness case #" + i + " input: " + in + " output: "
                        + out + "\n");

                _testCases.add(new GATestCase(in, out));
            }
        } else {
            // Get test cases from test-cases.
            Program caselist = new Program(_interpreter, cases);

            for (int i = 0; i < caselist.size(); i++) {
                Program p = (Program) caselist.peek(i);

                if (p.size() < 2)
                    throw new Exception(
                            "Not enough elements for fitness case \"" + p
                                    + "\"");

                Float in = Float.valueOf(p.peek(0).toString());
                Float out = Float.valueOf(p.peek(1).toString());

                Print(";; Fitness case #" + i + " input: " + in + " output: " + out + "\n");

                _testCases.add(new GATestCase(in, out));
            }
        }

        // Create and initialize predictors
        _predictorEffortPercent = GetFloatParam("PREDICTOR-effort-percent",
                true);
        _predictorGA = PredictionGA.PredictionGAWithParameters(this,
                GetPredictorParameters(_parameters));

    }

    protected void InitInterpreter(Interpreter inInterpreter) {
    }

    @Override
    protected void BeginGeneration() throws Exception {
        //trh Temporary solution, needs to actually use effort info
        if (_generationCount % 2 == 1) {
            _predictorGA.Run(1);
        }
    }

    /**
     * Evaluates a solution individual using the best predictor so far.
     */
    protected void PredictIndividual(GAIndividual inIndividual, boolean duringSimplify) {

        FloatRegFitPredictionIndividual predictor = (FloatRegFitPredictionIndividual) _predictorGA.GetBestPredictor();
        float fitness = predictor.PredictSolutionFitness((PushGPIndividual) inIndividual);

        inIndividual.SetFitness(fitness);
        inIndividual.SetErrors(new ArrayList<Float>());
    }

    public float EvaluateTestCase(GAIndividual inIndividual, Object inInput,
            Object inOutput) {
        _effort++;

        _interpreter.clearStacks();

        _currentInput = (Float) inInput;

        FloatStack fstack = _interpreter.floatStack();

        fstack.push(_currentInput);

        // Must be included in order to use the input stack.
        _interpreter.inputStack().push(_currentInput);

        _interpreter.Execute(((PushGPIndividual) inIndividual)._program,
                _executionLimit);

        float result = fstack.top();

        // System.out.println( _interpreter + " " + result );

        //trh
        /*
         * System.out.println("\nevaluations according to interpreter " +
         * Interpreter.GetEvaluationExecutions());
         * System.out.println("evaluations according to effort " + _effort);
         */

        // Penalize individual if there is no result on the stack.
        if (fstack.size() == 0) {
            return NO_RESULT_PENALTY;
        }

        return result - ((Float) inOutput);
    }

    protected boolean Success() {
        if (_success) {
            return true;
        }

        GAIndividual best = _populations[_currentPopulation][_bestIndividual];
        float predictedFitness = best.GetFitness();

        _predictorGA.EvaluateSolutionIndividual((PushGPIndividual) best);

        _bestMeanFitness = best.GetFitness();

        if (_bestMeanFitness <= 0.1) {
            _success = true;
            return true;
        }

        best.SetFitness(predictedFitness);
        return false;
    }

    protected String Report() {
        Success(); // Finds the real fitness of the best individual

        return super.Report();
    }

    private HashMap<String, String> GetPredictorParameters(
            HashMap<String, String> parameters) throws Exception {

        HashMap<String, String> predictorParameters = new HashMap<String, String>();

        predictorParameters.put("max-generations", Integer
                .toString(Integer.MAX_VALUE));

        predictorParameters.put("problem-class",
                GetParam("PREDICTOR-problem-class"));
        predictorParameters.put("individual-class",
                GetParam("PREDICTOR-individual-class"));
        predictorParameters.put("population-size",
                GetParam("PREDICTOR-population-size"));
        predictorParameters.put("mutation-percent",
                GetParam("PREDICTOR-mutation-percent"));
        predictorParameters.put("crossover-percent",
                GetParam("PREDICTOR-crossover-percent"));
        predictorParameters.put("tournament-size",
                GetParam("PREDICTOR-tournament-size"));
        predictorParameters.put("trivial-geography-radius",
                GetParam("PREDICTOR-trivial-geography-radius"));
        predictorParameters.put("generations-between-trainers",
                GetParam("PREDICTOR-generations-between-trainers"));
        predictorParameters.put("trainer-population-size",
                GetParam("PREDICTOR-trainer-population-size"));

        return predictorParameters;
    }

    /**
     * NOTE: This is entirely copied from PushGP, except EvaluateIndividual
     * was changed to PredictIndividual, as noted below.
     */
    protected void Evaluate() {
        float totalFitness = 0;
        _bestMeanFitness = Float.MAX_VALUE;

        for (int n = 0; n < _populations[_currentPopulation].length; n++) {
            GAIndividual i = _populations[_currentPopulation][n];

            PredictIndividual(i, false);

            totalFitness += i.GetFitness();

            if (i.GetFitness() < _bestMeanFitness) {
                _bestMeanFitness = i.GetFitness();
                _bestIndividual = n;
                _bestSize = ((PushGPIndividual) i)._program.programsize();
                _bestErrors = i.GetErrors();
            }
        }

        _populationMeanFitness = totalFitness / _populations[_currentPopulation].length;
    }

    /**
     * NOTE: This is entirely copied from PushGP, except EvaluateIndividual
     * was changed to PredictIndividual, as noted below (twice).
     */
    protected PushGPIndividual Autosimplify(PushGPIndividual inIndividual,
            int steps) {

        PushGPIndividual simplest = (PushGPIndividual) inIndividual.clone();
        PushGPIndividual trial = (PushGPIndividual) inIndividual.clone();
        PredictIndividual(simplest, true); // Changed from EvaluateIndividual
        float bestError = simplest.GetFitness();

        boolean madeSimpler = false;

        for (int i = 0; i < steps; i++) {
            madeSimpler = false;
            float method = _RNG.nextInt(100);

            if (trial._program.programsize() <= 0)
                break;
            if (method < _simplifyFlattenPercent) {
                // Flatten random thing
                int pointIndex = _RNG.nextInt(trial._program.programsize());
                Object point = trial._program.Subtree(pointIndex);

                if (point instanceof Program) {
                    trial._program.Flatten(pointIndex);
                    madeSimpler = true;
                }
            } else {
                // Remove small number of random things
                int numberToRemove = _RNG.nextInt(3) + 1;

                for (int j = 0; j < numberToRemove; j++) {
                    int trialSize = trial._program.programsize();

                    if (trialSize > 0) {
                        int pointIndex = _RNG.nextInt(trialSize);
                        trial._program.ReplaceSubtree(pointIndex, new Program(
                                _interpreter));
                        trial._program.Flatten(pointIndex);
                        madeSimpler = true;
                    }
                }
            }

            if (madeSimpler) {
                PredictIndividual(trial, true); // Changed from EvaluateIndividual

                if (trial.GetFitness() <= bestError) {
                    simplest = (PushGPIndividual) trial.clone();
                    bestError = trial.GetFitness();
                }
            }

            trial = (PushGPIndividual) simplest.clone();
        }

        return simplest;
    }

}
