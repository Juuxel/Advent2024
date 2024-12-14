package juuxel.advent2024;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public final class Day14 {
    private static final int GRID_WIDTH = 101;
    private static final int GRID_HEIGHT = 103;
    private static final int TIME = 100;
    private static final Pattern ROBOT_PATTERN = Pattern.compile("^p=(-?[0-9]+),(-?[0-9]+) v=(-?[0-9]+),(-?[0-9]+)$");

    public static void main(String[] args) throws Exception {
        part1(Loader.lines(14));
        part2(Loader.lines(14), true);
    }

    public static void part1(Stream<String> lines) {
        int[] robotsPerQuadrant = new int[4];
        lines.map(Day14::readRobot)
            .map(robot -> robot.simulate(TIME))
            .mapToInt(Vector::quadrant)
            .filter(q -> q >= 0)
            .forEach(quadrant -> robotsPerQuadrant[quadrant]++);
        System.out.println(robotsPerQuadrant[0] * robotsPerQuadrant[1] * robotsPerQuadrant[2] * robotsPerQuadrant[3]);
    }

    public static void part2(Stream<String> lines, boolean topLevel) {
        var robots = lines.map(Day14::readRobot).toList();
        SwingUtilities.invokeLater(() -> {
            var robotPositions = new Vector[robots.size()];
            for (int i = 0; i < robots.size(); i++) {
                robotPositions[i] = robots.get(i).startPos;
            }

            var frame = new JFrame("Day 14");
            var panel = new JPanel(new BorderLayout());
            var bottomPanel = new JPanel();
            var spinnerModel = new SpinnerNumberModel(0, 0, 10000, 1);
            var colorChooser = new JColorChooser(Color.GREEN);
            var colorButton = new JButton("Change colour");
            var colorDialog = JColorChooser.createDialog(colorButton, "Pick a colour", true, colorChooser, null, null);
            var spinner = new JSpinner(spinnerModel);
            var drawButton = new JButton("Draw 10k images");
            var canvas = new JComponent() {
                @Override
                protected void paintComponent(Graphics g) {
                    g = g.create();
                    g.translate((getWidth() - GRID_WIDTH) / 2, (getHeight() - GRID_HEIGHT) / 2);
                    drawRobots(g, colorChooser.getColor(), robotPositions);
                    g.dispose();
                }
            };
            spinner.addChangeListener(e -> {
                for (int i = 0; i < robots.size(); i++) {
                    robotPositions[i] = robots.get(i).simulate(spinnerModel.getNumber().intValue());
                }

                canvas.repaint();
            });

            drawButton.addActionListener(e -> {
                var progressBar = new JProgressBar();
                var progressDialog = new JDialog(frame, "Rendering...", true);
                var worker = new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        Files.createDirectories(Path.of("day14"));
                        BufferedImage image = new BufferedImage(GRID_WIDTH, GRID_HEIGHT, BufferedImage.TYPE_INT_ARGB);
                        var g = image.createGraphics();

                        for (int i = 0; i < 10_000; i++) {
                            if (Thread.interrupted()) break;
                            setProgress((int) Math.ceil(0.01 * i));
                            spinnerModel.setValue(i);
                            drawRobots(g, colorChooser.getColor(), robotPositions);
                            ImageIO.write(image, "PNG", Path.of("day14/" + i + ".png").toFile());
                        }
                        SwingUtilities.invokeLater(progressDialog::dispose);
                        return null;
                    }
                };
                progressDialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
                progressDialog.setContentPane(progressBar);
                worker.addPropertyChangeListener(evt -> {
                    if ("progress".equals(evt.getPropertyName())) {
                        progressBar.setValue((Integer) evt.getNewValue());
                    }
                });
                progressDialog.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        worker.cancel(true);
                        e.getWindow().dispose();
                    }
                });
                worker.execute();
                progressDialog.pack();
                progressDialog.setVisible(true);
                progressDialog.dispose();
            });

            colorButton.addActionListener(e -> {
                colorDialog.setVisible(true);
                canvas.repaint();
            });
            bottomPanel.add(colorButton);
            bottomPanel.add(spinner);
            bottomPanel.add(drawButton);
            panel.add(canvas, BorderLayout.CENTER);
            panel.add(bottomPanel, BorderLayout.SOUTH);
            frame.setContentPane(panel);
            frame.setDefaultCloseOperation(topLevel ? WindowConstants.EXIT_ON_CLOSE : WindowConstants.DISPOSE_ON_CLOSE);
            frame.setSize(400, 400);
            frame.setVisible(true);
        });
    }

    private static Robot readRobot(String line) {
        var matcher = ROBOT_PATTERN.matcher(line);
        if (matcher.matches()) {
            int px = Integer.parseInt(matcher.group(1));
            int py = Integer.parseInt(matcher.group(2));
            int vx = Integer.parseInt(matcher.group(3));
            int vy = Integer.parseInt(matcher.group(4));
            return new Robot(new Vector(px, py), new Vector(vx, vy));
        }
        throw new IllegalArgumentException("couldn't match robot line: " + line);
    }

    private static void drawRobots(Graphics g, Color c, Vector[] robotPositions) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, GRID_WIDTH, GRID_HEIGHT);
        g.setColor(c);
        for (Vector pos : robotPositions) {
            g.fillRect(pos.x, pos.y, 1, 1);
        }
    }

    private record Vector(int x, int y) {
        int quadrant() {
            int midX = GRID_WIDTH / 2;
            int midY = GRID_HEIGHT / 2;
            if (x == midX || y == midY) return -1;

            // quadrants:
            // 01
            // 32

            if (x < midX) {
                return y < midY ? 0 : 3;
            } else {
                return y < midY ? 1 : 2;
            }
        }
    }

    private record Robot(Vector startPos, Vector velocity) {
        Vector simulate(int time) {
            return new Vector(
                Mth.mod(startPos.x + time * velocity.x, GRID_WIDTH),
                Mth.mod(startPos.y + time * velocity.y, GRID_HEIGHT)
            );
        }
    }
}
