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

```scala
scala> val a = List(1,2,3)
a: List[Int] = List(1, 2, 3)

scala> val b = List(4,5,6)
b: List[Int] = List(4, 5, 6)

scala> val c = a ++ b
c: List[Int] = List(1, 2, 3, 4, 5, 6)

scala> val c = a ::: b
c: List[Int] = List(1, 2, 3, 4, 5, 6)

scala> val c = List.concat(a, b)
c: List[Int] = List(1, 2, 3, 4, 5, 6)
```

## Using Stream, a Lazy Version of a List

- A `Stream` is like a `List`, except that its elements are computed lazily, in a manner similar to how a view creates a lazy version of a collection.
  - Like a `view`, only the elements that are accessed are computed.
  - Other than this behavior, a Stream behaves similar to a `List`.

```scala
scala> val stream = 1 #:: 2 #:: 3 #:: Stream.empty
stream: scala.collection.immutable.Stream[Int] = Stream(1, ?)

scala> val stream = (1 to 1000000).toStream
stream: scala.collection.immutable.Stream[Int] = Stream(1, ?)
```
- a `List` can be constructed with `::`
- a `Stream` can be constructed with `#::`
- the stream begins with the number `1` but uses a `?` to denote the end of the stream. This is because the end of the stream hasn’t been evaluated yet.

```scala
scala> stream.head
res64: Int = 1

scala> stream.tail
res65: scala.collection.immutable.Stream[Int] = Stream(2, ?)

scala> stream
res66: scala.collection.immutable.Stream[Int] = Stream(1, 2, ?)
```
- The `?` symbol is the way a lazy collection shows that the end of the collection hasn’t been evaluated yet.

Calls to the following **strict** methods are evaluated immediately and can easily cause java.lang.OutOfMemoryError errors:
```scala
stream.max
stream.size
stream.sum
```

## Different Ways to Create and Update an Array

```scala
Array(1, 2, 3)                                  //> res0: Array[Int] = Array(1, 2, 3)
Array(1, 2.0, 3L)                               //> res1: Array[Double] = Array(1.0, 2.0, 3.0)
Array[Number](1, 2.0, 3L)                       //> res2: Array[Number] = Array(1, 2.0, 3)

val fruits = new Array[String](3)               //> fruits  : Array[String] = Array(null, null, null)
fruits(0) = "apple"
fruits(1) = "banana"
fruits(2) = "coconut"
fruits                                          //> res3: Array[String] = Array(apple, banana, coconut)

var array: Array[String] = null                 //> array  : Array[String] = null
array = fruits

Array.range(1, 10)                              //> res4: Array[Int] = Array(1, 2, 3, 4, 5, 6, 7, 8, 9)
Array.range(1, 10, 2)                           //> res5: Array[Int] = Array(1, 3, 5, 7, 9)
Array.fill(3)("foo")                            //> res6: Array[String] = Array(foo, foo, foo)
Array.tabulate(3)(n => n * n)                   //> res7: Array[Int] = Array(0, 1, 4)
Seq(1, 2, 3).toArray                            //> res8: Array[Int] = Array(1, 2, 3)
"hello".toArray                                 //> res9: Array[Char] = Array(h, e, l, l, o)
```
- The `Array` is  **mutable** in that its *elements* can be changed, but it’s **immutable** in that its *size* cannot be changed.

## Creating an Array Whose Size Can Change (ArrayBuffer)
- An Array is mutable in that its elements can change, but its size can’t change.
- To create a mutable, indexed sequence whose size can change, use the ArrayBuffer class.
 
```scala
val fruits = new scala.collection.mutable.ArrayBuffer[String]()
                                                //> fruits  : scala.collection.mutable.ArrayBuffer[String] = ArrayBuffer()
fruits += "apple"                               //> res0: myTest.test86.fruits.type = ArrayBuffer(apple)
fruits += "banana"                              //> res1: myTest.test86.fruits.type = ArrayBuffer(apple, banana)
fruits += "coconut"                             //> res2: myTest.test86.fruits.type = ArrayBuffer(apple, banana, coconut)

import scala.collection.mutable.ArrayBuffer
val nums = ArrayBuffer[Int](0)                  //> nums  : scala.collection.mutable.ArrayBuffer[Int] = ArrayBuffer(0)
nums += 1                                       //> res3: myTest.test86.nums.type = ArrayBuffer(0, 1)
nums += (2, 3)                                  //> res4: myTest.test86.nums.type = ArrayBuffer(0, 1, 2, 3)
nums ++= Seq(4, 5)                              //> res5: myTest.test86.nums.type = ArrayBuffer(0, 1, 2, 3, 4, 5)
nums.append(6)
nums                                            //> res6: scala.collection.mutable.ArrayBuffer[Int] = ArrayBuffer(0, 1, 2, 3, 4, 5, 6)
```

