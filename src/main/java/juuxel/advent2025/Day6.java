package juuxel.advent2025;

import juuxel.advent.CharGrid;
import juuxel.advent.Loader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.LongBinaryOperator;
import java.util.stream.IntStream;

public final class Day6 {
    public static void main(String[] args) throws Exception {
        part1(Loader.lines(2025, 6).toList());
        part2(Loader.lines(2025, 6).toList());
    }

    public static void part1(List<String> lines) {
        Input input = parse(lines);
        long part1 = input.columns.stream()
            .mapToLong(Column::compute)
            .sum();
        System.out.println(part1);
    }

    private static Input parse(List<String> lines) {
        int width = lines.getFirst().trim().split(" +").length;
        int height = lines.size() - 1;
        long[][] grid = new long[width][height];

        for (int i = 0; i < height; i++) {
            String[] line = lines.get(i).trim().split(" +");
            assert line.length == width;

            for (int x = 0; x < line.length; x++) {
                grid[x][i] = Long.parseLong(line[x]);
            }
        }

        List<Op> ops = Arrays.stream(lines.getLast().trim().split(" +"))
            .map(op -> switch (op) {
                case "+" -> Op.ADD;
                case "*" -> Op.MULTIPLY;
                default -> throw new IllegalArgumentException("Unknown op " + op);
            })
            .toList();

        List<Column> columns = IntStream.range(0, width)
            .mapToObj(x -> new Column(grid[x], ops.get(x)))
            .toList();

        return new Input(columns);
    }

    public static void part2(List<String> lines) {
        Input input = parseP2(lines);
        long part2 = input.columns.stream()
            .mapToLong(Column::compute)
            .sum();
        System.out.println(part2);
    }

    private static Input parseP2(List<String> lines) {
        CharGrid inputGrid = CharGrid.createRightPadded(lines.subList(0, lines.size() - 1), ' ');
        List<Integer> opPositions = new ArrayList<>();
        List<Op> ops = new ArrayList<>();

        for (int i = 0; i < lines.getLast().length(); i++) {
            char c = lines.getLast().charAt(i);
            if (c != ' ') {
                opPositions.add(i);
                ops.add(switch (c) {
                    case '+' -> Op.ADD;
                    case '*' -> Op.MULTIPLY;
                    default -> throw new IllegalArgumentException("Unknown op " + c);
                });
            }
        }

        List<Column> columns = new ArrayList<>();

        for (int opIndex = 0; opIndex < opPositions.size(); opIndex++) {
            int end = opIndex == opPositions.size() - 1 ? inputGrid.width() : opPositions.get(opIndex + 1) - 1;
            List<Long> inputs = new ArrayList<>();

            for (int x = opPositions.get(opIndex); x < end; x++) {
                StringBuilder sb = new StringBuilder();
                for (int y = 0; y < inputGrid.height(); y++) {
                    sb.append(inputGrid.getChar(x, y));
                }
                long input = Long.parseLong(sb.toString().trim());
                inputs.add(input);
            }

            columns.add(new Column(inputs.stream().mapToLong(x -> x).toArray(), ops.get(opIndex)));
        }

        return new Input(columns);
    }

    private enum Op {
        ADD,
        MULTIPLY;

        LongBinaryOperator asBinaryOp() {
            return switch (this) {
                case ADD -> Long::sum;
                case MULTIPLY -> (a, b) -> a * b;
            };
        }
    }

    private record Column(long[] inputs, Op op) {
        long compute() {
            return Arrays.stream(inputs).reduce(op.asBinaryOp()).orElseThrow();
        }
    }

    private record Input(List<Column> columns) {
    }
}
