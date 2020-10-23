# Java Collection Linq Project

A library to helper developer manipulate iterables collections or arrays, 
how like, linq principle to select, find, filter, group and manipulate elements 
on theses set. You can generate map groups to help data analyses, 
checking elements by conditions on set, filtering data or applying simple math 
operations.

## Iterable Result
All request generate a type of iterable result, each iterable result is not result of execution operation in time, 
but a scheduled of execution thats will be applied only when iterator is request, forced by method iterator(), or 
iteration in loop.

### IterableResult
Simple iterable result contained methods to schedule new manipulate action on future elements result.

### IterableResultGroup
Iterable result for elements grouped, each group contains the proposed key and set of elements how values of each key. 

### IterableResultMap
Iterable result map thats is base class of iterableResultGroup, contains methods
to filter and manipulate like a dictionary.

### CollectionHelper
<p>
Set of static methods to help for query actions (from any iterable, set or array) to
manipulate, filter, find, group elements on set or array fastestway for reducing code.
</p>
<p>
Every query action result generate an IterableResult, IterableResultGroup or IterableResultMap,
all thus are tree schedules Iterator actions that will be mounted and executed only when do a directed request it.
</p>

```
//Samples
Integer[] arr0 = new Integer[]{0, 2, 4, 6, 8};
Integer[] arr1 = new Integer[]{2, 3, 5, 7, 9};
```

```
System.out.println("\nMerge:");
IterableResult<Integer> result = CollectionHelper.merge(arr0, arr1);
result.foreach(System.out::println);
```
``
Merge: 
0
2
4
6
8
2
3
5
7
9
``

```
System.out.println("\nDistinct:");
result = result.distinct();
result.foreach(System.out::println);
```
``
Distinct:
0
2
4
6
8
3
5
7
9
``

```
System.out.println("\nIntersection:");
CollectionHelper
    .intersection(arr0, arr1)
    .foreach(System.out::println);
```
``
Intersection:
2
``

```
System.out.println("\nGroup by (grouping values pair and odd):");
result.groupBy(e -> e % 2)
      .foreach(System.out::println);
```

``Group by (grouping values pair and odd):``
<br/>
``0=[0, 2, 4, 6, 8]``
<br/>
``1=[3, 5, 7, 9]``