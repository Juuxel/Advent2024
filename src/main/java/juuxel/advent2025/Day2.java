package juuxel.advent2025;

import juuxel.advent.Loader;

import java.util.Arrays;
import java.util.List;

public final class Day2 {
    public static void main(String[] args) throws Exception {
        part1(Loader.lines(2025, 2).toList());
        part2(Loader.lines(2025, 2).toList());
    }

    private static List<Range> parse(String line) {
        return Arrays.stream(line.split(","))
            .map(x -> {
                String[] parts = x.split("-");
                return new Range(Long.parseLong(parts[0]), Long.parseLong(parts[1]));
            })
            .toList();
    }

    public static void part1(List<String> lines) {
        long invalidIdSum = parse(String.join("", lines))
            .stream()
            .mapToLong(Range::invalidIdSumPart1)
            .sum();
        System.out.println(invalidIdSum);
    }

    public static void part2(List<String> lines) {
        long invalidIdSum = parse(String.join("", lines))
            .stream()
            .mapToLong(Range::invalidIdSumPart2)
            .sum();
        System.out.println(invalidIdSum);
    }

    private record Range(long start, long end) {
        long invalidIdSumPart1() {
            long sum = 0;

            for (long i = start; i <= end; i++) {
                var s = String.valueOf(i);
                if (s.length() % 2 != 0) continue;

                String half = s.substring(0, s.length() / 2);
                if (s.equals(half + half)) {
                    sum += i;
                }
            }

            return sum;
        }

        long invalidIdSumPart2() {
            long sum = 0;

            for (long i = start; i <= end; i++) {
                var s = String.valueOf(i);

                // check potential lengths
                for (int l = 1; l <= s.length() / 2; l++) {
                    if (s.length() % l != 0) continue;

                    String part = s.substring(0, l);
                    if (s.equals(part.repeat(s.length() / l))) {
                        sum += i;
                        break;
                    }
                }
            }

            return sum;
        }
    }
}
