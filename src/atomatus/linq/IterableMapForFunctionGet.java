package atomatus.linq;

import java.util.*;

final class IterableMapForFunctionGet<K, V> implements Map<K, IterableResult<V>> {

    private class IterableResultInternal extends IterableResult<V>{

        private final List<V> list;

        {
            list = new ArrayList<>();
        }

        void add(V v){
            list.add(v);
        }

        @Override
        public Iterator<V> iterator() {
            return list.iterator();
        }

        @Override
        public String toString() {
            return IteratorForJoin.toString(list);
        }
    }

    private transient Map<K, IterableResult<V>> map;

    private final CollectionHelper.FunctionGet<Iterator<V>> iteratorFun;
    private final CollectionHelper.FunctionMount<V, K> groupFun;

    IterableMapForFunctionGet(CollectionHelper.FunctionGet<Iterator<V>> iteratorFun,
                              CollectionHelper.FunctionMount<V, K> groupFun){
        this.iteratorFun = Objects.requireNonNull(iteratorFun);
        this.groupFun = Objects.requireNonNull(groupFun);
    }

    //region init
    @SuppressWarnings("Java8MapApi")
    private Map<K, IterableResult<V>> init(){
        Map<K, IterableResult<V>> map = new HashMap<>();
        Iterator<V> iterator = iteratorFun.get();
        while (iterator.hasNext()) {
            V v = iterator.next();
            K k = groupFun.mount(v);
            IterableResult<V> iri = map.get(k);
            if (iri == null) {
                iri = new IterableResultInternal();
                map.put(k, iri);
            }
            ((IterableResultInternal) iri).add(v);
        }
        return map;
    }

    private Map<K, IterableResult<V>> getMap() {
        synchronized (this) {
            if (map == null) {
                map = init();
            }
            return map;
        }
    }
    //endregion

    //region map
    @Override
    public int size() {
        return getMap().size();
    }

    @Override
    public boolean isEmpty() {
        return getMap().isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return getMap().containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return getMap().containsValue(value);
    }

    @Override
    public IterableResult<V> get(Object key) {
        return getMap().get(key);
    }

    @Override
    public IterableResult<V> put(K key, IterableResult<V> value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IterableResult<V> remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends K, ? extends IterableResult<V>> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        synchronized (this) {
            if (map != null) {
                map.clear();
                map = null;
            }
        }
    }

    @Override
    public Set<K> keySet() {
        return getMap().keySet();
    }

    @Override
    public Collection<IterableResult<V>> values() {
        return getMap().values();
    }

    @Override
    public Set<Entry<K, IterableResult<V>>> entrySet() {
        return getMap().entrySet();
    }
    //endregion

    //region toString
    @Override
    public String toString() {
        synchronized (this) {
            return map == null ? "null" : map.toString();
        }
    }
    //ednregion

}
