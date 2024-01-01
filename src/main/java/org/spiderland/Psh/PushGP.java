package org.spiderland.Psh;

import java.util.ArrayList;
import java.util.List;

/**
 * The Push Genetic Programming core class.
 */
abstract public class PushGP extends GeneticAlgorithm {

    private static final long serialVersionUID = 1L;

    protected Interpreter interpreter;
    protected int maxRandomCodeSize;
    protected int maxPointsInProgram;
    protected int executionLimit;

    protected boolean useFairMutation;
    protected float fairMutationRange;
    protected String nodeSelectionMode;
    protected float nodeSelectionLeafProbability;
    protected int nodeSelectionTournamentSize;

    protected float averageSize;
    protected int bestSize;

    protected float simplificationPercent;
    protected float simplifyFlattenPercent;
    protected int reproductionSimplifications;
    protected int reportSimplifications;
    protected int finalSimplifications;

    protected String targetFunctionString;

    protected void initFromParameters() throws Exception {
        // Default parameters to be used when optional parameters are not
        // given.
        float defaultFairMutationRange = 0.3f;
        float defaultsimplifyFlattenPercent = 20f;
        String defaultInterpreterClass = "org.spiderland.Psh.Interpreter";
        String defaultInputPusherClass = "org.spiderland.Psh.InputPusher";
        String defaultTargetFunctionString = "";
        float defaultNodeSelectionLeafProbability = 10;
        int defaultNodeSelectionTournamentSize = 2;

        // Limits
        maxRandomCodeSize = (int) getFloatParam("max-random-code-size");
        executionLimit = (int) getFloatParam("execution-limit");
        maxPointsInProgram = (int) getFloatParam("max-points-in-program");

        // Fair mutation parameters
        useFairMutation = "fair".equals(getParam("mutation-mode", true));
        fairMutationRange = getFloatParam("fair-mutation-range", true);
        if (Float.isNaN(fairMutationRange)) {
            fairMutationRange = defaultFairMutationRange;
        }

        // Node selection parameters
        nodeSelectionMode = getParam("node-selection-mode", true);
        if (nodeSelectionMode != null) {
            if (!nodeSelectionMode.equals("unbiased")
                    && !nodeSelectionMode.equals("leaf-probability")
                    && !nodeSelectionMode.equals("size-tournament")) {
                throw new Exception(
                        "node-selection-mode must be set to unbiased,\n"
                                + "leaf-probability, or size-tournament. Currently set to "
                                + nodeSelectionMode);
            }

            nodeSelectionLeafProbability = getFloatParam(
                    "node-selection-leaf-probability", true);
            if (Float.isNaN(nodeSelectionLeafProbability)) {
                nodeSelectionLeafProbability = defaultNodeSelectionLeafProbability;
            }

            nodeSelectionTournamentSize = (int) getFloatParam(
                    "node-selection-tournament-size", true);
            if (Float.isNaN(getFloatParam("node-selection-tournament-size", true))) {
                nodeSelectionTournamentSize = defaultNodeSelectionTournamentSize;
            }

        } else {
            nodeSelectionMode = "unbiased";
        }

        // Simplification parameters
        simplificationPercent = getFloatParam("simplification-percent");
        simplifyFlattenPercent = getFloatParam("simplify-flatten-percent",
                true);
        if (Float.isNaN(simplifyFlattenPercent)) {
            simplifyFlattenPercent = defaultsimplifyFlattenPercent;
        }

        reproductionSimplifications = (int) getFloatParam("reproduction-simplifications");
        reportSimplifications = (int) getFloatParam("report-simplifications");
        finalSimplifications = (int) getFloatParam("final-simplifications");

        // ERC parameters
        int minRandomInt;
        int defaultMinRandomInt = -10;
        int maxRandomInt;
        int defaultMaxRandomInt = 10;
        int randomIntResolution;
        int defaultRandomIntResolution = 1;

        if (Float.isNaN(getFloatParam("min-random-integer", true))) {
            minRandomInt = defaultMinRandomInt;
        } else {
            minRandomInt = (int) getFloatParam("min-random-integer", true);
        }
        if (Float.isNaN(getFloatParam("max-random-integer", true))) {
            maxRandomInt = defaultMaxRandomInt;
        } else {
            maxRandomInt = (int) getFloatParam("max-random-integer", true);
        }
        if (Float.isNaN(getFloatParam("random-integer-resolution", true))) {
            randomIntResolution = defaultRandomIntResolution;
        } else {
            randomIntResolution = (int) getFloatParam(
                    "random-integer-resolution", true);
        }

        float minRandomFloat;
        float defaultMinRandomFloat = -10.0f;
        float maxRandomFloat;
        float defaultMaxRandomFloat = 10.0f;
        float randomFloatResolution;
        float defaultRandomFloatResolution = 0.01f;

        if (Float.isNaN(getFloatParam("min-random-float", true))) {
            minRandomFloat = defaultMinRandomFloat;
        } else {
            minRandomFloat = getFloatParam("min-random-float", true);
        }
        if (Float.isNaN(getFloatParam("max-random-float", true))) {
            maxRandomFloat = defaultMaxRandomFloat;
        } else {
            maxRandomFloat = getFloatParam("max-random-float", true);
        }
        if (Float.isNaN(getFloatParam("random-float-resolution", true))) {
            randomFloatResolution = defaultRandomFloatResolution;
        } else {
            randomFloatResolution = getFloatParam("random-float-resolution",
                    true);
        }

        // Setup our custom interpreter class based on the params we're given
        String interpreterClass = getParam("interpreter-class", true);
        if (interpreterClass == null) {
            interpreterClass = defaultInterpreterClass;
        }
        Class<?> iclass = Class.forName(interpreterClass);
        Object iObject = iclass.newInstance();
        if (!(iObject instanceof Interpreter))
            throw (new Exception(
                    "interpreter-class must inherit from class Interpreter"));

        interpreter = (Interpreter) iObject;
        interpreter.setInstructions(new Program(getParam("instruction-set")));
        interpreter.setRandomParameters(minRandomInt, maxRandomInt,
                randomIntResolution, minRandomFloat, maxRandomFloat,
                randomFloatResolution, maxRandomCodeSize, maxPointsInProgram);

        // Frame mode and input pusher class
        String framemode = getParam("push-frame-mode", true);

        String inputpusherClass = getParam("inputpusher-class", true);
        if (inputpusherClass == null) {
            inputpusherClass = defaultInputPusherClass;
        }

        iclass = Class.forName(inputpusherClass);

        iObject = iclass.newInstance();

        if (!(iObject instanceof InputPusher))
            throw new Exception(
                    "inputpusher-class must inherit from class InputPusher");

        interpreter.setInputPusher((InputPusher) iObject);

        // Initialize the interpreter
        initInterpreter(interpreter);

        if (framemode != null && framemode.equals("pushstacks"))
            interpreter.setUseFrames(true);

        // Target function string
        targetFunctionString = getParam("target-function-string", true);
        if (targetFunctionString == null) {
            targetFunctionString = defaultTargetFunctionString;
        }

        // Init the GA
        super.initFromParameters();

        // Print important parameters
        print("  Important Parameters\n");
        print(" ======================\n");

        if (!targetFunctionString.equals("")) {
            print("Target Function: " + targetFunctionString + "\n\n");
        }

        print("Population Size: " + (int) getFloatParam("population-size")
                + "\n");
        print("Generations: " + maxGenerations + "\n");
        print("Execution Limit: " + executionLimit + "\n\n");

        print("Crossover Percent: " + crossoverPercent + "\n");
        print("Mutation Percent: " + mutationPercent + "\n");
        print("Simplification Percent: " + simplificationPercent + "\n");
        print("Clone Percent: "
                + (100 - crossoverPercent - mutationPercent - simplificationPercent)
                + "\n\n");

        print("Tournament Size: " + tournamentSize + "\n");
        if (trivialGeographyRadius != 0) {
            print("Trivial Geography Radius: " + trivialGeographyRadius + "\n");
        }
        print("Node Selection Mode: " + nodeSelectionMode);
        print("\n");

        print("Instructions: " + interpreter.getInstructionsString() + "\n");

        print("\n");

    }

