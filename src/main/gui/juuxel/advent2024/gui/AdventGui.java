package juuxel.advent2024.gui;

import juuxel.advent2024.*;
import org.cactoos.io.OutputStreamTo;
import org.cactoos.io.TeeOutputStream;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.IOException;
import java.io.InputStream;
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
        new Solution("Day 1 (part 1, Java)", 1, lines(Day1::part1)),
        new Solution("Day 1 (part 2, Java)", 1, lines(Day1::part2)),
        new Solution("Day 2 (part 1, Java)", 2, lines(Day2::part1)),
        new Solution("Day 2 (part 2, Java)", 2, lines(Day2::part2)),
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
            // Sorry Yegor, but this is not "OO". Cactoos' IO converters are just too convenient, though ;)
            System.setOut(new PrintStream(new TeeOutputStream(System.out, new OutputStreamTo(outputWriter))));
            System.setErr(new PrintStream(new TeeOutputStream(System.err, new OutputStreamTo(outputWriter))));

            panel.add(mainSplit, BorderLayout.CENTER);
            panel.add(run, BorderLayout.SOUTH);

            frame.setSize(640, 480);
            frame.setContentPane(panel);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
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
            } catch (Exception e) {
                System.err.println(">>> '" + solution.name() + "' errored!");
                e.printStackTrace();
                System.err.println();
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
