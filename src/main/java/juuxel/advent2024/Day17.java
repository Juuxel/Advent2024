package juuxel.advent2024;

import juuxel.advent.Loader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class Day17 {
    private static final int OP_ADV = 0;
    private static final int OP_BXL = 1;
    private static final int OP_BST = 2;
    private static final int OP_JNZ = 3;
    private static final int OP_BXC = 4;
    private static final int OP_OUT = 5;
    private static final int OP_BDV = 6;
    private static final int OP_CDV = 7;

    private final int[] program;
    private long registerA;
    private long registerB;
    private long registerC;
    private int instructionPointer = 0;

    private final List<Integer> output = new ArrayList<>();

    private Day17(int[] program, long registerA, long registerB, long registerC) {
        this.program = program;
        this.registerA = registerA;
        this.registerB = registerB;
        this.registerC = registerC;
    }

    private void run() {
        while (instructionPointer < program.length) {
            int operand = program[instructionPointer + 1];

            switch (program[instructionPointer]) {
                case OP_ADV -> {
                    // division by 2^k == >>> k
                    registerA >>>= computeCombo(operand);
                }
                case OP_BXL -> {
                    registerB ^= operand;
                }
                case OP_BST -> {
                    registerB = computeCombo(operand) & 0b111;
                }
                case OP_JNZ -> {
                    if (registerA != 0) {
                        instructionPointer = operand;
                        continue;
                    }
                }
                case OP_BXC -> {
                    registerB ^= registerC;
                }
                case OP_OUT -> {
                    output.add((int) (computeCombo(operand) & 0b111));
                }
                case OP_BDV -> {
                    // division by 2^k == >>> k
                    registerB = registerA >>> computeCombo(operand);
                }
                case OP_CDV -> {
                    // division by 2^k == >>> k
                    registerC = registerA >>> computeCombo(operand);
                }
            }

            instructionPointer += 2;
        }
    }

    private long computeCombo(int key) {
        return switch (key) {
            case 0, 1, 2, 3 -> key;
            case 4 -> registerA;
            case 5 -> registerB;
            case 6 -> registerC;
            case 7 -> throw new IllegalArgumentException("Computing 'reserved' combo operand 7");
            default -> throw new IllegalArgumentException("Unknown combo operand " + key);
        };
    }

    public static void main(String[] args) throws Exception {
        part1(Loader.lines(2024, 17).toList());
        // part2(Loader.lines(17).toList());
    }

    private static Day17 readInput(List<String> lines) {
        long registerA = Long.parseLong(lines.get(0).substring("Register A: ".length()));
        long registerB = Long.parseLong(lines.get(1).substring("Register B: ".length()));
        long registerC = Long.parseLong(lines.get(2).substring("Register C: ".length()));
        int[] program = Arrays.stream(lines.get(4).substring("Program: ".length()).split(","))
            .mapToInt(Integer::parseInt)
            .toArray();
        return new Day17(program, registerA, registerB, registerC);
    }

    public static void part1(List<String> lines) {
        var executor = readInput(lines);
        executor.run();
        System.out.println(executor.output.stream().map(Object::toString).collect(Collectors.joining(",")));
    }
}
