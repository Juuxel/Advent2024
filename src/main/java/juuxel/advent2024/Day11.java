package juuxel.advent2024;

import juuxel.advent.Loader;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public final class Day11 {
    private static final BigInteger BIGINT_2024 = BigInteger.valueOf(2024);

    public static void main(String[] args) throws Exception {
        run(Loader.lines(2024, 11));
    }

    public static void run(Stream<String> lines) {
        var line = lines.toList().getFirst();
        var nums = Arrays.stream(line.split(" ")).map(BigInteger::new).toList();
        Map<CacheKey, Long> cache = new HashMap<>();

        long part1 = nums.stream().mapToLong(num -> blink(cache, num, 25)).sum();
        long part2 = nums.stream().mapToLong(num -> blink(cache, num, 75)).sum();
        System.out.println(part1);
        System.out.println(part2);
    }

    private static long blink(Map<CacheKey, Long> cache, BigInteger num, int depth) {
        var key = new CacheKey(depth, num);
        var cached = cache.get(key);
        if (cached != null) return cached;

        long result;
        if (depth <= 0) {
            result = 1;
        } else if (num.equals(BigInteger.ZERO)) {
            result = blink(cache, BigInteger.ONE, depth - 1);
        } else {
            int numOfDigits = numberOfDigits(num);
            if (numOfDigits % 2 == 0) {
                var divRem = num.divideAndRemainder(BigInteger.TEN.pow(numOfDigits / 2));
                result = blink(cache, divRem[0], depth - 1) + blink(cache, divRem[1], depth - 1);
            } else {
                result = blink(cache, num.multiply(BIGINT_2024), depth - 1);
            }
        }
        cache.put(key, result);
        return result;
    }

    private static int numberOfDigits(BigInteger num) {
        return 1 + (int) Math.log10(num.doubleValue());
    }

    private record CacheKey(int depth, BigInteger num) {
    }
}
