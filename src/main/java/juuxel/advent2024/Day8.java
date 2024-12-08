package juuxel.advent2024;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

public final class Day8 {
    public static void main(String[] args) throws Exception {
        part1(Loader.lines(8));
        part2(Loader.lines(8));
    }

    public static void part1(Stream<String> lines) {
        partN(lines, (grid, antennaA, antennaB, diff, sink) -> {
            sink.accept(antennaA.minus(diff));
            sink.accept(antennaB.plus(diff));
        });
    }

    public static void part2(Stream<String> lines) {
        partN(lines, (grid, antennaA, antennaB, diff, sink) -> {
            // Note: never happens in my puzzle input - but I want the solution to be input-proof.
            int gcd = Mth.gcd(diff.x, diff.y);
            if (gcd != 1) {
                diff = new Vec2i(diff.x / gcd, diff.y / gcd);
            }

            for (Vec2i current = antennaA; current.isIn(grid); current = current.minus(diff)) {
                sink.accept(current);
            }

            for (Vec2i current = antennaA; current.isIn(grid); current = current.plus(diff)) {
                sink.accept(current);
            }
        });
    }

    private static void partN(Stream<String> lines, AntinodeFinder finder) {
        CharGrid grid = new CharGrid(lines);
        Map<Character, List<Vec2i>> antennasByFrequency = new HashMap<>();

        for (int x = 0; x < grid.width(); x++) {
            for (int y = 0; y < grid.height(); y++) {
                char c = grid.getChar(x, y);
                if (c == '.') continue;

                antennasByFrequency.computeIfAbsent(c, unused -> new ArrayList<>())
                    .add(new Vec2i(x, y));
            }
        }

        long uniqueAntinodes = antennasByFrequency.values().stream()
            .mapMulti((List<Vec2i> antennas, Consumer<Vec2i> sink) -> {
                for (int i = 0; i < antennas.size() - 1; i++) {
                    var antennaA = antennas.get(i);

                    for (int j = i + 1; j < antennas.size(); j++) {
                        var antennaB = antennas.get(j);
                        var diff = antennaB.minus(antennaA);
                        finder.findAntinodes(grid, antennaA, antennaB, diff, sink);
                    }
                }
            })
            .filter(vec -> vec.isIn(grid)) // only needed for part 1
            .distinct()
            .count();
        System.out.println(uniqueAntinodes);
    }

    private record Vec2i(int x, int y) {
        Vec2i plus(Vec2i other) {
            return new Vec2i(x + other.x, y + other.y);
        }

        Vec2i minus(Vec2i other) {
            return new Vec2i(x - other.x, y - other.y);
        }

        boolean isIn(Grid<?> grid) {
            return grid.contains(x, y);
        }
    }

    @FunctionalInterface
    private interface AntinodeFinder {
        void findAntinodes(CharGrid grid, Vec2i antennaA, Vec2i antennaB, Vec2i diff, Consumer<Vec2i> sink);
    }
}
