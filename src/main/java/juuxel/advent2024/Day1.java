package juuxel.advent2024;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class Day1 {
    public static void main(String[] args) throws Exception {
        part1(Loader.lines(1));
        part2(Loader.lines(1));
    }

    public static void part1(Stream<String> lines) {
        var left = new ArrayList<Integer>();
        var right = new ArrayList<Integer>();
        lines.forEach(line -> {
            var parts = line.split(" +");
            left.add(Integer.parseInt(parts[0]));
            right.add(Integer.parseInt(parts[1]));
        });
        Collections.sort(left);
        Collections.sort(right);
        int part1 = IntStream.range(0, left.size())
            .map(index -> Math.abs(left.get(index) - right.get(index)))
            .sum();
        System.out.println(part1);
    }

    public static void part2(Stream<String> lines) {
        var left = new ArrayList<Integer>();
        var right = new ArrayList<Integer>();
        lines.forEach(line -> {
            var parts = line.split(" +");
            left.add(Integer.parseInt(parts[0]));
            right.add(Integer.parseInt(parts[1]));
        });
        var appearances = new HashMap<Integer, Integer>();
        int part2 = 0;

        for (int n : left) {
            part2 += n * appearances.computeIfAbsent(n, unused -> {
                int count = 0;
                for (int k : right) {
                    if (k == n) count++;
                }
                return count;
            });
        }

        System.out.println(part2);
    }
}