## Deleting Array and ArrayBuffer Elements
An `ArrayBuffer` is a mutable sequence, so you can delete elements with the usual `-=`, `--=`, `remove`, and `clear` methods.

```scala
import scala.collection.mutable.ArrayBuffer
val nums = ArrayBuffer(1, 2, 3, 4, 5, 6, 7, 8, 9)
                                                //> nums  : scala.collection.mutable.ArrayBuffer[Int] = ArrayBuffer(1, 2, 3, 4, 5, 6, 7, 8, 9)

nums -= 1                                       //> res0: myTest.test86.nums.type = ArrayBuffer(2, 3, 4, 5, 6, 7, 8, 9)
nums -= (2, 3)                                  //> res1: myTest.test86.nums.type = ArrayBuffer(4, 5, 6, 7, 8, 9)

nums.remove(0)                                  //> res2: Int = 4
nums                                            //> res3: scala.collection.mutable.ArrayBuffer[Int] = ArrayBuffer(5, 6, 7, 8, 9)

nums.remove(1, 3)
nums                                            //> res4: scala.collection.mutable.ArrayBuffer[Int] = ArrayBuffer(5, 9)

nums.clear
nums                                            //> res5: scala.collection.mutable.ArrayBuffer[Int] = ArrayBuffer()
```

```scala
import scala.collection.mutable.ArrayBuffer
val nums = ArrayBuffer(1, 2, 3, 4)              //> nums  : scala.collection.mutable.ArrayBuffer[Int] = ArrayBuffer(1, 2, 3, 4)

nums.filter(_ % 2 == 0)                         //> res0: scala.collection.mutable.ArrayBuffer[Int] = ArrayBuffer(2, 4)
nums                                            //> res1: scala.collection.mutable.ArrayBuffer[Int] = ArrayBuffer(1, 2, 3, 4)

nums.filterNot(_ % 2 == 0)                      //> res2: scala.collection.mutable.ArrayBuffer[Int] = ArrayBuffer(1, 3)
nums                                            //> res3: scala.collection.mutable.ArrayBuffer[Int] = ArrayBuffer(1, 2, 3, 4)

nums.take(2)                                    //> res4: scala.collection.mutable.ArrayBuffer[Int] = ArrayBuffer(1, 2)
nums                                            //> res5: scala.collection.mutable.ArrayBuffer[Int] = ArrayBuffer(1, 2, 3, 4)

nums.drop(2)                                    //> res6: scala.collection.mutable.ArrayBuffer[Int] = ArrayBuffer(3, 4)
nums                                            //> res7: scala.collection.mutable.ArrayBuffer[Int] = ArrayBuffer(1, 2, 3, 4)
```

## Sorting Arrays
If you’re working with an `Array` that holds elements that have an implicit `Ordering`, you can sort the Array in place using the `scala.util.Sorting.quickSort` method

```scala
val fruits = Array("cherry", "apple", "banana") //> fruits  : Array[String] = Array(cherry, apple, banana)
scala.util.Sorting.quickSort(fruits)
fruits                                          //> res0: Array[String] = Array(apple, banana, cherry)
```

If the type an `Array` is holding doesn’t have an implicit `Ordering`, you can either modify it to mix in the `Ordered` trait (which gives it an implicit Ordering), or sort it using the `sorted`, `sortWith`, or `sortBy` methods.

```scala
case class Foo(val n: Int) extends Ordered[Foo] {
  def compare(that: Foo) = this.n - that.n
}

val foos = Array(Foo(1), Foo(3), Foo(2))        //> foos  : Array[myTest.test86.Foo] = Array(Foo(1), Foo(3), Foo(2))
scala.util.Sorting.quickSort(foos)
foos                                            //> res0: Array[myTest.test86.Foo] = Array(Foo(1), Foo(2), Foo(3))
```

```scala
val nums = Array(1,3,2)                         //> nums  : Array[Int] = Array(1, 3, 2)
nums.sorted                                     //> res1: Array[Int] = Array(1, 2, 3)
nums                                            //> res2: Array[Int] = Array(1, 3, 2)
```