    public void initIndividual(GAIndividual inIndividual) {
        PushGPIndividual i = (PushGPIndividual) inIndividual;

        int randomCodeSize = random.nextInt(maxRandomCodeSize) + 2;
        Program p = interpreter.randomCode(randomCodeSize);

        i.setProgram(p);
    }

    protected void beginGeneration() throws Exception {
        averageSize = 0;
    }

    protected void endGeneration() {
        averageSize /= populations[0].length;
    }

    protected void evaluate() {
        float totalFitness = 0;
        bestMeanFitness = Float.MAX_VALUE;

        for (int n = 0; n < populations[currentPopulation].length; n++) {
            GAIndividual i = populations[currentPopulation][n];

            evaluateIndividual(i);

            totalFitness += i.getFitness();

            if (i.getFitness() < bestMeanFitness) {
                bestMeanFitness = i.getFitness();
                bestIndividual = n;
                bestSize = ((PushGPIndividual) i).program.programSize();
                bestErrors = i.getErrors();
            }
        }

        populationMeanFitness = totalFitness
                / populations[currentPopulation].length;
    }

    public void evaluateIndividual(GAIndividual inIndividual) {
        evaluateIndividual(inIndividual, false);
    }

    protected void evaluateIndividual(GAIndividual inIndividual, boolean duringSimplify) {
        List<Float> errors = new ArrayList<>();

        if (!duringSimplify)
            averageSize += ((PushGPIndividual) inIndividual).program
                    .programSize();

        long t = System.currentTimeMillis();

        for (final GATestCase testCase : testCases) {
            float e = evaluateTestCase(inIndividual, testCase.input(), testCase.output());
            errors.add(e);
        }
        t = System.currentTimeMillis() - t;

        inIndividual.setFitness(absoluteAverageOfErrors(errors));
        inIndividual.setErrors(errors);
    }

