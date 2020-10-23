package atomatus.linq;

import java.util.*;


abstract class IteratorForGroup<K, V> extends IteratorForMap<K, Iterable<V>> implements IterableResultGroup.IteratorGroup<K, V> {

    //region getInstanceForGroupImpl
    private static class IteratorForGroupImpl<G, E> extends IteratorForGroup<G, E> {

        private final CollectionHelper.FunctionGet<Iterator<E>> iteratorFun;
        private final CollectionHelper.FunctionMount<E, G> groupFun;

        IteratorForGroupImpl(CollectionHelper.FunctionGet<Iterator<E>> iteratorFun,
                             CollectionHelper.FunctionMount<E, G> groupFun) {
            this.iteratorFun = Objects.requireNonNull(iteratorFun);
            this.groupFun = Objects.requireNonNull(groupFun);
        }

        @Override
        @SuppressWarnings("All")
        protected Map<G, Iterable<E>> initResult() {
            Map<G, Iterable<E>> map = new HashMap<>();
            Iterator<E> iterator = iteratorFun.get();
            while (iterator.hasNext()) {
                E v = iterator.next();
                G k = groupFun.mount(v);
                Iterable<E> list = map.get(k);
                if (list == null) {
                    map.put(k, list = new ArrayList<>());
                }
                ((List<E>) list).add(v);
            }
            return map;
        }
    }

    private static class IteratorForGroupImplArray<G, E> extends IteratorForGroup<G, E> {

        private final E[] arr;
        private final CollectionHelper.FunctionMount<E, G> groupFun;

        IteratorForGroupImplArray(E[] arr, CollectionHelper.FunctionMount<E, G> groupFun) {
            this.arr = Objects.requireNonNull(arr);
            this.groupFun = Objects.requireNonNull(groupFun);
        }

        @Override
        @SuppressWarnings("All")
        protected Map<G, Iterable<E>> initResult() {
            Map<G, Iterable<E>> map = new HashMap<>();
            for (E v : arr) {
                G k = groupFun.mount(v);
                Iterable<E> list = map.get(k);
                if (list == null) {
                    map.put(k, list = new ArrayList<>());
                }
                ((List<E>) list).add(v);
            }
            return map;
        }
    }

    static <G, E> IteratorForGroup<G, E> getInstanceForIterator(
            CollectionHelper.FunctionGet<Iterator<E>> iteratorFun,
            CollectionHelper.FunctionMount<E, G> groupFun) {
        return new IteratorForGroupImpl<>(iteratorFun, groupFun);
    }

    static <G, E> IteratorForGroup<G, E> getInstanceForArray(
            E[] arr,
            CollectionHelper.FunctionMount<E, G> groupFun) {
        return new IteratorForGroupImplArray<>(arr, groupFun);
    }
    //endregion

    //region Impl
    private <IN, OUT> Map.Entry<K, OUT> getEntryMountValueOnRequest(Map.Entry<K, IN> entry,
                                                                    CollectionHelper.FunctionMount<IN, OUT> mountFun) {
        return new Map.Entry<K, OUT>() {

            final K key = entry.getKey();
            final IN inValue = entry.getValue();
            transient OUT outValue;

            @Override
            public K getKey() {
                return key;
            }

            @Override
            public OUT getValue() {
                if (outValue == null) outValue = mountFun.mount(inValue);
                return outValue;
            }

            @Override
            public OUT setValue(OUT value) {
                throw new UnsupportedOperationException("Read only entry: value can not be changed!");
            }
        };
    }


    private <N> IterableResultMap<K, N> calc(CollectionHelper.FunctionMount<Iterable<V>, N> calcFun) {
        return new IterableResultMap<K, N>() {
            @Override
            protected IterableResultMap.IteratorMap<K, N> initIterator() {
                return IteratorForMap.getInstanceForLazyReadOnlyMap(
                        IteratorForGroup.this.getResult().entrySet(),
                        e -> getEntryMountValueOnRequest(e, calcFun));
            }
        };
    }

    /**
     * Generate an iterable result with the amount of items in each entry.
     * @return
     */
    @Override
    public IterableResultMap<K, Integer> size() {
        return new IterableResultMap<K, Integer>() {
            @Override
            protected IterableResultMap.IteratorMap<K, Integer> initIterator() {
                return IteratorForMap.getInstanceForLazyReadOnlyMap(
                        IteratorForGroup.this.getResult().entrySet(),
                        e -> getEntryMountValueOnRequest(e, CollectionHelper::count));
            }
        };
    }

