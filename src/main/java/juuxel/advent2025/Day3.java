package juuxel.advent2025;

import juuxel.advent.Loader;

import java.util.List;
import java.util.stream.Stream;

public final class Day3 {
    public static void main(String[] args) throws Exception {
        part1(Loader.lines(2025, 3));
        part2(Loader.lines(2025, 3));
    }

    public static void part1(Stream<String> lines) {
        System.out.println(lines
            .mapToInt(Day3::maxJoltage)
            .sum());
    }

    private static int parseDigit(char digit) {
        return "0123456789".indexOf(digit);
    }

    private static int maxJoltage(String bank) {
        int max = Integer.MIN_VALUE;

        for (int i = 0; i < bank.length() - 1; i++) {
            for (int j = i + 1; j < bank.length(); j++) {
                int joltage = 10 * parseDigit(bank.charAt(i)) + parseDigit(bank.charAt(j));
                max = Integer.max(joltage, max);
            }
        }

        return max;
    }

    public static void part2(Stream<String> lines) {
        System.out.println(lines
            .mapToLong(bank -> maxJoltageN(bank, 0, 0, 12))
            .sum());
    }

    private static long maxJoltageN(String bank, long existing, int start, int remaining) {
        if (remaining == 0) {
            return existing;
        }

        long maxAtIndex = -1;
        int maxIndex = -1;

        for (int i = start; i < bank.length() - (remaining - 1); i++) {
            long here = parseDigit(bank.charAt(i));
            if (here > maxAtIndex) {
                maxAtIndex = here;
                maxIndex = i;
            }
        }

        return maxJoltageN(bank, existing * 10 + maxAtIndex, maxIndex + 1, remaining - 1);
    }
}
