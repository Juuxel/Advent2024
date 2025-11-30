package juuxel.advent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;

public final class Iterables {
    public static <T> List<List<T>> split(Iterable<T> ts, T separator) {
        List<List<T>> result = new ArrayList<>();
        List<T> buffer = new ArrayList<>();

        for (T t : ts) {
            if (Objects.equals(separator, t)) {
                result.add(List.copyOf(buffer));
                buffer.clear();
            } else {
                buffer.add(t);
            }
        }

        result.add(List.copyOf(buffer));
        return result;
    }

    public static <A> IterableX<A> x(Iterable<A> source) {
        return source instanceof IterableX<A> x ? x : source::iterator;
    }

    public static <A> IterableX<A> x(A[] source) {
        return () -> Iterators.of(source);
    }

    public static <A, B> IterableX<B> map(Iterable<A> source, Function<? super A, ? extends B> transform) {
        return new TransformedIterable<>(source, iter -> Iterators.map(iter, transform));
    }

    public static <A> IterableX<A> filter(Iterable<A> source, Predicate<? super A> filter) {
        return new TransformedIterable<>(source, iter -> Iterators.filter(iter, filter));
    }

    public static <T, A, R> R collect(Iterable<T> source, Collector<? super T, A, R> collector) {
        return Iterators.collect(source.iterator(), collector);
    }

    public static <T> IterableX<T> skip(Iterable<T> source, long count) {
        return new TransformedIterable<>(source, iter -> Iterators.skip(iter, count));
    }

    @FunctionalInterface
    public interface IterableX<A> extends Iterable<A> {
        default <B> Iterable<B> map(Function<? super A, ? extends B> transform) {
            return Iterables.map(this, transform);
        }

        default Iterable<A> filter(Predicate<? super A> filter) {
            return Iterables.filter(this, filter);
        }

        default <B, R> R collect(Collector<? super A, B, R> collector) {
            return Iterables.collect(this, collector);
        }

        default IterableX<A> skip(long count) {
            return Iterables.skip(this, count);
        }
    }

    private static final class TransformedIterable<A, B> implements IterableX<B> {
        private final Iterable<A> source;
        private final Function<? super Iterator<A>, ? extends Iterator<B>> transform;

        TransformedIterable(Iterable<A> source, Function<? super Iterator<A>, ? extends Iterator<B>> transform) {
            this.source = source;
            this.transform = transform;
        }

        @Override
        public Iterator<B> iterator() {
            return transform.apply(source.iterator());
        }
    }
}