```scala
case class Bar(n: Int)
val bars = Array(Bar(1), Bar(3), Bar(2))        //> bars  : Array[myTest.test86.Bar] = Array(Bar(1), Bar(3), Bar(2))

bars.sortWith((x, y) => x.n < y.n)              //> res1: Array[myTest.test86.Bar] = Array(Bar(1), Bar(2), Bar(3))
bars.sortBy(x => x.n)                           //> res2: Array[myTest.test86.Bar] = Array(Bar(1), Bar(2), Bar(3))
```

## Creating Multidimensional Arrays

```scala
scala> val a = Array.ofDim[Int](2, 3)
a: Array[Array[Int]] = Array(Array(0, 0, 0), Array(0, 0, 0))

scala> for {
     | i <- 0 until 2
     | j <- 0 until 3 }
     | a(i)(j) = i + j

scala> a
res4: Array[Array[Int]] = Array(Array(0, 1, 2), Array(1, 2, 3))
```

```scala
scala> val a = Array(Array("a", "b", "c"), Array("d", "e"))
a: Array[Array[String]] = Array(Array(a, b, c), Array(d, e))

scala> for {
     | i <- 0 until a.size
     | j <- 0 until a(i).size }
     | println(s"a($i)($j)=${a(i)(j)}")
a(0)(0)=a
a(0)(1)=b
a(0)(2)=c
a(1)(0)=d
a(1)(1)=e
```
- Creating an array whose elements are arrays gives you more control of the process, and lets you create “ragged” arrays (where each contained array may be a different size).

## Creating Maps

To use an immutable map, you don’t need an import statement, just create a `Map`:

```scala
val states = Map("AL" -> "Alabama", "AK" -> "Alaska")
                                                //> states  : scala.collection.immutable.Map[String,String] = Map(AL -> Alabama, AK -> Alaska)

val key = "AL"                                  //> key  : String = AL
val value = states(key)                         //> value  : String = Alabama
```

To create a mutable map, either use an import statement to bring it into scope, or specify the full path to the `scala.collection.mutable.Map` class when you create an instance:

```scala
var states = collection.mutable.Map("AL" -> "Alabama")
                                                //> states  : scala.collection.mutable.Map[String,String] = Map(AL -> Alabama)
states += ("AK" -> "Alaska")                    //> res0: scala.collection.mutable.Map[String,String] = Map(AL -> Alabama, AK -> Alaska)
```

```scala
var states = collection.mutable.Map[String, String]()
                                                //> states  : scala.collection.mutable.Map[String,String] = Map()
states += ("AL" -> "Alabama")                   //> res0: scala.collection.mutable.Map[String,String] = Map(AL -> Alabama)
```

```scala
import scala.collection.mutable.{ Map => MMap }

val m = MMap(1 -> 'a')                          //> m  : scala.collection.mutable.Map[Int,Char] = Map(1 -> a)
m += (2 -> 'b')                                 //> res0: myTest.test86.m.type = Map(2 -> b, 1 -> a)
```
- to give the mutable Map an alias when you **import** it, and then refer to it using that alias

## Choosing a Map Implementation

### `collection.immutable.Map`
This is the default, general-purpose immutable map you get if you don’t import anything.

```scala
val m = Map(1 -> 'a', 2 -> 'b', 3 -> 'c')       //> m  : scala.collection.immutable.Map[Int,Char] = Map(1 -> a, 2 -> b, 3 -> c)
```

### `collection.mutable.Map`
A mutable version of the basic map.

```scala
import scala.collection.mutable.{ Map => MMap }
val m = MMap(1 -> 'a', 2 -> 'b', 3 -> 'c')      //> m  : scala.collection.mutable.Map[Int,Char] = Map(2 -> b, 1 -> a, 3 -> c)
```

### `collection.mutable.LinkedHashMap`
All methods that traverse the elements will visit the elements in their **insertion order**.

```scala
import scala.collection.mutable.{ LinkedHashMap => LHMap }
val m = LHMap(3 -> 'c', 2 -> 'b', 1 -> 'a')     //> m  : scala.collection.mutable.LinkedHashMap[Int,Char] = Map(3 -> c, 2 -> b, 1 -> a)
```

