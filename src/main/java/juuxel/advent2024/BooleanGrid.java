package juuxel.advent2024;

public final class BooleanGrid extends ArrayGrid<Boolean> {
    public BooleanGrid(int width, int height) {
        super(width, height, false);
    }

    public BooleanGrid(int width, int height, boolean defaultValue) {
        super(width, height, defaultValue);
    }

    /**
     * Marks the position as true. Returns whether the state changed.
     */
    public boolean mark(int x, int y) {
        boolean old = get(x, y);
        set(x, y, true);
        return !old;
    }
}
