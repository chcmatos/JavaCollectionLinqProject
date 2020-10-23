package atomatus.linq;

/**
 * Generated result group from collection, set or array interation or filter using {@link CollectionHelper}
 * or another {@link IterableResult}.
 *
 * @param <K> iterable element key type
 * @param <V> iterable element value type
 * @author Carlos Matos
 */
public abstract class IterableResultGroup<K, V> extends IterableResultMap<K, Iterable<V>> {

    interface IteratorGroup<K, V> extends IterableResultMap.IteratorMap<K, Iterable<V>> {

        IterableResult<K> keySet();

        IterableResult<Iterable<V>> values();

        IterableResultMap<K, Integer> size();

        <N extends Number> IterableResultMap<K, N> sum(Class<N> resultClass);

        <N extends Number> IterableResultMap<K, N> sum(CollectionHelper.FunctionMount<V, N> mountFun);

        <N extends Number> IterableResultMap<K, N> average(Class<N> resultClass);

        <N extends Number> IterableResultMap<K, N> average(CollectionHelper.FunctionMount<V, N> mountFun);

        <N extends Number> IterableResultMap<K, N> mean(Class<N> resultClass);

        <N extends Number> IterableResultMap<K, N> mean(CollectionHelper.FunctionMount<V, N> mountFun);

        IterableResultMap<K, V> min();

        <C extends Comparable> IterableResultMap<K, V> min(CollectionHelper.FunctionMount<V, C> mountFun);

        IterableResultMap<K, V> max();

        <C extends Comparable> IterableResultMap<K, V> max(CollectionHelper.FunctionMount<V, C> mountFun);
    }

    protected abstract IteratorGroup<K, V> initIterator();

    protected IteratorGroup<K, V> getIteratorAsGroup() {
        return (IteratorGroup<K, V>) super.getIterator();
    }


    //region IteratorGroup Actions

    /**
     * Generate an iterable result with the amount of items in each entry.
     *
     * @return
     */
    public final IterableResultMap<K, Integer> size() {
        return getIteratorAsGroup().size();
    }

    /**
     * Apply summation operation in a sequence of any kind of number.
     *
     * @param resultClass number type class.
     * @param <N>         number type
     * @return summation result
     */
    public final <N extends Number> IterableResultMap<K, N> sum(Class<N> resultClass) {
        return getIteratorAsGroup().sum(resultClass);
    }

    /**
     * Apply summation operation in a sequence of any kind of number.
     *
     * @param mountFun function to get a target number in element.
     * @param <N>      number type
     * @return summation result
     */
    public final <N extends Number> IterableResultMap<K, N> sum(CollectionHelper.FunctionMount<V, N> mountFun) {
        return getIteratorAsGroup().sum(mountFun);
    }

    /**
     * Average is defined as the sum of all the values divided by the total number of values in a given set.
     *
     * @param resultClass result number class type
     * @param <N>         result number type
     * @return result number
     */
    public final <N extends Number> IterableResultMap<K, N> average(Class<N> resultClass) {
        return getIteratorAsGroup().average(resultClass);
    }

    /**
     * Average is defined as the sum of all the values divided by the total number of values in a given set.
     *
     * @param mountFun function to get target number
     * @param <N>      result number type
     * @return result number
     */
    public final <N extends Number> IterableResultMap<K, N> average(CollectionHelper.FunctionMount<V, N> mountFun) {
        return getIteratorAsGroup().average(mountFun);
    }

    /**
     * A mean is a mathematical term, that describes the average of a sample.<br/>
     * In Statistics, the definition of the mean is similar to the average.<br/>
     * But, it can also be defined as the sum of the smallest value and the largest value in the given data set divided by 2.
     *
     * @param resultClass number type class.
     * @return result number
     */
    public final <N extends Number> IterableResultMap<K, N> mean(Class<N> resultClass) {
        return getIteratorAsGroup().mean(resultClass);
    }

    /**
     * A mean is a mathematical term, that describes the average of a sample.<br/>
     * In Statistics, the definition of the mean is similar to the average.<br/>
     * But, it can also be defined as the sum of the smallest value and the largest value in the given data set divided by 2.
     *
     * @param mountFun function to get target number
     * @param <N>      result number type
     * @return result number
     */
    public final <N extends Number> IterableResultMap<K, N> mean(CollectionHelper.FunctionMount<V, N> mountFun) {
        return getIteratorAsGroup().mean(mountFun);
    }

    /**
     * Recover minimum value of collection
     *
     * @return minimum value
     */
    public final IterableResultMap<K, V> min() {
        return getIteratorAsGroup().min();
    }

    /**
     * Recover minimum value of collection
     *
     * @param mountFun function to get Comparable element target
     * @param <C>      result comparable element
     * @return minimum value
     */
    public final <C extends Comparable> IterableResultMap<K, V> min(CollectionHelper.FunctionMount<V, C> mountFun) {
        return getIteratorAsGroup().min(mountFun);
    }

    /**
     * Recover maximum value of collection
     *
     * @return maximum value
     */
    public final IterableResultMap<K, V> max() {
        return getIteratorAsGroup().max();
    }

    /**
     * Recover maximum value of collection
     *
     * @param mountFun function to get Comparable element target
     * @param <C>      result comparable element
     * @return maximum value
     */
    public final <C extends Comparable> IterableResultMap<K, V> max(CollectionHelper.FunctionMount<V, C> mountFun) {
        return getIteratorAsGroup().max(mountFun);
    }
    //endregion
}
