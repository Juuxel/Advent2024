package juuxel.advent2024;

import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class Day14Part2Statistical {
    public static void main(String[] args) throws Exception {
        part2(Loader.lines(14));
    }

    public static void part2(Stream<String> lines) {
        var robots = lines.map(Day14::readRobot).toList();
        int[] runs = IntStream.range(0, Day14.GRID_WIDTH * Day14.GRID_HEIGHT)
            .mapToObj(time -> robots.stream().map(robot -> robot.simulate(time)))
            .mapToInt(Day14Part2Statistical::numberOfRuns)
            .toArray();

        int min = Integer.MAX_VALUE;
        int minAt = -1;

        for (int i = 0; i < runs.length; i++) {
            if (runs[i] < min) {
                min = runs[i];
                minAt = i;
            }
        }

        System.out.printf("%d is most probable, %d runs%n", minAt, min);
    }

    private static int numberOfRuns(Stream<Day14.Vector> positions) {
        int[] observations = new int[Day14.GRID_WIDTH * Day14.GRID_HEIGHT];
        positions.forEach(vector -> observations[vector.x() + vector.y() * Day14.GRID_WIDTH]++);
        int runs = 0;
        int current = -1;

        for (int observation : observations) {
            if (current == -1 || ((current == 0) != (observation == 0))) {
                runs++;
                current = observation;
            }
        }

        return runs;
    }
}
