package juuxel.advent.gui;

import juuxel.advent2025.*;

import static juuxel.advent.gui.AdventGui.lines;
import static juuxel.advent.gui.AdventGui.linesToList;

public final class Advent2025Solutions {
    public static final AdventGui.Solution[] SOLUTIONS = {
        new AdventGui.Solution("Day 1", 1, linesToList(Day1::part12)),
        new AdventGui.Solution("Day 2, part 1", 2, linesToList(Day2::part1)),
        new AdventGui.Solution("Day 2, part 2", 2, linesToList(Day2::part2)),
        new AdventGui.Solution("Day 3, part 1", 3, lines(Day3::part1)),
        new AdventGui.Solution("Day 3, part 2", 3, lines(Day3::part2)),
        new AdventGui.Solution("Day 4, part 1", 4, lines(Day4::part1)),
        new AdventGui.Solution("Day 4, part 2", 4, lines(Day4::part2)),
        new AdventGui.Solution("Day 5, part 1", 5, linesToList(Day5::part1)),
        new AdventGui.Solution("Day 5, part 2", 5, linesToList(Day5::part2)),
        new AdventGui.Solution("Day 6, part 1", 6, linesToList(Day6::part1)),
        new AdventGui.Solution("Day 6, part 2", 6, linesToList(Day6::part2)),
        new AdventGui.Solution("Day 7, part 1", 7, lines(Day7::part1)),
        new AdventGui.Solution("Day 7, part 2", 7, lines(Day7::part2)),
    };

    public static final AdventGui.Year YEAR = new AdventGui.Year(2025, SOLUTIONS);
}
