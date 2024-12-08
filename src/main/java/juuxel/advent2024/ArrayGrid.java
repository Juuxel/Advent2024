package juuxel.advent2024;

import java.util.Arrays;

public class ArrayGrid<T> implements Grid<T> {
    private final Object[][] grid;
    private final int width;
    private final int height;

    public ArrayGrid(T[][] grid) {
        this.grid = grid;
        this.width = grid.length;
        this.height = grid[0].length;
    }

    public ArrayGrid(int width, int height) {
        this.grid = new Object[width][height];
        this.width = width;
        this.height = height;
    }

    public ArrayGrid(int width, int height, T defaultValue) {
        this(width, height);

        for (int x = 0; x < width; x++) {
            Arrays.fill(grid[x], defaultValue);
        }
    }

    @Override
    public int width() {
        return width;
    }

    @Override
    public int height() {
        return height;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T get(int x, int y) {
        return (T) grid[x][y];
    }

    public void set(int x, int y, T t) {
        grid[x][y] = t;
    }
}
