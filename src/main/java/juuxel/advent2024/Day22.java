package juuxel.advent2024;

import java.util.List;
import java.util.stream.Stream;

public final class Day22 {
    private static final long PRUNE = 0xFFFFFF;

    public static void main(String[] args) throws Exception {
        part1(Loader.lines(22).toList());
        part2(Loader.lines(22).toList());
    }

    public static void part1(List<String> lines) {
        System.out.println(lines.stream()
            .mapToLong(Long::parseLong)
            .map(x -> {
                for (int i = 0; i < 2000; i++) {
                    x = evolve(x);
                }
                return x;
            })
            .sum());
    }

    public static void part2(List<String> lines) {
        record PriceData(long[] prices, long[] changes) {
        }

        var allData = lines.stream()
            .mapToLong(Long::parseLong)
            .mapToObj(x -> {
                long[] prices = new long[2000];
                long[] changes = new long[2000];
                for (int i = 0; i < 2000; i++) {
                    long prevPrice = x % 10;
                    x = evolve(x);
                    long newPrice = x % 10;
                    prices[i] = newPrice;
                    changes[i] = newPrice - prevPrice;
                }
                return new PriceData(prices, changes);
            })
            .toList();

        record Sequence(long a, long b, long c, long d) {
        }

        Stream.Builder<Sequence> builder = Stream.builder();
        for (long a = -9; a <= 9; a++) {
            for (long b = -9; b <= 9; b++) {
                for (long c = -9; c <= 9; c++) {
                    for (long d = -9; d <= 9; d++) {
                        builder.add(new Sequence(a, b, c, d));
                    }
                }
            }
        }
        long maxBananaCount = builder.build()
            .parallel()
            .mapToLong(sequence -> allData.stream()
                .mapToLong(data -> {
                    for (int i = 3; i < 2000; i++) {
                        if (data.changes[i - 3] == sequence.a
                            && data.changes[i - 2] == sequence.b
                            && data.changes[i - 1] == sequence.c
                            && data.changes[i] == sequence.d) {
                            return data.prices[i];
                        }
                    }

                    return 0;
                })
                .sum())
            .max()
            .orElseThrow();
        System.out.println(maxBananaCount);
    }

    private static long evolve(long secret) {
        secret ^= secret << 6;
        secret &= PRUNE;
        secret ^= secret >>> 5;
        secret &= PRUNE;
        secret ^= secret << 11;
        secret &= PRUNE;
        return secret;
    }
}
