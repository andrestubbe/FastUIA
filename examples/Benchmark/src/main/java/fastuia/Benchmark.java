package fastuia;

import fastuia.FastUIA;

/**
 * Benchmark comparing FastUIA vs standard alternatives.
 */
public class Benchmark {
    public static void main(String[] args) {
        System.out.println("=== FastUIA Benchmark ===");
        
        FastUIA uia = new FastUIA();
        
        // Warmup
        for (int i = 0; i < 10; i++) {
            uia.GetFocusedElement();
        }
        
        // Benchmark GetFocusedElement
        int iterations = 1000;
        long start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            uia.GetFocusedElement();
        }
        long end = System.nanoTime();
        double avgTimeMs = (end - start) / 1_000_000.0 / iterations;
        
        System.out.println("GetFocusedElement average time: " + String.format("%.3f", avgTimeMs) + " ms");
        System.out.println("=== Benchmark Complete ===");
    }
}
