package juuxel.advent2024;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Stream;

public final class Day10 {
    public static void main(String[] args) throws Exception {
        run(Loader.lines(10));
    }

    public static void run(Stream<String> lines) {
        var grid = new CharGrid(lines);

        record Movement(int x, int y, int fromHeight, int startX, int startY) {
        }

        Queue<Movement> queue = new ArrayDeque<>();

        for (int x = 0; x < grid.width(); x++) {
            for (int y = 0; y < grid.height(); y++) {
                if (grid.getChar(x, y) == '0') {
                    queue.offer(new Movement(x - 1, y, 0, x, y));
                    queue.offer(new Movement(x + 1, y, 0, x, y));
                    queue.offer(new Movement(x, y - 1, 0, x, y));
                    queue.offer(new Movement(x, y + 1, 0, x, y));
                }
            }
        }

        Set<Movement> trailheads = new HashSet<>(); // deduplicate based on starting position + trailhead pos
        int part2 = 0;
        Movement movement;
        while ((movement = queue.poll()) != null) {
            if (!grid.contains(movement.x, movement.y)) continue;
            int height = grid.getChar(movement.x, movement.y) - '0';
            if (height != movement.fromHeight + 1) continue;

            if (height == 9) {
                trailheads.add(movement);
                part2++;
            } else {
                queue.offer(new Movement(movement.x - 1, movement.y, height, movement.startX, movement.startY));
                queue.offer(new Movement(movement.x + 1, movement.y, height, movement.startX, movement.startY));
                queue.offer(new Movement(movement.x, movement.y - 1, height, movement.startX, movement.startY));
                queue.offer(new Movement(movement.x, movement.y + 1, height, movement.startX, movement.startY));
            }
        }
        System.out.println(trailheads.size());
        System.out.println(part2);
    }
}
