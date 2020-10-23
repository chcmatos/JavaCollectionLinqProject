package atomatus.linq;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

final class IteratorForSelectArray<IN, OUT> implements Iterator<OUT> {

    private final IN[] arr;
    private final CollectionHelper.FunctionMount<IN, OUT> mount;

    private OUT next;
    private int index;

    IteratorForSelectArray(IN[] arr, CollectionHelper.FunctionMount<IN, OUT> mount) {
        this.arr = Objects.requireNonNull(arr);
        this.mount = Objects.requireNonNull(mount);
    }

    @Override
    public boolean hasNext() {
        return index < arr.length;
    }

    @Override
    public OUT next() {
        if(index == arr.length){
            throw new NoSuchElementException();
        }
        return mount.mount(arr[index++]);
    }
}
