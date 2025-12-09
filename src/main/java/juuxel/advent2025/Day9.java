package juuxel.advent2025;

import juuxel.advent.Loader;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public final class Day9 {
    private static final boolean DRAW_IMAGE = false;

    public static void main(String[] args) throws Exception {
        part1(Loader.lines(2025, 9));
        part2(Loader.lines(2025, 9));

        if (DRAW_IMAGE) {
            drawImage(Loader.lines(2025, 9));
        }
    }

    public static void part1(Stream<String> lines) {
        List<Pos> positions = parse(lines);
        List<PosPair> pairs = computePairs(positions);
        long part1 = pairs.stream()
            .mapToLong(PosPair::area)
            .max()
            .orElseThrow();
        System.out.println(part1);
    }

    public static void drawImage(Stream<String> lines) throws Exception {
        List<Pos> positions = parse(lines);
        long max = positions.stream()
            .mapToLong(pos -> Math.max(pos.x, pos.y))
            .max()
            .orElseThrow() + 1;
        double scaleFactor = 1000.0 / max;

        BufferedImage image = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 1000, 1000);

        Path2D.Double path = new Path2D.Double();
        path.moveTo(positions.getFirst().x * scaleFactor, positions.getFirst().y * scaleFactor);
        for (Pos pos : positions.reversed()) {
            path.lineTo(pos.x * scaleFactor, pos.y * scaleFactor);
        }

        g.setColor(Color.BLUE);
        g.fill(path);

        g.dispose();

        ImageIO.write(image, "PNG", new File("day9.png"));
    }

    private static List<Pos> parse(Stream<String> lines) {
        return lines.map(line -> {
            var parts = line.split(",", 2);
            return new Pos(Long.parseLong(parts[0]), Long.parseLong(parts[1]));
        }).toList();
    }

    private static List<PosPair> computePairs(List<Pos> positions) {
        List<PosPair> pairs = new ArrayList<>();

        for (int i = 0; i < positions.size() - 1; i++) {
            for (int j = i + 1; j < positions.size(); j++) {
                pairs.add(new PosPair(i, positions.get(i), j, positions.get(j)));
            }
        }

        return pairs;
    }

    public static void part2(Stream<String> lines) {
        List<Pos> positions = parse(lines);
        List<PosPair> pairs = computePairs(positions);
        record Segment(Pos start, Pos end) {
            Segment {
                if (start.x != end.x && start.y != end.y) {
                    throw new IllegalArgumentException("Not axis-aligned: " + start + ", " + end);
                }
            }
        }
        record Rect(long x0, long y0, long x1, long y1) {
            Rect(Pos a, Pos b) {
                this(Math.min(a.x, b.x), Math.min(a.y, b.y), Math.max(a.x, b.x), Math.max(a.y, b.y));
            }

            Rect(PosPair pair) {
                this(pair.a, pair.b);
            }

            boolean intersects(Rect other) {
                return other.x0 <= x1 && x0 <= other.x1 && other.y0 <= y1 && y0 <= other.y1;
            }

            long area() {
                return (x1 - x0 + 1) * (y1 - y0 + 1);
            }
        }
        List<Segment> segments = new ArrayList<>();
        for (int i = 0; i < positions.size() - 1; i++) {
            segments.add(new Segment(positions.get(i), positions.get(i + 1)));
        }
        segments.add(new Segment(positions.getLast(), positions.getFirst()));

        // Valid because the red-green area is simply connected (as in topology):
        // compute the left and right edge of the blob.
        long width = positions.stream().mapToLong(Pos::x).max().orElseThrow() + 1;
        long height = positions.stream().mapToLong(Pos::y).max().orElseThrow() + 1;
        long[] minXPerY = new long[(int) height];
        long[] maxXPerY = new long[(int) height];
        Arrays.fill(minXPerY, Long.MAX_VALUE);
        Arrays.fill(maxXPerY, Long.MIN_VALUE);
        for (Segment segment : segments) {
            if (segment.start.x == segment.end.x) { // vertical
                for (int y = (int) Math.min(segment.start.y, segment.end.y); y <= (int) Math.max(segment.start.y, segment.end.y); y++) {
                    minXPerY[y] = Math.min(segment.start.x, minXPerY[y]);
                    maxXPerY[y] = Math.max(segment.start.x, maxXPerY[y]);
                }
            } else { // horizontal
                minXPerY[(int) segment.start.y] = Math.min(Math.min(segment.start.x, segment.end.x), minXPerY[(int) segment.start.y]);
                maxXPerY[(int) segment.start.y] = Math.max(Math.max(segment.start.x, segment.end.x), maxXPerY[(int) segment.start.y]);
            }
        }

        // Compute the outside area of the blob as rectangles based on the left/right edges computed earlier.
        List<Rect> outerRects = new ArrayList<>();
        for (long y = 0; y < height; y++) {
            long minX = minXPerY[(int) y];
            long maxX = maxXPerY[(int) y];

            if (minX == Long.MAX_VALUE) {
                outerRects.add(new Rect(0, y, width - 1, y));
            } else if (minX == 0 && maxX == width - 1) {
                // full row occupied
            } else {
                if (minX > 0) {
                    outerRects.add(new Rect(0, y, minX - 1, y));
                }

                if (maxX < width - 1) {
                    outerRects.add(new Rect(maxX + 1, y, width - 1, y));
                }
            }
        }

        // Filter rectangles based on whether they intersect any of the "outerRects".
        // Reject the ones that do, then compute the max area.
        long part2 = pairs.parallelStream() // can also be a single-threaded stream, a parallel one is slightly faster
            .map(Rect::new)
            .filter(rect -> {
                for (Rect outerRect : outerRects) {
                    if (outerRect.intersects(rect)) {
                        return false;
                    }
                }
                return true;
            })
            .mapToLong(Rect::area)
            .max()
            .orElseThrow();
        System.out.println(part2);
    }

    private record Pos(long x, long y) {
    }

    private record PosPair(int indexA, Pos a, int indexB, Pos b) {
        long area() {
            return (Math.abs(a.x - b.x) + 1) * (Math.abs(a.y - b.y) + 1);
        }
    }
}
