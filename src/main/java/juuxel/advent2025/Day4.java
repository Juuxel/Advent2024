package juuxel.advent2025;

import juuxel.advent.CharGrid;
import juuxel.advent.IntGrid;
import juuxel.advent.Loader;

import java.util.stream.Stream;

public final class Day4 {
    public static void main(String[] args) throws Exception {
        part1(Loader.lines(2025, 4));
        part2(Loader.lines(2025, 4));
    }

    public static void part1(Stream<String> lines) {
        var inputGrid = new CharGrid(lines);
        IntGrid neighborGrid = new IntGrid(inputGrid.width(), inputGrid.height());

        for (int x = 0; x < inputGrid.width(); x++) {
            for (int y = 0; y < inputGrid.height(); y++) {
                if (inputGrid.getChar(x, y) == '@') {
                    neighborGrid.incrementIfContains(x - 1, y);
                    neighborGrid.incrementIfContains(x + 1, y);
                    neighborGrid.incrementIfContains(x, y + 1);
                    neighborGrid.incrementIfContains(x, y - 1);
                    neighborGrid.incrementIfContains(x - 1, y - 1);
                    neighborGrid.incrementIfContains(x + 1, y - 1);
                    neighborGrid.incrementIfContains(x - 1, y + 1);
                    neighborGrid.incrementIfContains(x + 1, y + 1);
                }
            }
        }

        int accessible = 0;

        for (int x = 0; x < neighborGrid.width(); x++) {
            for (int y = 0; y < neighborGrid.height(); y++) {
                if (inputGrid.getChar(x, y) == '@' && neighborGrid.get(x, y) < 4) {
                    accessible++;
                }
            }
        }

        System.out.println(accessible);
    }

    public static void part2(Stream<String> lines) {
        var map = new CharGrid(lines).map(x -> x == '@');
        int removedTotal = 0;
        int removedNow;

        do {
            removedNow = 0;
            IntGrid neighborGrid = new IntGrid(map.width(), map.height());

            for (int x = 0; x < map.width(); x++) {
                for (int y = 0; y < map.height(); y++) {
                    if (map.get(x, y)) {
                        neighborGrid.incrementIfContains(x - 1, y);
                        neighborGrid.incrementIfContains(x + 1, y);
                        neighborGrid.incrementIfContains(x, y + 1);
                        neighborGrid.incrementIfContains(x, y - 1);
                        neighborGrid.incrementIfContains(x - 1, y - 1);
                        neighborGrid.incrementIfContains(x + 1, y - 1);
                        neighborGrid.incrementIfContains(x - 1, y + 1);
                        neighborGrid.incrementIfContains(x + 1, y + 1);
                    }
                }
            }

            for (int x = 0; x < neighborGrid.width(); x++) {
                for (int y = 0; y < neighborGrid.height(); y++) {
                    if (map.get(x, y) && neighborGrid.get(x, y) < 4) {
                        removedNow++;
                        removedTotal++;
                        map.set(x, y, false);
                    }
                }
            }
        } while (removedNow > 0);

        System.out.println(removedTotal);
    }
}
