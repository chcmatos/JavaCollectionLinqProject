package atomatus;

import atomatus.linq.CollectionHelper;
import atomatus.linq.IterableResult;

import java.util.Arrays;

public class Main {

    public static void main(String[] args) {

        Integer[] arr0 = new Integer[]{0, 2, 4, 6, 8};
        Integer[] arr1 = new Integer[]{2, 3, 5, 7, 9};

        System.out.println("Java Collection Linq:");
        System.out.println("\nSample array (arr0):");
        System.out.println(Arrays.toString(arr0));
        System.out.println("\nSample array (arr1):");
        System.out.println(Arrays.toString(arr1));

        System.out.println("\nMerge:");
        IterableResult<Integer> result = CollectionHelper.merge(arr0, arr1);
        result.foreach(System.out::println);

        System.out.println("\nDistinct:");
        (result = result.distinct())
                .foreach(System.out::println);

        System.out.println("\nIntersection:");
        CollectionHelper
                .intersection(arr0, arr1)
                .foreach(System.out::println);

        System.out.println("\nGroup by (grouping values pair and odd):");
        result.groupBy(e -> e % 2)
                .foreach(System.out::println);

        Integer i = CollectionHelper.sum(arr0);
        System.out.printf("\nSum (all values in arr0):\n%d\n", i);

        i = CollectionHelper.sum(arr1);
        System.out.printf("\nSum (all values in arr1):\n%d\n", i);

        i = CollectionHelper.min(arr0);
        System.out.printf("\nMin (all values in arr0):\n%d\n", i);

        i = CollectionHelper.min(arr1);
        System.out.printf("\nMin (all values in arr1):\n%d\n", i);

        i = CollectionHelper.max(arr0);
        System.out.printf("\nMax (all values in arr0):\n%d\n", i);

        i = CollectionHelper.max(arr1);
        System.out.printf("\nMax (all values in arr1):\n%d\n", i);

        i = CollectionHelper.mean(arr0);
        System.out.printf("\nMean (all values in arr0):\n%d\n", i);

        i = CollectionHelper.mean(arr1);
        System.out.printf("\nMean (all values in arr1):\n%d\n", i);

        System.out.println("\nAny (value equals 2):");
        System.out.println(CollectionHelper.any(arr0, e -> e == 2));

        System.out.println("\nAll (values pair in arr0):");
        System.out.println(CollectionHelper.all(arr0, e -> e % 2 == 0));

        System.out.println("\nAll (values pair in arr1):");
        System.out.println(CollectionHelper.all(arr1, e -> e % 2 == 0));
    }
}
