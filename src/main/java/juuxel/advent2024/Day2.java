package juuxel.advent2024;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class Day2 {
    public static void main(String[] args) throws Exception {
        part1(Loader.lines(2));
        part2(Loader.lines(2));
    }

    public static void part1(Stream<String> lines) {
        partN(lines, Report::isSafeP1);
    }

    public static void part2(Stream<String> lines) {
        partN(lines, Report::isSafeP2);
    }

    private static void partN(Stream<String> lines, Predicate<Report> isSafe) {
        long safeReports = lines
            .map(line -> {
                var parts = line.split(" ");
                var levels = new ArrayList<Integer>(parts.length);
                for (String part : parts) {
                    levels.add(Integer.parseInt(part));
                }
                return levels;
            })
            .map(Report::new)
            .filter(isSafe)
            .count();
        System.out.println(safeReports);
    }

    private record Report(List<Integer> levels) {
        boolean isSafeP1() {
            return isSafe(levels, false);
        }

        boolean isSafeP2() {
            return isSafe(levels, true);
        }

        private static boolean isSafe(List<Integer> levels, boolean allowRemoving) {
            var direction = Direction.NONE;
            boolean success = true;

            for (int i = 0; i < levels.size() - 1; i++) {
                int a = levels.get(i);
                int b = levels.get(i + 1);
                int diff = b - a;

                if (a == b || Math.abs(diff) > 3 || !direction.matches(diff)) {
                    success = false;
                    break;
                }

                if (direction == Direction.NONE) {
                    direction = a < b ? Direction.INCREASING : Direction.DECREASING;
                }
            }

            if (allowRemoving && !success) {
                for (int i = 0; i < levels.size(); i++) {
                    List<Integer> subLevels = except(levels, i);
                    if (isSafe(subLevels, false)) {
                        return true;
                    }
                }
            }

            return success;
        }

        private static <T> List<T> except(List<T> list, int index) {
            List<T> subList = new ArrayList<>(list);
            subList.remove(index);
            return subList;
        }
    }

    private enum Direction {
        INCREASING,
        DECREASING,
        NONE,
        ;

        boolean matches(int n) {
            return switch (this) {
                case INCREASING -> n > 0;
                case DECREASING -> n < 0;
                case NONE -> true;
            };
        }
    }
}
