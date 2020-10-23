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

