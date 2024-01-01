package org.spiderland.Psh;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.spiderland.Psh.ProbClass.CartCentering;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class GeneticAlgorithmTest {

    @Test
    @SneakyThrows
    public void shouldLoadGeneticAlgorithmStateFromCheckpoint() {
        final CartCentering geneticAlgorithm = (CartCentering) GeneticAlgorithm.gaWithParameters(Params.readFromFile(TestUtil.getFileFromResource("CartCenter.pushgp")));
        geneticAlgorithm.run(1);
        final CartCentering instanceUnderTest = (CartCentering) GeneticAlgorithm.gaWithCheckpoint("target/CartCenter0.gz");
        assertThat(instanceUnderTest.maxRandomCodeSize, is(geneticAlgorithm.maxRandomCodeSize));
        assertThat(instanceUnderTest.maxPointsInProgram, is(geneticAlgorithm.maxPointsInProgram));
        assertThat(instanceUnderTest.executionLimit, is(geneticAlgorithm.executionLimit));
        assertThat(instanceUnderTest.useFairMutation, is(geneticAlgorithm.useFairMutation));
        assertThat(instanceUnderTest.fairMutationRange, is(geneticAlgorithm.fairMutationRange));
        assertThat(instanceUnderTest.nodeSelectionMode, is(geneticAlgorithm.nodeSelectionMode));
        assertThat(instanceUnderTest.nodeSelectionLeafProbability, is(geneticAlgorithm.nodeSelectionLeafProbability));
        assertThat(instanceUnderTest.nodeSelectionTournamentSize, is(geneticAlgorithm.nodeSelectionTournamentSize));
        assertThat(instanceUnderTest.averageSize, is(geneticAlgorithm.averageSize));
        assertThat(instanceUnderTest.bestSize, is(geneticAlgorithm.bestSize));
        assertThat(instanceUnderTest.simplificationPercent, is(geneticAlgorithm.simplificationPercent));
        assertThat(instanceUnderTest.simplifyFlattenPercent, is(geneticAlgorithm.simplifyFlattenPercent));
        assertThat(instanceUnderTest.reproductionSimplifications, is(geneticAlgorithm.reproductionSimplifications));
        assertThat(instanceUnderTest.reportSimplifications, is(geneticAlgorithm.reportSimplifications));
        assertThat(instanceUnderTest.finalSimplifications, is(geneticAlgorithm.finalSimplifications));
        assertThat(instanceUnderTest.targetFunctionString, is(geneticAlgorithm.targetFunctionString));
        assertThat(instanceUnderTest.populations, is(geneticAlgorithm.populations));
        assertThat(instanceUnderTest.mutationPercent, is(geneticAlgorithm.mutationPercent));
        assertThat(instanceUnderTest.crossoverPercent, is(geneticAlgorithm.crossoverPercent));
        assertThat(instanceUnderTest.bestMeanFitness, is(geneticAlgorithm.bestMeanFitness));
        assertThat(instanceUnderTest.populationMeanFitness, is(geneticAlgorithm.populationMeanFitness));
        assertThat(instanceUnderTest.bestIndividual, is(geneticAlgorithm.bestIndividual));
        assertThat(instanceUnderTest.bestErrors, is(geneticAlgorithm.bestErrors));
        assertThat(instanceUnderTest.maxGenerations, is(geneticAlgorithm.maxGenerations));
        assertThat(instanceUnderTest.tournamentSize, is(geneticAlgorithm.tournamentSize));
        assertThat(instanceUnderTest.trivialGeographyRadius, is(geneticAlgorithm.trivialGeographyRadius));
        assertThat(instanceUnderTest.parameters, is(geneticAlgorithm.parameters));
        assertThat(instanceUnderTest.testCases, is(geneticAlgorithm.testCases));
        assertThat(instanceUnderTest.individualClass, is(geneticAlgorithm.individualClass));
        assertThat(instanceUnderTest.checkpointPrefix, is(geneticAlgorithm.checkpointPrefix));
        assertThat(instanceUnderTest.outputFile, is(geneticAlgorithm.outputFile));
        assertThat(instanceUnderTest.checkpoint.checkpointNumber, is(geneticAlgorithm.checkpoint.checkpointNumber));
        assertThat(instanceUnderTest.currentPopulation, is(0));
        assertThat(instanceUnderTest.generationCount, is(0));
    }
}