    /**
     * Apply summation operation in a sequence of any kind of number.
     * @param resultClass number type class.
     * @param <N> number type
     * @return summation result
     */
    @Override
    public <N extends Number> IterableResultMap<K, N> sum(Class<N> resultClass) {
        Objects.requireNonNull(resultClass);
        return sum(i -> IteratorForMath.parseNumber(i, resultClass));
    }

    /**
     * Apply summation operation in a sequence of any kind of number.
     *
     * @param mountFun  function to get a target number in element.
     * @param <N> number type
     * @return summation result
     */
    @Override
    public <N extends Number> IterableResultMap<K, N> sum(CollectionHelper.FunctionMount<V, N> mountFun) {
        Objects.requireNonNull(mountFun);
        return calc(list -> IteratorForMath.sum(list.iterator(), mountFun));
    }

    /**
     * Average is defined as the sum of all the values divided by the total number of values in a given set.
     *
     * @param resultClass result number class type
     * @param <N> result number type
     * @return result number
     */
    @Override
    public <N extends Number> IterableResultMap<K, N> average(Class<N> resultClass) {
        Objects.requireNonNull(resultClass);
        return average(i -> IteratorForMath.parseNumber(i, resultClass));
    }

    /**
     * Average is defined as the sum of all the values divided by the total number of values in a given set.
     *
     * @param mountFun function to get target number
     * @param <N> result number type
     * @return result number
     */
    @Override
    public <N extends Number> IterableResultMap<K, N> average(CollectionHelper.FunctionMount<V, N> mountFun) {
        Objects.requireNonNull(mountFun);
        return calc(list -> IteratorForMath.average(list.iterator(), mountFun));
    }

    /**
     * A mean is a mathematical term, that describes the average of a sample.<br/>
     * In Statistics, the definition of the mean is similar to the average.<br/>
     * But, it can also be defined as the sum of the smallest value and the largest value in the given data set divided by 2.
     * @param resultClass number type class.
     * @return result number
     */
    @Override
    public <N extends Number> IterableResultMap<K, N> mean(Class<N> resultClass) {
        Objects.requireNonNull(resultClass);
        return mean(i -> IteratorForMath.parseNumber(i, resultClass));
    }

    /**
     * A mean is a mathematical term, that describes the average of a sample.<br/>
     * In Statistics, the definition of the mean is similar to the average.<br/>
     * But, it can also be defined as the sum of the smallest value and the largest value in the given data set divided by 2.
     *
     * @param mountFun   function to get target number
     * @param <N> result number type
     * @return result number
     */
    @Override
    public <N extends Number> IterableResultMap<K, N> mean(CollectionHelper.FunctionMount<V, N> mountFun) {
        Objects.requireNonNull(mountFun);
        return calc(list -> IteratorForMath.mean(list.iterator(), mountFun));
    }

    /**
     * Recover minimum value of collection
     * @return minimum value
     */
    @Override
    public IterableResultMap<K, V> min() {
        return calc(list -> IteratorForMath.min(list.iterator()));
    }

    /**
     * Recover minimum value of collection
     * @param mountFun function to get Comparable element target
     * @param <C> result comparable element
     * @return minimum value
     */
    @Override
    public <C extends Comparable> IterableResultMap<K, V> min(CollectionHelper.FunctionMount<V, C> mountFun) {
        Objects.requireNonNull(mountFun);
        return calc(list -> IteratorForMath.min(list.iterator(), (v) -> new Comparable<C>() {
            V value = v;
            C c = mountFun.mount(v);

            @Override
            public int compareTo(C o) {
                //noinspection unchecked
                return o.compareTo(c);
            }
        }).value);
    }

    /**
     * Recover maximum value of collection
     * @return maximum value
     */
    @Override
    public IterableResultMap<K, V> max() {
        return calc(list -> IteratorForMath.max(list.iterator()));
    }

    /**
     * Recover maximum value of collection
     * @param mountFun function to get Comparable element target
     * @param <C> result comparable element
     * @return maximum value
     */
    @Override
    public <C extends Comparable> IterableResultMap<K, V> max(CollectionHelper.FunctionMount<V, C> mountFun) {
        Objects.requireNonNull(mountFun);
        return calc(list -> IteratorForMath.max(list.iterator(), (v) -> new Comparable<C>() {
            V value = v;
            C c = mountFun.mount(v);

            @Override
            public int compareTo(C o) {
                //noinspection unchecked
                return o.compareTo(c);
            }
        }).value);
    }
    //endregion
}
