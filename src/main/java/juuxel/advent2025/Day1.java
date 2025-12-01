package juuxel.advent2025;

import juuxel.advent.Loader;

import java.util.List;

public final class Day1 {
    public static void main(String[] args) throws Exception {
        part12(Loader.lines(2025, 1).toList());
    }

    public static void part12(List<String> lines) {
        int part1 = 0;
        int part2 = 0;
        int dial = 50;

        for (String line : lines) {
            final int amt = Integer.parseInt(line.substring(1));
            boolean left = line.charAt(0) == 'L';

            for (int i = 0; i < amt; i++) {
                if ((left ? --dial : ++dial) % 100 == 0) {
                    part2++;
                }
            }

            dial = Math.floorMod(dial, 100);

            if (dial == 0) {
                part1++;
            }
        }

        System.out.println(part1);
        System.out.println(part2);
    }
}
