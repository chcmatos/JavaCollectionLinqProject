package atomatus.linq;

import java.util.Iterator;
import java.util.Map;

interface IterableMap<K, V> extends Map<K, V>, Iterable<Map.Entry<K, V>> {

    Iterator<K> iteratorKeys();

    Iterator<V> iteratorValues();

    Map.Entry<K, V> minEntry();

    Map.Entry<K, V> maxEntry();

    void foreach(CollectionHelper.ForEachEntryConsumer<Entry<K, V>> action);
}