### `collection.immutable.ListMap` & `collection.mutable.ListMap`
Per the Scaladoc, “implements immutable maps using a *list-based* data structure.” As shown in the examples, elements that are added are prepended to the head of the list. Scala has both mutable and immutable `ListMap` classes. They return elements in the opposite order in which you inserted them, as though each insert was at the head of the map (like a List):

```scala
scala> scala.collection.immutable.ListMap(3 -> 'c', 2 -> 'b', 1 -> 'a')
res7: scala.collection.immutable.ListMap[Int,Char] = Map(3 -> c, 2 -> b, 1 -> a)

scala> scala.collection.mutable.ListMap(3 -> 'c', 2 -> 'b', 1 -> 'a')
res8: scala.collection.mutable.ListMap[Int,Char] = Map(1 -> a, 3 -> c, 2 -> b)
```

### `collection.SortedMap`
Keys of the map are returned in sorted order. Therefore, all traversal methods (such as foreach) return keys in that order.

```scala
scala> scala.collection.SortedMap(3 -> 'c', 2 -> 'b', 1 -> 'a')
res9: scala.collection.SortedMap[Int,Char] = Map(1 -> a, 2 -> b, 3 -> c)
```

## Adding, Updating, and Removing Elements with a Mutable Map

- Add elements to a mutable map by simply assigning them, or with the += method.
- Remove elements with -= or --=.
- Update elements by reassigning them.

```scala
val map = scala.collection.mutable.Map[Int, String]()
                                                //> map  : scala.collection.mutable.Map[Int,String] = Map()
map(1) = "one"
map                                             //> res0: scala.collection.mutable.Map[Int,String] = Map(1 -> one)

map += (2 -> "two")                             //> res1: myTest.test86.map.type = Map(2 -> two, 1 -> one)
map += (3 -> "three", 4 -> "four")              //> res2: myTest.test86.map.type = Map(2 -> two, 4 -> four, 1 -> one, 3 -> three)
map ++= List((5 -> "five"), (6 -> "six"))       //> res3: myTest.test86.map.type = Map(2 -> two, 5 -> five, 4 -> four, 1 -> one, 3 -> three, 6 -> six)

map -= 2                                        //> res4: myTest.test86.map.type = Map(5 -> five, 4 -> four, 1 -> one, 3 -> three, 6 -> six)
map -= (3, 4)                                   //> res5: myTest.test86.map.type = Map(5 -> five, 1 -> one, 6 -> six)
map --= List(5, 6)                              //> res6: myTest.test86.map.type = Map(1 -> one)

map(1) = "ONE"
map                                             //> res7: scala.collection.mutable.Map[Int,String] = Map(1 -> ONE)
```

- use `put` to add an element (or replace an existing element)
- use `retain` to keep only the elements in the map that match the predicate you supply
- use `remove` to remove an element by its key value
- use `clear` to delete all elements in the map

```scala
val map = scala.collection.mutable.Map(1 -> "one", 2 -> "two", 3 -> "three")
                                                //> map  : scala.collection.mutable.Map[Int,String] = Map(2 -> two, 1 -> one, 3 -> three)

map.put(4, "four")                              //> res0: Option[String] = None
map                                             //> res1: scala.collection.mutable.Map[Int,String] = Map(2 -> two, 4 -> four, 1 -> one, 3 -> three)

map.put(1, "ONE")                               //> res2: Option[String] = Some(one)
map                                             //> res3: scala.collection.mutable.Map[Int,String] = Map(2 -> two, 4 -> four, 1 -> ONE, 3 -> three)

map.retain((k, v) => k > 2)                     //> res4: myTest.test86.map.type = Map(4 -> four, 3 -> three)
map                                             //> res5: scala.collection.mutable.Map[Int,String] = Map(4 -> four, 3 -> three)

map.remove(4)                                   //> res6: Option[String] = Some(four)
map                                             //> res7: scala.collection.mutable.Map[Int,String] = Map(3 -> three)

map.clear
map                                             //> res8: scala.collection.mutable.Map[Int,String] = Map()
```

## Adding, Updating, and Removing Elements with Immutable Maps

Use the correct operator for each purpose, remembering to assign the results to a new map.

