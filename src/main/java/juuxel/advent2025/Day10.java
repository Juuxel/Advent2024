package juuxel.advent2025;

import juuxel.advent.Loader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public final class Day10 {
    private static final Pattern LINE_PATTERN = Pattern.compile("^\\[(.+)] (.+) \\{(.+)}$");

    public static void main(String[] args) throws Exception {
        part1(Loader.lines(2025, 10));
        part2(Loader.lines(2025, 10));
    }

    public static void part1(Stream<String> lines) {
        List<Input> inputs = parse(lines);

        int part1 = inputs.stream()
            .mapToInt(Day10::solvePart1)
            .sum();

        System.out.println(part1);
    }

    private static int solvePart1(Input input) {
        int n = 1;

        while (true) {
            VisitResult<Integer> result = visitCombinations(input.buttons, 0, n, List.of(), combination -> {
                int initialState = 0;

                for (Button button : combination) {
                    initialState ^= button.mask;
                }

                if ((initialState & input.lightMask) == input.targetState) {
                    return new VisitResult.Success<>(combination.size());
                }

                return new VisitResult.Pass<>();
            });

            if (result instanceof VisitResult.Success<Integer>) {
                return n;
            }

            n++;
        }
    }

    private static List<Input> parse(Stream<String> lines) {
        return lines.map(line -> {
            Matcher matcher = LINE_PATTERN.matcher(line);
            if (!matcher.matches()) throw new IllegalArgumentException("Invalid input line: " + line);

            int lightCount = matcher.group(1).length();
            int lightMask = (1 << lightCount) - 1;
            int targetState = 0;
            for (int i = 0; i < lightCount; i++) {
                if (matcher.group(1).charAt(i) == '#') {
                    targetState |= (1 << i);
                }
            }

            List<Button> buttons = parseButtons(matcher.group(2));
            int[] joltageRequirements = Arrays.stream(matcher.group(3).split(",")).mapToInt(Integer::parseInt).toArray();

            return new Input(targetState, lightCount, lightMask, buttons, joltageRequirements);
        }).toList();
    }

    private static List<Button> parseButtons(String line) {
        int startIndex;
        int endIndex = 0;
        int buttonIndex = 0;
        List<Button> buttons = new ArrayList<>();

        while (true) {
            startIndex = line.indexOf('(', endIndex);
            endIndex = line.indexOf(')', startIndex);

            if (startIndex == -1) break;

            String[] buttonContents = line.substring(startIndex + 1, endIndex).split(",");
            List<Integer> lights = new ArrayList<>();
            int mask = 0;
            for (String content : buttonContents) {
                int light = Integer.parseInt(content);
                lights.add(light);
                mask |= (1 << light);
            }
            buttons.add(new Button(buttonIndex++, mask, lights));
        }

        return buttons;
    }

    private static <T, R> VisitResult<R> visitCombinations(List<T> pool, int startIndex, int remainingDepth, List<T> existing, CombinationVisitor<T, R> visitor) {
        if (remainingDepth == 0) {
            return visitor.visit(existing);
        }

        List<T> next = new ArrayList<>(existing.size() + 1);
        next.addAll(existing);

        for (int i = startIndex; i <= pool.size() - remainingDepth; i++) {
            next.add(pool.get(i));

            VisitResult<R> result = visitCombinations(pool, i + 1, remainingDepth - 1, next, visitor);

            if (!(result instanceof VisitResult.Pass<R>)) {
                return result;
            }

            next.removeLast();
        }

        return new VisitResult.Pass<>();
    }

    // OOMs with the puzzle input, also extremely slow.
    private static <R> VisitResult<R> visitCombinationsWithReplacement(List<Button> pool, int remainingDepth, List<Button> existing, AlreadyVisitedTree alreadyExamined, CombinationVisitor<Button, R> visitor) {
        if (remainingDepth == 0) {
            return visitor.visit(existing);
        }

        List<Button> next = new ArrayList<>(existing.size() + 1);
        next.addAll(existing);
        int[] buttonIndices = new int[existing.size() + 1];

        for (int i = 0; i < pool.size(); i++) {
            next.add(pool.get(i));

            for (int j = 0; j < next.size(); j++) {
                buttonIndices[j] = next.get(j).index;
            }
            Arrays.sort(buttonIndices);

            if (alreadyExamined.add(buttonIndices)) {
                VisitResult<R> result = visitCombinationsWithReplacement(pool, remainingDepth - 1, next, alreadyExamined, visitor);

                if (!(result instanceof VisitResult.Pass<R>)) {
                    return result;
                }
            }

            next.removeLast();
        }

        return new VisitResult.Pass<>();
    }

    // EXTREMELY slow.
    private static <T, R> VisitResult<R> visitPermutations(List<T> pool, int remainingDepth, List<T> existing, CombinationVisitor<T, R> visitor) {
        if (remainingDepth == 0) {
            return visitor.visit(existing);
        }

        List<T> next = new ArrayList<>(existing.size() + 1);
        next.addAll(existing);

        for (int i = 0; i < pool.size(); i++) {
            next.add(pool.get(i));

            VisitResult<R> result = visitPermutations(pool, remainingDepth - 1, next, visitor);

            if (!(result instanceof VisitResult.Pass<R>)) {
                return result;
            }

            next.removeLast();
        }

        return new VisitResult.Pass<>();
    }

    public static void part2(Stream<String> lines) {
        List<Input> inputs = parse(lines);

        int part2 = inputs.parallelStream()
            .mapToInt(Day10::solvePart2)
            .sum();

        System.out.println(part2);
    }

    private static int solvePart2(Input input) {
        int n = 1;

        while (true) {
            VisitResult<Integer> result = visitCombinationsWithReplacement(input.buttons, n, List.of(), new AlreadyVisitedTree(-1), permutation -> {
                int[] counters = new int[input.joltageRequirements.length];

                for (Button button : permutation) {
                    for (int counterIndex : button.lights) {
                        counters[counterIndex]++;
                    }
                }

                if (Arrays.equals(counters, input.joltageRequirements)) {
                    return new VisitResult.Success<>(permutation.size());
                }

                return new VisitResult.Pass<>();
            });

            if (result instanceof VisitResult.Success<Integer>) {
                return n;
            }

            n++;
        }
    }

    private record Input(int targetState, int lightCount, int lightMask, List<Button> buttons, int[] joltageRequirements) {
    }

    private record Button(int index, int mask, List<Integer> lights) {
    }

    @FunctionalInterface
    private interface CombinationVisitor<T, R> {
        VisitResult<R> visit(List<T> combination);
    }

    private sealed interface VisitResult<T> {
        record Success<T>(T value) implements VisitResult<T> {
        }

        record Pass<T>() implements VisitResult<T> {
        }
    }

    private static final class AlreadyVisitedTree {
        private final int value;
        private final List<AlreadyVisitedTree> children = new ArrayList<>();

        AlreadyVisitedTree(int value) {
            this.value = value;
        }

        boolean add(int[] path) {
            return add(path, 0);
        }

        boolean add(int[] path, int fromIndex) {
            int valueAtHead = path[fromIndex];
            AlreadyVisitedTree matching = null;
            boolean addNewNode = true;

            for (int i = 0; i < children.size(); i++) {
                AlreadyVisitedTree child = children.get(i);
                if (child.value == valueAtHead) {
                    matching = child;
                    addNewNode = false;
                    break;
                }
            }

            if (addNewNode) {
                matching = new AlreadyVisitedTree(valueAtHead);
                children.add(matching);
            }

            if (fromIndex + 1 < path.length) {
                boolean childValue = matching.add(path, fromIndex + 1);

                if (!addNewNode) {
                    return childValue;
                }
            }

            return addNewNode;
        }
    }
}
