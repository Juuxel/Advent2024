package juuxel.advent2024;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class Day14InputGenerator {
    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.err.println("Usage: java Day14InputGenerator <image> <output file>");
            return;
        }
        Path imagePath = Path.of(args[0]);
        Path outputPath = Path.of(args[1]);
        BufferedImage image = ImageIO.read(imagePath.toFile());
        int width = image.getWidth();
        int height = image.getHeight();
        var random = new Random();

        List<Robot> robots = new ArrayList<>();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int c = image.getRGB(x, y) & 0xFFFFFF;

                if (c != 0x000000) {
                    int vx = random.nextInt(-6, 6);
                    if (vx == 0) vx = 6;
                    int vy = random.nextInt(-6, 6);
                    if (vy == 0) vy = 6;
                    robots.add(new Robot(new Vector(x, y), new Vector(vx, vy)));
                }
            }
        }

        int time = random.nextInt(2 * width * height / 5, 4 * width * height / 5);
        List<String> lines = new ArrayList<>();

        for (Robot robot : robots) {
            var newPos = robot.simulate(width, height, time);
            lines.add("p=%d,%d v=%d,%d".formatted(newPos.x, newPos.y, robot.velocity.x, robot.velocity.y));
        }

        Files.write(outputPath, lines);
    }

    private record Vector(int x, int y) {
    }

    private record Robot(Vector startPos, Vector velocity) {
        Vector simulate(int gridWidth, int gridHeight, int time) {
            return new Vector(
                Mth.mod(startPos.x + time * velocity.x, gridWidth),
                Mth.mod(startPos.y + time * velocity.y, gridHeight)
            );
        }
    }
}
