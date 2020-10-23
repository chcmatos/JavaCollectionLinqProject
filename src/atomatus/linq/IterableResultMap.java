package atomatus.linq;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Generated result map from collection, set or array interation or filter using {@link CollectionHelper}
 * or another {@link IterableResult} or {@link IterableResultGroup}.
 *
 * @param <K> iterable element key type
 * @param <V> iterable element value type
 * @author Carlos Matos
 */
public abstract class IterableResultMap<K, V> implements Iterable<Map.Entry<K, V>> {

    interface IteratorMap<K, V> extends Iterator<Map.Entry<K, V>> {

        IterableResult<K> keySet();

        IterableResult<V> values();

        int count();

        boolean isEmpty();

        Map<K, V> toMap();

        Set<Map.Entry<K, V>> toSet();
    }

    private IteratorMap<K, V> iterator;

    protected abstract IteratorMap<K, V> initIterator();

    protected final synchronized IteratorMap<K, V> getIterator() {
        if (iterator == null) {
            iterator = this.initIterator();
        }
        return iterator;
    }

    @Override
    public final Iterator<Map.Entry<K, V>> iterator() {
        return getIterator();
    }

    /**
     * All keys of iterable result map.
     *
     * @return
     */
    public final IterableResult<K> keySet() {
        return getIterator().keySet();
    }

    /**
     * All values of iterable result map.
     *
     * @return
     */
    public final IterableResult<V> values() {
        return getIterator().values();
    }

    /**
     * Count of element.
     *
     * @return
     */
    public int count() {
        return getIterator().count();
    }

    /**
     * Check if is empty.
     *
     * @return
     */
    public boolean isEmpty() {
        return getIterator().isEmpty();
    }

    /**
     * Check if result contains data.
     *
     * @return
     */
    public boolean any() {
        return !getIterator().isEmpty();
    }

    /**
     * Generate a map.
     *
     * @return
     */
    public Map<K, V> toMap() {
        return getIterator().toMap();
    }

    /**
     * Generate a set of result.
     *
     * @return
     */
    public Set<Map.Entry<K, V>> toSet() {
        return getIterator().toSet();
    }

    /**
     * A simple foreach action.
     *
     * @param action action to recover each element on collection
     */
    public void foreach(CollectionHelper.ForEachEntryConsumer<Map.Entry<K, V>> action) {
        CollectionHelper.foreach(this, action);
    }
}
