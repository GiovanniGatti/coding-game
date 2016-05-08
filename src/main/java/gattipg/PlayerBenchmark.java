package gattipg;

import gattipg.Player.Block;
import gattipg.Player.GamePlanner;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PlayerBenchmark {

    @State(Scope.Benchmark)
    public static class Data {

        Player.Grid grid;

        List<Block> incoming;

        @Setup
        public void setup() {
            grid = new Player.Grid(12, 6);
            grid.parseLine(0, "......");
            grid.parseLine(1, "......");
            grid.parseLine(2, "......");
            grid.parseLine(3, "......");
            grid.parseLine(4, "......");
            grid.parseLine(5, "......");
            grid.parseLine(6, "......");
            grid.parseLine(7, "......");
            grid.parseLine(8, "......");
            grid.parseLine(9, "......");
            grid.parseLine(10, "......");
            grid.parseLine(11, "......");

            //~100ms
            incoming = Arrays.asList(
                    new Block(1, 2), new Block(4, 2),
                    new Block(3, 4));

//            incoming = Arrays.asList(
//                    new Player.Block(1, 1), new Player.Block(2, 2),
//                    new Player.Block(3, 3), new Player.Block(3, 3));
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void planning_benchmark(Data d) throws InterruptedException {
        GamePlanner.run(d.grid, d.incoming);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(PlayerBenchmark.class.getSimpleName())
                .warmupIterations(5)
                .measurementIterations(5)
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
