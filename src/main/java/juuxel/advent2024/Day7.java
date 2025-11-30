package juuxel.advent2024;

import juuxel.advent.Loader;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public final class Day7 {
    public static void main(String[] args) throws Exception {
        part1(Loader.lines(2024, 7));
        part2(Loader.lines(2024, 7));
    }

    public static void part1(Stream<String> lines) {
        record Equation(long lhs, List<Long> rhs) {
            boolean isTrue() {
                int length = rhs.size() - 1;
                for (int i = 0; i < (1 << length); i++) {
                    long current = rhs.getFirst();
                    for (int j = 1; j < rhs.size(); j++) {
                        if ((i & (1 << (j - 1))) == 0) {
                            current += rhs.get(j);
                        } else {
                            current *= rhs.get(j);
                        }
                    }

                    if (current == lhs) return true;
                }
                return false;
            }
        }

        long part1 = lines
            .map(line -> {
                var split = line.split(": ");
                long lhs = Long.parseLong(split[0]);

                var subsplit = split[1].split(" ");
                List<Long> rhs = Arrays.stream(subsplit)
                    .map(Long::parseLong)
                    .toList();

                return new Equation(lhs, rhs);
            })
            .filter(Equation::isTrue)
            .mapToLong(Equation::lhs)
            .sum();
        System.out.println(part1);
    }

    public static void part2(Stream<String> lines) {
        record Equation(long lhs, List<Long> rhs) {
            boolean isTrue() {
                int length = rhs.size() - 1;
                enum Op { ADD, MUL, CONCAT }
                List<List<Op>> possibilities = allTuples(List.of(Op.ADD, Op.MUL, Op.CONCAT), length);
                for (List<Op> ops : possibilities) {
                    BigInteger current = BigInteger.valueOf(rhs.getFirst());
                    for (int j = 1; j < rhs.size(); j++) {
                        current = switch (ops.get(j - 1)) {
                            case ADD -> current.add(BigInteger.valueOf(rhs.get(j)));
                            case MUL -> current.multiply(BigInteger.valueOf(rhs.get(j)));
                            case CONCAT -> concat(current, BigInteger.valueOf(rhs.get(j)));
                        };
                    }

                    if (current.longValueExact() == lhs) {
                        return true;
                    }
                }
                return false;
            }
        }

        long part2 = lines
            .map(line -> {
                var split = line.split(": ");
                long lhs = Long.parseLong(split[0]);

                var subsplit = split[1].split(" ");
                List<Long> rhs = Arrays.stream(subsplit)
                    .map(Long::parseLong)
                    .toList();

                return new Equation(lhs, rhs);
            })
            .filter(Equation::isTrue)
            .mapToLong(Equation::lhs)
            .sum();
        System.out.println(part2);
    }

    private static BigInteger concat(BigInteger a, BigInteger b) {
        return new BigInteger(a.toString() + b.toString());
    }

    private static <T> List<List<T>> allTuples(List<T> values, int length) {
        List<List<T>> current = List.of(List.of());

        while (length-- >= 1) {
            List<List<T>> next = new ArrayList<>(current.size() * values.size());
            for (List<T> ts : current) {
                for (T value : values) {
                    List<T> newTs = new ArrayList<>(ts.size() + 1);
                    newTs.addAll(ts);
                    newTs.add(value);
                    next.add(newTs);
                }
            }
            current = next;
        }

        return current;
    }
}
