package juuxel.advent2024;

import java.util.List;
import java.util.stream.Stream;

public final class Day15 {
    public static void main(String[] args) throws Exception {
        part1(Loader.lines(15));
        part2(Loader.lines(15));
    }

    public static void part1(Stream<String> lines) {
        List<List<String>> splitLines = Iterables.split(lines.toList(), "");

        var grid = new ArrayGrid<>(new CharGrid(splitLines.getFirst()));
        int robotX = -1, robotY = -1;

        outer: for (int x = 0; x < grid.width(); x++) {
            for (int y = 0; y < grid.height(); y++) {
                if (grid.get(x, y) == '@') {
                    robotX = x;
                    robotY = y;
                    break outer;
                }
            }
        }

        for (String line : splitLines.getLast()) {
            for (int i = 0; i < line.length(); i++) {
                char insn = line.charAt(i);
                var direction = switch (insn) {
                    case '^' -> Direction.UP;
                    case '<' -> Direction.LEFT;
                    case '>' -> Direction.RIGHT;
                    case 'v' -> Direction.DOWN;
                    default -> throw new RuntimeException("unknown char " + insn);
                };
                if (moveInto(grid, robotX + direction.x, robotY + direction.y, direction, '@')) {
                    robotX += direction.x;
                    robotY += direction.y;
                }
            }
        }

        int score = 0;
        for (int x = 0; x < grid.width(); x++) {
            for (int y = 0; y < grid.height(); y++) {
                if (grid.get(x, y) == 'O') {
                    int gps = 100 * y + x;
                    score += gps;
                }
            }
        }
        System.out.println(score);
    }

    private static boolean moveInto(ArrayGrid<Character> grid, int x, int y, Direction direction, char movedObject) {
        char c = grid.get(x, y);
        boolean allowed = switch (c) {
            case '.' -> true;
            case 'O', '[', ']' -> moveInto(grid, x + direction.x, y + direction.y, direction, c);
            case '#' -> false;
            default -> throw new RuntimeException("unknown char " + c);
        };

        if (allowed) {
            grid.set(x - direction.x, y - direction.y, '.');
            grid.set(x, y, movedObject);
        }

        return allowed;
    }

    public static void part2(Stream<String> lines) {
        List<List<String>> splitLines = Iterables.split(lines.toList(), "");

        var originalGrid = new CharGrid(splitLines.getFirst());
        ArrayGrid<Character> grid = new ArrayGrid<>(originalGrid.width() * 2, originalGrid.height());

        for (int x = 0; x < originalGrid.width(); x++) {
            for (int y = 0; y < originalGrid.height(); y++) {
                char c = originalGrid.get(x, y);
                grid.set(2 * x, y, switch (c) {
                    case '#' -> '#';
                    case 'O' -> '[';
                    case '.' -> '.';
                    case '@' -> '@';
                    default -> throw new RuntimeException("unknown char " + c);
                });
                grid.set(2 * x + 1, y, switch (c) {
                    case '#' -> '#';
                    case 'O' -> ']';
                    case '.', '@' -> '.';
                    default -> throw new RuntimeException("unknown char " + c);
                });
            }
        }

        int robotX = -1, robotY = -1;

        outer: for (int x = 0; x < grid.width(); x++) {
            for (int y = 0; y < grid.height(); y++) {
                if (grid.get(x, y) == '@') {
                    robotX = x;
                    robotY = y;
                    break outer;
                }
            }
        }

        for (String line : splitLines.getLast()) {
            for (int i = 0; i < line.length(); i++) {
                char insn = line.charAt(i);
                var direction = switch (insn) {
                    case '^' -> Direction.UP;
                    case '<' -> Direction.LEFT;
                    case '>' -> Direction.RIGHT;
                    case 'v' -> Direction.DOWN;
                    default -> throw new RuntimeException("unknown char " + insn);
                };
                if (moveIntoP2(grid, robotX + direction.x, robotY + direction.y, direction, '@')) {
                    robotX += direction.x;
                    robotY += direction.y;
                }
            }
        }

        int score = 0;
        for (int x = 0; x < grid.width(); x++) {
            for (int y = 0; y < grid.height(); y++) {
                if (grid.get(x, y) == '[') {
                    int gps = 100 * y + x;
                    score += gps;
                }
            }
        }
        System.out.println(score);
    }

    private static boolean moveIntoP2(ArrayGrid<Character> grid, int x, int y, Direction direction, char movedObject) {
        if (!direction.isVertical()) return moveInto(grid, x, y, direction, movedObject);

        // Let's only consider the left box edge.
        if (movedObject == ']') {
            x--;
            movedObject = '[';
        }

        boolean allowed;
        if (movedObject == '[') {
            char c = grid.get(x, y);
            char d = grid.get(x + 1, y);

            if (c == '#' || d == '#') {
                allowed = false;
            } else if (c == ']' && d == '[') {
                var cloneGrid = new ArrayGrid<>(grid);
                allowed = moveIntoP2(cloneGrid, x + direction.x, y + direction.y, direction, ']') &&
                    moveIntoP2(cloneGrid, x + direction.x + 1, y + direction.y, direction, '[');

                if (allowed) {
                    moveIntoP2(grid, x + direction.x, y + direction.y, direction, ']');
                    moveIntoP2(grid, x + direction.x + 1, y + direction.y, direction, '[');
                }
            } else if (c == ']' || c == '[') { // ]. or []
                allowed = moveIntoP2(grid, x + direction.x, y + direction.y, direction, c);
            } else if (d == '[') { // .[
                allowed = moveIntoP2(grid, x + direction.x + 1, y + direction.y, direction, '[');
            } else { // ..
                allowed = true;
            }

            if (allowed) {
                grid.set(x - direction.x, y - direction.y, '.');
                grid.set(x - direction.x + 1, y - direction.y, '.');
                grid.set(x, y, '[');
                grid.set(x + 1, y, ']');
            }
        } else {
            char c = grid.get(x, y);
            allowed = switch (c) {
                case '.' -> true;
                case '[', ']' -> moveIntoP2(grid, x + direction.x, y + direction.y, direction, c);
                case '#' -> false;
                default -> throw new RuntimeException("unknown char " + c);
            };

            if (allowed) {
                grid.set(x - direction.x, y - direction.y, '.');
                grid.set(x, y, movedObject);
            }
        }

        return allowed;
    }

    private enum Direction {
        UP(0, -1), DOWN(0, 1), LEFT(-1, 0), RIGHT(1, 0);

        final int x;
        final int y;

        Direction(int x, int y) {
            this.x = x;
            this.y = y;
        }

        boolean isVertical() {
            return this == UP || this == DOWN;
        }
    }
}
