package juuxel.advent2024;

import org.apache.commons.statistics.distribution.NormalDistribution;

import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class Day14Part2Statistical {
    public static void main(String[] args) throws Exception {
        part2(Loader.lines(14));
    }

    public static void part2(Stream<String> lines) {
        var robots = lines.map(Day14::readRobot).toList();
        double[] pValues = IntStream.range(0, Day14.GRID_WIDTH * Day14.GRID_HEIGHT)
            .mapToObj(time -> robots.stream().map(robot -> robot.simulate(time)))
            .mapToDouble(Day14Part2Statistical::pValue)
            .toArray();

        double min = Double.MAX_VALUE;
        int minAt = -1;

        for (int i = 0; i < pValues.length; i++) {
            if (pValues[i] < min) {
                min = pValues[i];
                minAt = i;
            }
        }

        System.out.printf("%d is most probable, p = %f%n", minAt, min);
    }

    private static double pValue(Stream<Day14.Vector> positions) {
        int[] observations = new int[Day14.GRID_WIDTH * Day14.GRID_HEIGHT];
        positions.forEach(vector -> observations[vector.x() + vector.y() * Day14.GRID_WIDTH]++);
        int runs = 0;
        int current = -1;
        int zeros = 0;

        for (int observation : observations) {
            if (current == -1 || ((current == 0) != (observation == 0))) {
                runs++;
                current = observation;
            }

            if (current == 0) {
                zeros++;
            }
        }

        int nonzeros = observations.length - zeros;
        double twoNm = 2.0 * zeros * nonzeros;
        double size = observations.length;
        double mean = twoNm / size + 1;
        double variance = (twoNm * (twoNm - zeros - nonzeros)) / (size * size * (size - 1));
        var distribution = NormalDistribution.of(mean, Math.sqrt(variance));
        return 2 * Math.min(distribution.cumulativeProbability(runs + 0.5), distribution.survivalProbability(runs - 0.5));
    }
}
