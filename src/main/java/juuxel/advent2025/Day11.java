package juuxel.advent2025;

import juuxel.advent.Loader;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Stream;

public final class Day11 {
    public static void main(String[] args) throws Exception {
        part1(Loader.lines(2025, 11));
        part2(Loader.lines(2025, 11));
    }

    public static void part1(Stream<String> lines) {
        var graph = parse(lines);

        long paths = 0;
        Queue<List<String>> queue = new ArrayDeque<>();
        queue.add(List.of("you"));

        while (!queue.isEmpty()) {
            List<String> pathThusFar = queue.remove();
            String mostRecentNode = pathThusFar.getLast();

            if (mostRecentNode.equals("out")) {
                paths++;
                continue;
            }

            for (String next : graph.get(mostRecentNode)) {
                if (pathThusFar.contains(next)) {
                    // I think this is what they mean by moving backwards
                    continue;
                }

                List<String> nextPath = new ArrayList<>(pathThusFar.size() + 1);
                nextPath.addAll(pathThusFar);
                nextPath.add(next);
                queue.add(nextPath);
            }
        }

        System.out.println(paths);
    }

    private static Map<String, List<String>> parse(Stream<String> lines) {
        Map<String, List<String>> out = new HashMap<>();

        lines.forEach(line -> {
            int colonIndex = line.indexOf(':');
            String head = line.substring(0, colonIndex);
            String[] tail = line.substring(colonIndex + 2).split(" ");
            out.put(head, List.of(tail));
        });

        return out;
    }

    // The puzzle input is designed in a very peculiar way.
    // To cause a combinatorial explosion and to invalidate the approach used in part 1,
    // it has layers with a bunch of "Rome nodes" (nodes with a lot of connected edges).
    // These nodes are organised into layers ("empires"), between which the graph is relatively straightforward.
    // My approach is as follows:
    //  - locate these Rome nodes and their corresponding Roman empires
    //  - find where dac and fft are located within these empires (note: they're assumed to be in different empires)
    //  - count the paths *within a single empire* and multiply in the paths counted from preceding empires
    //  - see how many paths lead to out
    public static void part2(Stream<String> lines) {
        var graph = parse(lines);
        List<List<String>> empires = findRomanEmpires(graph);
        empires.add(List.of("out"));
        List<String> dacEmpire = null;
        List<String> fftEmpire = null;

        for (int i = 0; i < empires.size() - 1; i++) {
            List<String> currentEmpire = empires.get(i);
            Set<String> exclude = Set.copyOf(empires.get(i + 1));

            if (dacEmpire == null && hasPathBetween(graph, currentEmpire, List.of("dac"), exclude)) {
                dacEmpire = currentEmpire;
            }

            if (fftEmpire == null && hasPathBetween(graph, currentEmpire, List.of("fft"), exclude)) {
                fftEmpire = currentEmpire;
            }
        }

        if (dacEmpire == fftEmpire) throw new IllegalStateException("??");

        empires.add(empires.indexOf(dacEmpire) + 1, List.of("dac"));
        empires.add(empires.indexOf(fftEmpire) + 1, List.of("fft"));
        empires.addFirst(List.of("svr"));

        Map<String, Long> pathsTo = new HashMap<>();
        pathsTo.put("svr", 1L);

        for (int i = 0; i < empires.size() - 1; i++) {
            Set<String> excludeHeader = i < empires.size() - 2 ? Set.copyOf(empires.get(i + 2)) : Set.of();

            for (String targetNode : empires.get(i + 1)) {
                long pathsToTarget = 0;

                for (String sourceNode : empires.get(i)) {
                    Set<String> fullExclude = new HashSet<>(excludeHeader);
                    fullExclude.addAll(empires.get(i + 1));
                    fullExclude.remove(targetNode);

                    pathsToTarget += pathsTo.get(sourceNode) * countPathsBetween(graph, List.of(sourceNode), List.of(targetNode), fullExclude);
                }

                pathsTo.put(targetNode, pathsToTarget);
            }
        }

        System.out.println(pathsTo.get("out"));
    }

