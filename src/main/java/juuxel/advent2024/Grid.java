package juuxel.advent2024;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public interface Grid<T> {
    int width();
    int height();
    T get(int x, int y);

    default List<T> columnAt(int x) {
        return new AbstractList<>() {
            @Override
            public T get(int index) {
                return Grid.this.get(x, index);
            }

            @Override
            public int size() {
                return Grid.this.height();
            }
        };
    }

    default List<T> rowAt(int y) {
        return new AbstractList<>() {
            @Override
            public T get(int index) {
                return Grid.this.get(index, y);
            }

            @Override
            public int size() {
                return Grid.this.width();
            }
        };
    }

    default List<List<T>> columns() {
        List<List<T>> result = new ArrayList<>(width());
        for (int x = 0; x < width(); x++) {
            result.add(columnAt(x));
        }
        return result;
    }

    default List<List<T>> rows() {
        List<List<T>> result = new ArrayList<>(height());
        for (int y = 0; y < height(); y++) {
            result.add(rowAt(y));
        }
        return result;
    }

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

    default String prettyPrint(Function<T, String> displayer) {
        StringBuilder sb = new StringBuilder();

        for (int y = 0; y < height(); y++) {
            if (y > 0) sb.append('\n');

            for (int x = 0; x < width(); x++) {
                sb.append(displayer.apply(get(x, y)));
            }
        }

        return sb.toString();
    }
}
