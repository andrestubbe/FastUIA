package fastuia.benchmark;

import fastuia.FastUIA;
import fastuia.FastUIAElement;
import fastuia.Rect;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * JMH Microbenchmark Suite for FastUIA.
 *
 * Measures throughput of core UIA operations:
 * - getFocusedElement: direct IUIAutomation COM query
 * - getElementFromPoint: screen-coordinate element lookup
 * - getName: native string property retrieval
 * - getBoundingRect: bounding rectangle query (zero-copy int[])
 * - supportsValue + getValue: pattern check + value read
 *
 * Run via: java -jar fastuia-benchmark.jar
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 3, time = 2)
@Fork(1)
public class Benchmark {

    private FastUIA uia;
    private FastUIAElement focused;

    @Setup(Level.Trial)
    public void setup() {
        uia = new FastUIA();
        // Acquire a focused element once for property benchmarks.
        // Click into any window before running to ensure a non-null element.
        focused = uia.getFocusedElement();
    }

    @TearDown(Level.Trial)
    public void teardown() {
        if (focused != null) {
            focused.release();
        }
    }

    /**
     * Benchmark: IUIAutomation.GetFocusedElement()
     * Measures how fast we can query the focused desktop element per call.
     */
    @Benchmark
    public FastUIAElement benchmarkGetFocusedElement() {
        return uia.getFocusedElement();
    }

    /**
     * Benchmark: IUIAutomation.ElementFromPoint()
     * Measures screen-coordinate lookup cost at the center of the screen.
     */
    @Benchmark
    public FastUIAElement benchmarkGetElementFromPoint() {
        return uia.getElementFromPoint(960, 540);
    }

    /**
     * Benchmark: IUIAutomationElement.get_CurrentName()
     * Measures native string property retrieval (UIA_NamePropertyId).
     */
    @Benchmark
    public String benchmarkGetName() {
        if (focused == null) return null;
        return focused.getName();
    }

    /**
     * Benchmark: IUIAutomationElement.get_CurrentBoundingRectangle()
     * Measures bounding rect retrieval via GetPrimitiveArrayCritical (zero-copy int[]).
     */
    @Benchmark
    public Rect benchmarkGetBoundingRect() {
        if (focused == null) return null;
        return focused.getBoundingRect();
    }

    /**
     * Benchmark: Pattern support check + getValue()
     * Measures the cost of QueryInterface for ValuePattern + value string read.
     */
    @Benchmark
    public String benchmarkSupportsValueAndGet() {
        if (focused == null) return null;
        if (focused.supportsValue()) {
            return focused.getValue();
        }
        return null;
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(Benchmark.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }
}
