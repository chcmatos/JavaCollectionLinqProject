package atomatus.linq;

import java.lang.reflect.Array;
import java.util.*;

/**
 * <p>
 * Helper for query actions (from any iterable, set or array) to
 * manipulate, filter, find, group elements on set or array fastestway for reducing code.
 * </p>
 * <p>
 * Every query action result generate an {@link IterableResult}, {@link IteratorForGroup} or {@link IteratorForMap},
 * all thus are tree schedules Iterator actions that will be mounted and executed only when do a directed request it.
 * </p>
 *
 * @author Carlos Matos
 */
public final class CollectionHelper {

    private CollectionHelper() {
    }

    //region Compare and Functions

    /**
     * Compare if entry is valid for class.
     *
     * @param <I> entry to be compared it.
     * @param <C> dessired class type.
     */
    public interface CompareEntryForClass<I, C> {
        boolean isValidFor(I i, Class<C> classType);
    }

    /**
     * Check if current entry is valid.
     *
     * @param <I> target entry.
     */
    public interface CompareEntryValid<I> {
        boolean isValid(I i);
    }

    /**
     * Receive a entry value.
     *
     * @param <I> entry value.
     */
    public interface ForEachEntryConsumer<I> {
        /**
         * Current accept.
         *
         * @param i current element.
         */
        void accept(I i);
    }

    /**
     * Receive a entry value and index.
     *
     * @param <I> entry value.
     */
    public interface ForEachIterableEntryConsumer<I> {
        /**
         * Current accept.
         *
         * @param i     current element.
         * @param index current element index.
         */
        void accept(I i, int index);
    }

    /**
     * Receive a entry value and mount a element.
     *
     * @param <I> entry value.
     * @param <E> generated element from entry value.
     */
    public interface FunctionMount<I, E> {
        E mount(I i);
    }

    /**
     * Small function to get a element.
     *
     * @param <E>
     */
    public interface FunctionGet<E> {
        E get();
    }

    /**
     * Reduce function.
     *
     * @param <IN>
     * @param <OUT>
     */
    public interface FunctionReduce<IN, OUT> {
        OUT reduce(OUT acc, IN curr);
    }
    //endregion

    //region toArray

    /**
     * Create a new object array from current object (current object referecing a array).
     *
     * @param arr target object.
     * @return object array.
     */
    public static Object[] toArray(Object arr) {
        Objects.requireNonNull(arr);
        if (!arr.getClass().isArray()) {
            throw new IllegalArgumentException("Object is not an array!");
        }
        int len = Array.getLength(arr);
        Object[] objArr = new Object[len];
        for (int i = 0; i < len; i++) {
            objArr[i] = Array.get(arr, i);
        }
        return objArr;
    }

    /**
     * Create a new object array from current object (current object referecing a array).
     *
     * @param arr       target object.
     * @param clazzType array class type.
     * @param <E>       array type.
     * @return object array.
     */
    @SuppressWarnings("unchecked")
    public static <E> E[] toArray(Object arr, Class<E> clazzType) {
        Objects.requireNonNull(arr);
        if (!arr.getClass().isArray()) {
            throw new IllegalArgumentException("Object is not an array!");
        }
        int len = Array.getLength(arr);
        E[] objArr = (E[]) Array.newInstance(clazzType, len);
        for (int i = 0; i < len; i++) {
            Object obj = Array.get(arr, i);
            if (clazzType.isInstance(obj)) {
                objArr[i] = (E) obj;
            }
        }

        return objArr;
    }

    /**
     * Convert a collection to array.
     *
     * @param col target collection.
     * @param <C> collection type.
     * @param <I> collection element type.
     * @return a new array within collection's elements reference.
     */
    public static <C extends Iterable<? extends I>, I> I[] toArray(C col) {
        Objects.requireNonNull(col);
        int i = 0;
        int len = 100;
        I[] arr = null;

        for (I t : col) {
            if (arr == null) {
                arr = copyOf(len, t);
            } else if (i == arr.length) {
                arr = copyOf(arr.length + len, arr);
            }
            arr[i++] = t;
        }

        return arr == null ? copyOf(0) :
                i < arr.length ? copyOf(i, arr) : arr;
    }

    /**
     * Convert a collection to array.
     *
     * @param col  target collection.
     * @param size new array size.
     * @param <C>  collection type.
     * @param <I>  collection element type.
     * @return a new array within collection's elements reference.
     */
    public static <C extends Iterable<? extends I>, I> I[] toArray(C col, int size) {
        Objects.requireNonNull(col);
        int i = 0;
        I[] arr = copyOf(size);

        for (I t : col) {
            if (i == size) {
                break;
            }
            arr[i++] = t;
        }

        return arr;
    }

    /**
     * Generate a copy of array with a new length.
     *
     * @param length new array length.
     * @param array  target array or elements.
     * @param <E>    element type.
     * @return copy of array within elements reference.
     */
    @SafeVarargs
    @SuppressWarnings("unchecked")
    private static <E> E[] copyOf(int length, E... array) {
        if (array.length > 0) {
            Class<?> type;
            if (array.length == 1) {
                type = array[0].getClass();
            } else {
                List<Class<?>> res = nonNull(array).<Class<?>>distinct(E::getClass).toList();
                type = res.size() == 1 ? array[0].getClass() : array.getClass().getComponentType();
            }

            E[] copy = (E[]) Array.newInstance(type, length);
            System.arraycopy(array, 0, copy, 0, Math.min(array.length, length));
            return copy;
        }

        return (E[]) new Object[0];
    }

    /**
     * Insert a new element at object array (on index).
     *
     * @param arr   target array
     * @param e     new element to array
     * @param index index on array
     * @param <E>   element type
     * @return new array.
     */
    @SuppressWarnings("unchecked")
    public static <E> E[] insertAt(E[] arr, E e, int index) {
        Objects.requireNonNull(arr);
        if (index < 0) {
            throw new IndexOutOfBoundsException();
        }

        int length = arr.length;
        int newLength = Math.max(index, length) + 1;
        Class<?> newType = arr.getClass();
        E[] nArr = (newType == Object[].class) ?
                (E[]) new Object[newLength] :
                (E[]) Array.newInstance(newType.getComponentType(), newLength);

        boolean found = false;
        for (int i = 0, j = 0; i < length; j++, i++) {
            if (!found && (found = i == index)) {
                j++;
            }
            nArr[j] = arr[i];
        }
        nArr[index] = e;
        return nArr;
    }

