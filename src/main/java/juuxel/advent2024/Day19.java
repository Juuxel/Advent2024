package juuxel.advent2024;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.stream.Stream;

public final class Day19 {
    public static void main(String[] args) throws Exception {
        part1(Loader.lines(19).toList());
        part2(Loader.lines(19));
    }

    public static void part1(List<String> lines) {
        var parts = Arrays.asList(lines.getFirst().split(", "));
        // Optimisation: sort the strings by length, descending
        parts.sort(Comparator.comparingInt(String::length).reversed());

        int size = lines.size() - 2;
        var iter = lines.listIterator(2);
        int possible = 0;
        int i = 0;
        while (iter.hasNext()) {
            System.out.printf("Processing %d/%d%n", ++i, size);
            var target = iter.next();
            if (canMakeWithConcats(target, parts)) {
                possible++;
            }
        }
        System.out.println(possible);
    }

    public static void part2(Stream<String> lines) {
    }

    private static boolean canMakeWithConcats(String target, List<String> parts) {
        Deque<String> queue = new ArrayDeque<>();
        queue.offer(target);

        String current;
        while ((current = queue.pollFirst()) != null) {
            if (current.isEmpty()) return true;

            for (String part : parts) {
                if (current.startsWith(part)) {
                    queue.offerFirst(current.substring(part.length()));
                }
            }
        }

        return false;
    }
}