```scala
var map = Map(1 -> "one")                       //> map  : scala.collection.immutable.Map[Int,String] = Map(1 -> one)

map = map + (2 -> "two")
map                                             //> res0: scala.collection.immutable.Map[Int,String] = Map(1 -> one, 2 -> two)

map = map + (3 -> "three", 4 -> "four")
map                                             //> res1: scala.collection.immutable.Map[Int,String] = Map(1 -> one, 2 -> two, 3 -> three, 4 -> four)

map = map ++ List((5 -> "five"), (6 -> "six"))
map                                             //> res2: scala.collection.immutable.Map[Int,String] = Map(5 -> five, 1 -> one, 6 -> six, 2 -> two, 3 -> three, 4 -> four)

map = map - 2
map                                             //> res3: scala.collection.immutable.Map[Int,String] = Map(5 -> five, 1 -> one, 6 -> six, 3 -> three, 4 -> four)

map = map - (3, 4)
map                                             //> res4: scala.collection.immutable.Map[Int,String] = Map(5 -> five, 1 -> one, 6 -> six)

map = map -- List(5, 6)
map                                             //> res5: scala.collection.immutable.Map[Int,String] = Map(1 -> one)
```

```scala
map(1) = "ONE"                                  //error: value update is not a member of scala.collection.immutable.Map[Int,String]
```

## Accessing Map Values
- Access the value associated with a key
- If the map doesn’t contain the requested key, a java.util.NoSuchElementException exception is thrown. To avoid this problem is to create the map with the `withDefaultValue` method.
- To use the `getOrElse` method when attempting to find a value.
- To use the `get` method, which returns an Option

```scala
scala> val map = Map(1 -> "one", 2 -> "two", 3 -> "three")
map: scala.collection.immutable.Map[Int,String] = Map(1 -> one, 2 -> two, 3 -> three)

scala> map(1)
res0: String = one

scala> map(4)
java.util.NoSuchElementException: key not found: 4
```

```scala
scala> val map = Map(1 -> "one", 2 -> "two", 3 -> "three").withDefaultValue("???")
map: scala.collection.immutable.Map[Int,String] = Map(1 -> one, 2 -> two, 3 -> three)

scala> map(4)
res2: String = ???
```

```scala
scala> val map = Map(1 -> "one", 2 -> "two", 3 -> "three")
map: scala.collection.immutable.Map[Int,String] = Map(1 -> one, 2 -> two, 3 -> three)

scala> map.getOrElse(1, "???")
res5: String = one

scala> map.getOrElse(4, "???")
res6: String = ???
```

```scala
scala> val map = Map(1 -> "one", 2 -> "two", 3 -> "three")
map: scala.collection.immutable.Map[Int,String] = Map(1 -> one, 2 -> two, 3 -> three)

scala> map.get(1)
res7: Option[String] = Some(one)

scala> map.get(4)
res8: Option[String] = None
```

## Traversing a Map

```scala
val nums = Map(1 -> "A", 2 -> "B", 3 -> "C")    //> nums  : scala.collection.immutable.Map[Int,String] = Map(1 -> A, 2 -> B, 3 -> C)

for ((k, v) <- nums) println(s"$k->$v")         //> 1->A
                                                //| 2->B
                                                //| 3->C

nums.foreach(x => println(s"${x._1}->${x._2}")) //> 1->A
                                                //| 2->B
                                                //| 3->C

nums.foreach {
  case (k, v) => println(s"$k->$v")
}                                               //> 1->A
                                                //| 2->B
                                                //| 3->C

nums.keys.foreach(println)                      //> 1
                                                //| 2
                                                //| 3

nums.values.foreach(println)                    //> A
                                                //| B
                                                //| C
```

```scala
cala> val nums = Map(1 -> "a", 2 -> "b", 3 -> "c")
nums: scala.collection.immutable.Map[Int,String] = Map(1 -> a, 2 -> b, 3 -> c)

scala> nums.map(x => (x._1 -> x._2.toUpperCase))
res0: scala.collection.immutable.Map[Int,String] = Map(1 -> A, 2 -> B, 3 -> C)

scala> nums.mapValues(_.toUpperCase)
res1: scala.collection.immutable.Map[Int,String] = Map(1 -> A, 2 -> B, 3 -> C)

scala> nums.transform((k,v) => v.toUpperCase)
res2: scala.collection.immutable.Map[Int,String] = Map(1 -> A, 2 -> B, 3 -> C)
```

## Getting the Keys or Values from a Map

