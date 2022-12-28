package org.spiderland.Psh;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * An abstract class for running genetic algorithms.
 */
public abstract class GA implements Serializable {
    private static final long serialVersionUID = 1L;

    protected GAIndividual[][] populations;
    protected int currentPopulation;
    protected int generationCount;

    protected float mutationPercent;
    protected float crossoverPercent;

    protected float bestMeanFitness;
    protected double populationMeanFitness;
    protected int bestIndividual;

    protected ArrayList<Float> bestErrors;

    protected int maxGenerations;
    protected int tournamentSize;
    protected int trivialGeographyRadius;

    protected Random random;

    protected HashMap<String, String> parameters;
    public List<GATestCase> testCases;

    protected Class<?> individualClass;

    protected transient OutputStream outputStream;

    protected Checkpoint checkpoint;
    protected String checkpointPrefix;
    protected String outputFile;

    /**
     * Factor method for creating a GA object, with the GA class specified by
     * the problem-class parameter.
     */

    public static GA gaWithParameters(HashMap<String, String> inParams)
            throws Exception {

        Class<?> cls = Class.forName(inParams.get("problem-class"));

        Object gaObject = cls.newInstance();

        if (!(gaObject instanceof GA ga))
            throw (new Exception("problem-class must inherit from class GA"));

        ga.setParams(inParams);
        ga.initFromParameters();

        return ga;
    }

    public static GA gaWithCheckpoint(String checkpoint) throws Exception {
        File checkpointFile = new File(checkpoint);
        FileInputStream zin = new FileInputStream(checkpointFile);
        GZIPInputStream in = new GZIPInputStream(zin);
        ObjectInputStream oin = new ObjectInputStream(in);

        Checkpoint ckpt = (Checkpoint) oin.readObject();
        GA ga = ckpt.ga;
        ga.checkpoint = ckpt;
        ckpt.checkpointNumber++; // because it gets increased only after ckpt is
        // written

        oin.close();

        System.out.println(ckpt.report.toString());

        // Do we want to append to the file if it exists? Or just overwrite it?
        // Heu! Quae enim quaestio animas virorum vero pertemptit.
        // Wowzers! This is, indeed, a question that truly tests mens' souls.

        if (ga.outputFile != null)
            ga.outputStream = new FileOutputStream(ga.outputFile);
        else
            ga.outputStream = System.out;

        return ga;
    }

    protected GA() {
        random = new Random();
        testCases = new ArrayList<>();
        bestMeanFitness = Float.MAX_VALUE;
        outputStream = System.out;
    }

    /**
     * Sets the parameters dictionary for this GA run.
     */

    protected void setParams(HashMap<String, String> inParams) {
        parameters = inParams;
    }

    /**
     * Utility function to fetch an non-optional string value from the parameter
     * list.
     *
     * @param inName the name of the parameter.
     */

    protected String getParam(String inName) throws Exception {
        return getParam(inName, false);
    }

    /**
     * Utility function to fetch a string value from the parameter list,
     * throwing an exception.
     *
     * @param inName     the name of the parameter.
     * @param inOptional whether the parameter is optional. If a parameter is not
     *                   optional, this method will throw an exception if the parameter
     *                   is not found.
     * @return the string value for the parameter.
     */

    protected String getParam(String inName, boolean inOptional)
            throws Exception {
        String value = parameters.get(inName);

        if (value == null && !inOptional)
            throw new Exception("Could not locate required parameter \""
                    + inName + "\"");

        return value;
    }

    /**
     * Utility function to fetch an non-optional float value from the parameter
     * list.
     *
     * @param inName the name of the parameter.
     */

    protected float getFloatParam(String inName) throws Exception {
        return getFloatParam(inName, false);
    }

    /**
     * Utility function to fetch a float value from the parameter list, throwing
     * an exception.
     *
     * @param inName     the name of the parameter.
     * @param inOptional whether the parameter is optional. If a parameter is not
     *                   optional, this method will throw an exception if the parameter
     *                   is not found.
     * @return the float value for the parameter.
     */

    protected float getFloatParam(String inName, boolean inOptional) throws Exception {
        String value = parameters.get(inName);

        if (value == null && !inOptional)
            throw new Exception("Could not locate required parameter \""
                    + inName + "\"");

        if (value == null)
            return Float.NaN;

        return Float.parseFloat(value);
    }

    /**
     * Sets up the GA object with the previously set parameters. This method is
     * typically overridden to read in custom parameters associated with custom
     * subclasses. Subclasses must always call the base class implementation
     * first to ensure that all base parameters are setup.
     */

