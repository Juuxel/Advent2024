package juuxel.advent2024;

import java.util.Arrays;
import java.util.stream.Stream;

public final class Day9 {
    public static void main(String[] args) throws Exception {
        part1(Loader.lines(9));
        part2(Loader.lines(9));
    }

    public static void part1(Stream<String> lines) {
        String line = lines.toList().getFirst();
        int[] nums = new int[line.length()];

        for (int i = 0; i < nums.length; i++) {
            nums[i] = line.charAt(i) - '0';
        }

        int[] tape = new int[Arrays.stream(nums).sum()];
        for (int i = 0, j = 0, fileIndex = 0; i < nums.length; i++) {
            boolean gap = i % 2 != 0;
            int current = nums[i];
            Arrays.fill(tape, j, j + current, gap ? -1 : fileIndex++);
            j += current;
        }

        int gapLocation = 0;
        int movedFrom = tape.length;
        while (!isDone(tape)) {
            while (tape[--movedFrom] < 0);
            int num = tape[movedFrom];
            tape[movedFrom] = -1;
            while (tape[++gapLocation] >= 0);
            tape[gapLocation] = num;
        }

        long part1 = 0;
        for (int j = 0; j < tape.length; j++) {
            if (tape[j] < 0) break;
            part1 += (long) j * (long) tape[j];
        }
        System.out.println(part1);
    }

    private static boolean isDone(int[] tape) {
        boolean foundGap = false;
        for (int i : tape) {
            if (foundGap) {
                if (i >= 0) return false;
            } else {
                if (i < 0) foundGap = true;
            }
        }
        return true;
    }

    private static void printTape(int[] tape) {
        for (int i : tape) {
            if (i < 0) {
                System.out.print('.');
            } else {
                System.out.print(i);
            }
        }
        System.out.println();
    }

    public static void part2(Stream<String> lines) {
        String line = lines.toList().getFirst();
        int[] nums = new int[line.length()];

        for (int i = 0; i < nums.length; i++) {
            nums[i] = line.charAt(i) - '0';
        }

        int totalFiles = (nums.length + 1) / 2;
        int[] fileStarts = new int[totalFiles];
        int[] fileLengths = new int[totalFiles];
        int[] gapStarts = new int[nums.length - totalFiles];
        int[] gapLength = new int[nums.length - totalFiles];

        for (int i = 0, j = 0, fileIndex = 0, gapIndex = 0; i < nums.length; i++) {
            boolean gap = i % 2 != 0;
            int current = nums[i];
            if (gap) {
                gapStarts[gapIndex] = j;
                gapLength[gapIndex++] = current;
            } else {
                fileStarts[fileIndex] = j;
                fileLengths[fileIndex++] = current;
            }
            j += current;
        }

        for (int file = totalFiles - 1; file >= 0; file--) {
            int fileStart = fileStarts[file];
            int length = fileLengths[file];

            for (int gap = 0; gap < gapStarts.length; gap++) {
                if (gapLength[gap] < length) continue;
                if (gapStarts[gap] + gapLength[gap] >= fileStart) break;

                fileStarts[file] = gapStarts[gap];
                gapLength[gap] -= length;
                gapStarts[gap] += length;
                break;
            }
        }

        long part2 = 0;
        for (int file = 0; file < totalFiles; file++) {
            for (int j = 0; j < fileLengths[file]; j++) {
                part2 += (long) file * (long) (j + fileStarts[file]);
            }
        }
        System.out.println(part2);
    }
}
