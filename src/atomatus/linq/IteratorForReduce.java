package atomatus.linq;

import java.util.Iterator;
import java.util.Objects;

final class IteratorForReduce {

    @SuppressWarnings({"unchecked", "CatchMayIgnoreException"})
    static <IN, OUT> OUT reduce(Iterator<IN> iterator,
                                CollectionHelper.FunctionReduce<IN, OUT> reduceFun,
                                OUT acc) {
        Objects.requireNonNull(iterator);
        Objects.requireNonNull(reduceFun);
        boolean first = acc == null;
        while (iterator.hasNext()) {
            IN next = iterator.next();
            if(first) {
                first = false;
                try {
                    acc = (OUT) next;
                    continue;
                }catch (ClassCastException ex) { }
            }
            acc = reduceFun.reduce(acc, next);
        }
        return acc;
    }

    static <IN, OUT> OUT reduce(Iterable<IN> iterable,
                                CollectionHelper.FunctionReduce<IN, OUT> reduceFun,
                                OUT acc) {
        return reduce(Objects.requireNonNull(iterable).iterator(), reduceFun, acc);
    }

    @SuppressWarnings({"unchecked", "CatchMayIgnoreException"})
    static <IN, OUT> OUT reduce(IN[] arr, CollectionHelper.FunctionReduce<IN, OUT> reduceFun, OUT acc) {
        Objects.requireNonNull(arr);
        Objects.requireNonNull(reduceFun);
        boolean first = acc == null;
        for (IN i : arr) {
            if(first) {
                first = false;
                try {
                    acc = (OUT) i;
                    continue;
                }catch (ClassCastException ex) { }
            }
            acc = reduceFun.reduce(acc, i);
        }
        return acc;
    }
}