    // All roads lead to Rome.
    private static List<List<String>> findRomanEmpires(Map<String, List<String>> graph) {
        Set<String> romeNodes = new HashSet<>();

        // svr only leads to Rome nodes
        romeNodes.addAll(graph.get("svr"));

        // then find and count incoming edges
        Map<String, List<String>> incoming = new HashMap<>();
        graph.forEach((from, to) -> {
            for (String node : to) {
                if (incoming.merge(node, List.of(from), (a, b) -> {
                    List<String> next = new ArrayList<>(a.size() + b.size());
                    next.addAll(a);
                    next.addAll(b);
                    return next;
                }).size() >= 5 && !"out".equals(node)) {
                    romeNodes.add(node);
                }
            }
        });

        List<String> romeAsList = List.copyOf(romeNodes);
        Map<String, Set<String>> empireByNode = new HashMap<>();

        for (String node : romeAsList) {
            Set<String> empire = new HashSet<>();
            empire.add(node);
            empireByNode.put(node, empire);
        }

        for (int i = 0; i < romeAsList.size() - 1; i++) {
            String nodeA = romeAsList.get(i);
            List<String> outgoingA = graph.get(nodeA);
            List<String> incomingA = incoming.get(nodeA);
            Set<String> empire = empireByNode.get(nodeA);

            for (int j = i + 1; j < romeAsList.size(); j++) {
                String nodeB = romeAsList.get(j);
                List<String> outgoingB = graph.get(nodeB);
                List<String> incomingB = incoming.get(nodeB);

                if (overlaps(outgoingA, outgoingB) || overlaps(incomingA, incomingB)) {
                    Set<String> empireB = empireByNode.get(nodeB);
                    empire.addAll(empireB);

                    for (String node : empireB) {
                        empireByNode.put(node, empire);
                    }
                }
            }
        }

        empireByNode.values().removeIf(empire -> empire.size() <= 2);

        Set<String> finalRomeNodes = empireByNode.keySet();

        List<List<String>> empires = new ArrayList<>();

        for (Set<String> empire : Set.copyOf(empireByNode.values())) {
            empires.add(List.copyOf(empire));
        }

        Map<Integer, Integer> sortings = new HashMap<>();

        for (int i = 0; i < empires.size() - 1; i++) {
            List<String> empireA = empires.get(i);

            for (int j = i + 1; j < empires.size(); j++) {
                List<String> empireB = empires.get(j);
                Set<String> exclude = new HashSet<>(finalRomeNodes);
                exclude.removeAll(empireA);
                exclude.removeAll(empireB);

                if (hasPathBetween(graph, empireA, empireB, exclude)) {
                    sortings.put(i, j);
                } else if (hasPathBetween(graph, empireB, empireA, exclude)) {
                    sortings.put(j, i);
                }
            }
        }

        int[] order = new int[empires.size()];

        List<Integer> startOptions = new ArrayList<>(sortings.keySet());
        sortings.forEach((from, to) -> startOptions.remove(to));
        if (startOptions.size() > 1) throw new IllegalStateException("??");

        int index = 0;
        Integer current = startOptions.getFirst();
        do {
            order[current] = index++;
        } while ((current = sortings.get(current)) != null);

        List<List<String>> sortedEmpires = new ArrayList<>(empires);
        sortedEmpires.sort(Comparator.comparingInt(empire -> order[empires.indexOf(empire)]));

        return sortedEmpires;
    }

    private static <E> boolean overlaps(Collection<? extends E> as, Collection<? extends E> bs) {
        for (E a : as) {
            for (E b : bs) {
                if (Objects.equals(a, b)) {
                    return true;
                }
            }
        }

        return false;
    }

    private static long countPathsBetween(Map<String, List<String>> graph, List<String> starts, List<String> ends, Set<String> disallowed) {
        long paths = 0;
        Queue<List<String>> queue = new ArrayDeque<>();
        for (String start : starts) {
            queue.add(List.of(start));
        }

        while (!queue.isEmpty()) {
            List<String> pathThusFar = queue.remove();
            String mostRecentNode = pathThusFar.getLast();

            if (ends.contains(mostRecentNode)) {
                paths++;
                continue;
            }

            for (String next : graph.getOrDefault(mostRecentNode, List.of())) {
                if (pathThusFar.contains(next) || disallowed.contains(next)) {
                    // I think this is what they mean by moving backwards
                    continue;
                }

                List<String> nextPath = new ArrayList<>(pathThusFar.size() + 1);
                nextPath.addAll(pathThusFar);
                nextPath.add(next);
                queue.add(nextPath);
            }
        }

        return paths;
    }

    private static boolean hasPathBetween(Map<String, List<String>> graph, List<String> starts, List<String> ends, Set<String> disallowed) {
        List<List<String>> queue = new ArrayList<>();
        for (String start : starts) {
            queue.add(List.of(start));
        }

        while (!queue.isEmpty()) {
            List<String> pathThusFar = queue.removeFirst();
            String mostRecentNode = pathThusFar.getLast();

            if (ends.contains(mostRecentNode)) {
                return true;
            }

            for (String next : graph.getOrDefault(mostRecentNode, List.of())) {
                if (pathThusFar.contains(next) || disallowed.contains(next)) {
                    // I think this is what they mean by moving backwards
                    continue;
                }

                List<String> nextPath = new ArrayList<>(pathThusFar.size() + 1);
                nextPath.addAll(pathThusFar);
                nextPath.add(next);
                queue.addFirst(nextPath);
            }
        }

        return false;
    }
}
