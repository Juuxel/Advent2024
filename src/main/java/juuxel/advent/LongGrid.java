package juuxel.advent;

public final class LongGrid extends ArrayGrid<Long> {
    public LongGrid(int width, int height) {
        super(width, height, 0L);
    }

    public LongGrid(int width, int height, long defaultValue) {
        super(width, height, defaultValue);
    }

    public LongGrid(Grid<Long> other) {
        super(other);
    }

    public void add(int x, int y, long toAdd) {
        long current = get(x, y);
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
