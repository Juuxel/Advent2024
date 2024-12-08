package juuxel.advent2024;

import java.util.function.Function;

public interface Grid<T> {
    int width();
    int height();
    T get(int x, int y);

    default <U> Grid<U> map(Function<T, U> transform) {
        ArrayGrid<U> mapped = new ArrayGrid<>(width(), height());

        for (int x = 0; x < mapped.width(); x++) {
            for (int y = 0; y < mapped.height(); y++) {
                mapped.set(x, y, transform.apply(get(x, y)));
            }
        }

        return mapped;
    }

    default boolean contains(int x, int y) {
        return 0 <= x && x < width() && 0 <= y && y < height();
    }
}