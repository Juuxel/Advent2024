package juuxel.advent2024;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Day3 {
    public static void main(String[] args) throws Exception {
        part1(Loader.lines(3));
        part2(Loader.lines(3));
    }

    public static void part1(Stream<String> lines) {
        partN(lines, false);
    }

    public static void part2(Stream<String> lines) {
        partN(lines, true);
    }

    private static void partN(Stream<String> lines, boolean doDontEnabled) {
        String content = lines.collect(Collectors.joining());
        Parser parser = new Parser(doDontEnabled);

        for (int i = 0; i < content.length(); i++) {
            parser.accept(content.charAt(i));
        }

        System.out.println(parser.results.stream().mapToLong(Mul::value).sum());
    }

    private record Mul(long a, long b) {
        long value() {
            return a * b;
        }
    }

    private static final class Parser {
        private State state = State.READY;
        private final boolean doDontEnabled;
        private boolean mulEnabled = true;

        // Buffers
        private final StringBuilder buffer = new StringBuilder();
        private long num1;

        final List<Mul> results = new ArrayList<>();

        private Parser(boolean doDontEnabled) {
            this.doDontEnabled = doDontEnabled;
        }

        void accept(char c) {
            switch (state) {
                case READY -> {
                    if (mulEnabled && c == 'm') state = State.IN_MUL_U;
                    if (doDontEnabled && c == 'd') state = State.IN_DO_DONT_O;
                }
                case IN_MUL_U -> {
                    state = c == 'u' ? State.IN_MUL_L : State.READY;
                }
                case IN_MUL_L -> {
                    state = c == 'l' ? State.IN_MUL_LEFT_PAREN : State.READY;
                }
                case IN_MUL_LEFT_PAREN -> {
                    state = c == '(' ? State.IN_MUL_NUM_1 : State.READY;
                }
                case IN_MUL_NUM_1 -> {
                    if ("0123456789".indexOf(c) >= 0) {
                        buffer.append(c);
                    } else if (c == ',' && !buffer.isEmpty()) {
                        num1 = Long.parseLong(buffer.toString());
                        buffer.setLength(0);
                        state = State.IN_MUL_NUM_2;
                    } else {
                        buffer.setLength(0);
                        state = State.READY;
                    }
                }
                case IN_MUL_NUM_2 -> {
                    if ("0123456789".indexOf(c) >= 0) {
                        buffer.append(c);
                    } else if (c == ')' && !buffer.isEmpty()) {
                        long num2 = Long.parseLong(buffer.toString());
                        buffer.setLength(0);
                        results.add(new Mul(num1, num2));
                        state = State.READY;
                    } else {
                        buffer.setLength(0);
                        state = State.READY;
                    }
                }
                case IN_DO_DONT_O -> {
                    state = c == 'o' ? State.IN_DO_DONT_LEFT_PAREN_N : State.READY;
                }
                case IN_DO_DONT_LEFT_PAREN_N -> {
                    state = switch (c) {
                        case '(' -> State.IN_DO_RIGHT_PAREN;
                        case 'n' -> State.IN_DONT_APOSTROPHE;
                        default -> State.READY;
                    };
                }
                case IN_DO_RIGHT_PAREN -> {
                    if (c == ')') mulEnabled = true;
                    state = State.READY;
                }
                case IN_DONT_APOSTROPHE -> {
                    state = c == '\'' ? State.IN_DONT_T : State.READY;
                }
                case IN_DONT_T -> {
                    state = c == 't' ? State.IN_DONT_LEFT_PAREN : State.READY;
                }
                case IN_DONT_LEFT_PAREN -> {
                    state = c == '(' ? State.IN_DONT_RIGHT_PAREN : State.READY;
                }
                case IN_DONT_RIGHT_PAREN -> {
                    if (c == ')') mulEnabled = false;
                    state = State.READY;
                }
            }
        }

        private enum State {
            READY,
            IN_MUL_U,
            IN_MUL_L,
            IN_MUL_LEFT_PAREN,
            IN_MUL_NUM_1,
            IN_MUL_NUM_2,
            IN_DO_DONT_O,
            IN_DO_DONT_LEFT_PAREN_N,
            IN_DO_RIGHT_PAREN,
            IN_DONT_APOSTROPHE,
            IN_DONT_T,
            IN_DONT_LEFT_PAREN,
            IN_DONT_RIGHT_PAREN,
        }
    }
}
