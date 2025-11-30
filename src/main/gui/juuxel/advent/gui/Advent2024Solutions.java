package juuxel.advent.gui;

import juuxel.advent2024.*;

import static juuxel.advent.gui.AdventGui.lines;
import static juuxel.advent.gui.AdventGui.linesToList;

public final class Advent2024Solutions {
    public static final AdventGui.Solution[] SOLUTIONS = {
        new AdventGui.Solution("Day 1, part 1", 1, lines(Day1::part1)),
        new AdventGui.Solution("Day 1, part 2", 1, lines(Day1::part2)),
        new AdventGui.Solution("Day 2, part 1", 2, lines(Day2::part1)),
        new AdventGui.Solution("Day 2, part 2", 2, lines(Day2::part2)),
        new AdventGui.Solution("Day 3, part 1", 3, lines(Day3::part1)),
        new AdventGui.Solution("Day 3, part 2", 3, lines(Day3::part2)),
        new AdventGui.Solution("Day 4, part 1", 4, lines(Day4::part1)),
        new AdventGui.Solution("Day 4, part 2", 4, lines(Day4::part2)),
        new AdventGui.Solution("Day 5, part 1", 5, lines(Day5::part1)),
        new AdventGui.Solution("Day 5, part 2", 5, lines(Day5::part2)),
        new AdventGui.Solution("Day 6", 6, lines(Day6::run)),
        new AdventGui.Solution("Day 7, part 1", 7, lines(Day7::part1)),
        new AdventGui.Solution("Day 7, part 2", 7, lines(Day7::part2)),
        new AdventGui.Solution("Day 8, part 1", 8, lines(Day8::part1)),
        new AdventGui.Solution("Day 8, part 2", 8, lines(Day8::part2)),
        new AdventGui.Solution("Day 9, part 1", 9, lines(Day9::part1)),
        new AdventGui.Solution("Day 9, part 2", 9, lines(Day9::part2)),
        new AdventGui.Solution("Day 10", 10, lines(Day10::run)),
        new AdventGui.Solution("Day 11", 11, lines(Day11::run)),
        new AdventGui.Solution("Day 12", 12, lines(Day12::run)),
        new AdventGui.Solution("Day 13", 13, lines(Day13::run)),
        new AdventGui.Solution("Day 14, part 1", 14, lines(Day14::part1)),
        new AdventGui.Solution("Day 14, part 2 (GUI)", 14, lines(lines -> Day14.part2(lines, false))),
        new AdventGui.Solution("Day 14, part 2 (statistics)", 14, lines(Day14Part2Statistical::part2)),
        new AdventGui.Solution("Day 15, part 1", 15, lines(Day15::part1)),
        new AdventGui.Solution("Day 15, part 2", 15, lines(Day15::part2)),
        new AdventGui.Solution("Day 16", 16, lines(Day16::run)),
        new AdventGui.Solution("Day 17, part 1", 17, linesToList(Day17::part1)),
        new AdventGui.Solution("Day 18, part 1", 18, lines(Day18::part1)),
        new AdventGui.Solution("Day 18, part 2", 18, lines(Day18::part2)),
        new AdventGui.Solution("Day 22, part 1", 22, linesToList(Day22::part1)),
        new AdventGui.Solution("Day 22, part 2", 22, linesToList(Day22::part1)),
        new AdventGui.Solution("Day 23, part 1", 23, linesToList(Day23::part1)),
        new AdventGui.Solution("Day 24, part 1", 24, linesToList(Day24::part1)),
        new AdventGui.Solution("Day 25, part 1", 25, linesToList(Day25::part1)),
    };

    public static final AdventGui.Year YEAR = new AdventGui.Year(2024, SOLUTIONS);
}
