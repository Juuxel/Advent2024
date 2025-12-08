package juuxel.advent2025;

import juuxel.advent.ArrayGrid;
import juuxel.advent.CharGrid;
import juuxel.advent.Loader;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public final class Day7 {
    public static void main(String[] args) throws Exception {
        part1(Loader.lines(2025, 7));
        part2(Loader.lines(2025, 7));
    }

    public static void part1(Stream<String> lines) {
        CharGrid inputGrid = new CharGrid(lines);
        Set<Integer> beamPositions = Set.of(inputGrid.rowAt(0).indexOf('S'));
        int splits = 0;

        for (int y = 1; y < inputGrid.height(); y++) {
            Set<Integer> nextBeamPositions = new HashSet<>();

            for (int beamPos : beamPositions) {
                if (inputGrid.get(beamPos, y) == '^') {
                    nextBeamPositions.add(beamPos - 1);
                    nextBeamPositions.add(beamPos + 1);
                    splits++;
                } else {
                    nextBeamPositions.add(beamPos);
                }
            }

            beamPositions = nextBeamPositions;
        }

        System.out.println(splits);
    }

    // Do this part by keeping track of how many timelines can reach each position on a grid,
    // then summing up the counts along the bottom edge of the manifold.
    public static void part2(Stream<String> lines) {
        CharGrid inputGrid = new CharGrid(lines);
        ArrayGrid<Long> outputGrid = new ArrayGrid<>(inputGrid.width(), inputGrid.height() + 1, 0L);
        outputGrid.set(inputGrid.rowAt(0).indexOf('S'), 1, 1L);

        for (int y = 1; y < inputGrid.height(); y++) {
            for (int x = 0; x < inputGrid.width(); x++) {
                long timelinesHere = outputGrid.get(x, y);
                if (timelinesHere == 0) continue;

                if (inputGrid.getChar(x, y) == '^') {
                    add(outputGrid, x - 1, y + 1, timelinesHere);
                    add(outputGrid, x + 1, y + 1, timelinesHere);
                } else {
                    add(outputGrid, x, y + 1, timelinesHere);
                }
            }
        }

        long timelineCount = outputGrid.rowAt(inputGrid.height()).stream().mapToLong(Long::longValue).sum();
        System.out.println(timelineCount);
    }

    private static void add(ArrayGrid<Long> grid, int x, int y, long toAdd) {
        grid.set(x, y, grid.get(x, y) + toAdd);
    }
}
