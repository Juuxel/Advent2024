package juuxel.advent2024;

import juuxel.advent.Loader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public final class Day9 {
    public static void main(String[] args) throws Exception {
        part1(Loader.lines(2024, 9));
        part2(Loader.lines(2024, 9));
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
        int totalFiles = 0;
        List<Node> nodes = new ArrayList<>(line.length());

        for (int i = 0; i < line.length(); i++) {
            int length = line.charAt(i) - '0';
            var node = i % 2 == 0 ? Node.file(length, totalFiles++) : Node.gap(length);
            nodes.add(node);
        }

        int filePos = nodes.size();
        for (int fileId = totalFiles - 1; fileId >= 0; fileId--) {
            // Backtrack to the matching file node.
            while (nodes.get(--filePos).fileId() != fileId);
            Node file = nodes.get(filePos);

            for (int i = 0; i < filePos; i++) {
                Node other = nodes.get(i);

                if (other.isGap() && other.length() >= file.length()) {
                    nodes.remove(filePos);
                    nodes.add(filePos, Node.gap(file.length()));
                    nodes.add(i, file);
                    other.resize(other.length() - file.length());
                    break;
                }
            }
        }

        long part2 = 0;
        int position = 0;
        for (Node node : nodes) {
            if (!node.isGap()) {
                for (int i = 0; i < node.length(); i++) {
                    part2 += (long) (i + position) * (long) node.fileId();
                }
            }

            position += node.length();
        }
        System.out.println(part2);
    }

    private static final class Node {
        private int length;
        private final int fileId;
        private final boolean gap;

        private Node(int length, int fileId, boolean gap) {
            this.length = length;
            this.fileId = fileId;
            this.gap = gap;
        }

        static Node file(int length, int fileId) {
            return new Node(length, fileId, false);
        }

        static Node gap(int length) {
            return new Node(length, -1, true);
        }

        public int fileId() {
            return fileId;
        }

        public boolean isGap() {
            return gap;
        }

        public int length() {
            return length;
        }

        public void resize(int length) {
            this.length = length;
        }
    }
}
