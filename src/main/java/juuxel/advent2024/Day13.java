package juuxel.advent2024;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public final class Day13 {
    private static final Pattern BUTTON_PATTERN = Pattern.compile("^Button .: X\\+([0-9]+), Y\\+([0-9]+)+$");
    private static final Pattern PRIZE_PATTERN = Pattern.compile("^Prize: X=([0-9]+), Y=([0-9]+)$");
    private static final long OFFSET = 10_000_000_000_000L;

    public static void main(String[] args) throws Exception {
        run(Loader.lines(13));
    }

    public static void run(Stream<String> lines) {
        var machines = Iterables.split(lines::iterator, "")
            .stream()
            .map(Day13::readMachine)
            .toList();
        int part1 = machines.stream()
            .map(Day13::solveMachine)
            .flatMapToInt(OptionalInt::stream)
            .sum();
        System.out.println(part1);
        long part2 = machines.stream()
            .map(Day13::solveMachineP2)
            .flatMapToLong(OptionalLong::stream)
            .sum();
        System.out.println(part2);
    }

    private static OptionalInt solveMachine(Machine machine) {
        // The solutions to buttonA.x * a + buttonB.x * b = prizeX
        // and buttonA.y * a + buttonB.y * b = prizeY
        // exist iff gcd(buttonA.x, buttonB.x) | prizeX and
        // gcd(buttonA.y, buttonB.y) | prizeY.

        var solX = solveLinearDiophantineEquation(machine.buttonA.x, machine.buttonB.x, machine.prizeX).orElse(null);
        if (solX == null) return OptionalInt.empty();
        var solY = solveLinearDiophantineEquation(machine.buttonA.y, machine.buttonB.y, machine.prizeY).orElse(null);
        if (solY == null) return OptionalInt.empty();

        int minPrice = Integer.MAX_VALUE;
        {
            int k = 0;
            while (inInterval(solX.a0 + k * solX.da, solX.da, 0, 100) &&
                inInterval(solX.b0 + k * solX.db, solX.db, 0, 100)) {
                int a = solX.a0 + k * solX.da;
                int b = solX.b0 + k * solX.db;
                if (machine.buttonA.y * a + machine.buttonB.y * b == machine.prizeY) {
                    int price = 3 * a + b;
                    minPrice = Math.min(minPrice, price);
                }
                k++;
            }
        }
        {
            int k = 0;
            while (inInterval(solX.a0 + k * solX.da, -solX.da, 0, 100) &&
                inInterval(solX.b0 + k * solX.db, -solX.db, 0, 100)) {
                int a = solX.a0 + k * solX.da;
                int b = solX.b0 + k * solX.db;
                if (machine.buttonA.y * a + machine.buttonB.y * b == machine.prizeY) {
                    int price = 3 * a + b;
                    minPrice = Math.min(minPrice, price);
                }
                k--;
            }
        }
        if (minPrice == Integer.MAX_VALUE) return OptionalInt.empty();
        return OptionalInt.of(minPrice);
    }

    private static OptionalLong solveMachineP2(Machine machine) {
        var sol = solveLinearDiophantineEquationP2(machine.buttonA.x, machine.buttonB.x, machine.prizeX + OFFSET).orElse(null);
        if (sol == null) return OptionalLong.empty();

        long denom = (long) machine.buttonA.y * sol.da + (long) machine.buttonB.y * sol.db;
        long numer = machine.prizeY + OFFSET - machine.buttonA.y * sol.a0 - machine.buttonB.y * sol.b0;
        if (denom == 0 || numer % denom != 0) return OptionalLong.empty();
        long k = numer / denom;
        long a = sol.a0 + k * sol.da;
        long b = sol.b0 + k * sol.db;
        long price = 3 * a + b;
        return OptionalLong.of(price);
    }

    private static boolean inInterval(int x, int dx, int a, int b) {
        return dx < 0 ? a <= x : x <= b;
    }

    // Solves na + mb = c.
    private static Optional<Solution> solveLinearDiophantineEquation(int n, int m, int c) {
        int gcd = Mth.gcd(n, m);
        // no solution iff gcd doesn't divide c
        if (c % gcd != 0) return Optional.empty();

        int da = m / gcd;
        int db = -n / gcd;

        for (int a = 0; a <= 100; a++) {
            // we write mb = c - na
            int rhs = c - n * a;
            if (rhs % m == 0) {
                int b = rhs / m;
                return Optional.of(new Solution(a, b, da, db));
            }
        }

        throw new RuntimeException("couldn't solve %da + %db = %d".formatted(n, m, c));
    }

    private record Solution(int a0, int b0, int da, int db) {
    }

    // Solves na + mb = c.
    private static Optional<SolutionP2> solveLinearDiophantineEquationP2(int n, int m, long c) {
        int gcd = Mth.gcd(n, m);
        // no solution iff gcd doesn't divide c
        if (c % gcd != 0) return Optional.empty();

        int da = m / gcd;
        int db = -n / gcd;

        for (long a = 0;; a++) {
            // we write mb = c - na
            long rhs = c - n * a;
            if (rhs % m == 0) {
                long b = rhs / m;
                return Optional.of(new SolutionP2(a, b, da, db));
            }
        }
    }

    private record SolutionP2(long a0, long b0, int da, int db) {
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