    /**
     * Insert a new element at first index of object array.
     *
     * @param arr target array
     * @param e   new element to array
     * @param <E> element type
     * @return new array.
     */
    public static <E> E[] push(E[] arr, E e) {
        return insertAt(arr, e, 0);
    }

    /**
     * Insert a new element at end of object array.
     *
     * @param arr target array
     * @param e   new element to array
     * @param <E> element type
     * @return new array.
     */
    public static <E> E[] add(E[] arr, E e) {
        return insertAt(arr, e, arr.length);
    }
    //endregion

    //region toList

    /**
     * Convert any iterable to list.
     *
     * @param iterable target
     * @param <E>      element type
     * @return new list within iterable elements
     */
    public static <E> List<E> toList(Iterable<E> iterable) {
        List<E> list = new ArrayList<>();
        foreach(iterable, (ForEachEntryConsumer<E>) list::add);
        return list;
    }

    /**
     * Convert an array to list
     *
     * @param arr target
     * @param <E> element type
     * @return new list within iterable elements
     */
    public static <E> List<E> toList(E[] arr) {
        List<E> list = new ArrayList<>();
        foreach(arr, (ForEachEntryConsumer<E>) list::add);
        return list;
    }
    //endregion

    //region toSet

    /**
     * Convert any iterable to set.
     *
     * @param iterable target
     * @param <E>      element type
     * @return new set within iterable elements
     */
    public static <E> Set<E> toSet(Iterable<E> iterable) {
        Set<E> set = new HashSet<>();
        foreach(iterable, set::add);
        return set;
    }

    /**
     * Convert an array to set
     *
     * @param arr target
     * @param <E> element type
     * @return new set within iterable elements
     */
    public static <E> Set<E> toSet(E[] arr) {
        Set<E> set = new HashSet<>();
        foreach(arr, set::add);
        return set;
    }
    //endregion

    //region filterAs
    //region filterAsIterable

    /**
     * Build an interable from any collection, filtering values and accepting only who is valid for classType and
     * recovering from a iterable mode.
     *
     * @param classType                   dessired class type.
     * @param getCollection               target collection.
     * @param checkEntryValidForClassType check if current entry is valid for classType.
     * @param mountElementFun             mount a element from current entry.
     * @param <I>                         current entry type, same type (or herintenced type) for collection S.
     * @param <E>                         mounted element from entry type.
     * @param <C>                         dessired class type.
     * @param <S>                         collection.
     * @return return a iterable of filtered element (E) of collection (S) within entries (I) valid for ClassType (C).
     */
    public static <I, E, C, S extends Collection<? extends I>> IterableResult<E> filterAsIterable(Class<C> classType,
                                                                                                  FunctionGet<S> getCollection,
                                                                                                  CompareEntryForClass<I, C> checkEntryValidForClassType,
                                                                                                  FunctionMount<I, E> mountElementFun) {
        return IterableResultFactory.getInstanceForFilterAsClassType(classType,
                getCollection,
                checkEntryValidForClassType,
                mountElementFun);
    }

    /**
     * Build an interable from any collection, filtering values and accepting only who is valid for classType and
     * recovering from a iterable mode.
     *
     * @param classType                   dessired class type.
     * @param set                         target collection.
     * @param checkEntryValidForClassType check if current entry is valid for classType.
     * @param mountElementFun             mount a element from current entry.
     * @param <I>                         current entry type, same type (or herintenced type) for collection S.
     * @param <E>                         mounted element from entry type.
     * @param <C>                         dessired class type.
     * @return return a iterable of filtered element (E) of collection within entries (I) valid for ClassType (C).
     */
    public static <I, E, C> IterableResult<E> filterAsIterable(Class<C> classType,
                                                               Collection<? extends I> set,
                                                               CompareEntryForClass<I, C> checkEntryValidForClassType,
                                                               FunctionMount<I, E> mountElementFun) {
        Objects.requireNonNull(set);
        return filterAsIterable(classType, () -> set, checkEntryValidForClassType, mountElementFun);
    }

    /**
     * Build an interable from any collection, filtering values and accepting only who is valid for function validate entry and
     * recovering from a iterable mode.
     *
     * @param set               target collection.
     * @param checkEntryIsValid check which entry is valid to be filtering.
     * @param mountElementFun   mount a element from each entry.
     * @param <I>               current entry type.
     * @param <E>               mounted element from entry type.
     * @return return a iterable of filtered element (E) of collection within entries (I) valid for compareEntryValid function.
     */
    public static <I, E> IterableResult<E> filterAsIterable(Collection<? extends I> set,
                                                            CompareEntryValid<I> checkEntryIsValid,
                                                            FunctionMount<I, E> mountElementFun) {
        Objects.requireNonNull(checkEntryIsValid);
        return filterAsIterable(Object.class, set, (i, clazz) -> checkEntryIsValid.isValid(i), mountElementFun);
    }

    /**
     * Build an interable from any collection, filtering values and accepting only who is valid for function validate entry and
     * recovering from a iterable mode.
     *
     * @param set               target collection.
     * @param checkEntryIsValid check which entry is valid to be filtering.
     * @param <I>               entry type.
     * @return return a iterable of filtered element of collection valid for compareEntryValid function.
     */
    public static <I> IterableResult<I> filterAsIterable(Collection<? extends I> set, CompareEntryValid<I> checkEntryIsValid) {
        return filterAsIterable(set, checkEntryIsValid, (i) -> i);
    }
    //endregion

    //region filterAsList
    private static <I> List<I> filterAsList(List<?> list, Class<I> classType) {
        Iterable<I> i = filterAsIterable(classType, list, null, null);
        List<I> result = new ArrayList<>();
        foreach(i, (ForEachEntryConsumer<I>) result::add);
        return result;
    }

    /**
     * Generate a list filtered by super class type of elements on target list.
     *
     * @param list      list of elements are super then target classType.
     * @param classType super class type
     * @param <I>       element type (herintence from super class type)
     * @return new list with elements are instanceof classType
     */
    public static <I> List<I> filterAsListS(List<? super I> list, Class<I> classType) {
        return filterAsList(list, classType);
    }

