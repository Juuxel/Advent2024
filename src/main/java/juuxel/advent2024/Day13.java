package juuxel.advent2024;

import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public final class Day13 {
    private static final Pattern BUTTON_PATTERN = Pattern.compile("^Button .: X\\+([0-9]+), Y\\+([0-9]+)+$");
    private static final Pattern PRIZE_PATTERN = Pattern.compile("^Prize: X=([0-9]+), Y=([0-9]+)$");
    private static final long PART_2_OFFSET = 10_000_000_000_000L;

    public static void main(String[] args) throws Exception {
        run(Loader.lines(13));
    }

    public static void run(Stream<String> lines) {
        var machines = Iterables.split(lines::iterator, "")
            .stream()
            .map(Day13::readMachine)
            .toList();
        long part1 = machines.stream()
            .map(machine -> solveMachine(machine, 100, 0))
            .flatMapToLong(OptionalLong::stream)
            .sum();
        System.out.println(part1);
        long part2 = machines.stream()
            .map(machine -> solveMachine(machine, Long.MAX_VALUE, PART_2_OFFSET))
            .flatMapToLong(OptionalLong::stream)
            .sum();
        System.out.println(part2);
    }

    private static OptionalLong solveMachine(Machine machine, long upperBound, long offset) {
        var sol = solveLinearDiophantineEquation(machine.buttonA.x, machine.buttonB.x, machine.prizeX + offset, upperBound).orElse(null);
        if (sol == null) return OptionalLong.empty();

        long denom = (long) machine.buttonA.y * sol.da + (long) machine.buttonB.y * sol.db;
        long numer = machine.prizeY + offset - machine.buttonA.y * sol.a0 - machine.buttonB.y * sol.b0;
        if (denom == 0 || numer % denom != 0) return OptionalLong.empty();
        long k = numer / denom;
        long a = sol.a0 + k * sol.da;
        long b = sol.b0 + k * sol.db;
        long price = 3 * a + b;
        return OptionalLong.of(price);
    }

    // Solves na + mb = c.
    private static Optional<Solution> solveLinearDiophantineEquation(int n, int m, long c, long upperBound) {
        int gcd = Mth.gcd(n, m);
        // no solution iff gcd doesn't divide c
        if (c % gcd != 0) return Optional.empty();

        int da = m / gcd;
        int db = -n / gcd;

        for (long a = 0; a <= upperBound; a++) {
            // we write mb = c - na
            long rhs = c - n * a;
            if (rhs % m == 0) {
                long b = rhs / m;
                return Optional.of(new Solution(a, b, da, db));
            }
        }

        throw new RuntimeException("Could not solve equation %da + %db = %d (a <= %d)".formatted(n, m, c, upperBound));
    }

    private record Solution(long a0, long b0, int da, int db) {
    }

    private static Machine readMachine(List<String> lines) {
        var buttonA = readButton(lines.get(0));
        var buttonB = readButton(lines.get(1));

        var matcher = PRIZE_PATTERN.matcher(lines.get(2));
        if (matcher.matches()) {
            int x = Integer.parseInt(matcher.group(1));
            int y = Integer.parseInt(matcher.group(2));
            return new Machine(buttonA, buttonB, x, y);
        } else {
            throw new IllegalArgumentException("Can't read machine: " + lines);
        }
    }

    private static Button readButton(String line) {
        var matcher = BUTTON_PATTERN.matcher(line);
        if (matcher.matches()) {
            int x = Integer.parseInt(matcher.group(1));
            int y = Integer.parseInt(matcher.group(2));
            return new Button(x, y);
        } else {
            throw new IllegalArgumentException("Can't read button: " + line);
        }
    }

    private record Button(int x, int y) {
    }

    private record Machine(Button buttonA, Button buttonB, int prizeX, int prizeY) {
    }
}