    protected void initFromParameters() throws Exception {
        // Default parameters to be used when optional parameters are not
        // given.
        int defaultTrivialGeographyRadius = 0;
        String defaultIndividualClass = "org.spiderland.Psh.PushGPIndividual";

        String individualClass = getParam("individual-class", true);
        if (individualClass == null) {
            individualClass = defaultIndividualClass;
        }
        this.individualClass = Class.forName(individualClass);

        mutationPercent = getFloatParam("mutation-percent");
        crossoverPercent = getFloatParam("crossover-percent");
        maxGenerations = (int) getFloatParam("max-generations");
        tournamentSize = (int) getFloatParam("tournament-size");

        // trivial-geography-radius is an optional parameter
        if (Float.isNaN(getFloatParam("trivial-geography-radius", true))) {
            trivialGeographyRadius = defaultTrivialGeographyRadius;
        } else {
            trivialGeographyRadius = (int) getFloatParam("trivial-geography-radius", true);
        }

        checkpointPrefix = getParam("checkpoint-prefix", true);
        checkpoint = new Checkpoint(this);

        resizeAndInitialize((int) getFloatParam("population-size"));

        outputFile = getParam("output-file", true);

        if (outputFile != null)
            outputStream = new FileOutputStream(outputFile);
    }

    /**
     * Sets the population size and resets the GA generation count, as well as
     * initializing the population with random individuals.
     *
     * @param inSize the size of the new GA population.
     */

    protected void resizeAndInitialize(int inSize) throws Exception {
        populations = new GAIndividual[2][inSize];
        currentPopulation = 0;
        generationCount = 0;

        Object iObject = individualClass.newInstance();

        if (!(iObject instanceof GAIndividual individual))
            throw new Exception(
                    "individual-class must inherit from class GAIndividual");

        for (int i = 0; i < inSize; i++) {
            populations[0][i] = individual.clone();
            initIndividual(populations[0][i]);
        }

    }

    /**
     * Run the main GP run loop until the generation limit it met.
     *
     * @return true, indicating the the execution of the GA is complete.
     */

    public boolean run() throws Exception {
        return run(-1);
    }

    /**
     * Run the main GP run loop until the generation limit it met, or until the
     * provided number of generations has elapsed.
     *
     * @param inGenerations The maximum number of generations to run during this call.
     *                      This is distinct from the hard generation limit which
     *                      determines when the GA is actually complete.
     * @return true if the the execution of the GA is complete.
     */

    public boolean run(int inGenerations) throws Exception {
        // inGenerations below must have !=, not >, since often inGenerations
        // is called at -1
        while (!terminate() && inGenerations != 0) {
            beginGeneration();

            evaluate();
            reproduce();

            endGeneration();

            print(report());

            checkpoint();

            System.gc();

            currentPopulation = (currentPopulation == 0 ? 1 : 0);
            generationCount++;
            inGenerations--;
        }

        if (terminate()) {
            // Since this value was changed after termination conditions were
            // set, revert back to previous state.
            currentPopulation = (currentPopulation == 0 ? 1 : 0);

            print(finalReport());
        }

        return (generationCount < maxGenerations);
    }

    /**
     * Determine whether the GA should terminate. This method may be overridden
     * by subclasses to customize GA behavior.
     */

    public boolean terminate() {
        return (generationCount >= maxGenerations || success());
    }

    /**
     * Determine whether the GA has succeeded. This method may be overridden by
     * subclasses to customize GA behavior.
     */

    protected boolean success() {
        return bestMeanFitness == 0.0;
    }

    /**
     * Evaluates the current population and updates their fitness values. This
     * method may be overridden by subclasses to customize GA behavior.
     */
    protected void evaluate() {
        double totalFitness = 0;
        bestMeanFitness = Float.MAX_VALUE;

        for (int n = 0; n < populations[currentPopulation].length; n++) {
            GAIndividual i = populations[currentPopulation][n];

            evaluateIndividual(i);

            totalFitness += i.GetFitness();

            if (i.GetFitness() < bestMeanFitness) {
                bestMeanFitness = i.GetFitness();
                bestIndividual = n;
                bestErrors = i.GetErrors();
            }
        }

        populationMeanFitness = totalFitness / populations[currentPopulation].length;
    }

    /**
     * Reproduces the current population into the next population slot. This
     * method may be overridden by subclasses to customize GA behavior.
     */
    protected void reproduce() {
        int nextPopulation = currentPopulation == 0 ? 1 : 0;

        for (int n = 0; n < populations[currentPopulation].length; n++) {
            float method = random.nextInt(100);
            GAIndividual next;

            if (method < mutationPercent) {
                next = reproduceByMutation(n);
            } else if (method < crossoverPercent + mutationPercent) {
                next = reproduceByCrossover(n);
            } else {
                next = reproduceByClone(n);
            }

            populations[nextPopulation][n] = next;

        }
    }

    /**
     * Prints out population report statistics. This method may be overridden by
     * subclasses to customize GA behavior.
     */

    protected String report() {
        String report = "\n";
        report += ";;--------------------------------------------------------;;\n";
        report += ";;---------------";
        report += " Report for Generation " + generationCount + " ";

        if (generationCount < 10)
            report += "-";
        if (generationCount < 100)
            report += "-";
        if (generationCount < 1000)
            report += "-";

        report += "-------------;;\n";
        report += ";;--------------------------------------------------------;;\n";

        return report;
    }

