package juuxel.advent;

import java.util.AbstractList;
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

    public static CharGrid createRightPadded(List<String> lines, char padding) {
        int gridWidth = lines.stream().mapToInt(String::length).max().orElseThrow();
        List<String> paddedLines = lines.stream()
            .map(line -> line + ("" + padding).repeat(gridWidth - line.length()))
            .toList();
        return new CharGrid(paddedLines);
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

    @Override
    public List<Character> rowAt(int y) {
        return new StringList(lines.get(y));
    }

    private static final class StringList extends AbstractList<Character> {
        private final String str;

        StringList(String str) {
            this.str = str;
        }

        @Override
        public Character get(int index) {
            return str.charAt(index);
        }

        @Override
        public int size() {
            return str.length();
        }
    }
}