    abstract protected void initInterpreter(Interpreter inInterpreter) throws Exception;

    protected String report() {
        String report = super.report();

        if (Double.isInfinite(populationMeanFitness))
            populationMeanFitness = Double.MAX_VALUE;

        report += ";; Best Program:\n  "
                + populations[currentPopulation][bestIndividual] + "\n\n";

        report += ";; Best Program Fitness (mean): " + bestMeanFitness + "\n";
        if (testCases.size() == bestErrors.size()) {
            report += ";; Best Program Errors: (";
            for (int i = 0; i < testCases.size(); i++) {
                if (i != 0)
                    report += " ";
                report += "(" + testCases.get(i).input() + " ";
                report += Math.abs(bestErrors.get(i)) + ")";
            }
            report += ")\n";
        }
        report += ";; Best Program Size: " + bestSize + "\n\n";

        report += ";; Mean Fitness: " + populationMeanFitness + "\n";
        report += ";; Mean Program Size: " + averageSize + "\n";

        PushGPIndividual simplified = autosimplify(
                (PushGPIndividual) populations[currentPopulation][bestIndividual],
            reportSimplifications);

        report += ";; Number of Evaluations Thus Far: "
                + interpreter.getEvaluationExecutions() + "\n";
        String mem = String
                .valueOf(Runtime.getRuntime().totalMemory() / 10000000.0f);
        report += ";; Memory usage: " + mem + "\n\n";

        report += ";; Partial Simplification (may beat best):\n  ";
        report += simplified.program + "\n";
        report += ";; Partial Simplification Size: ";
        report += simplified.program.programSize() + "\n\n";

        return report;
    }

    protected String finalReport() {
        String report = "";

        report += super.finalReport();

        if (!targetFunctionString.equals("")) {
            report += ">> Target Function: " + targetFunctionString + "\n\n";
        }

        PushGPIndividual simplified = autosimplify(
                (PushGPIndividual) populations[currentPopulation][bestIndividual],
            finalSimplifications);

        // Note: The number of evaluations here will likely be higher than that
        // given during the last generational report, since evaluations made
        // during simplification count towards the total number of
        // simplifications.
        report += ">> Number of Evaluations: "
                + interpreter.getEvaluationExecutions() + "\n";

        report += ">> Best Program: "
                + populations[currentPopulation][bestIndividual] + "\n";
        report += ">> Fitness (mean): " + bestMeanFitness + "\n";
        if (testCases.size() == bestErrors.size()) {
            report += ">> Errors: (";
            for (int i = 0; i < testCases.size(); i++) {
                if (i != 0)
                    report += " ";
                report += "(" + testCases.get(i).input() + " ";
                report += Math.abs(bestErrors.get(i)) + ")";
            }
            report += ")\n";
        }
        report += ">> Size: " + bestSize + "\n\n";

        report += "<<<<<<<<<< After Simplification >>>>>>>>>>\n";
        report += ">> Best Program: ";
        report += simplified.program + "\n";
        report += ">> Size: ";
        report += simplified.program.programSize() + "\n\n";

        return report;
    }

    public String getTargetFunctionString() {
        return targetFunctionString;
    }

