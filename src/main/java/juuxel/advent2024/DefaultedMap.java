package juuxel.advent2024;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Supplier;

public final class DefaultedMap<K, V> extends AbstractMap<K, V> {
    private final Map<K, V> parent;
    private final Supplier<V> defaultValue;

    public DefaultedMap(Map<K, V> parent, Supplier<V> defaultValue) {
        this.parent = parent;
        this.defaultValue = defaultValue;
    }

    public V getOrInit(K key) {
        return computeIfAbsent(key, k -> defaultValue.get());
    }

    @Override
    public V put(K key, V value) {
        return parent.put(key, value);
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return parent.entrySet();
    }

    public static <K> Builder<K> hash() {
        return new Builder<>(new HashMap<>());
    }

    public static <K extends Comparable<K>> Builder<K> tree() {
        return new Builder<>(new TreeMap<>());
    }

    public static <K> Builder<K> tree(Comparator<? super K> keyComparator) {
        return new Builder<>(new TreeMap<>(keyComparator));
    }

    public static final class Builder<K> {
        private final Map<K, ?> parent;

        private Builder(Map<K, ?> parent) {
            this.parent = parent;
        }

        @SuppressWarnings("unchecked")
        public <L extends K, V> DefaultedMap<L, V> withInitial(Supplier<V> defaultValue) {
            return new DefaultedMap<>((Map<L, V>) parent, defaultValue);
        }

        public <L extends K, T> DefaultedMap<L, List<T>> withEmptyList() {
            return withInitial(ArrayList::new);
        }

        public <L extends K, T> DefaultedMap<L, Set<T>> withEmptySet() {
            return withInitial(HashSet::new);
        }
    }
}
