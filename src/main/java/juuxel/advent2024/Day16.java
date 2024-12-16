package juuxel.advent2024;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.stream.Stream;

public final class Day16 {
    public static void main(String[] args) throws Exception {
        run(Loader.lines(16));
    }

    public static void run(Stream<String> lines) {
        var grid = new CharGrid(lines);
        int startX = -1, startY = -1;
        int endX = -1, endY = -1;

        for (int x = 0; x < grid.width(); x++) {
            for (int y = 0; y < grid.height(); y++) {
                switch (grid.getChar(x, y)) {
                    case 'S' -> {
                        startX = x;
                        startY = y;
                    }
                    case 'E' -> {
                        endX = x;
                        endY = y;
                    }
                }
            }
        }

        // Part 1
        int minScore = aStar(grid, new Point(startX, startY), new Point(endX, endY));
        System.out.println(minScore);

        // Part 2
        List<List<Point>> paths = findAllPathsWithScore(grid, new Point(startX, startY), new Point(endX, endY), minScore);

        BooleanGrid visited = new BooleanGrid(grid.width(), grid.height());
        for (var path : paths) {
            for (Point point : path) {
                visited.mark(point.x, point.y);
            }
        }

        int marked = 0;
        for (int x = 0; x < grid.width(); x++) {
            for (int y = 0; y < grid.height(); y++) {
                if (visited.get(x, y)) {
                    marked++;
                }
            }
        }

        System.out.println(marked);
    }

    private static <T> List<T> append(List<T> list, T value) {
        List<T> result = new ArrayList<>(list.size() + 1);
        result.addAll(list);
        result.add(value);
        return result;
    }

    private static int aStar(CharGrid grid, Point start, Point end) {
        record Pwd(Point point, Direction direction) {
        }
        record Neighbour(Point point, Direction direction, int score) {
        }

        Multimap<Integer, Pwd> openSet = MultimapBuilder.treeKeys().hashSetValues().build();
        openSet.put(0, new Pwd(start, Direction.EAST));
        Map<Point, Integer> pathScores = new HashMap<>(); // min score to get to key from start
        pathScores.put(start, 0);

        while (!openSet.isEmpty()) {
            var iter = openSet.values().iterator();
            var currentPwd = iter.next();
            var current = currentPwd.point;
            var direction = currentPwd.direction;
            iter.remove();

            if (grid.getChar(current.x, current.y) == '#') {
                // We can't move here
                continue;
            }

            int score = pathScores.get(current);

            if (current.equals(end)) {
                return score;
            }

            var cw = direction.turnClockwise();
            var ccw = direction.turnCounterclockwise();
            Neighbour[] neighbours = {
                new Neighbour(new Point(current.x + direction.x, current.y + direction.y), direction, score + 1),
                new Neighbour(new Point(current.x + cw.x, current.y + cw.y), cw, score + 1001),
                new Neighbour(new Point(current.x + ccw.x, current.y + ccw.y), ccw, score + 1001),
            };

            for (Neighbour neighbour : neighbours) {
                if (neighbour.score < pathScores.getOrDefault(neighbour.point, Integer.MAX_VALUE)) {
                    pathScores.put(neighbour.point, neighbour.score);
                    var pwd = new Pwd(neighbour.point, neighbour.direction);
                    if (!openSet.containsValue(pwd)) {
                        openSet.put(neighbour.score + 1000 + Math.abs(neighbour.point.x - end.x) + Math.abs(neighbour.point.y - end.y), pwd);
                    }
                }
            }
        }

        return -1;
    }

    private static List<List<Point>> findAllPathsWithScore(CharGrid grid, Point start, Point end, int targetScore) {
        record Movement(Point point, Direction direction, int score, List<Point> path) {
        }
        record Pwd(Point point, Direction direction) {
        }
        record Neighbour(Point point, Direction direction, int score) {
        }

        Multimap<Integer, Movement> openSet = MultimapBuilder.treeKeys().hashSetValues().build();
        openSet.put(0, new Movement(start, Direction.EAST, 0, List.of(start)));
        Map<Pwd, Integer> pathScores = new HashMap<>(); // min score to get to key from start
        pathScores.put(new Pwd(start, Direction.EAST), 0);

        List<List<Point>> allPaths = new ArrayList<>();

        while (!openSet.isEmpty()) {
            var iter = openSet.values().iterator();
            var movement = iter.next();
            var current = movement.point;
            var direction = movement.direction;
            iter.remove();

            if (grid.getChar(current.x, current.y) == '#') {
                // We can't move here
                continue;
            }

            int score = movement.score;
            if (score > targetScore) continue;

            if (current.equals(end)) {
                allPaths.add(movement.path);
                continue;
            }

            var cw = direction.turnClockwise();
            var ccw = direction.turnCounterclockwise();
            Neighbour[] neighbours = {
                new Neighbour(new Point(current.x + direction.x, current.y + direction.y), direction, score + 1),
                new Neighbour(new Point(current.x + cw.x, current.y + cw.y), cw, score + 1001),
                new Neighbour(new Point(current.x + ccw.x, current.y + ccw.y), ccw, score + 1001),
            };

            for (Neighbour neighbour : neighbours) {
                var neighbourPwd = new Pwd(neighbour.point, neighbour.direction);
                if (neighbour.score <= pathScores.getOrDefault(neighbourPwd, Integer.MAX_VALUE)) {
                    pathScores.put(neighbourPwd, neighbour.score);
                    var nextMovement = new Movement(neighbour.point, neighbour.direction, neighbour.score, append(movement.path, neighbour.point));
                    if (!openSet.containsValue(nextMovement)) {
                        openSet.put(neighbour.score + 1000 + Math.abs(neighbour.point.x - end.x) + Math.abs(neighbour.point.y - end.y), nextMovement);
                    }
                }
            }
        }

        return allPaths;
    }

    private record Point(int x, int y) {
    }

    private enum Direction {
        NORTH(0, -1), SOUTH(0, 1), WEST(-1, 0), EAST(1, 0);

        final int x;
        final int y;

        Direction(int x, int y) {
            this.x = x;
            this.y = y;
        }

        Direction turnClockwise() {
            return switch (this) {
                case NORTH -> EAST;
                case SOUTH -> WEST;
                case WEST -> NORTH;
                case EAST -> SOUTH;
            };
        }

        Direction turnCounterclockwise() {
            return switch (this) {
                case NORTH -> WEST;
                case SOUTH -> EAST;
                case WEST -> SOUTH;
                case EAST -> NORTH;
            };
        }
    }
}