    protected PushGPIndividual autosimplify(PushGPIndividual inIndividual, int steps) {
        PushGPIndividual simplest = (PushGPIndividual) inIndividual.clone();
        PushGPIndividual trial = (PushGPIndividual) inIndividual.clone();
        evaluateIndividual(simplest, true);
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
                evaluateIndividual(trial, true);

                if (trial.getFitness() <= bestError) {
                    simplest = (PushGPIndividual) trial.clone();
                    bestError = trial.getFitness();
                }
            }

            trial = (PushGPIndividual) simplest.clone();
        }

        return simplest;
    }

    protected void reproduce() {
        int nextPopulation = currentPopulation == 0 ? 1 : 0;

        for (int n = 0; n < populations[currentPopulation].length; n++) {
            float method = random.nextInt(100);
            GAIndividual next;

            if (method < mutationPercent) {
                next = reproduceByMutation(n);
            } else if (method < crossoverPercent + mutationPercent) {
                next = reproduceByCrossover(n);
            } else if (method < crossoverPercent + mutationPercent
                    + simplificationPercent) {
                next = reproduceBySimplification(n);
            } else {
                next = reproduceByClone(n);
            }

            populations[nextPopulation][n] = next;
        }
    }

    protected GAIndividual reproduceByCrossover(int inIndex) {
        PushGPIndividual a = (PushGPIndividual) reproduceByClone(inIndex);
        PushGPIndividual b = (PushGPIndividual) tournamentSelect(
            tournamentSize, inIndex);

        if (a.program.programSize() <= 0) {
            return b;
        }
        if (b.program.programSize() <= 0) {
            return a;
        }

        int aindex = reproductionNodeSelection(a);
        int bindex = reproductionNodeSelection(b);

        if (a.program.programSize() + b.program.subtreeSize(bindex)
                - a.program.subtreeSize(aindex) <= maxPointsInProgram)
            a.program.replaceSubtree(aindex, b.program.subtree(bindex));

        return a;
    }

    protected GAIndividual reproduceByMutation(int inIndex) {
        PushGPIndividual i = (PushGPIndividual) reproduceByClone(inIndex);

        int totalsize = i.program.programSize();
        int which = reproductionNodeSelection(i);

        int oldsize = i.program.subtreeSize(which);
        int newsize = 0;

        if (useFairMutation) {
            int range = (int) Math.max(1, fairMutationRange * oldsize);
            newsize = Math.max(1, oldsize + random.nextInt(2 * range) - range);
        } else {
            newsize = random.nextInt(maxRandomCodeSize);
        }

        Object newtree;

        if (newsize == 1)
            newtree = interpreter.randomAtom();
        else
            newtree = interpreter.randomCode(newsize);

        if (newsize + totalsize - oldsize <= maxPointsInProgram)
            i.program.replaceSubtree(which, newtree);

        return i;
    }

    /**
     * Selects a node to use during crossover or mutation. The selection
     * mechanism depends on the global parameter nodeSelectionMode.
     *
     * @param inInd = Individual to select node from.
     * @return Index of the node to use for reproduction.
     */
    protected int reproductionNodeSelection(PushGPIndividual inInd) {
        int totalSize = inInd.program.programSize();
        int selectedNode = 0;

        if (totalSize <= 1) {
            selectedNode = 0;
        } else if (nodeSelectionMode.equals("unbiased")) {
            selectedNode = random.nextInt(totalSize);
        } else if (nodeSelectionMode.equals("leaf-probability")) {
            // TODO Implement. Currently runs unbiased

            // note: if there aren't any internal nodes, must select leaf, and
            // if no leaf, must select internal

            selectedNode = random.nextInt(totalSize);
        } else {
            // size-tournament
            int maxSize = -1;
            selectedNode = 0;

            for (int j = 0; j < nodeSelectionTournamentSize; j++) {
                int nextwhich = random.nextInt(totalSize);
                int nextwhichsize = inInd.program.subtreeSize(nextwhich);

                if (nextwhichsize > maxSize) {
                    selectedNode = nextwhich;
                    maxSize = nextwhichsize;
                }
            }
        }

        return selectedNode;
    }

    protected GAIndividual reproduceBySimplification(int inIndex) {
        PushGPIndividual i = (PushGPIndividual) reproduceByClone(inIndex);

        i = autosimplify(i, reproductionSimplifications);

        return i;
    }

    public void runTestProgram(Program p, int inTestCaseIndex) {
        PushGPIndividual i = new PushGPIndividual(p);
        GATestCase test = testCases.get(inTestCaseIndex);

        System.out.println("Executing program: " + p);

        evaluateTestCase(i, test.input(), test.output());

        System.out.println(interpreter);
    }
}