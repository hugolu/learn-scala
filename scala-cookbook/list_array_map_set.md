# List, Array, Map, Set (and More)

List: 
- It’s implemented as a linked list.
- The Scala List class is immutable, so its size as well as the elements it refers to can’t change.

Array:
- The class is mutable in that its elements can be changed, but once the size of an Array is set, it can never grow or shrink.

Map:
- A Scala Map is a collection of key/value pairs, like a Java Map, Ruby Hash, or Python dictionary. 

Set:
- It’s a collection that contains only unique elements.
- If you attempt to add duplicate elements to a set, the set silently ignores the request.

## Different Ways to Create and Populate a List

```scala
1 :: 2 :: 3 :: Nil                              //> res0: List[Int] = List(1, 2, 3)
List(1, 2, 3)                                   //> res1: List[Int] = List(1, 2, 3)
List(1, 2.0, 3L)                                //> res2: List[Double] = List(1.0, 2.0, 3.0)
List[Number](1, 2.0, 3L)                        //> res3: List[Number] = List(1, 2.0, 3)
List.range(1, 4)                                //> res4: List[Int] = List(1, 2, 3)
List.range(1, 10, 2)                            //> res5: List[Int] = List(1, 3, 5, 7, 9)
List.fill(3)("foo")                             //> res6: List[String] = List(foo, foo, foo)
List.tabulate(3)(n => n * n)                    //> res7: List[Int] = List(0, 1, 4)
collection.mutable.ListBuffer(1, 2, 3).toList   //> res8: List[Int] = List(1, 2, 3)
"hello".toList                                  //> res9: List[Char] = List(h, e, l, l, o)
```
- the `::` method (called cons) takes two arguments: a `head` element, which is a single element, and a `tail`, which is another List.

## Creating a Mutable List

## Adding Elements to a List

## Deleting Elements from a List (or ListBuffer)

## Merging (Concatenating) Lists

## Using Stream, a Lazy Version of a List

## Different Ways to Create and Update an Array

## Creating an Array Whose Size Can Change (ArrayBuffer)

## Deleting Array and ArrayBuffer Elements

## Sorting Arrays

## Creating Multidimensional Arrays

## Creating Maps

## Choosing a Map Implementation

## Adding, Updating, and Removing Elements with a Mutable Map

## Adding, Updating, and Removing Elements with Immutable Maps

## Accessing Map Values

## Traversing a Map

## Getting the Keys or Values from a Map

## Reversing Keys and Values

## Testing for the Existence of a Key or Value in a Map

## Filtering a Map

## Sorting an Existing Map by Key or Value

## Finding the Largest Key or Value in a Map

## Adding Elements to a Set

## Deleting Elements from Sets

## Using Sortable Sets

## Using a Queue

## Using a Stack

## Using a Range 
