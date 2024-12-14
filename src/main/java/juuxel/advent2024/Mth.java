package juuxel.advent2024;

public final class Mth {
    /**
     * {@return the greatest long {@code l} that satisfies {@code l < x}}
     * @param x the number to compare
     */
    public static long previousLong(double x) {
        // Round up to the next long, then move backwards by one
        return (long) Math.ceil(x) - 1;
    }

    /**
     * {@return the least long {@code l} that satisfies {@code x < l}}
     * @param x the number to compare
     */
    public static long nextLong(double x) {
        // Remove the fractional part of x, then move up to the next long
        return (long) Math.floor(x) + 1;
    }

    public static int gcd(int a, int b) {
        while (b != 0) {
            int newA = b;
            b = a % b;
            a = newA;
        }

        return Math.abs(a);
    }

    public static long gcd(long a, long b) {
        while (b != 0) {
            long newA = b;
            b = a % b;
            a = newA;
        }

        return Math.abs(a);
    }

    public static long lcm(long a, long b) {
        return a * (b / gcd(a, b));
    }

    public static int mod(int a, int b) {
        int result = a % b;
        if (result < 0) return result + b;
        return result;
    }
}
