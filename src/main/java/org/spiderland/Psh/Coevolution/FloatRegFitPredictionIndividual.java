package org.spiderland.Psh.Coevolution;

import org.spiderland.Psh.GAIndividual;
import org.spiderland.Psh.GATestCase;
import org.spiderland.Psh.PushGP;
import org.spiderland.Psh.PushGPIndividual;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FloatRegFitPredictionIndividual extends PredictionGAIndividual {
    private static final long serialVersionUID = 1L;

    // The sample test cases used for fitness prediction.
    private int[] sampleIndices;
    protected static int sampleSize = 8;

    protected PushGP solutionGeneticAlgorithm;

    public FloatRegFitPredictionIndividual() {
        sampleIndices = new int[sampleSize];
        solutionGeneticAlgorithm = null;
    }

    public FloatRegFitPredictionIndividual(PushGP inSolutionGA) {
        sampleIndices = new int[sampleSize];
        solutionGeneticAlgorithm = inSolutionGA;
    }

    public FloatRegFitPredictionIndividual(PushGP inSolutionGA, int[] inSamples) {
        sampleIndices = new int[sampleSize];
        System.arraycopy(inSamples, 0, sampleIndices, 0, sampleSize);
        solutionGeneticAlgorithm = inSolutionGA;
    }


    /**
     * Gets the given sample index
     *
     * @param inIndex
     * @return sample index
     */
    public int GetSampleIndex(int inIndex) {
        return sampleIndices[inIndex];
    }

    /**
     * Sets one of the sample indices to a new sample index.
     *
     * @param index
     * @param sample
     */
    public void SetSampleIndex(int index, int sample) {
        sampleIndices[index] = sample;
    }

    public void SetSampleIndicesAndSolutionGA(PushGP inSolutionGA, int[] inSamples) {
        sampleIndices = new int[sampleSize];
        System.arraycopy(inSamples, 0, sampleIndices, 0, sampleSize);
        solutionGeneticAlgorithm = inSolutionGA;
    }

    @Override
    public float PredictSolutionFitness(PushGPIndividual pgpIndividual) {
        List<Float> errors = new ArrayList<>();

        for (int n = 0; n < sampleSize; n++) {
            GATestCase test = solutionGeneticAlgorithm.testCases.get(sampleIndices[n]);
            float e = solutionGeneticAlgorithm.evaluateTestCase(pgpIndividual, test.input(), test.output());
            errors.add(e);
        }

        return AbsoluteAverageOfErrors(errors);
    }

    @Override
    public GAIndividual clone() {
        return new FloatRegFitPredictionIndividual(solutionGeneticAlgorithm, sampleIndices);
    }

    public String toString() {
        String str = "Prediction Indices: [ ";
        for (int i : sampleIndices) {
            str += i + " ";
        }
        str += "]";
        return str;
    }

    public boolean equalPredictors(GAIndividual inB) {
        int[] a = copyArray(sampleIndices);
        int[] b = copyArray(((FloatRegFitPredictionIndividual) inB).sampleIndices);
        Arrays.sort(a);
        Arrays.sort(b);
        return Arrays.equals(a, b);
    }

    private int[] copyArray(int[] inArray) {
        int[] newArray = new int[inArray.length];

        System.arraycopy(inArray, 0, newArray, 0, inArray.length);

        return newArray;
    }

}
