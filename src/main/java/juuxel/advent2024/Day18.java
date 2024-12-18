package juuxel.advent2024;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public final class Day18 {
    private static final int END = 70;

    public static void main(String[] args) throws Exception {
        part1(Loader.lines(18));
        part2(Loader.lines(18));
    }

    public static void part1(Stream<String> lines) {
        BooleanGrid corrupted = new BooleanGrid(END + 1, END + 1);
        lines.limit(1024).forEach(s -> markGrid(corrupted, s));
        System.out.println(aStar(corrupted));
    }

    public static void part2(Stream<String> lines) {
        BooleanGrid corrupted = new BooleanGrid(END + 1, END + 1);
        var lineList = lines.toList();
        for (String line : lineList) {
            markGrid(corrupted, line);

            var pathLength = aStar(corrupted);
            if (pathLength < 0) {
                System.out.println(line);
                break;
            }
        }
    }

    private static void markGrid(BooleanGrid corrupted, String line) {
        var parts = line.split(",");
        int x = Integer.parseInt(parts[0]);
        int y = Integer.parseInt(parts[1]);
        corrupted.mark(x, y);
    }

    private static int aStar(BooleanGrid corrupted) {
        record Point(int x, int y) {
        }
        Multimap<Integer, Point> openSet = MultimapBuilder.treeKeys().hashSetValues().build();
        openSet.put(0, new Point(0, 0));
        Map<Point, Integer> pathLengths = new HashMap<>(); // min length to get to key from start
        pathLengths.put(new Point(0, 0), 0);

        while (!openSet.isEmpty()) {
            var iter = openSet.values().iterator();
            var current = iter.next();
            iter.remove();

            if (!corrupted.contains(current.x, current.y) || corrupted.get(current.x, current.y)) {
                // We can't move here
                continue;
            }

            int length = pathLengths.get(current);
            if (current.x == END && current.y == END) {
                return length;
            }

            Point[] neighbours = {
                new Point(current.x - 1, current.y),
                new Point(current.x + 1, current.y),
                new Point(current.x, current.y - 1),
                new Point(current.x, current.y + 1),
            };

            for (Point neighbour : neighbours) {
                if (length + 1 < pathLengths.getOrDefault(neighbour, Integer.MAX_VALUE)) {
                    pathLengths.put(neighbour, length + 1);
                    if (!openSet.containsValue(neighbour)) {
                        openSet.put(length + 1 + Math.abs(neighbour.x - END) + Math.abs(neighbour.y - END), neighbour);
                    }
                }
            }
        }

        return -1;
    }
}
