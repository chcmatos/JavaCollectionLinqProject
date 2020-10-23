package atomatus.linq;

import java.util.*;

abstract class IteratorForMap<K, V> implements IterableResultMap.IteratorMap<K, V> {

    private static class LazyReadOnlyMap<IN, K, V> implements Map<K, V> {

        private Set<Entry<K, V>> set;
        private transient Set<K> keySet;
        private transient Collection<V> values;
        private final Iterable<IN> input;
        private final CollectionHelper.FunctionMount<IN, Entry<K, V>> mountFun;

        LazyReadOnlyMap(Iterable<IN> input,
                        CollectionHelper.FunctionMount<IN, Entry<K, V>> mountFun) {
            this.input = input;
            this.mountFun = mountFun;
        }

        private synchronized Set<Entry<K, V>> getSet() {
            if (set == null) {
                set = CollectionHelper.select(input, mountFun).toSet();
            }
            return set;
        }

        @Override
        public int size() {
            return getSet().size();
        }

        @Override
        public boolean isEmpty() {
            return getSet().isEmpty();
        }

        @Override
        public boolean containsKey(Object key) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsValue(Object value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public V get(Object key) {
            for (Entry<K, V> entry : getSet()) {
                K k = entry.getKey();
                if (k == key || key.equals(k)) {
                    return entry.getValue();
                }
            }
            return null;
        }

        @Override
        public V put(K key, V value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public V remove(Object key) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void putAll(Map<? extends K, ? extends V> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Set<K> keySet() {
            if (keySet == null) {
                keySet = CollectionHelper.select(getSet(), Entry::getKey).toSet();
            }
            return keySet;
        }

        @Override
        public Collection<V> values() {
            if (values == null) {
                values = CollectionHelper.select(getSet(), Entry::getValue).toList();
            }
            return values;
        }

        @Override
        public Set<Entry<K, V>> entrySet() {
            return getSet();
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder().append("[");
            List<Entry<K, V>> list = new ArrayList<>(getSet());
            char eq     = '=';
            String sep  = ", ";
            for (int i = 0, l = list.size(), j = l - 1; i < l; i++) {
                Entry<K, V> e = list.get(i);
                sb.append(e.getKey())
                        .append(eq)
                        .append(IteratorForJoin.toString(e.getValue()));
                if (i < j) {
                    sb.append(sep);
                }
            }
            return sb.append("]").toString();
        }
    }

    static <IN, K, V> IteratorForMap<K, V> getInstanceForLazyReadOnlyMap(Iterable<IN> input,
                                                                         CollectionHelper.FunctionMount<IN, Map.Entry<K, V>> mountFun) {
        return new IteratorForMap<K, V>() {
            @Override
            protected Map<K, V> initResult() {
                return new LazyReadOnlyMap<>(input, mountFun);
            }
        };
    }

    private Map<K, V> result;
    private Iterator<Map.Entry<K, V>> iterator;

    protected Map<K, V> getResult() {
        checkInit();
        return result;
    }

    private Iterator<Map.Entry<K, V>> getIterator() {
        checkInit();
        return iterator;
    }

    protected abstract Map<K, V> initResult();

    private synchronized void checkInit() {
        if (result == null) {
            result = this.initResult();
            iterator = result.entrySet().iterator();
        }
    }

    @Override
    public IterableResult<K> keySet() {
        return new IterableResult<K>() {
            @Override
            public Iterator<K> iterator() {
                return getResult().keySet().iterator();
            }
        };
    }

    @Override
    public IterableResult<V> values() {
        return new IterableResult<V>() {
            @Override
            public Iterator<V> iterator() {
                return getResult().values().iterator();
            }
        };
    }

    @Override
    public boolean hasNext() {
        return getIterator().hasNext();
    }

    @Override
    public Map.Entry<K, V> next() {
        return getIterator().next();
    }

    @Override
    public int count() {
        return getResult().size();
    }

    @Override
    public boolean isEmpty() {
        return getResult().isEmpty();
    }

    @Override
    public Map<K, V> toMap() {
        return new HashMap<>(getResult());
    }

    @Override
    public Set<Map.Entry<K, V>> toSet() {
        return new HashSet<>(getResult().entrySet());
    }
}
