package juuxel.advent2024;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Stream;

public final class Day12 {
    public static void main(String[] args) throws Exception {
        run(Loader.lines(12));
    }

    public static void run(Stream<String> lines) {
        var grid = new CharGrid(lines);
        var visited = new BooleanGrid(grid.width(), grid.height());
        List<RegionData> regions = new ArrayList<>();

        for (int x = 0; x < grid.width(); x++) {
            for (int y = 0; y < grid.height(); y++) {
                if (!visited.get(x, y)) {
                    regions.add(probeRegion(grid, visited, x, y));
                }
            }
        }

        System.out.println(regions.stream().mapToInt(RegionData::price).sum());
        System.out.println(regions.stream().mapToInt(RegionData::part2Price).sum());
    }

    private static RegionData probeRegion(CharGrid grid, BooleanGrid visited, int x, int y) {
        // TODO: Replace with a single grid with bitmasks?
        BooleanGrid leftGrid = new BooleanGrid(grid.width(), grid.height());
        BooleanGrid rightGrid = new BooleanGrid(grid.width(), grid.height());
        BooleanGrid upGrid = new BooleanGrid(grid.width(), grid.height());
        BooleanGrid downGrid = new BooleanGrid(grid.width(), grid.height());
        char c = grid.getChar(x, y);
        int area = 0;
        int perimeter = 0;
        int sides = 0;

        record Movement(int x, int y) {
        }

        Queue<Movement> movements = new ArrayDeque<>();
        movements.offer(new Movement(x, y));
        Movement movement;

        while ((movement = movements.poll()) != null) {
            if (!grid.contains(movement.x, movement.y)) continue;
            if (grid.getChar(movement.x, movement.y) != c) continue;

            if (visited.mark(movement.x, movement.y)) {
                area++;
                if (!grid.contains(movement.x - 1, movement.y) || grid.getChar(movement.x - 1, movement.y) != c) {
                    perimeter++;
                    leftGrid.mark(movement.x, movement.y);
                    if ((!grid.contains(movement.x, movement.y - 1) || !leftGrid.get(movement.x, movement.y - 1))
                        && (!grid.contains(movement.x, movement.y + 1) || !leftGrid.get(movement.x, movement.y + 1))) {
                        sides++;
                    }
                }
                if (!grid.contains(movement.x + 1, movement.y) || grid.getChar(movement.x + 1, movement.y) != c) {
                    perimeter++;
                    rightGrid.mark(movement.x, movement.y);
                    if ((!grid.contains(movement.x, movement.y - 1) || !rightGrid.get(movement.x, movement.y - 1))
                        && (!grid.contains(movement.x, movement.y + 1) || !rightGrid.get(movement.x, movement.y + 1))) {
                        sides++;
                    }
                }
                if (!grid.contains(movement.x, movement.y - 1) || grid.getChar(movement.x, movement.y - 1) != c) {
                    perimeter++;
                    upGrid.mark(movement.x, movement.y);
                    if ((!grid.contains(movement.x - 1, movement.y) || !upGrid.get(movement.x - 1, movement.y))
                        && (!grid.contains(movement.x + 1, movement.y) || !upGrid.get(movement.x + 1, movement.y))) {
                        sides++;
                    }
                }
                if (!grid.contains(movement.x, movement.y + 1) || grid.getChar(movement.x, movement.y + 1) != c) {
                    perimeter++;
                    downGrid.mark(movement.x, movement.y);
                    if ((!grid.contains(movement.x - 1, movement.y) || !downGrid.get(movement.x - 1, movement.y))
                        && (!grid.contains(movement.x + 1, movement.y) || !downGrid.get(movement.x + 1, movement.y))) {
                        sides++;
                    }
                }

                movements.offer(new Movement(movement.x - 1, movement.y));
                movements.offer(new Movement(movement.x + 1, movement.y));
                movements.offer(new Movement(movement.x, movement.y - 1));
                movements.offer(new Movement(movement.x, movement.y + 1));
            }
        }

        return new RegionData(area, perimeter, sides);
    }

    private record RegionData(int area, int perimeter, int sides) {
        int price() {
            return area * perimeter;
        }

        int part2Price() {
            return area * sides;
        }
    }
}
