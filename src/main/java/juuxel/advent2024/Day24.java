package juuxel.advent2024;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public final class Day24 {
    public static void main(String[] args) throws Exception {
        part1(Loader.lines(24).toList());
        part2(Loader.lines(24).toList());
    }

    public static void part1(List<String> lines) {
        Map<String, Long> values = new HashMap<>();
        Map<String, Recipe> recipes = new HashMap<>();

        lines.forEach(line -> {
            if (line.contains(":")) {
                var parts = line.split(": ");
                values.put(parts[0], Long.parseLong(parts[1]));
            } else if (line.contains("->")) {
                var parts = line.split(" ");
                recipes.put(parts[4], new Recipe(parts[0], parts[2], Operation.valueOf(parts[1])));
            }
        });

        long z = 0;
        for (String name : recipes.keySet()) {
            if (name.startsWith("z")) {
                long value = compute(values, recipes, name);
                z |= value << Long.parseLong(name.substring(1));
            }
        }
        System.out.println(z);
    }

    public static void part2(List<String> lines) {
    }

    private static long compute(Map<String, Long> values, Map<String, Recipe> recipes, String name) {
        Long existing = values.get(name);
        if (existing != null) return existing;

        var recipe = recipes.get(name);
        long left = compute(values, recipes, recipe.left);
        long right = compute(values, recipes, recipe.right);
        return recipe.op.compute(left, right);
    }

    private record Recipe(String left, String right, Operation op) {
    }

    private enum Operation {
        AND, OR, XOR;

        long compute(long a, long b) {
            return switch (this) {
                case AND -> a & b;
                case OR -> a | b;
                case XOR -> a ^ b;
            };
        }
    }
}
