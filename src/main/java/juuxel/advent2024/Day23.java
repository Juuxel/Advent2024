package juuxel.advent2024;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class Day23 {
    private static final String ORDER = "tabcdefghijklmnopqrsuvwxyz";

    public static void main(String[] args) throws Exception {
        part1(Loader.lines(23).toList());
        part2(Loader.lines(23).toList());
    }

    public static void part1(List<String> lines) {
        record Edge(String left, String right) {
            public String otherNode(String node) {
                return node.equals(left) ? right : left;
            }

            @Override
            public boolean equals(Object obj) {
                return obj == this || obj instanceof Edge other &&
                    ((left.equals(other.right) && right.equals(other.left)) ||
                        (left.equals(other.left) && right.equals(other.right)));
            }

            @Override
            public int hashCode() {
                String[] params = { left, right };
                Arrays.sort(params);
                return Arrays.hashCode(params);
            }

            @Override
            public String toString() {
                return left + "-" + right;
            }
        }
        DefaultedMap<String, Set<Edge>> edgesByComputer = DefaultedMap.hash().withEmptySet();

        for (String line : lines) {
            var parts = line.split("-");
            var l = parts[0];
            var r = parts[1];
            var edge = new Edge(l, r);
            edgesByComputer.getOrInit(l).add(edge);
            edgesByComputer.getOrInit(r).add(edge);
        }

        Set<Set<String>> groups = new HashSet<>();
        for (String computerA : edgesByComputer.keySet()) {
            if (!computerA.startsWith("t")) continue;

            var edgesA = edgesByComputer.get(computerA);
            var edgeListA = List.copyOf(edgesA);

            for (int i = 0; i < edgeListA.size() - 1; i++) {
                var edgeAb = edgeListA.get(i);
                var computerB = edgeAb.otherNode(computerA);

                for (int j = i + 1; j < edgeListA.size(); j++) {
                    var edgeAc = edgeListA.get(j);
                    var computerC = edgeAc.otherNode(computerA);
                    var edgeBc = new Edge(computerB, computerC);
                    var edgesB = edgesByComputer.get(computerB);

                    if (edgesB.contains(edgeBc)) {
                        groups.add(Set.of(computerA, computerB, computerC));
                    }
                }
            }
        }
        System.out.println(groups.size());
    }

    public static void part2(List<String> lines) {
        // sketch of algorithm for future ref.

        // 1. same init as part 1 (compute edges)
        // 2. starting from EACH key (not just the t keys), find all computers BCDEFGHIJ... connected to it
        // 3. get all pairs (edges) and see what combos exist
        // 4. assign that count as the max count of computer A
        // 5. take the max of max counts
    }
}