    protected String finalReport() {
        boolean success = success();
        String report = "\n";

        report += "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>\n";
        report += "                        ";

        if (success) {
            report += "Success";
        } else {
            report += "Failure";
        }
        report += " at Generation " + (generationCount - 1) + "\n";
        report += "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>\n";

        return report;
    }

    /**
     * Logs output of the GA run to the appropriate location (which may be
     * stdout, or a file).
     */

    protected void print(String inStr) throws Exception {
        if (outputStream != null) {
            outputStream.write(inStr.getBytes());
            outputStream.flush();
        }
        checkpoint.report.append(inStr);
    }

    /**
     * Preforms a tournament selection, return the best individual from a sample
     * of the given size.
     *
     * @param inSize The number of individuals to consider in the tournament
     *               selection.
     */

    protected GAIndividual tournamentSelect(int inSize, int inIndex) {
        int popsize = populations[currentPopulation].length;

        int best = tournamentSelectionIndex(inIndex, popsize);
        float bestFitness = populations[currentPopulation][best].GetFitness();

        for (int n = 0; n < inSize - 1; n++) {
            int candidate = tournamentSelectionIndex(inIndex, popsize);
            float candidateFitness = populations[currentPopulation][candidate]
                    .GetFitness();

            if (candidateFitness < bestFitness) {
                best = candidate;
                bestFitness = candidateFitness;
            }
        }

        return populations[currentPopulation][best];
    }

    /**
     * Produces an index for a tournament selection candidate.
     *
     * @param inIndex   The index which is to be replaced by the current reproduction
     *                  event (used only if trivial-geography is enabled).
     * @param inPopsize The size of the population.
     * @return the index for the tournament selection.
     */

    protected int tournamentSelectionIndex(int inIndex, int inPopsize) {
        if (trivialGeographyRadius > 0) {
            int index = (random.nextInt(trivialGeographyRadius * 2) - trivialGeographyRadius)
                    + inIndex;
            if (index < 0)
                index += inPopsize;

            return (index % inPopsize);
        } else {
            return random.nextInt(inPopsize);
        }
    }

    /**
     * Clones an individual selected through tournament selection.
     *
     * @return the cloned individual.
     */

    protected GAIndividual reproduceByClone(int inIndex) {
        return tournamentSelect(tournamentSize, inIndex).clone();
    }

    /**
     * Computes the absolute-average-of-errors fitness from an error vector.
     *
     * @return the average error value for the vector.
     */
    protected float absoluteAverageOfErrors(ArrayList<Float> inArray) {
        float total = 0.0f;

        for (Float aFloat : inArray) total += Math.abs(aFloat);

        if (Float.isInfinite(total))
            return Float.MAX_VALUE;

        return (total / inArray.size());
    }

    /**
     * Retrieves GAIndividual at index i from the current population.
     *
     * @param i
     * @return GAIndividual at index i
     */
    public GAIndividual getIndividualFromPopulation(int i) {
        return populations[currentPopulation][i];
    }

    /**
     * Retrieves the best individual from the current population.
     *
     * @return the best GAIndividual in the population.
     */
    public GAIndividual getBestIndividual() {
        if (terminate()) {
            return populations[currentPopulation][bestIndividual];
        }
        int oldpop = (currentPopulation == 0 ? 1 : 0);
        return populations[oldpop][bestIndividual];
    }

    /**
     * @return population size
     */
    public int GetPopulationSize() {
        return populations[currentPopulation].length;
    }

    /**
     * @return generation count
     */
    public int getGenerationCount() {
        return generationCount;
    }

    public int getMaxGenerations() {
        return maxGenerations;
    }

    /**
     * Called at the beginning of each generation. This method may be overridden
     * by subclasses to customize GA behavior.
     *
     * @throws Exception
     */
    protected void beginGeneration() throws Exception {
    }

    /**
     * Called at the end of each generation. This method may be overridden by
     * subclasses to customize GA behavior.
     */
    protected void endGeneration() {
    }

    abstract protected void initIndividual(GAIndividual inIndividual);

    abstract protected void evaluateIndividual(GAIndividual inIndividual);

    abstract public float evaluateTestCase(GAIndividual inIndividual, Object inInput, Object inOutput);

    abstract protected GAIndividual reproduceByCrossover(int inIndex);

    abstract protected GAIndividual reproduceByMutation(int inIndex);

    protected void checkpoint() throws Exception {
        if (checkpointPrefix == null)
            return;

        File file = new File(checkpointPrefix + checkpoint.checkpointNumber
                + ".gz");
        ObjectOutputStream out = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(file)));

        out.writeObject(checkpoint);
        out.flush();
        out.close();
        System.out.println("Wrote checkpoint file " + file.getAbsolutePath());
        checkpoint.checkpointNumber++;
    }
}
