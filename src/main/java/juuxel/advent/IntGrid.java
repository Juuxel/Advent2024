package juuxel.advent;

public final class IntGrid extends ArrayGrid<Integer> {
    public IntGrid(int width, int height) {
        super(width, height, 0);
    }

    public IntGrid(int width, int height, int defaultValue) {
        super(width, height, defaultValue);
    }

    public IntGrid(Grid<Integer> other) {
        super(other);
    }

    public void add(int x, int y, int toAdd) {
        int current = get(x, y);
        set(x, y, current + toAdd);
    }

    public void increment(int x, int y) {
        add(x, y, 1);
    }

    public void incrementIfContains(int x, int y) {
        if (contains(x, y)) {
            add(x, y, 1);
        }
    }
}