```scala
scala> val nums = Map(1 -> "a", 2 -> "b", 3 -> "c")
nums: scala.collection.immutable.Map[Int,String] = Map(1 -> a, 2 -> b, 3 -> c)

scala> nums.keySet
res0: scala.collection.immutable.Set[Int] = Set(1, 2, 3)

scala> nums.keys
res1: Iterable[Int] = Set(1, 2, 3)

scala> nums.keys.iterator
res2: Iterator[Int] = non-empty iterator

scala> nums.keysIterator
res3: Iterator[Int] = non-empty iterator

scala> nums.values
res4: Iterable[String] = MapLike(a, b, c)

scala> nums.values.iterator
res5: Iterator[String] = non-empty iterator

scala> nums.valuesIterator
res6: Iterator[String] = non-empty iterator
```

```scala
scala> val nums = Map(1 -> "a", 2 -> "b", 3 -> "c")
nums: scala.collection.immutable.Map[Int,String] = Map(1 -> a, 2 -> b, 3 -> c)

scala> nums.keysIterator.toList
res0: List[Int] = List(1, 2, 3)

scala> nums.toList
res1: List[(Int, String)] = List((1,a), (2,b), (3,c))

scala> nums.keysIterator.foreach(println)
1
2
3

scala> nums.keys.for
forall   foreach

scala> nums.keys.foreach(println)
1
2
3
```

## Reversing Keys and Values

```scala
scala> val nums = Map(1 -> "a", 2 -> "b", 3 -> "c")
nums: scala.collection.immutable.Map[Int,String] = Map(1 -> a, 2 -> b, 3 -> c)

scala> for ((k,v) <- nums) yield (v, k)
res0: scala.collection.immutable.Map[String,Int] = Map(a -> 1, b -> 2, c -> 3)

scala> nums.map(n => (n._2, n._1))
res1: scala.collection.immutable.Map[String,Int] = Map(a -> 1, b -> 2, c -> 3)
```

## Testing for the Existence of a Key or Value in a Map

To test for the existence of a `key` in a map, use the `contains` method:
```scala
scala> val nums = Map(1->"one", 2->"two", 3->"three")
nums: scala.collection.immutable.Map[Int,String] = Map(1 -> one, 2 -> two, 3 -> three)

scala> nums.contains(1)
res0: Boolean = true

scala> nums.contains(4)
res1: Boolean = false
```

To test whether a value exists in a map, use the `valuesIterator` method to search for the value using `exists` and `contains`:

Method I:
```scala
scala> nums.valuesIterator.exists(_.contains("one"))
res3: Boolean = true

scala> nums.valuesIterator.exists(_.contains("four"))
res4: Boolean = false
```

Method II:
```scala
scala> nums.valuesIterator.contains("one")
res3: Boolean = true

scala> nums.valuesIterator.contains("four")
res4: Boolean = false
```

Differents between method I && II:
```scala
scala> nums.valuesIterator.exists(_.contains("on"))
res5: Boolean = true

scala> nums.valuesIterator.contains("on")
res6: Boolean = false
```
- `nums.valuesIterator.exists(_.contains("on"))` 測試每個value裡面是否包含"on"
- `nums.valuesIterator.contains("on")` 測試nums是否含有"on"的value

## Filtering a Map

Use the `retain` method to define the elements to retain when using a **mutable** map
```scala
scala> val nums = scala.collection.mutable.Map(1->"one", 2->"two", 3->"three")
nums: scala.collection.mutable.Map[Int,String] = Map(2 -> two, 1 -> one, 3 -> three)

scala> nums.retain((k,v) => k > 1)
res0: nums.type = Map(2 -> two, 3 -> three)

scala> nums
res1: scala.collection.mutable.Map[Int,String] = Map(2 -> two, 3 -> three)
```

Use `filterKeys` or `filter` to filter the elements in a mutable or **immutable** map
```scala
scala> val nums = Map(1->"one", 2->"two", 3->"three")
nums: scala.collection.immutable.Map[Int,String] = Map(1 -> one, 2 -> two, 3 -> three)

scala> val x = nums.filterKeys(_ > 2)
x: scala.collection.immutable.Map[Int,String] = Map(3 -> three)

scala> val y = nums.filter(n => n._1 > 2)
y: scala.collection.immutable.Map[Int,String] = Map(3 -> three)

scala> val z = nums.filter(n => n._2.length > 3)
z: scala.collection.immutable.Map[Int,String] = Map(3 -> three)
```
- x, y: nums is filtered by key
- z: nums is filtered by value

## Sorting an Existing Map by Key or Value

## Finding the Largest Key or Value in a Map

## Adding Elements to a Set

## Deleting Elements from Sets

## Using Sortable Sets

## Using a Queue

## Using a Stack

## Using a Range 
