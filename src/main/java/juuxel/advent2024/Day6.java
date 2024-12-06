package juuxel.advent2024;

import java.util.stream.Stream;

public final class Day6 {
    private static final Direction START_DIRECTION = Direction.UP;

    public static void main(String[] args) throws Exception {
        part1(Loader.lines(6));
        part2(Loader.lines(6));
    }

    public static void part1(Stream<String> lines) {
        var data = readData(lines);
        var movement = new Part1GuardMovement(data);
        movement.simulate();
        System.out.println(movement.visitCount);
    }

    private static boolean isWithinBounds(int x, int y, int width, int height) {
        return 0 <= x && x < width && 0 <= y && y < height;
    }

    public static void part2(Stream<String> lines) {
        var data = readData(lines);
        var movement = new Part2OuterGuardMovement(data);
        movement.simulate();
        System.out.println(movement.possibleObstacles);
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

    private static abstract class GuardMovement {
        protected final Data data;
        protected int guardX;
        protected int guardY;
        protected Direction guardDirection;

        protected GuardMovement(Data data, int guardX, int guardY, Direction guardDirection) {
            this.data = data;
            this.guardX = guardX;
            this.guardY = guardY;
            this.guardDirection = guardDirection;
        }

        protected boolean hasObstacleAt(int x, int y) {
            return data.obstacles[x][y];
        }

        protected abstract boolean shouldContinue();
        protected abstract void onEachStep();
        protected void onRotate(int obstacleX, int obstacleY) {
        }

        protected void simulate() {
            while (isWithinBounds(guardX, guardY, data.width, data.height) && shouldContinue()) {
                onEachStep();

                while (isWithinBounds(guardX + guardDirection.offsetX, guardY + guardDirection.offsetY, data.width, data.height)
                    && hasObstacleAt(guardX + guardDirection.offsetX, guardY + guardDirection.offsetY)) {
                    int obstacleX = guardX + guardDirection.offsetX;
                    int obstacleY = guardY + guardDirection.offsetY;
                    guardDirection = guardDirection.rotate();
                    onRotate(obstacleX, obstacleY);
                }

                guardX += guardDirection.offsetX;
                guardY += guardDirection.offsetY;
            }
        }
    }

    private static final class Part1GuardMovement extends GuardMovement {
        private final boolean[][] visited;
        int visitCount = 0;

        Part1GuardMovement(Data data) {
            super(data, data.guardStartX, data.guardStartY, START_DIRECTION);
            visited = new boolean[data.width][data.height];
        }

        @Override
        protected boolean shouldContinue() {
            return true;
        }

        @Override
        protected void onEachStep() {
            if (!visited[guardX][guardY]) {
                visited[guardX][guardY] = true;
                visitCount++;
            }
        }
    }

    private static abstract class Part2GuardMovement extends GuardMovement {
        protected final int[][] visited;

        protected Part2GuardMovement(Data data, int guardX, int guardY, Direction guardDirection) {
            super(data, guardX, guardY, guardDirection);
            visited = new int[data.width][data.height];
        }

        @Override
        protected void onEachStep() {
            markCurrentPos();
        }

        @Override
        protected void onRotate(int obstacleX, int obstacleY) {
            markCurrentPos();
        }

        private void markCurrentPos() {
            visited[guardX][guardY] |= guardDirection.mask;
        }
    }

    private static final class Part2OuterGuardMovement extends Part2GuardMovement {
        private final boolean[][] newObstacles;
        int possibleObstacles = 0;

        Part2OuterGuardMovement(Data data) {
            super(data, data.guardStartX, data.guardStartY, START_DIRECTION);
            newObstacles = new boolean[data.width][data.height];
        }

        @Override
        protected boolean shouldContinue() {
            return true;
        }

        @Override
        protected void onEachStep() {
            super.onEachStep();

            int frontX = guardX + guardDirection.offsetX;
            int frontY = guardY + guardDirection.offsetY;
            if (isWithinBounds(frontX, frontY, data.width, data.height) && !hasObstacleAt(frontX, frontY)
                && !newObstacles[frontX][frontY]
                && !(frontX == data.guardStartX && frontY == data.guardStartY)) {
                // Let's add the obstacle and then move forward to see if we encounter a possible loop.
                var submovement = new Part2LoopDetectorGuardMovement(data, guardX, guardY, guardDirection, frontX, frontY);
                submovement.simulate();
                if (submovement.foundLoop) {
                    newObstacles[frontX][frontY] = true;
                    possibleObstacles++;
                }
            }
        }
    }

    private static final class Part2LoopDetectorGuardMovement extends Part2GuardMovement {
        private final int newObstacleX;
        private final int newObstacleY;
        boolean foundLoop = false;

        // debugging fields
        // private static boolean printedMap = false;
        // private final int startX, startY;
        // private final Direction startDirection;

        Part2LoopDetectorGuardMovement(Data data, int guardX, int guardY, Direction guardDirection, int newObstacleX, int newObstacleY) {
            super(data, guardX, guardY, guardDirection);
            this.newObstacleX = newObstacleX;
            this.newObstacleY = newObstacleY;

            // startX = guardX;
            // startY = guardY;
            // startDirection = guardDirection;
        }

        @Override
        protected boolean hasObstacleAt(int x, int y) {
            return super.hasObstacleAt(x, y) || (x == newObstacleX && y == newObstacleY);
        }

        @Override
        protected boolean shouldContinue() {
            return !foundLoop;
        }

        @Override
        protected void onEachStep() {
            // If we've visited this square before with this rotation, we're in a loop.
            if ((visited[guardX][guardY] & guardDirection.mask) != 0) {
                foundLoop = true;

                // if (!printedMap) {
                //     printedMap = true;
                //     boolean[][] newObstacles = new boolean[data.width][data.height];
                //     newObstacles[newObstacleX][newObstacleY] = true;
                //     printMap(data, visited, newObstacles, startX, startY, startDirection, guardX, guardY);
                // }
            }

            super.onEachStep();
        }
    }

    private static void printMap(Data data, int[][] visited, boolean[][] newObstacles) {
        printMap(data, visited, newObstacles, data.guardStartX, data.guardStartY, START_DIRECTION, -1, -1);
    }

    private static void printMap(Data data, int[][] visited, boolean[][] newObstacles, int startX, int startY, Direction startDirection, int loopX, int loopY) {
        System.out.printf("loop at %d, %d%n", loopX, loopY);
        for (int y = 0; y < data.height; y++) {
            for (int x = 0; x < data.width; x++) {
                if (x == loopX && y == loopY) {
                    System.out.print('L');
                } else if (startX == x && startY == y) {
                    System.out.print(startDirection.symbol());
                } else if (data.obstacles[x][y]) {
                    System.out.print('#');
                } else if (newObstacles != null && newObstacles[x][y]) {
                    System.out.print('O');
                } else {
                    boolean visitedLr = visited != null && (visited[x][y] & (Direction.LEFT.mask | Direction.RIGHT.mask)) != 0;
                    boolean visitedUd = visited != null && (visited[x][y] & (Direction.UP.mask | Direction.DOWN.mask)) != 0;

                    if (visitedLr) {
                        System.out.print(visitedUd ? '+' : '-');
                    } else if (visitedUd) {
                        System.out.print('|');
                    } else {
                        System.out.print('.');
                    }
                }
            }

            System.out.println();
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

        char symbol() {
            return switch (this) {
                case UP -> '^';
                case RIGHT -> '>';
                case DOWN -> 'v';
                case LEFT -> '<';
            };
        }
    }
}
