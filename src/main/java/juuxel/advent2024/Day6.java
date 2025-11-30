package juuxel.advent2024;

import juuxel.advent.Loader;

import java.util.stream.Stream;

public final class Day6 {
    private static final Direction START_DIRECTION = Direction.UP;

    public static void main(String[] args) throws Exception {
        run(Loader.lines(2024, 6));
    }

    public static void run(Stream<String> lines) {
        var data = readData(lines);
        var movement = new Part2OuterGuardMovement(data);
        movement.simulate();
        System.out.println(movement.uniqueVisits);
        System.out.println(movement.possibleObstacles);
    }

    private static boolean isWithinBounds(int x, int y, int width, int height) {
        return 0 <= x && x < width && 0 <= y && y < height;
    }

    private static Data readData(Stream<String> lines) {
        var lineList = lines.toList();
        int width = lineList.getFirst().length();
        int height = lineList.size();
        boolean[][] obstacles = new boolean[width][height];
        int guardStartX = 0;
        int guardStartY = 0;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                char c = lineList.get(y).charAt(x);
                switch (c) {
                    case '#' -> obstacles[x][y] = true;
                    case '^' -> {
                        guardStartX = x;
                        guardStartY = y;
                    }
                    case '.' -> {} // ignore
                    default -> throw new IllegalArgumentException("Unknown char in map: " + c);
                }
            }
        }

        return new Data(width, height, obstacles, guardStartX, guardStartY);
    }

    private static class GuardMovement {
        protected final Data data;
        protected int guardX;
        protected int guardY;
        protected Direction guardDirection;

        protected final int[][] visits;
        int uniqueVisits = 0;

        protected GuardMovement(Data data) {
            this(data, data.guardStartX, data.guardStartY, START_DIRECTION);
        }

        protected GuardMovement(Data data, int guardX, int guardY, Direction guardDirection) {
            this.data = data;
            this.guardX = guardX;
            this.guardY = guardY;
            this.guardDirection = guardDirection;
            visits = new int[data.width][data.height];
        }

        protected boolean hasObstacleAt(int x, int y) {
            return data.obstacles[x][y];
        }

        protected void onEachStep() {
        }

        protected boolean simulate() {
            while (isWithinBounds(guardX, guardY, data.width, data.height)) {
                if (visits[guardX][guardY] == 0) {
                    uniqueVisits++;
                } else if ((visits[guardX][guardY] & guardDirection.mask) != 0) {
                    return true; // we found a loop
                }

                markCurrentPos();

                while (isWithinBounds(guardX + guardDirection.offsetX, guardY + guardDirection.offsetY, data.width, data.height)
                    && hasObstacleAt(guardX + guardDirection.offsetX, guardY + guardDirection.offsetY)) {
                    guardDirection = guardDirection.rotate();
                    markCurrentPos();
                }

                onEachStep();

                guardX += guardDirection.offsetX;
                guardY += guardDirection.offsetY;
            }

            // not in a loop
            return false;
        }

        private void markCurrentPos() {
            visits[guardX][guardY] |= guardDirection.mask;
        }
    }

    private static final class Part2OuterGuardMovement extends GuardMovement {
        private final boolean[][] newObstacles;
        int possibleObstacles = 0;

        Part2OuterGuardMovement(Data data) {
            super(data);
            newObstacles = new boolean[data.width][data.height];
        }

        @Override
        protected void onEachStep() {
            super.onEachStep();

            if (!newObstacles[guardX][guardY] && !(guardX == data.guardStartX && guardY == data.guardStartY)) {
                // Let's add the obstacle and then move *from the start* to see if we encounter a possible loop.
                data.obstacles[guardX][guardY] = true;
                var submovement = new GuardMovement(data);
                if (submovement.simulate()) {
                    newObstacles[guardX][guardY] = true;
                    possibleObstacles++;
                }
                data.obstacles[guardX][guardY] = false;
            }
        }
    }

    private record Data(int width, int height, boolean[][] obstacles, int guardStartX, int guardStartY) {
    }

    private enum Direction {
        UP(0, -1, 0b1000), RIGHT(1, 0, 0b0100), DOWN(0, 1, 0b0010), LEFT(-1, 0, 0b0001);

        final int offsetX, offsetY;
        final int mask;

        Direction(int offsetX, int offsetY, int mask) {
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.mask = mask;
        }

        Direction rotate() {
            return switch (this) {
                case UP -> RIGHT;
                case RIGHT -> DOWN;
                case DOWN -> LEFT;
                case LEFT -> UP;
            };
        }
    }
}
