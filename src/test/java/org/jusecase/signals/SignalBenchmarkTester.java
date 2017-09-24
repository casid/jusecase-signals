package org.jusecase.signals;

import org.junit.Test;
import org.jusecase.signals.example.ResizeSignal;

public class SignalBenchmarkTester {

    private static final int prewarmIterations = 1000000;
    private static final int benchmarkIterations = 100000000;

    private ResizeSignal resizeSignal;


    @Test
    public void withDefaultPool() {
        resizeSignal = new ResizeSignal();
        runBenchmark("default pool");
    }

    @SuppressWarnings("SameParameterValue")
    private void runBenchmark(String name) {
        resizeSignal.add(e -> e.width = e.height * 2 + e.width);

        System.out.println("Prewarming jvm");
        for (int i = 0; i < prewarmIterations; ++i) {
            benchmark();
        }

        System.out.println("Running benchmark");
        long start = System.nanoTime();
        for (int i = 0; i < benchmarkIterations; ++i) {
            benchmark();
        }
        long end = System.nanoTime();
        System.out.println("Benchmark '" + name + "' took " + ((end - start) / 1000000000.0) + "s");
        System.out.println("Used Memory:" + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024)));
    }

    private void benchmark() {
        resizeSignal.dispatch(1024, 768);
    }
}
