package juuxel.advent2024;

import java.util.List;
import java.util.stream.Stream;

public final class CharGrid implements Grid<Character> {
    private final List<String> lines;
    private final int width;
    private final int height;

    public CharGrid(List<String> lines) {
        this.lines = lines;
        this.width = lines.getFirst().length();
        this.height = lines.size();
    }

    public CharGrid(Stream<String> lines) {
        this(lines.toList());
    }

    @Override
    public int width() {
        return width;
    }

    @Override
    public int height() {
        return height;
    }

    @Override
    public Character get(int x, int y) {
        return getChar(x, y);
    }

    public char getChar(int x, int y) {
        return lines.get(y).charAt(x);
    }
}
