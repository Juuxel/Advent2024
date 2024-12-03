package juuxel.advent2024;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;

public final class Iterators {
    public static <A, B> Iterator<B> map(Iterator<A> iter, Function<? super A, ? extends B> transform) {
        return new Iterator<>() {
            @Override
            public boolean hasNext() {
                return iter.hasNext();
            }

            @Override
            public B next() {
                return transform.apply(iter.next());
            }

            @Override
            public void remove() {
                iter.remove();
            }
        };
    }

    public static <A> Iterator<A> filter(Iterator<A> iter, Predicate<? super A> filter) {
        return new Iterator<>() {
            private byte state = 0; // 0: ready, 1: queued, 2: eos
            private A next;

            @Override
            public boolean hasNext() {
                while (state == 0) {
                    if (iter.hasNext()) {
                        next = iter.next();

                        if (filter.test(next)) {
                            state = 1;
                        } else {
                            next = null;
                        }
                    } else {
                        state = 2;
                    }
                }

                return state == 1;
            }

            @Override
            public A next() {
                if (!hasNext()) throw new NoSuchElementException();
                A result = next;
                next = null;
                return result;
            }

            @Override
            public void remove() {
                iter.remove();
            }
        };
    }

    public static <T, A, R> R collect(Iterator<T> ts, Collector<? super T, A, R> collector) {
        A state = collector.supplier().get();

        while (ts.hasNext()) {
            collector.accumulator().accept(state, ts.next());
        }

        return collector.finisher().apply(state);
    }

    public static <A> Iterator<A> skip(Iterator<A> iter, long count) {
        return new Iterator<>() {
            private long skipped = 0;

            @Override
            public boolean hasNext() {
                while (skipped < count) {
                    if (!iter.hasNext()) {
                        return false;
                    }

                    iter.next(); // discard next value
                    skipped++;
                }

                return iter.hasNext();
            }

            @Override
            public A next() {
                if (!hasNext()) throw new NoSuchElementException();
                return iter.next();
            }

            @Override
            public void remove() {
                iter.remove();
            }
        };
    }

    public static <A> Iterator<A> of(A[] as) {
        return new Iterator<>() {
            private int i = 0;

            @Override
            public boolean hasNext() {
                return i < as.length;
            }

            @Override
            public A next() {
                if (!hasNext()) throw new NoSuchElementException();
                return as[i++];
            }
        };
    }
}