    /**
     * Generate a list filtered by extends class type of elements on target list.
     *
     * @param list      list of elements are extends then target classType.
     * @param classType extended class type
     * @param <I>       element type (herintence from extend class type)
     * @return new list with elements are instanceof classType
     */
    public static <I> List<I> filterAsListE(List<? extends I> list, Class<I> classType) {
        return filterAsList(list, classType);
    }
    //endregion

    //region filterAsCollection
    private static <I> Collection<I> filterAsCollection(Collection<?> col, Class<I> classType) {
        Iterable<I> i = filterAsIterable(classType, col, null, null);
        try {
            //noinspection unchecked
            Collection<I> result = col.getClass().getDeclaredConstructor().newInstance();
            foreach(i, result::add);
            return result;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Generate a list filtered by super class type of elements on target list.
     *
     * @param col       list of elements are super then target classType.
     * @param classType super class type
     * @param <I>       element type (herintence from super class type)
     * @return new list with elements are instanceof classType
     */
    public static <I> Collection<I> filterAsCollectionS(Collection<? super I> col, Class<I> classType) {
        return filterAsCollection(col, classType);
    }

    /**
     * Generate a list filtered by extends class type of elements on target list.
     *
     * @param col       list of elements are extends then target classType.
     * @param classType extended class type
     * @param <I>       element type (herintence from extend class type)
     * @return new list with elements are instanceof classType
     */
    public static <I> Collection<I> filterAsCollectionE(Collection<? extends I> col, Class<I> classType) {
        return filterAsCollection(col, classType);
    }
    //endregion

    //region filterAsSet
    private static <I> Set<I> filterAsSet(Set<?> set, Class<I> classType) {
        Iterable<I> i = filterAsIterable(classType, set, null, null);
        try {
            //noinspection unchecked
            Set<I> result = set.getClass().getDeclaredConstructor().newInstance();
            foreach(i, result::add);
            return result;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Generate a set filtered by super class type of elements on target set.
     *
     * @param set       set of elements are super then target classType.
     * @param classType super class type
     * @param <I>       element type (herintence from super class type)
     * @return new set with elements are instanceof classType
     */
    public static <I> Set<I> filterAsSetS(Set<? super I> set, Class<I> classType) {
        return filterAsSet(set, classType);
    }

    /**
     * Generate a set filtered by extends class type of elements on target set.
     *
     * @param set       set of elements are extends then target classType.
     * @param classType extended class type
     * @param <I>       element type (herintence from extend class type)
     * @return new set with elements are instanceof classType
     */
    public static <I> Set<I> filterAsSetE(Set<? extends I> set, Class<I> classType) {
        return filterAsSet(set, classType);
    }
    //endregion
    //endregion

    //region filter

    /**
     * Filter an iterator by condition
     *
     * @param iterator target
     * @param where    filter condition
     * @param <I>      element type
     * @return a new iterable result within elements filtered
     */
    public static <I> IterableResult<I> filter(Iterator<I> iterator, CompareEntryValid<I> where) {
        return IterableResultFactory.getInstanceForFilter(() -> iterator, where);
    }

    /**
     * Filter an iterable by condition
     *
     * @param col   target
     * @param where filter condition
     * @param <I>   element type
     * @return a new iterable result within elements filtered
     */
    public static <I> IterableResult<I> filter(Iterable<I> col, CompareEntryValid<I> where) {
        return IterableResultFactory.getInstanceForFilter(col::iterator, where);
    }

    /**
     * Filter an array by condition
     *
     * @param arr   target
     * @param where filter condition
     * @param <I>   element type
     * @return a new iterable result within elements filtered
     */
    public static <I> IterableResult<I> filter(I[] arr, CompareEntryValid<I> where) {
        return IterableResultFactory.getInstanceForFilterArray(arr, where);
    }
    //endregion

    //region nonNull

    /**
     * Filter an iterator recovering only non null elements.
     *
     * @param iterator target
     * @param <I>      element type
     * @return a new iterable result within elements filtered by non null condition.
     */
    public static <I> IterableResult<I> nonNull(Iterator<I> iterator) {
        return IterableResultFactory.getInstanceForFilter(() -> iterator, Objects::nonNull);
    }

    /**
     * Filter an collection recovering only non null elements.
     *
     * @param col target
     * @param <I> element type
     * @return a new iterable result within elements filtered by non null condition.
     */
    public static <I> IterableResult<I> nonNull(Iterable<I> col) {
        return IterableResultFactory.getInstanceForFilter(col::iterator, Objects::nonNull);
    }

    /**
     * Filter an array recovering only non null elements.
     *
     * @param arr target
     * @param <I> element type
     * @return a new iterable result within elements filtered by non null condition.
     */
    public static <I> IterableResult<I> nonNull(I[] arr) {
        return IterableResultFactory.getInstanceForFilterArray(arr, Objects::nonNull);
    }
    //endregion

    //region groupBy

    /**
     * Generate an iterable result grouping elements by groupfun key result.
     *
     * @param iterator target.
     * @param groupFun group function to get grouping key.
     * @param <K>      key type
     * @param <V>      value type
     * @return an instance of toMap whitin set values grouped by key.
     */
    public static <K, V> IterableResultGroup<K, V> groupBy(Iterator<V> iterator, FunctionMount<V, K> groupFun) {
        return IterableResultFactory.getInstanceForGroup(() -> iterator, groupFun);
    }

    /**
     * Generate an iterable result grouping elements by groupfun key result.
     *
     * @param col      target.
     * @param groupFun group function to get grouping key.
     * @param <K>      key type
     * @param <V>      value type
     * @return an instance of toMap whitin set values grouped by key.
     */
    public static <K, V> IterableResultGroup<K, V> groupBy(Iterable<V> col, FunctionMount<V, K> groupFun) {
        return IterableResultFactory.getInstanceForGroup(col::iterator, groupFun);
    }

    /**
     * Generate an iterable result grouping elements by groupfun key result.
     *
     * @param arr      target.
     * @param groupFun group function to get grouping key.
     * @param <K>      key type
     * @param <V>      value type
     * @return an instance of toMap whitin array values grouped by key.
     */
    public static <K, V> IterableResultGroup<K, V> groupBy(V[] arr, FunctionMount<V, K> groupFun) {
        return IterableResultFactory.getInstanceForGroupArray(arr, groupFun);
    }
    //endregion

    //region select

    /**
     * Generate an iterable result within a set of values recovered from mount function.
     *
     * @param iterator iterator target.
     * @param mount    mount function to get new data
     * @param <IN>     element type
     * @param <OUT>    element mounted from target iterator.
     * @return new iterable wihitin set of values from mount function.
     */
    public static <IN, OUT> IterableResult<OUT> select(Iterator<IN> iterator, FunctionMount<IN, OUT> mount) {
        return IterableResultFactory.getInstanceForSelect(() -> iterator, mount);
    }

    /**
     * Generate an iterable result within a set of values recovered from mount function.
     *
     * @param col   target
     * @param mount mount function to get new data
     * @param <IN>  element type
     * @param <OUT> element mounted from target iterator.
     * @return new iterable wihitin set of values from mount function.
     */
    public static <IN, OUT> IterableResult<OUT> select(Iterable<IN> col, FunctionMount<IN, OUT> mount) {
        return IterableResultFactory.getInstanceForSelect(col::iterator, mount);
    }

    /**
     * Generate an iterable result within a set of values recovered from mount function.
     *
     * @param arr   target
     * @param mount mount function to get new data
     * @param <IN>  element type
     * @param <OUT> element mounted from target iterator.
     * @return new iterable wihitin set of values from mount function.
     */
    public static <IN, OUT> IterableResult<OUT> select(IN[] arr, FunctionMount<IN, OUT> mount) {
        return IterableResultFactory.getInstanceForSelectArray(arr, mount);
    }
    //endregion

    //region merge

    /**
     * Union of two or more collections.
     *
     * @param curr current iterable base.
     * @param args one or more iterable whitin compatible elements with current iterable base.
     * @param <I>  iterable base type
     * @return new iterable result.
     */
    @SafeVarargs
    public static <I> IterableResult<I> merge(Iterable<I> curr, Iterable<? extends I>... args) {
        return IterableResultFactory.getInstanceForMerge(curr, args);
    }

    /**
     * Union of two or more collections.
     *
     * @param args one or more iterable.
     * @param <I>  iterable base type
     * @return new iterable result.
     */
    @SafeVarargs
    public static <I> IterableResult<I> merge(Iterable<? extends I>... args) {
        return IterableResultFactory.getInstanceForMerge(args);
    }

    /**
     * Union of two or more collections.
     *
     * @param arr  current array base.
     * @param args one or more iterable whitin compatible elements with current array base.
     * @param <I>  iterable base type
     * @return new iterable result.
     */
    @SafeVarargs
    public static <I> IterableResult<I> merge(I[] arr, Iterable<? extends I>... args) {
        return IterableResultFactory.getInstanceForMerge(arr, args);
    }

    /**
     * Union of two or more collections.
     *
     * @param args one or more arrays.
     * @param <I>  array base type
     * @return new iterable result.
     */
    @SafeVarargs
    public static <I> IterableResult<I> merge(I[]... args) {
        return IterableResultFactory.getInstanceForMergeArray(args);
    }

    /**
     * Union of two or more collections.
     *
     * @param iterable current iterable base.
     * @param args     one or more arrays whitin compatible elements with current iterable base.
     * @param <I>      iterable base type
     * @return new iterable result.
     */
    @SafeVarargs
    public static <I> IterableResult<I> merge(Iterable<I> iterable, I[]... args) {
        return IterableResultFactory.getInstanceForMergeArray(iterable, args);
    }
    //endregion

    //region intersection
    /**
     * Intersection of two or more collections.
     * @param args one or more iterable.
     * @param <I>  iterable base type
     * @return new iterable result.
     */
    @SafeVarargs
    public static <I> IterableResult<I> intersection(Iterable<I>... args) {
        return IterableResultFactory.getInstanceForIntersection(args);
    }

    /**
     * Intersection of two or more collections.
     * @param args one or more iterable.
     * @param <I>  iterable base type
     * @return new iterable result.
     */
    @SafeVarargs
    public static <I> IterableResult<I> intersection(I[]... args) {
        return IterableResultFactory.getInstanceForIntersectionArray(args);
    }
    //endregion

    //region reduce

    /**
     * Reduce method execute the reduceFun function to generate an accumulate result for each element on collection.
     *
     * @param iterator  target
     * @param reduceFun reduce function
     * @param acc       initial accumulate value, maybe null. When null first value of collection is the first accumulate.
     * @param <IN>      collection element type
     * @param <OUT>     accumulate type, indicate same IN type when accumulate start null.
     * @return final accumulate result.
     */
    public static <IN, OUT> OUT reduce(Iterator<IN> iterator, FunctionReduce<IN, OUT> reduceFun, OUT acc) {
        return IteratorForReduce.reduce(iterator, reduceFun, acc);
    }

    /**
     * Reduce method execute the reduceFun function to generate an accumulate result for each element on collection.
     * Starting accumulate null.
     *
     * @param iterator  target
     * @param reduceFun reduce function
     * @param <IN>      collection element type
     * @param <OUT>     accumulate type, indicate same IN type when accumulate start null.
     * @return final accumulate result.
     */
    public static <IN, OUT> OUT reduce(Iterator<IN> iterator, FunctionReduce<IN, OUT> reduceFun) {
        return IteratorForReduce.reduce(iterator, reduceFun, null);
    }

    /**
     * Reduce method execute the reduceFun function to generate an accumulate result for each element on collection.
     *
     * @param col       target
     * @param reduceFun reduce function
     * @param acc       initial accumulate value, maybe null. When null first value of collection is the first accumulate.
     * @param <IN>      collection element type
     * @param <OUT>     accumulate type, indicate same IN type when accumulate start null.
     * @return final accumulate result.
     */
    public static <IN, OUT> OUT reduce(Iterable<IN> col, FunctionReduce<IN, OUT> reduceFun, OUT acc) {
        return IteratorForReduce.reduce(col, reduceFun, acc);
    }

    /**
     * Reduce method execute the reduceFun function to generate an accumulate result for each element on collection.
     * Starting accumulate null.
     *
     * @param col       target
     * @param reduceFun reduce function
     * @param <IN>      collection element type
     * @param <OUT>     accumulate type, indicate same IN type when accumulate start null.
     * @return final accumulate result.
     */
    public static <IN, OUT> OUT reduce(Iterable<IN> col, FunctionReduce<IN, OUT> reduceFun) {
        return IteratorForReduce.reduce(col, reduceFun, null);
    }

    /**
     * Reduce method execute the reduceFun function to generate an accumulate result for each element on collection.
     *
     * @param arr       target
     * @param reduceFun reduce function
     * @param acc       initial accumulate value, maybe null. When null first value of collection is the first accumulate.
     * @param <IN>      collection element type
     * @param <OUT>     accumulate type, indicate same IN type when accumulate start null.
     * @return final accumulate result.
     */
    public static <IN, OUT> OUT reduce(IN[] arr, FunctionReduce<IN, OUT> reduceFun, OUT acc) {
        return IteratorForReduce.reduce(arr, reduceFun, acc);
    }

    /**
     * Reduce method execute the reduceFun function to generate an accumulate result for each element on collection.
     * Starting accumulate null.
     *
     * @param arr       target
     * @param reduceFun reduce function
     * @param <IN>      collection element type
     * @param <OUT>     accumulate type, indicate same IN type when accumulate start null.
     * @return final accumulate result.
     */
    public static <IN, OUT> OUT reduce(IN[] arr, FunctionReduce<IN, OUT> reduceFun) {
        return IteratorForReduce.reduce(arr, reduceFun, null);
    }
    //endregion

    //region math
    //region sum

    /**
     * Apply summation operation in a sequence of any kind of number.
     *
     * @param iterator target
     * @param fun      function to get a target number in element.
     * @param <OUT>    number type
     * @return summation result
     */
    public static <IN, OUT extends Number> OUT sum(Iterator<IN> iterator, FunctionMount<IN, OUT> fun) {
        return IteratorForMath.sum(iterator, fun);
    }

    /**
     * Apply summation operation in a sequence of any kind of number.
     *
     * @param col   target
     * @param fun   function to get a target number in element.
     * @param <OUT> number type
     * @return summation result
     */
    public static <IN, OUT extends Number> OUT sum(Iterable<IN> col, FunctionMount<IN, OUT> fun) {
        return IteratorForMath.sum(col.iterator(), fun);
    }

    /**
     * Apply summation operation in a sequence of any kind of number.
     *
     * @param arr   target
     * @param fun   function to get a target number in element.
     * @param <OUT> number type
     * @return summation result
     */

    public static <IN, OUT extends Number> OUT sum(IN[] arr, FunctionMount<IN, OUT> fun) {
        return IteratorForMath.sum(arr, fun);
    }

    /**
     * Apply summation operation in a sequence of any kind of number.
     *
     * @param iterator target
     * @return summation result
     */
    public static <IN extends Number> IN sum(Iterator<IN> iterator) {
        return IteratorForMath.sum(iterator);
    }

    /**
     * Apply summation operation in a sequence of any kind of number.
     *
     * @param col target
     * @return summation result
     */
    public static <IN extends Number> IN sum(Iterable<IN> col) {
        return IteratorForMath.sum(col.iterator());
    }

    /**
     * Apply summation operation in a sequence of any kind of number.
     *
     * @param arr target
     * @return summation result
     */
    public static <IN extends Number> IN sum(IN[] arr) {
        return IteratorForMath.sum(arr);
    }
    //endregion

    //region average

    /**
     * Average is defined as the sum of all the values divided by the total number of values in a given set.
     *
     * @param iterator target
     * @param fun      function to get target number
     * @param <IN>     element type
     * @param <OUT>    result type
     * @return result number
     */
    public static <IN, OUT extends Number> OUT average(Iterator<IN> iterator, FunctionMount<IN, OUT> fun) {
        return IteratorForMath.average(iterator, fun);
    }

    /**
     * Average is defined as the sum of all the values divided by the total number of values in a given set.
     *
     * @param col   target
     * @param fun   function to get target number
     * @param <IN>  element type
     * @param <OUT> result type
     * @return result number
     */
    public static <IN, OUT extends Number> OUT average(Iterable<IN> col, FunctionMount<IN, OUT> fun) {
        return IteratorForMath.average(col.iterator(), fun);
    }

    /**
     * Average is defined as the sum of all the values divided by the total number of values in a given set.
     *
     * @param arr   target
     * @param fun   function to get target number
     * @param <IN>  element type
     * @param <OUT> result type
     * @return result number
     */
    public static <IN, OUT extends Number> OUT average(IN[] arr, FunctionMount<IN, OUT> fun) {
        return IteratorForMath.average(arr, fun);
    }

    /**
     * Average is defined as the sum of all the values divided by the total number of values in a given set.
     *
     * @param iterator target
     * @param <IN>     element number type
     * @return result number
     */
    public static <IN extends Number> IN average(Iterator<IN> iterator) {
        return IteratorForMath.average(iterator);
    }

    /**
     * Average is defined as the sum of all the values divided by the total number of values in a given set.
     *
     * @param col  target
     * @param <IN> element number type
     * @return result number
     */
    public static <IN extends Number> IN average(Iterable<IN> col) {
        return IteratorForMath.average(col.iterator());
    }

    /**
     * Average is defined as the sum of all the values divided by the total number of values in a given set.
     *
     * @param arr  target
     * @param <IN> element number type
     * @return result number
     */
    public static <IN extends Number> IN average(IN[] arr) {
        return IteratorForMath.average(arr);
    }
    //endregion

    //region mean

    /**
     * A mean is a mathematical term, that describes the average of a sample.<br/>
     * In Statistics, the definition of the mean is similar to the average.<br/>
     * But, it can also be defined as the sum of the smallest value and the largest value in the given data set divided by 2.
     *
     * @param iterator target
     * @param fun function to get target number
     * @param <IN> collection element type
     * @param <OUT> result number type
     * @return result number
     */
    public static <IN, OUT extends Number> OUT mean(Iterator<IN> iterator, FunctionMount<IN, OUT> fun) {
        return IteratorForMath.mean(iterator, fun);
    }

    /**
     * A mean is a mathematical term, that describes the average of a sample.<br/>
     * In Statistics, the definition of the mean is similar to the average.<br/>
     * But, it can also be defined as the sum of the smallest value and the largest value in the given data set divided by 2.
     *
     * @param col target
     * @param fun function to get target number
     * @param <IN> collection element type
     * @param <OUT> result number type
     * @return result number
     */
    public static <IN, OUT extends Number> OUT mean(Iterable<IN> col, FunctionMount<IN, OUT> fun) {
        return IteratorForMath.mean(col.iterator(), fun);
    }

    /**
     * A mean is a mathematical term, that describes the average of a sample.<br/>
     * In Statistics, the definition of the mean is similar to the average.<br/>
     * But, it can also be defined as the sum of the smallest value and the largest value in the given data set divided by 2.
     *
     * @param arr target
     * @param fun function to get target number
     * @param <IN> collection element type
     * @param <OUT> result number type
     * @return result number
     */
    public static <IN, OUT extends Number> OUT mean(IN[] arr, FunctionMount<IN, OUT> fun) {
        return IteratorForMath.mean(arr, fun);
    }

    /**
     * A mean is a mathematical term, that describes the average of a sample.<br/>
     * In Statistics, the definition of the mean is similar to the average.<br/>
     * But, it can also be defined as the sum of the smallest value and the largest value in the given data set divided by 2.
     *
     * @param iterator target
     * @param <IN> collection element number type
     * @return result number
     */
    public static <IN extends Number> IN mean(Iterator<IN> iterator) {
        return IteratorForMath.mean(iterator);
    }

    /**
     * A mean is a mathematical term, that describes the average of a sample.<br/>
     * In Statistics, the definition of the mean is similar to the average.<br/>
     * But, it can also be defined as the sum of the smallest value and the largest value in the given data set divided by 2.
     *
     * @param col function to get target number
     * @param <IN> collection element number type
     * @return result number
     */
    public static <IN extends Number> IN mean(Iterable<IN> col) {
        return IteratorForMath.mean(col.iterator());
    }

    /**
     * A mean is a mathematical term, that describes the average of a sample.<br/>
     * In Statistics, the definition of the mean is similar to the average.<br/>
     * But, it can also be defined as the sum of the smallest value and the largest value in the given data set divided by 2.
     *
     * @param arr target
     * @param <IN> collection element number type
     * @return result number
     */
    public static <IN extends Number> IN mean(IN[] arr) {
        return IteratorForMath.mean(arr);
    }
    //endregion

    //region min

    /**
     * Recover minimum value of collection
     * @param iterator target
     * @param fun function to get Comparable element target
     * @param <IN> collection element
     * @param <OUT> result comparable element
     * @return minimal value
     */
    public static <IN, OUT extends Comparable<OUT>> OUT min(Iterator<IN> iterator, FunctionMount<IN, OUT> fun) {
        return IteratorForMath.min(iterator, fun);
    }

    /**
     * Recover minimum value of collection
     * @param <OUT> result comparable element
     * @return minimal value
     */
    public static <IN, OUT extends Comparable<OUT>> OUT min(Iterable<IN> col, FunctionMount<IN, OUT> fun) {
        return IteratorForMath.min(col.iterator(), fun);
    }

    /**
     * Recover minimum value of collection
     * @param arr target
     * @param fun function to get Comparable element target
     * @param <IN> collection element
     * @param <OUT> result comparable element
     * @return minimum value
     */
    public static <IN, OUT extends Comparable<OUT>> OUT min(IN[] arr, FunctionMount<IN, OUT> fun) {
        return IteratorForMath.min(arr, fun);
    }

    /**
     * Recover minimum value of collection
     * @param iterator target
     * @param <E> collection comparable element
     * @return minimum value
     */
    public static <E extends Comparable<E>> E min(Iterator<E> iterator) {
        return IteratorForMath.min(iterator);
    }

    /**
     * Recover minimum value of collection
     * @param col target
     * @param <E> collection comparable element
     * @return minimum value
     */
    public static <E extends Comparable<E>> E min(Iterable<E> col) {
        return IteratorForMath.min(col.iterator());
    }

    /**
     * Recover minimum value of collection
     * @param arr target
     * @param <E> collection comparable element
     * @return minimum value
     */
    public static <E extends Comparable<E>> E min(E[] arr) {
        return IteratorForMath.min(arr);
    }
    //endregion

    //region max

    /**
     * Recover maximum value of collection
     * @param iterator target
     * @param fun function to get Comparable element target
     * @param <IN> collection element
     * @param <OUT> result comparable element
     * @return maximum value
     */
    public static <IN, OUT extends Comparable<OUT>> OUT max(Iterator<IN> iterator, FunctionMount<IN, OUT> fun) {
        return IteratorForMath.max(iterator, fun);
    }

    /**
     * Recover maximum value of collection
     * @param col target
     * @param fun function to get Comparable element target
     * @param <IN> collection element
     * @param <OUT> result comparable element
     * @return maximum value
     */
    public static <IN, OUT extends Comparable<OUT>> OUT max(Iterable<IN> col, FunctionMount<IN, OUT> fun) {
        return IteratorForMath.max(col.iterator(), fun);
    }

    /**
     * Recover maximum value of collection
     * @param arr target
     * @param fun function to get Comparable element target
     * @param <IN> collection element
     * @param <OUT> result comparable element
     * @return maximum value
     */
    public static <IN, OUT extends Comparable<OUT>> OUT max(IN[] arr, FunctionMount<IN, OUT> fun) {
        return IteratorForMath.max(arr, fun);
    }

    /**
     * Recover maximum value of collection
     * @param iterator target
     * @param <E> collection comparable element
     * @return maximum value
     */
    public static <E extends Comparable<E>> E max(Iterator<E> iterator) {
        return IteratorForMath.max(iterator);
    }

    /**
     * Recover maximum value of collection
     * @param col target
     * @param <E> collection comparable element
     * @return maximum value
     */
    public static <E extends Comparable<E>> E max(Iterable<E> col) {
        return IteratorForMath.max(col.iterator());
    }

    /**
     * Recover maximum value of collection
     * @param arr target
     * @param <E> collection comparable element
     * @return maximum value
     */
    public static <E extends Comparable<E>> E max(E[] arr) {
        return IteratorForMath.max(arr);
    }
    //endregion
    //endregion

    //region distinct

    /**
     * Recover distinct (non duplicated) element of collection
     * @param iterator target
     * @param mount function to mount output distinct value
     * @param <OUT> output type
     * @return new iterable result with distinct elements.
     */
    public static <IN, OUT> IterableResult<OUT> distinct(Iterator<IN> iterator, FunctionMount<IN, OUT> mount) {
        return IterableResultFactory.getInstanceForDistinct(() -> iterator, mount);
    }

    /**
     * Recover distinct (non duplicated) element of collection
     * @param col target
     * @param mount function to mount output distinct value
     * @param <OUT> output type
     * @return new iterable result with distinct elements.
     */
    public static <IN, OUT> IterableResult<OUT> distinct(Iterable<IN> col, FunctionMount<IN, OUT> mount) {
        return IterableResultFactory.getInstanceForDistinct(col::iterator, mount);
    }

    /**
     * Recover distinct (non duplicated) element of collection
     * @param arr target
     * @param mount function to mount output distinct value
     * @param <OUT> output type
     * @return new iterable result with distinct elements.
     */
    public static <IN, OUT> IterableResult<OUT> distinct(IN[] arr, FunctionMount<IN, OUT> mount) {
        return IterableResultFactory.getInstanceForDistinctArray(arr, mount);
    }

    /**
     * Recover distinct (non duplicated) element of collection
     * @param iterator target
     * @return new iterable result with distinct elements.
     */
    public static <E> IterableResult<E> distinct(Iterator<E> iterator) {
        return distinct(iterator, i -> i);
    }

    /**
     * Recover distinct (non duplicated) element of collection
     * @param col target
     * @return new iterable result with distinct elements.
     */
    public static <E> IterableResult<E> distinct(Iterable<E> col) {
        return distinct(col, i -> i);
    }

    /**
     * Recover distinct (non duplicated) element of collection
     * @param arr target
     * @return new iterable result with distinct elements.
     */
    public static <E> IterableResult<E> distinct(E[] arr) {
        return distinct(arr, i -> i);
    }
    //endregion

    //region foreach

    /**
     *  A simple foreach action.
     * @param list target
     * @param action action to recover each element on collection
     * @param <I> collection element type
     */
    public static <I> void foreach(Iterable<I> list, ForEachEntryConsumer<I> action) {
        Objects.requireNonNull(list);
        Objects.requireNonNull(action);
        for (I e : list) action.accept(e);
    }

    /**
     *  A simple foreach action.
     * @param list target
     * @param action action with entry index to recover each element on collection
     * @param <I> collection element type
     */
    public static <I> void foreach(Iterable<I> list, ForEachIterableEntryConsumer<I> action) {
        Objects.requireNonNull(list);
        Objects.requireNonNull(action);
        int i = 0;
        for (I e : list) action.accept(e, i++);
    }

    /**
     *  A simple foreach action.
     * @param arr target
     * @param action action to recover each element on collection
     * @param <I> collection element type
     */
    public static <I> void foreach(I[] arr, ForEachEntryConsumer<I> action) {
        Objects.requireNonNull(arr);
        Objects.requireNonNull(action);
        for (I e : arr) action.accept(e);
    }

    /**
     *  A simple foreach action.
     * @param arr target
     * @param action action with entry index to recover each element on collection
     * @param <I> collection element type
     */
    public static <I> void foreach(I[] arr, ForEachIterableEntryConsumer<I> action) {
        Objects.requireNonNull(arr);
        Objects.requireNonNull(action);
        for (int i = 0, l = arr.length; i < l; i++) action.accept(arr[i], i);
    }
    //endregion

    //region all

    /**
     * Check if all elements on iterable pass on test action.
     * @param list target
     * @param action check pass action
     * @return return true when all elements on collection pass on test action.
     */
    public static <I> boolean all(Iterable<I> list, CompareEntryValid<I> action) {
        Objects.requireNonNull(list);
        Objects.requireNonNull(action);
        for (I e : list) {
            if (!action.isValid(e)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if all elements on iterable pass on test action.
     * @param arr target
     * @param action check pass action
     * @return return true when all elements on collection pass on test action.
     */
    public static <I> boolean all(I[] arr, CompareEntryValid<I> action) {
        Objects.requireNonNull(arr);
        Objects.requireNonNull(action);
        for (I e : arr) {
            if (!action.isValid(e)) {
                return false;
            }
        }
        return true;
    }
    //endregion

    //region any

    /**
     * Check if at least one element on iterable pass on test action.
     * @param list target
     * @param action check pass action
     * @return return true when at least one element on collection pass on test action.
     */
    public static <I> boolean any(Iterable<I> list, CompareEntryValid<I> action) {
        Objects.requireNonNull(list);
        Objects.requireNonNull(action);
        for (I e : list) {
            if (action.isValid(e)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if at least one element on iterable pass on test action.
     * @param arr target
     * @param action check pass action
     * @return return true when at least one element on collection pass on test action.
     */
    public static <I> boolean any(I[] arr, CompareEntryValid<I> action) {
        Objects.requireNonNull(arr);
        Objects.requireNonNull(action);
        for (I e : arr) {
            if (action.isValid(e)) {
                return true;
            }
        }
        return false;
    }
    //endregion

    //region jump

    /**
     * How like literally named, "jump" elements on collection
     * returning all others elements after offset count it.
     * @param set target
     * @param count count of elements will be discarted
     * @param <I> element type
     * @return new iterable result with non elements after offset count.
     */
    public static <I> IterableResult<I> jump(Iterable<I> set, int count) {
        return IterableResultFactory.getInstanceForJump(set::iterator, count);
    }

    /**
     * How like literally named, "jump" elements on collection
     * returning all others elements after offset count it.
     * @param iterator target
     * @param count count of elements will be discarted
     * @param <I> element type
     * @return new iterable result with non elements after offset count.
     */
    public static <I> IterableResult<I> jump(final Iterator<I> iterator, int count) {
        return IterableResultFactory.getInstanceForJump(() -> iterator, count);
    }
    //endregion

    //region take

    /**
     * Take only amount of elements set on count.
     * @param set target
     * @param count count of elements
     * @param <I> element type
     * @return new iterable result with taked elements.
     */
    public static <I> IterableResult<I> take(Iterable<I> set, int count) {
        return IterableResultFactory.getInstanceForTake(set::iterator, count);
    }

    /**
     * Take only amount of elements set on count.
     * @param iterator target
     * @param count count of elements
     * @param <I> element type
     * @return new iterable result with taked elements.
     */

    public static <I> IterableResult<I> take(final Iterator<I> iterator, int count) {
        return IterableResultFactory.getInstanceForTake(() -> iterator, count);
    }
    //endregion

    //region count

    /**
     * Count of elements on iterable.
     * @param set target
     * @return total elements on iterable.
     */
    public static int count(Iterable<?> set) {
        if (set instanceof Collection) {
            return ((Collection<?>) set).size();
        } else if (set instanceof Map) {
            return ((Map) set).size();
        } else {
            int c = 0;
            for (Object i : set) c++;
            return c;
        }
    }
    //endregion

    //region empty
    private static <I> Iterable<I> emptyIterable() {
        return () -> new Iterator<I>() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public I next() {
                throw new NoSuchElementException();
            }
        };
    }
    //endregion

    //region for primitives
    //region foreach
    public static void foreach(char[] arr, ForEachEntryConsumer<Character> action) {
        Objects.requireNonNull(arr);
        Objects.requireNonNull(action);
        for (char e : arr) action.accept(e);
    }

    public static void foreach(char[] arr, ForEachIterableEntryConsumer<Character> action) {
        Objects.requireNonNull(arr);
        Objects.requireNonNull(action);
        for (int i = 0, l = arr.length; i < l; i++) {
            action.accept(arr[i], i);
        }
    }

    public static void foreach(int[] arr, ForEachEntryConsumer<Integer> action) {
        Objects.requireNonNull(arr);
        Objects.requireNonNull(action);
        for (int e : arr) action.accept(e);
    }

    public static void foreach(int[] arr, ForEachIterableEntryConsumer<Integer> action) {
        Objects.requireNonNull(arr);
        Objects.requireNonNull(action);
        for (int i = 0, l = arr.length; i < l; i++) {
            action.accept(arr[i], i);
        }
    }

    public static void foreach(long[] arr, ForEachEntryConsumer<Long> action) {
        Objects.requireNonNull(arr);
        Objects.requireNonNull(action);
        for (long e : arr) action.accept(e);
    }

    public static void foreach(long[] arr, ForEachIterableEntryConsumer<Long> action) {
        Objects.requireNonNull(arr);
        Objects.requireNonNull(action);
        for (int i = 0, l = arr.length; i < l; i++) {
            action.accept(arr[i], i);
        }
    }

    public static void foreach(double[] arr, ForEachEntryConsumer<Double> action) {
        Objects.requireNonNull(arr);
        Objects.requireNonNull(action);
        for (double e : arr) action.accept(e);
    }

    public static void foreach(double[] arr, ForEachIterableEntryConsumer<Double> action) {
        Objects.requireNonNull(arr);
        Objects.requireNonNull(action);
        for (int i = 0, l = arr.length; i < l; i++) {
            action.accept(arr[i], i);
        }
    }

    public static void foreach(boolean[] arr, ForEachEntryConsumer<Boolean> action) {
        Objects.requireNonNull(arr);
        Objects.requireNonNull(action);
        for (boolean e : arr) action.accept(e);
    }

    public static void foreach(boolean[] arr, ForEachIterableEntryConsumer<Boolean> action) {
        Objects.requireNonNull(arr);
        Objects.requireNonNull(action);
        for (int i = 0, l = arr.length; i < l; i++) {
            action.accept(arr[i], i);
        }
    }

    public static void foreach(byte[] arr, ForEachEntryConsumer<Byte> action) {
        Objects.requireNonNull(arr);
        Objects.requireNonNull(action);
        for (byte e : arr) action.accept(e);
    }

    public static void foreach(byte[] arr, ForEachIterableEntryConsumer<Byte> action) {
        Objects.requireNonNull(arr);
        Objects.requireNonNull(action);
        for (int i = 0, l = arr.length; i < l; i++) {
            action.accept(arr[i], i);
        }
    }
    //endregion

    //region all
    public static boolean all(char[] arr, CompareEntryValid<Character> action) {
        Objects.requireNonNull(arr);
        Objects.requireNonNull(action);
        for (char e : arr) {
            if (!action.isValid(e)) {
                return false;
            }
        }
        return true;
    }

    public static boolean all(int[] arr, CompareEntryValid<Integer> action) {
        Objects.requireNonNull(arr);
        Objects.requireNonNull(action);
        for (int e : arr) {
            if (!action.isValid(e)) {
                return false;
            }
        }
        return true;
    }

    public static boolean all(long[] arr, CompareEntryValid<Long> action) {
        Objects.requireNonNull(arr);
        Objects.requireNonNull(action);
        for (long e : arr) {
            if (!action.isValid(e)) {
                return false;
            }
        }
        return true;
    }

    public static boolean all(double[] arr, CompareEntryValid<Double> action) {
        Objects.requireNonNull(arr);
        Objects.requireNonNull(action);
        for (double e : arr) {
            if (!action.isValid(e)) {
                return false;
            }
        }
        return true;
    }

    public static boolean all(boolean[] arr, CompareEntryValid<Boolean> action) {
        Objects.requireNonNull(arr);
        Objects.requireNonNull(action);
        for (boolean e : arr) {
            if (!action.isValid(e)) {
                return false;
            }
        }
        return true;
    }

    public static boolean all(byte[] arr, CompareEntryValid<Byte> action) {
        Objects.requireNonNull(arr);
        Objects.requireNonNull(action);
        for (byte e : arr) {
            if (!action.isValid(e)) {
                return false;
            }
        }
        return true;
    }
    //endregion

    //region any
    @SuppressWarnings("unchecked")
    private static <N> boolean any(Object arr, int len, CompareEntryValid<N> action, FunctionMount<Object, N> mountFun) {
        Objects.requireNonNull(arr);
        Objects.requireNonNull(action);
        for (int i = 0; i < len; i++) {
            Object obj = Array.get(arr, i);
            if (action.isValid(mountFun.mount(obj))) {
                return true;
            }
        }
        return false;
    }

    public static boolean any(char[] arr, CompareEntryValid<Character> action) {
        return any(arr, arr.length, action, e -> (char) e);
    }

    public static boolean any(int[] arr, CompareEntryValid<Integer> action) {
        return any(arr, arr.length, action, e -> (int) e);
    }

    public static boolean any(long[] arr, CompareEntryValid<Long> action) {
        return any(arr, arr.length, action, e -> (long) e);
    }

    public static boolean any(double[] arr, CompareEntryValid<Double> action) {
        return any(arr, arr.length, action, e -> (double) e);
    }

    public static boolean any(boolean[] arr, CompareEntryValid<Boolean> action) {
        return any(arr, arr.length, action, e -> (boolean) e);
    }

    public static boolean any(byte[] arr, CompareEntryValid<Byte> action) {
        return any(arr, arr.length, action, e -> (byte) e);
    }
    //endregion
    //endregion
}
