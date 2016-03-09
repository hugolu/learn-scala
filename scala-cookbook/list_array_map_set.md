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

Use a `ListBuffer`, and convert the `ListBuffer` to a `List` when needed.

```scala
val fruits = new scala.collection.mutable.ListBuffer[String]()
                                                //> fruits  : scala.collection.mutable.ListBuffer[String] = ListBuffer()
fruits += "apple"                               //> res0: myTest.test86.fruits.type = ListBuffer(apple)
fruits += "banana"                              //> res1: myTest.test86.fruits.type = ListBuffer(apple, banana)
fruits += "coconut"                             //> res2: myTest.test86.fruits.type = ListBuffer(apple, banana, coconut)
```
- A `ListBuffer` is a Buffer implementation backed by a list. It provides constant time prepend and append. Most other operations are **linear**.
- Don’t use `ListBuffer` if you want to access elements arbitrarily, such as accessing items by index (like list(10000)); use `ArrayBuffer` instead.

## Adding Elements to a List

- A List is immutable, so you can’t actually add elements to it.
- To work with a List, the general approach is to prepend items to the list while assigning the results to a new List.

```scala
scala> val x = 3 :: Nil
x: List[Int] = List(3)

scala> val y = 2 :: x
y: List[Int] = List(2, 3)

scala> val z = 1 :: y
z: List[Int] = List(1, 2, 3)
```

```scala
scala> var x = List[Int]()
x: List[Int] = List()

scala> x = 3 :: x
x: List[Int] = List(3)

scala> x = 2 :: x
x: List[Int] = List(2, 3)

scala> x = 1 :: x
x: List[Int] = List(1, 2, 3)
```

```scala
scala> var x = List(3)
x: List[Int] = List(3)

scala> x = 2 +: x
x: List[Int] = List(2, 3)

scala> x = 1 +: x
x: List[Int] = List(1, 2, 3)

scala> x.head
res0: Int = 1

scala> x = x.tail
x: List[Int] = List(2, 3)
```
- List 具有 FILO 的特性

## Deleting Elements from a List (or ListBuffer)

### List
A `List` is immutable, so you can’t delete elements from it, but you can filter out the elements you don’t want while you assign the result to a new variable.

```scala
scala> val x = List.range(0,10)
x: List[Int] = List(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)

scala> val y = x.filter(_ % 2 == 0)
y: List[Int] = List(0, 2, 4, 6, 8)
```

### ListBuffer
If you’re going to be modifying a list frequently, it may be better to use a `ListBuffer` instead of a List.

```scala
scala> val x = scala.collection.mutable.ListBuffer((1 to 10): _*)
x: scala.collection.mutable.ListBuffer[Int] = ListBuffer(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

scala> x -= 5
res55: x.type = ListBuffer(1, 2, 3, 4, 6, 7, 8, 9, 10)

scala> x -= (3, 4)
res56: x.type = ListBuffer(1, 2, 6, 7, 8, 9, 10)

scala> x.remove(0)
res57: Int = 1

scala> x
res58: scala.collection.mutable.ListBuffer[Int] = ListBuffer(2, 6, 7, 8, 9, 10)

scala> x.remove(1,3)

scala> x
res60: scala.collection.mutable.ListBuffer[Int] = ListBuffer(2, 9, 10)

scala> x --= Seq(9, 10)
res61: x.type = ListBuffer(2)
```
- use `-` to delete one element at a time
- use `--` to delete two or more elements at once

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
