package juuxel.advent2025;

import juuxel.advent.Loader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public final class Day8 {
    public static void main(String[] args) throws Exception {
        part1(Loader.lines(2025, 8));
        part2(Loader.lines(2025, 8));
    }

    public static void part1(Stream<String> lines) {
        part1(lines, 1000);
    }

    private static List<Pos> parse(Stream<String> lines) {
        return lines.map(line -> {
            var parts = line.split(",", 3);
            return new Pos(Long.parseLong(parts[0]), Long.parseLong(parts[1]), Long.parseLong(parts[2]));
        }).toList();
    }

    private static List<PosPair> computePairs(List<Pos> positions) {
        List<PosPair> pairs = new ArrayList<>();

        for (int i = 0; i < positions.size() - 1; i++) {
            for (int j = i + 1; j < positions.size(); j++) {
                pairs.add(new PosPair(i, positions.get(i), j, positions.get(j)));
            }
        }

        return pairs;
    }

    @SuppressWarnings("unchecked")
    private static void part1(Stream<String> lines, int pairCount) {
        List<Pos> positions = parse(lines);
        List<PosPair> pairs = computePairs(positions);
        pairs.sort(PosPair.COMPARATOR);

        Set<Integer>[] circuits = new Set[positions.size()];

        for (int i = 0; i < positions.size(); i++) {
            Set<Integer> circuit = new HashSet<>();
            circuit.add(i);
            circuits[i] = circuit;
        }

        for (PosPair pair : pairs.subList(0, pairCount)) {
            Set<Integer> circuitA = circuits[pair.indexA];
            Set<Integer> circuitB = circuits[pair.indexB];

            if (circuitA == circuitB) {
                continue;
            }

            circuitA.addAll(circuitB);
            for (int index : circuitB) {
                circuits[index] = circuitA;
            }
        }

        long part1 = Arrays.stream(circuits)
            .distinct()
            .sorted(Comparator.<Set<?>>comparingInt(Set::size).reversed())
            .limit(3)
            .mapToLong(Set::size)
            .reduce(1, (a, b) -> a * b);
        System.out.println(part1);
    }

    @SuppressWarnings("unchecked")
    public static void part2(Stream<String> lines) {
        List<Pos> positions = parse(lines);
        List<PosPair> pairs = computePairs(positions);
        pairs.sort(PosPair.COMPARATOR);

        int circuitCount = positions.size();
        Set<Integer>[] circuits = new Set[positions.size()];

        for (int i = 0; i < positions.size(); i++) {
            Set<Integer> circuit = new HashSet<>();
            circuit.add(i);
            circuits[i] = circuit;
        }

        Iterator<PosPair> pairIter = pairs.iterator();
        PosPair last = null;
        while (circuitCount > 1) {
            PosPair pair = last = pairIter.next();
            Set<Integer> circuitA = circuits[pair.indexA];
            Set<Integer> circuitB = circuits[pair.indexB];

            if (circuitA == circuitB) {
                continue;
            }

            circuitCount--;
            circuitA.addAll(circuitB);
            for (int index : circuitB) {
                circuits[index] = circuitA;
            }
        }

        long part2 = last.a.x * last.b.x;
        System.out.println(part2);
    }

    private record Pos(long x, long y, long z) {
        static long squaredDistance(Pos a, Pos b) {
            long dx = a.x - b.x;
            long dy = a.y - b.y;
            long dz = a.z - b.z;
            return dx * dx + dy * dy + dz * dz;
        }
    }

    private record PosPair(int indexA, Pos a, int indexB, Pos b) {
        static final Comparator<PosPair> COMPARATOR = Comparator.comparingLong(PosPair::squaredDistance);

        long squaredDistance() {
            return Pos.squaredDistance(a, b);
        }
    }
}
