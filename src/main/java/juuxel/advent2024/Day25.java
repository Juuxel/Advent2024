package juuxel.advent2024;

import java.util.ArrayList;
import java.util.List;

public final class Day25 {
    private static final int TOTAL_HEIGHT = 5;

    public static void main(String[] args) throws Exception {
        part1(Loader.lines(25).toList());
    }

    public static void part1(List<String> lines) {
        List<BooleanGrid> locks = new ArrayList<>();
        List<BooleanGrid> keys = new ArrayList<>();

        for (List<String> pattern : Iterables.split(lines, "")) {
            var grid = new CharGrid(pattern);
            boolean isKey = grid.get(0, 0) == '.';
            BooleanGrid patternGrid = new BooleanGrid(grid.width(), TOTAL_HEIGHT);
            for (int x = 0; x < grid.width(); x++) {
                for (int y = 0; y < TOTAL_HEIGHT; y++) {
                    patternGrid.set(x, y, grid.get(x, y + 1) == '#');
                }
            }
            (isKey ? keys : locks).add(patternGrid);
        }

        int count = 0;
        for (BooleanGrid lock : locks) {
            key: for (BooleanGrid key : keys) {
                BooleanGrid base = new BooleanGrid(lock);

                for (int x = 0; x < base.width(); x++) {
                    for (int y = 0; y < base.height(); y++) {
                        if (key.get(x, y) && !base.mark(x, y)) continue key;
                    }
                }

                count++;
            }
        }

        System.out.println(count);
    }
}
