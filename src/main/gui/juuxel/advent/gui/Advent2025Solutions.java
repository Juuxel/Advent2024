package juuxel.advent.gui;

import juuxel.advent2025.*;

import static juuxel.advent.gui.AdventGui.lines;
import static juuxel.advent.gui.AdventGui.linesToList;

public final class Advent2025Solutions {
    public static final AdventGui.Solution[] SOLUTIONS = {
        new AdventGui.Solution("Day 1", 1, linesToList(Day1::part12)),
        new AdventGui.Solution("Day 2, part 1", 2, linesToList(Day2::part1)),
        new AdventGui.Solution("Day 2, part 2", 2, linesToList(Day2::part2)),
    };

    public static final AdventGui.Year YEAR = new AdventGui.Year(2025, SOLUTIONS);
}
