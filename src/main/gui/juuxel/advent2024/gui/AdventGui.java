package juuxel.advent2024.gui;

import juuxel.advent2024.*;
import org.apache.commons.io.output.TeeOutputStream;
import org.apache.commons.io.output.WriterOutputStream;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.IntConsumer;
import java.util.stream.Stream;

public final class AdventGui {
    private static final Executor EXECUTOR = Executors.newSingleThreadExecutor(r -> new Thread(r, "Advent"));
    private static final NumberFormat TIME_FORMAT = new DecimalFormat("#######.###");
    private static final int CURRENT_DAY;

    static {
        LocalDate today = LocalDate.now();
        int currentDayUnclamped = today.getMonth() == Month.DECEMBER ? today.getDayOfMonth() : 1;
        CURRENT_DAY = Math.min(currentDayUnclamped, 25);
    }

    private static final Solution[] SOLUTIONS = {
        new Solution("Day 1, part 1", 1, lines(Day1::part1)),
        new Solution("Day 1, part 2", 1, lines(Day1::part2)),
        new Solution("Day 2, part 1", 2, lines(Day2::part1)),
        new Solution("Day 2, part 2", 2, lines(Day2::part2)),
        new Solution("Day 3, part 1", 3, lines(Day3::part1)),
        new Solution("Day 3, part 2", 3, lines(Day3::part2)),
        new Solution("Day 4, part 1", 4, lines(Day4::part1)),
        new Solution("Day 4, part 2", 4, lines(Day4::part2)),
        new Solution("Day 5, part 1", 5, lines(Day5::part1)),
        new Solution("Day 5, part 2", 5, lines(Day5::part2)),
        new Solution("Day 6", 6, lines(Day6::run)),
        new Solution("Day 7, part 1", 7, lines(Day7::part1)),
        new Solution("Day 7, part 2", 7, lines(Day7::part2)),
        new Solution("Day 8, part 1", 8, lines(Day8::part1)),
        new Solution("Day 8, part 2", 8, lines(Day8::part2)),
        new Solution("Day 9, part 1", 9, lines(Day9::part1)),
        new Solution("Day 9, part 2", 9, lines(Day9::part2)),
        new Solution("Day 10", 10, lines(Day10::run)),
        new Solution("Day 11", 11, lines(Day11::run)),
        new Solution("Day 12", 12, lines(Day12::run)),
        new Solution("Day 13", 13, lines(Day13::run)),
        new Solution("Day 14, part 1", 14, lines(Day14::part1)),
        new Solution("Day 14, part 2 (GUI)", 14, lines(lines -> Day14.part2(lines, false))),
        new Solution("Day 14, part 2 (statistics)", 14, lines(Day14Part2Statistical::part2)),
        new Solution("Day 15, part 1", 15, lines(Day15::part1)),
        new Solution("Day 15, part 2", 15, lines(Day15::part2)),
        new Solution("Day 16", 16, lines(Day16::run)),
    };

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            JPanel panel = new JPanel(new BorderLayout());
            JList<Solution> solutions = new JList<>(SOLUTIONS);
            JButton run = new JButton("Run");
            JButton load = new JButton("Load input");
            JButton loadToday = new JButton("Today's input");
            JTextArea input = new JTextArea();
            JTextArea output = new JTextArea();
            JPanel inputArea = new JPanel(new BorderLayout());
            JPanel inputButtons = new JPanel(new GridLayout(1, 0));
            JScrollPane inputScroll = new JScrollPane(input);
            JScrollPane outputScroll = new JScrollPane(output);
            JSplitPane sideSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(solutions), inputArea);
            JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sideSplit, outputScroll);
            JFrame frame = new JFrame("Advent of Code 2024");

            IntConsumer setData = day -> {
                try {
                    InputStream in = Loader.stream(day);

                    if (in == null) {
                        JOptionPane.showMessageDialog(load, "It's not " + day + " December yet!", "Could not find data", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    try (in) {
                        input.setText(new String(in.readAllBytes(), StandardCharsets.UTF_8));
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            };

            solutions.setSelectedIndex(0);
            for (Solution solution : SOLUTIONS) {
                if (solution.day() == CURRENT_DAY) {
                    solutions.setSelectedValue(solution, true);
                    break;
                }
            }

            output.setEditable(false);
            sideSplit.setDividerLocation(0.5);

            inputButtons.add(load);
            inputButtons.add(loadToday);
            inputArea.add(inputScroll, BorderLayout.CENTER);
            inputArea.add(inputButtons, BorderLayout.SOUTH);
            inputArea.setBorder(BorderFactory.createTitledBorder("Input"));
            outputScroll.setBorder(BorderFactory.createTitledBorder("Output"));

            run.addActionListener(e -> {
                EXECUTOR.execute(timed(input.getText(), solutions.getSelectedValue()));
            });

            load.addActionListener(e -> {
                JDialog dialog = new JDialog(frame, "Choose a day", true);
                JPanel dialogPanel = new JPanel(new BorderLayout());
                JLabel currentDayLabel = new JLabel(CURRENT_DAY + " December");
                JSlider slider = new JSlider(1, 25, CURRENT_DAY);
                JButton done = new JButton("Done");
                boolean[] accepted = {false};

                currentDayLabel.setHorizontalAlignment(SwingConstants.CENTER);
                slider.setMajorTickSpacing(7);
                slider.setMinorTickSpacing(1);
                slider.setPaintLabels(true);
                slider.setPaintTicks(true);
                slider.addChangeListener(f -> currentDayLabel.setText(slider.getValue() + " December"));

                dialogPanel.add(currentDayLabel, BorderLayout.NORTH);
                dialogPanel.add(slider, BorderLayout.CENTER);
                dialogPanel.add(done, BorderLayout.SOUTH);

                done.addActionListener(f -> {
                    accepted[0] = true;
                    dialog.dispose();
                });

                dialog.setContentPane(dialogPanel);
                dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                dialog.setSize(200, 130);
                dialog.setVisible(true);

                if (!accepted[0]) return;
                int day = slider.getValue();
                setData.accept(day);
            });

            loadToday.addActionListener(e -> setData.accept(solutions.getSelectedValue().day()));

            Writer outputWriter = new TextAreaWriter(output);
            System.setOut(new PrintStream(attachWriter(System.out, outputWriter)));
            System.setErr(new PrintStream(attachWriter(System.err, outputWriter)));

            panel.add(mainSplit, BorderLayout.CENTER);
            panel.add(run, BorderLayout.SOUTH);

            frame.setSize(640, 480);
            frame.setContentPane(panel);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }

    private static OutputStream attachWriter(OutputStream out, Writer writer) {
        return new TeeOutputStream(out, WriterOutputStream.builder().setWriter(writer).getUnchecked());
    }

    private static Runnable timed(String input, Solution solution) {
        return () -> {
            try {
                long start = System.nanoTime();

                solution.task().run(input);

                long end = System.nanoTime();
                long duration = end - start;
                double durationMs = (double) duration / 1_000_000.0;
                String durationMessage = ">>> Completed '" + solution.name() + "' in " + duration + " ns = " + TIME_FORMAT.format(durationMs) + " ms";

                if (durationMs >= 1000) {
                    double durationS = durationMs / 1000.0;
                    durationMessage += " = " + TIME_FORMAT.format(durationS) + " s";
                }

                System.out.println(durationMessage);
                System.out.println();
                System.out.flush();
            } catch (Exception e) {
                System.err.println(">>> '" + solution.name() + "' errored!");
                e.printStackTrace();
                System.err.println();
                System.err.flush();
            }
        };
    }

    private record Solution(String name, int day, ThrowingMain task) {
        @Override
        public String toString() {
            return name;
        }
    }

    @FunctionalInterface
    private interface ThrowingMain {
        void run(String data) throws Exception;
    }

    @FunctionalInterface
    private interface ThrowingMainWithLineStream {
        void run(Stream<String> lines) throws Exception;
    }

    @FunctionalInterface
    private interface ThrowingMainWithArgs {
        void run(String[] args) throws Exception;
    }

    private static ThrowingMain linesToArgs(ThrowingMainWithArgs main) {
        return data -> main.run(data.lines().toArray(String[]::new));
    }

    private static ThrowingMain lines(ThrowingMainWithLineStream main) {
        return data -> main.run(data.lines());
    }

    private static final class TextAreaWriter extends Writer {
        private final JTextArea area;

        TextAreaWriter(JTextArea area) {
            this.area = area;
        }

        @Override
        public void write(char[] cbuf, int off, int len) {
            if (SwingUtilities.isEventDispatchThread()) {
                synchronized (area) {
                    area.append(new String(Arrays.copyOfRange(cbuf, off, off + len)));
                }
            } else {
                char[] clone = cbuf.clone();
                SwingUtilities.invokeLater(() -> write(clone, off, len));
            }
        }

        @Override
        public void flush() {
        }

        @Override
        public void close() {
        }
    }
}
