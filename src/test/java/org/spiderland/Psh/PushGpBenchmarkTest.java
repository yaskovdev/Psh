package org.spiderland.Psh;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.results.BenchmarkResult;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.VerboseMode;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.spiderland.Psh.GA.gaWithParameters;
import static org.spiderland.Psh.Params.readFromFile;

@Tag("BenchmarkTest")
public class PushGpBenchmarkTest {

    private static final double AVERAGE_EXPECTED_TIME_MS = 15000;

    @Test
    public void runBenchmarks() throws Exception {
        final Options opt = initBenchmark();
        final Collection<RunResult> results = runBenchmark(opt);
        assertOutputs(results);
    }

    @Benchmark
    public void benchmarkCartCentering() throws Exception {
        final GA ga = gaWithParameters(readFromFile(getFileFromResource("CartCenterBenchmark.pushgp")));
        ga.run();
    }

    private Collection<RunResult> runBenchmark(Options opt) throws RunnerException {
        return new Runner(opt).run();
    }

    private Options initBenchmark() {
        return new OptionsBuilder()
                .include(PushGpBenchmarkTest.class.getSimpleName() + ".*")
                .mode(Mode.AverageTime)
                .verbosity(VerboseMode.EXTRA)
                .timeUnit(TimeUnit.MILLISECONDS)
                .measurementIterations(2)
                .warmupIterations(1)
                .shouldFailOnError(true)
                .shouldDoGC(true)
                .forks(1)
                .build();
    }

    private void assertOutputs(Collection<RunResult> results) {
        for (final RunResult runResult : results) {
            for (final BenchmarkResult benchmarkResult : runResult.getBenchmarkResults()) {
                final Mode mode = benchmarkResult.getParams().getMode();
                final double score = benchmarkResult.getPrimaryResult().getScore();
                final String methodName = benchmarkResult.getPrimaryResult().getLabel();
                assertEquals(Mode.AverageTime, mode, "Test mode is not average mode. Method is " + methodName + ".");
                assertTrue(score < AVERAGE_EXPECTED_TIME_MS, "Benchmark score " + score
                        + " is higher than " + AVERAGE_EXPECTED_TIME_MS + " " + benchmarkResult.getScoreUnit() + ". Too slow.");
            }
        }
    }

    private static File getFileFromResource(final String fileName) throws URISyntaxException {
        final ClassLoader classLoader = PushGpBenchmarkTest.class.getClassLoader();
        final URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("File with name " + fileName + " was not found");
        } else {
            return new File(resource.toURI());
        }
    }
}
