package juuxel.advent2024;

import juuxel.advent.Loader;

import java.util.stream.Stream;

public final class Day4 {
    public static void main(String[] args) throws Exception {
        part1(Loader.lines(2024, 4));
        part2(Loader.lines(2024, 4));
    }

    public static void part1(Stream<String> lines) {
        var grid = lines.toList();
        var width = grid.getFirst().length();
        var height = grid.size();
        int found = 0;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                char start = grid.get(y).charAt(x);
                if (start == 'X') {
                    directions: for (Direction direction : Direction.values()) {
                        for (int j = 0; j < 3; j++) {
                            int xo = x + (j + 1) * direction.x;
                            int yo = y + (j + 1) * direction.y;
                            if (!contains(width, height, xo, yo)) continue directions;

                            var lookingFor = "MAS".charAt(j);
                            if (grid.get(yo).charAt(xo) != lookingFor) continue directions;
                        }

                        found++;
                    }
                }
            }
        }

        System.out.println(found);
    }

    private static boolean contains(int width, int height, int x, int y) {
        return 0 <= x && x < width && 0 <= y && y < height;
    }

    public static void part2(Stream<String> lines) {
        var grid = lines.toList();
        var width = grid.getFirst().length();
        var height = grid.size();
        int found = 0;

        for (int x = 1; x < width - 1; x++) {
            for (int y = 1; y < height - 1; y++) {
                char start = grid.get(y).charAt(x);
                if (start == 'A') {
                    char tl = grid.get(y - 1).charAt(x - 1);
                    char tr = grid.get(y - 1).charAt(x + 1);
                    char bl = grid.get(y + 1).charAt(x - 1);
                    char br = grid.get(y + 1).charAt(x + 1);
                    if ((tl == 'S' && br == 'M') || (br == 'S' && tl == 'M')) {
                        if ((tr == 'S' && bl == 'M') || (bl == 'S' && tr == 'M')) {
                            found++;
                        }
                    }
                }
            }
        }

        System.out.println(found);
    }

    private enum Direction {
        N(0, 1),
        NE(1, 1),
        E(1, 0),
        SE(1, -1),
        S(0, -1),
        SW(-1, -1),
        W(-1, 0),
        NW(-1, 1),
        ;

        final int x, y;

        Direction(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
