package juuxel.advent2024;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public final class Day5 {
    public static void main(String[] args) throws Exception {
        part1(Loader.lines(5));
        part2(Loader.lines(5));
    }

    public static void part1(Stream<String> lines) {
        var data = readData(lines);
        var orderings = data.orderings;
        var updates = data.updates;

        int part1 = 0;
        for (Update update : updates) {
            if (update.isInCorrectOrder(orderings)) {
                part1 += update.middlePage();
            }
        }
        System.out.println(part1);
    }

    public static void part2(Stream<String> lines) {
        var data = readData(lines);
        var orderings = data.orderings;
        var updates = data.updates;

        int part2 = 0;
        for (Update update : updates) {
            if (update.isInCorrectOrder(orderings)) continue;

            do {
                update = update.fixOrder(orderings);
            } while (!update.isInCorrectOrder(orderings));

            part2 += update.middlePage();
        }
        System.out.println(part2);
    }

    private static Data readData(Stream<String> lines) {
        List<Ordering> orderings = new ArrayList<>();
        List<Update> updates = new ArrayList<>();

        boolean inOrderings = true;
        var iter = lines.iterator();
        while (iter.hasNext()) {
            var line = iter.next();

            if (line.isBlank()) {
                inOrderings = false;
            } else if (inOrderings) {
                var parts = line.split("\\|");
                orderings.add(new Ordering(Integer.parseInt(parts[0]), Integer.parseInt(parts[1])));
            } else {
                var parts = line.split(",");
                updates.add(new Update(Arrays.stream(parts).map(Integer::parseInt).toList()));
            }
        }

        return new Data(orderings, updates);
    }

    private record Data(List<Ordering> orderings, List<Update> updates) {
    }

    private record Ordering(int a, int b) {
    }

    private record Update(List<Integer> pages) {
        private static Comparator<Integer> orderingComparator(List<Ordering> orderings) {
            return (o1, o2) -> {
                for (Ordering ordering : orderings) {
                    if (o1 == ordering.a && o2 == ordering.b) {
                        return -1;
                    } else if (o1 == ordering.b && o2 == ordering.a) {
                        return 1;
                    }
                }

                return 0;
            };
        }

        boolean isInCorrectOrder(List<Ordering> orderings) {
            var copy = new ArrayList<>(pages);
            copy.sort(orderingComparator(orderings));
            return pages.equals(copy);
        }

        Update fixOrder(List<Ordering> orderings) {
            var copy = new ArrayList<>(pages);
            copy.sort(orderingComparator(orderings));
            return new Update(copy);
        }

        int middlePage() {
            return pages.get(pages.size() / 2);
        }
    }
}
