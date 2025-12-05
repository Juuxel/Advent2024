package juuxel.advent2025;

import juuxel.advent.Loader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class Day5 {
    public static void main(String[] args) throws Exception {
        part1(Loader.lines(2025, 5).toList());
        part2(Loader.lines(2025, 5).toList());
    }

    public static void part1(List<String> lines) {
        Input input = parse(lines);
        long part1 = input.available.stream()
            .filter(ingredient -> input.freshRanges.stream().anyMatch(range -> range.contains(ingredient)))
            .count();
        System.out.println(part1);
    }

    private static Input parse(List<String> lines) {
        int delimiterIdx = lines.indexOf("");
        List<Range> ranges = lines.subList(0, delimiterIdx).stream()
            .map(line -> {
                var parts = line.split("-", 2);
                return new Range(Long.parseLong(parts[0]), Long.parseLong(parts[1]));
            })
            .toList();
        List<Long> available = lines.subList(delimiterIdx + 1, lines.size()).stream().map(Long::parseLong).toList();
        return new Input(ranges, available);
    }

    public static void part2(List<String> lines) {
        Input input = parse(lines);
        List<Range> disjoint = splitIntoDisjoint(input.freshRanges);
        long part2 = disjoint.stream()
            .mapToLong(Range::count)
            .sum();
        System.out.println(part2);
    }

    private static List<Range> splitIntoDisjoint(List<Range> ranges) {
        List<Range> input = new ArrayList<>(ranges);

        while (!isInReducedForm(input)) {
            List<Range> next = new ArrayList<>();

            for (Range range : input) {
                Iterator<Range> iterator = next.iterator();

                while (iterator.hasNext()) {
                    Range existing = iterator.next();

                    if (existing.overlapsWith(range)) {
                        long start = Long.min(existing.start, range.start);
                        long end = Long.max(existing.end, range.end);
                        iterator.remove();
                        range = new Range(start, end);
                    }
                }

                next.add(range);
            }

            input = next;
        }

        return input;
    }

    private static boolean isInReducedForm(List<Range> ranges) {
        for (int i = 0; i < ranges.size() - 1; i++) {
            for (int j = i + 1; j < ranges.size(); j++) {
                if (ranges.get(i).overlapsWith(ranges.get(j))) {
                    return false;
                }
            }
        }

        return true;
    }

    private record Input(List<Range> freshRanges, List<Long> available) {
    }

    private record Range(long start, long end) {
        boolean contains(long value) {
            return start <= value && value <= end;
        }

        boolean overlapsWith(Range other) {
            // [a    b]
            //    [c    d]

            // [a    b]
            // [c       d]

            //    [a    b]
            // [c       d]

            //    [a  b]
            // [c       d]

            return other.start <= end && start <= other.end;
        }

        long count() {
            return end - start + 1;
        }

        @Override
        public String toString() {
            return start + "-" + end;
        }
    }
}
