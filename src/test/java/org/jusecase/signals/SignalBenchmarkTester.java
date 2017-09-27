package org.jusecase.signals;

import org.junit.jupiter.api.Test;
import org.jusecase.signals.example.ResizeListener;

public class SignalBenchmarkTester {

    private static final int prewarmIterations = 1000000;
    private static final int benchmarkIterations = 100000000;

    private Signal<ResizeListener> resizeSignal;

    private int totalWidth;
    private int totalHeight;

    @Test
    public void withDefaultPool() {
        resizeSignal = new Signal<>();
        runBenchmark("default pool");
    }

    @SuppressWarnings("SameParameterValue")
    private void runBenchmark(String name) {
        resizeSignal.add((w, h) -> {
            totalWidth += w;
            totalHeight += h;
        });

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
        System.out.println("Total height: " + totalHeight + ", totalWidth: " + totalWidth);
    }

    private void benchmark() {
        resizeSignal.dispatch(l -> l.onResize(1024, 768));
    }
}
