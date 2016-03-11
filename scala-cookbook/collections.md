# Collections

There are a few important concepts to know when working with the methods of the Scala collection classes:
- A _predicate_ is simply a method, function, or anonymous function that takes one or more parameters and returns a Boolean value
- anonymous function
- implied loops

## Understanding the Collections Hierarchy

At a high level, Scala’s collection classes begin with the `Traversable` and `Iterable` traits, and extend into the three main categories of sequences (Seq), sets (Set), and maps (Map). Sequences further branch off into indexed and linear sequences.
- `Traversable` trait lets you traverse an entire collection
  - it implements the behavior common to all collections in terms of a foreach method
- `Iterable` trait defines an iterator, which lets you loop through a collection’s elements one at a time
  - when using an iterator, the collection can be traversed only once, because each element is consumed during the iteration process.

- `Traversable`
  - `Iterable`
    - `Seq`
      - `IndexedSeq`
      - `LinearSeq`
    - `Set`
    - `Map`

- `Seq`: A sequence is a linear collection of elements and may be indexed or linear (a linked list).
  - `IndexedSeq`
    - `Array`
    - `StringBuilder`
    - `Range`
    - `String`
    - `Vector`
    - `ArrayBuffer`
  - `Buffer`
    - `ArrayBuffer`
    - `ListBuffer`
  - `LinearSeq`
    - `List`
    - `Queue`
    - `LinkedList`
    - `Stack`
    - `MutableList`
    - `Stream`

- `Map`: A map contains a collection of key/value pairs, like a Java Map, Ruby Hash, or Python dictionary.
  - `HashMap`
  - `WeakHashMap`
  - `SortedMap`
  - `TreeMap`
  - `LinkedHashMap`
  - `ListMap`

- `Set`: A set is a collection that contains no duplicate elements.
  - `BitSet`
  - `HashSet`
  - `ListSet`
  - `SortedSet`
    - `TreeSet`

## Choosing a Collection Class

### Choosing a sequence
- Should the sequence be indexed (like an array), allowing rapid access to any ele‐ ments, or should it be implemented as a linked list?
- Do you want a mutable or immutable collection?

|   | Immutable | Mutable |
|---|-----------|---------|
| Indexed | Vector | ArrayBuffer |
| Linear (Linked lists) | List | ListBuffer |

Traits commonly used in library APIs
- `IndexedSeq` Implies that random access of elements is efficient.
- `LinearSeq` Implies that linear access to elements is efficient.
- `Seq` Used when it isn’t important to indicate that the sequence is indexed or linear in nature.

### Choosing a map
Choosing a map class is easier than choosing a sequence. There are the base mutable and immutable map classes, a SortedMap trait to keep elements in sorted order by key, a LinkedHashMap to store elements in insertion order, and a few other maps for special purposes.

### Choosing a set
Choosing a set is similar to choosing a map. There are base mutable and immutable set classes, a SortedSet to return elements in sorted order by key, a LinkedHashSet to store elements in insertion order, and a few other sets for special purposes.

### Types that act like collections
- `Enumeration` - A finite collection of constant values (i.e., the days in a week or months in a year).
- `Iterator` - An iterator isn’t a collection; instead, it gives you a way to access the elements in a collection. It does, however, define many of the methods you’ll see in a normal collection class, including foreach, map, flatMap, etc. You can also convert an iterator to a collection when needed.
- `Option` - Acts as a collection that contains zero or one elements. The Some class and None object extend Option. Some is a container for one element, and None holds zero elements.
- `Tuple` - Supports a heterogeneous collection of elements. There is no one “Tuple” class; tuples are implemented as case classes ranging from Tuple1 to Tuple22, which support 1 to 22 elements.

## Choosing a Collection Method to Solve a Problem

## Understanding the Performance of Collections

## Declaring a Type When Creating a Collection

```scala
scala> List(1, 2.0, 3L)
res0: List[Double] = List(1.0, 2.0, 3.0)

scala> List[Number](1, 2.0, 3L)
res1: List[Number] = List(1, 2.0, 3)

scala> List[AnyVal](1, 2.0, 3L)
res2: List[AnyVal] = List(1, 2.0, 3)
```
- By manually specifying a type, in this case Number, you control the collection type.

## Understanding Mutable Variables with Immutable Collections

```scala
scala> var a = Vector(1, 2, 3)
a: scala.collection.immutable.Vector[Int] = Vector(1, 2, 3)

scala> a = a :+ 4
vector: scala.collection.immutable.Vector[Int] = Vector(1, 2, 3, 4)

scala> val b = Vector(1, 2, 3)
b: scala.collection.immutable.Vector[Int] = Vector(1, 2, 3)

scala> b = b :+ 4
<console>:11: error: reassignment to val
       b = b :+ 4
         ^
```
- A mutable variable (`var`) can be reassigned to point at new data.
- An immutable variable (`val`) is like a final variable in Java; it can never be reassigned.

```scala
scala> val a = scala.collection.mutable.ArrayBuffer(1,2,3)
a: scala.collection.mutable.ArrayBuffer[Int] = ArrayBuffer(1, 2, 3)

scala> a(1) = 99

scala> a
res3: scala.collection.mutable.ArrayBuffer[Int] = ArrayBuffer(1, 99, 3)

scala> val b = Vector(1,2,3)
b: scala.collection.immutable.Vector[Int] = Vector(1, 2, 3)

scala> b(1) = 99
<console>:12: error: value update is not a member of scala.collection.immutable.Vector[Int]
       b(1) = 99
       ^
```
- The elements in a `mutable` collection (like ArrayBuffer) **can be changed**.
- The elements in an `immutable` collection (like Vector) **cannot be changed**.

## Make Vector Your “Go To” Immutable Sequence

`Vector` is a fast, general-purpose, **immutable**, sequential collection type.
```scala
scala> var v = Vector(1,2,3)
v: scala.collection.immutable.Vector[Int] = Vector(1, 2, 3)

scala> v(0)
res5: Int = 1

scala> v = v ++ Vector(4,5,6)
v: scala.collection.immutable.Vector[Int] = Vector(1, 2, 3, 4, 5, 6)

scala> v = v.updated(0, 99)
v: scala.collection.immutable.Vector[Int] = Vector(99, 2, 3, 4, 5, 6)

scala> v.take(2)
res6: scala.collection.immutable.Vector[Int] = Vector(99, 2)

scala> v.filter(_ > 2)
res7: scala.collection.immutable.Vector[Int] = Vector(99, 3, 4, 5, 6)
```

## Make ArrayBuffer Your “Go To” Mutable Sequence

ArrayBuffer is a general-purpose, **mutable**, sequential collection type.
```scala
scala> val v = scala.collection.mutable.ArrayBuffer(1, 2, 3)
v: scala.collection.mutable.ArrayBuffer[Int] = ArrayBuffer(1, 2, 3)

scala> v += (4, 5, 6)
res8: v.type = ArrayBuffer(1, 2, 3, 4, 5, 6)

scala> v.append(7)

scala> v
res10: scala.collection.mutable.ArrayBuffer[Int] = ArrayBuffer(1, 2, 3, 4, 5, 6, 7)

scala> v.appendAll(Seq(8, 9))

scala> v
res12: scala.collection.mutable.ArrayBuffer[Int] = ArrayBuffer(1, 2, 3, 4, 5, 6, 7, 8, 9)

scala> v.insert(0)

scala> v
res14: scala.collection.mutable.ArrayBuffer[Int] = ArrayBuffer(1, 2, 3, 4, 5, 6, 7, 8, 9)

scala> v.insert(0, 0)

scala> v
res16: scala.collection.mutable.ArrayBuffer[Int] = ArrayBuffer(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)

scala> v.prepend(-1)

scala> v
res18: scala.collection.mutable.ArrayBuffer[Int] = ArrayBuffer(-1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9)

scala> v.remove(0, 2)

scala> v
res20: scala.collection.mutable.ArrayBuffer[Int] = ArrayBuffer(1, 2, 3, 4, 5, 6, 7, 8, 9)

scala> v.trimStart(3)

scala> v
res22: scala.collection.mutable.ArrayBuffer[Int] = ArrayBuffer(4, 5, 6, 7, 8, 9)

scala> v.trimEnd(3)

scala> v
res24: scala.collection.mutable.ArrayBuffer[Int] = ArrayBuffer(4, 5, 6)
```

## Looping over a Collection with foreach

As long as your function (or method) takes one parameter of the same type as the elements in the collection and returns nothing (Unit), it can be called from a foreach method.
```scala
scala> val fruits = Array("apple", "banana", "coconut")
fruits: Array[String] = Array(apple, banana, coconut)

scala> fruits.foreach(n => println(n))
apple
banana
coconut

scala> fruits.foreach(println)
apple
banana
coconut
```

## Looping over a Collection with a for Loop

You can loop over any `Traversable` type (basically any sequence) using a `for` loop:
```scala
scala> val fruits = Array("apple", "banana", "coconut")
fruits: Array[String] = Array(apple, banana, coconut)

scala> for(f <- fruits) println(f)
apple
banana
coconut

scala> for(f <- fruits) yield f.capitalize
res28: Array[String] = Array(Apple, Banana, Coconut)
```

## Using zipWithIndex or zip to Create Loop Counters

```scala
scala> val fruits = Array("apple", "banana", "coconut")
fruits: Array[String] = Array(apple, banana, coconut)

scala> fruits.zipWithIndex
res35: Array[(String, Int)] = Array((apple,0), (banana,1), (coconut,2))
```
- `value` first, then `index`

```scala
scala> val fruits = Array("apple", "banana", "coconut")
fruits: Array[String] = Array(apple, banana, coconut)

scala> fruits.zipWithIndex.foreach {
     | case (value, index) => println(s"fruits[$index] = $value")
     | }
fruits[0] = apple
fruits[1] = banana
fruits[2] = coconut

scala> for(i <- 0 until fruits.size) {
     | println(s"fruits[$i] = ${fruits(i)}")
     | }
fruits[0] = apple
fruits[1] = banana
fruits[2] = coconut
```

## Using Iterators

### Why iterator?
That being said, sometimes you’ll run into an iterator, with one of the best examples being the io.Source.fromFile method. This method returns an iterator, which makes sense, because when you’re working with very large files, it’s not practical to read the entire file into memory.

An iterator isn’t a collection; instead, it gives you a way to access the elements in a collection, one by one.

```scala
scala> val list = List(1,2,3)
list: List[Int] = List(1, 2, 3)

scala> val it = list.iterator
it: Iterator[Int] = non-empty iterator

scala> it.max
res36: Int = 3

scala> it.foreach(println)
```
- An important part of using an iterator is knowing that it’s exhausted after you use it. As you access each element, you mutate the iterator, and the previous element is discarded.

```scala
scala> val it = Iterator(1, 2, 3)
it: Iterator[Int] = non-empty iterator

scala> it.toList
res38: List[Int] = List(1, 2, 3)
```

## Transforming One Collection to Another with for/ yield

```scala
scala> val nums = List(1, 2, 3)
nums: List[Int] = List(1, 2, 3)

scala> for(n <- nums) yield n*n
res42: List[Int] = List(1, 4, 9)

scala> for(n <- nums if n % 2 != 0) yield n
res43: List[Int] = List(1, 3)

scala> for(n <- nums if n % 2 != 0) yield n
res44: List[Int] = List(1, 9)
```
- This combination of a for `loop` and `yield` statement is known as a *for comprehension* or *sequence comprehension*. 
- In general, the collection type that’s returned by a for comprehension will be the same type that you begin with.

## Transforming One Collection to Another with map

```scala
scala> val nums = List(1, 2, 3)
nums: List[Int] = List(1, 2, 3)

scala> nums.map(n => n*n)
res45: List[Int] = List(1, 4, 9)

scala> nums.filter(n => n % 2 != 0)
res46: List[Int] = List(1, 3)

scala> nums.filter(n => n % 2 != 0).map(n => n*n)
res47: List[Int] = List(1, 9)
```

## Flattening a List of Lists with flatten

Use the `flatten` method to convert *a list of lists* into a single list.
```scala
scala> val lol = List(List(1,2,3), List(4,5,6))
lol: List[List[Int]] = List(List(1, 2, 3), List(4, 5, 6))

scala> val list = lol.flatten
list: List[Int] = List(1, 2, 3, 4, 5, 6)

scala> for {
     | list <- lol
     | elem <- list
     | } yield elem
res51: List[Int] = List(1, 2, 3, 4, 5, 6)
```

Because an `Option` can be thought of as a container that holds zero or one elements, `flatten` has a very useful effect on a sequence of Some and None elements. It pulls the values out of the Some elements to create the new list, and drops the None elements:
```scala
scala> val list = List(Some(1), None, Some(2), Some(3), Some(4), None)
list: List[Option[Int]] = List(Some(1), None, Some(2), Some(3), Some(4), None)

scala> list.flatten
res49: List[Int] = List(1, 2, 3, 4)

scala> for {
     | Some(x) <- list
     | } yield x
res50: List[Int] = List(1, 2, 3, 4)
```

## Combining map and flatten with flatMap

Use `flatMap` in situations where you run `map` followed by `flatten`. 
- You’re using map (or a for/yield expression) to create a new collection from an existing collection.
- The resulting collection is a list of lists.
- You call flatten immediately after map (or a for/yield expression).

```scala
scala> val lol = for (i <- List(1, 4, 7)) yield List(i, i+1, i+2)
lol: List[List[Int]] = List(List(1, 2, 3), List(4, 5, 6), List(7, 8, 9))

scala> val list1 = lol.flatten
list1: List[Int] = List(1, 2, 3, 4, 5, 6, 7, 8, 9)
```
```scala
scala> val list2 = List(1, 4, 7).flatMap(i => List(i, i+1, i+2))
list2: List[Int] = List(1, 2, 3, 4, 5, 6, 7, 8, 9)
```

將 `bag` 裡面能轉成數字的值做總和：
```scala
scala> def toInt(str: String) = {
     | try {
     | Some(str.toInt)
     | } catch {
     | case e: Exception => None
     | }
     | }
toInt: (str: String)Option[Int]

scala> val number = toInt("1")
number: Option[Int] = Some(1)

scala> val notNumber = toInt("apple")
notNumber: Option[Int] = None

scala> val bag = List("1", "apple", "2", "banana", "3", "coconut")
bag: List[String] = List(1, apple, 2, banana, 3, coconut)

scala> val nums = bag.map(toInt)
nums: List[Option[Int]] = List(Some(1), None, Some(2), None, Some(3), None)

scala> val ints = nums.flatten
ints: List[Int] = List(1, 2, 3)

scala> val sum = ints.sum
sum: Int = 6
```
- `flatten` works very well with a list of `Some` and `None` elements. It extracts the values from the `Some` elements while discarding the `None` elements.

使用 `flatMap` 簡化過程
```scala
scala> def toInt(str: String) = {
     | try {
     | Some(str.toInt)
     | } catch {
     | case e: Exception => None
     | }
     | }
toInt: (str: String)Option[Int]

scala> val bag = List("1", "apple", "2", "banana", "3", "coconut")
bag: List[String] = List(1, apple, 2, banana, 3, coconut)

scala> val sum = bag.flatMap(toInt).sum
sum: Int = 6
```

## Using filter to Filter a Collection

To use `filter` on your collection, give it a *predicate* to filter the collection elements as desired.
```scala
scala> val list = List.range(1, 10)
list: List[Int] = List(1, 2, 3, 4, 5, 6, 7, 8, 9)

scala> val odds = list.filter(_ % 2 != 0)
odds: List[Int] = List(1, 3, 5, 7, 9)
```

## Extracting a Sequence of Elements from a Collection

There are quite a few collection methods you can use to extract a contiguous list of elements from a sequence, including `drop`, `dropWhile`, `head`, `headOption`, `init`, `last`, `lastOption`, `slice`, `tail`, `take`, `takeWhile`.
```scala
scala> val list = List.range(1, 10)
list: List[Int] = List(1, 2, 3, 4, 5, 6, 7, 8, 9)

scala> list.filter(_ % 2 != 0)
res68: List[Int] = List(1, 3, 5, 7, 9)

scala> val odds = list.filter(_ % 2 != 0)
odds: List[Int] = List(1, 3, 5, 7, 9)

scala> val x = (1 to 10).toArray
x: Array[Int] = Array(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

scala> x.drop(3)
res69: Array[Int] = Array(4, 5, 6, 7, 8, 9, 10)

scala> x.dropWhile(_ < 6)
res70: Array[Int] = Array(6, 7, 8, 9, 10)

scala> x.dropRight(4)
res71: Array[Int] = Array(1, 2, 3, 4, 5, 6)

scala> x.take(3)
res72: Array[Int] = Array(1, 2, 3)

scala> x.takeWhile(_ < 6)
res74: Array[Int] = Array(1, 2, 3, 4, 5)

scala> x.takeRight(3)
res75: Array[Int] = Array(8, 9, 10)

scala> x.head
res79: Int = 1

scala> x.headOption
res80: Option[Int] = Some(1)

scala> x.tail
res81: Array[Int] = Array(2, 3, 4, 5, 6, 7, 8, 9, 10)

scala> x.init
res87: Array[Int] = Array(1, 2, 3, 4, 5, 6, 7, 8, 9)

scala> x.last
res88: Int = 10

scala> x.lastOption
res89: Option[Int] = Some(10)
```

But I cannot explain this :(
```
scala> x.slice(3, 3)
res95: Array[Int] = Array()
```

## Splitting Sequences into Subsets (groupBy, partition, etc.)

Use the `groupBy`, `partition`, `span`, or `splitAt` methods to partition a sequence into subsequences.
```scala
scala> val x = List(15, 10, 5, 8, 20, 12)
x: List[Int] = List(15, 10, 5, 8, 20, 12)

scala> val y = x.groupBy(_ > 10)
y: scala.collection.immutable.Map[Boolean,List[Int]] = Map(false -> List(10, 5, 8), true -> List(15, 20, 12))

scala> val y = x.partition(_ > 10)
y: (List[Int], List[Int]) = (List(15, 20, 12),List(10, 5, 8))

scala> val y = x.span(_ < 20)
y: (List[Int], List[Int]) = (List(15, 10, 5, 8),List(20, 12))

scala> val y = x.splitAt(2)
y: (List[Int], List[Int]) = (List(15, 10),List(5, 8, 20, 12))
```
- The `span` method returns a `Tuple2` based on your predicate p, consisting of **“the longest prefix of this list whose elements all satisfy p, and the rest of this list.”**

```scala
scala> val x = List(15, 10, 5, 8, 20, 12)
x: List[Int] = List(15, 10, 5, 8, 20, 12)

scala> val groups = x.groupBy(_ > 10)
groups: scala.collection.immutable.Map[Boolean,List[Int]] = Map(false -> List(10, 5, 8), true -> List(15, 20, 12))

scala> val g1 = groups(true)
g1: List[Int] = List(15, 20, 12)

scala> val g2 = groups(false)
g2: List[Int] = List(10, 5, 8)
```

The `sliding(size, step)` method is an interesting creature that can be used to break a sequence into many groups. It can be called with just a size, or both a size and step:
```scala
scala> val list = (1 to 12).toList
list: List[Int] = List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)

scala> val groups = list.sliding(3,3).toList
groups: List[List[Int]] = List(List(1, 2, 3), List(4, 5, 6), List(7, 8, 9), List(10, 11, 12))

scala> val groups = list.sliding(4, 4).toList
groups: List[List[Int]] = List(List(1, 2, 3, 4), List(5, 6, 7, 8), List(9, 10, 11, 12))
```
- As shown, sliding works by passing a “sliding window” over the original sequence, returning sequences of a length given by size. 

The `unzip` method is also interesting. It can be used to take a sequence of Tuple2 values and create two resulting lists: one that contains the first element of each tuple, and another that contains the second element from each tuple:
```scala
scala> val a = List(1, 2, 3)
a: List[Int] = List(1, 2, 3)

scala> val b = List("apple", "banana", "coconut")
b: List[String] = List(apple, banana, coconut)

scala> val c = a.zip(b)
c: List[(Int, String)] = List((1,apple), (2,banana), (3,coconut))

scala> val (nums, fruits) = c.unzip
nums: List[Int] = List(1, 2, 3)
fruits: List[String] = List(apple, banana, coconut)
```

## Walking Through a Collection with the reduce and fold Methods

Use the `reduceLeft`, `foldLeft`, `reduceRight`, and `foldRight` methods to walk through the elements in a sequence, applying your function to neighboring elements to yield a new result, which is then compared to the next element in the sequence to yield a new result.
```scala
scala> val list = (1 to 10).toList
list: List[Int] = List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

scala> list.reduceLeft(_ + _)
res101: Int = 55

scala> list.foldLeft(0)(_ + _)
res102: Int = 55
```

The difference between reduceLeft and reduceRight: “The order in which operations are performed on elements is unspecified and may be nondeterministic.”
```scala
scala> val a = Array(1.0, 2.0, 3.0)
a: Array[Double] = Array(1.0, 2.0, 3.0)

scala> a.reduceLeft(_/_)
res124: Double = 0.16666666666666666

scala> a.reduceRight(_/_)
res125: Double = 1.5
```

Two methods named `scanLeft` and `scanRight` walk through a sequence in a manner similar to `reduceLeft` and `reduceRight`, but they return a sequence instead of a single value.
```scala
val product = (x: Int, y: Int) => {
  val result = x * y
  println(s"$x x $y = $result")
  result
}                                               //> product  : (Int, Int) => Int = <function2>

val a = Array(1, 2, 3)                          //> a  : Array[Int] = Array(1, 2, 3)
a.scanLeft(10)(product)                         //> 10 x 1 = 10
                                                //| 10 x 2 = 20
                                                //| 20 x 3 = 60
                                                //| res0: Array[Int] = Array(10, 10, 20, 60)
```
```scala
def add(x: Int, y: Int) = {
  val result = x + y
  println(s"$x + $y = $result")
  result
}                                               //> add: (x: Int, y: Int)Int

val list = (1 to 10).toList                     //> list  : List[Int] = List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
list.scanLeft(0)(add)                           //> 0 + 1 = 1
                                                //| 1 + 2 = 3
                                                //| 3 + 3 = 6
                                                //| 6 + 4 = 10
                                                //| 10 + 5 = 15
                                                //| 15 + 6 = 21
                                                //| 21 + 7 = 28
                                                //| 28 + 8 = 36
                                                //| 36 + 9 = 45
                                                //| 45 + 10 = 55
                                                //| res1: List[Int] = List(0, 1, 3, 6, 10, 15, 21, 28, 36, 45, 55)
```
- scanLeft “Produces a collection containing cumulative results of applying the operator going left to right.”

## Extracting Unique Elements from a Sequence

```scala
scala> val seq = Seq(1, 1, 2, 2, 3, 4)
seq: Seq[Int] = List(1, 1, 2, 2, 3, 4)

scala> val y = seq.distinct
y: Seq[Int] = List(1, 2, 3, 4)

scala> val y = seq.toSet
y: scala.collection.immutable.Set[Int] = Set(1, 2, 3, 4)
```

## Merging Sequential Collections

Use the `++=` method to merge a sequence into a mutable sequence:
```scala
scala> var a = scala.collection.mutable.ArrayBuffer(1,2)
a: scala.collection.mutable.ArrayBuffer[Int] = ArrayBuffer(1, 2)

scala> a ++= Seq(3,4)
res128: scala.collection.mutable.ArrayBuffer[Int] = ArrayBuffer(1, 2, 3, 4)
```

Use the `++` method to merge two mutable or immutable sequences:
```scala
scala> val a = List(1,2)
a: List[Int] = List(1, 2)

scala> val b = a ++ List(3,4)
b: List[Int] = List(1, 2, 3, 4)
```

Use collection methods like `union`, `diff`, and `intersect`:
```scala
scala> val a = List(1,2)
a: List[Int] = List(1, 2)

scala> val b = a ++ List(3,4)
b: List[Int] = List(1, 2, 3, 4)

scala> val a = List(1, 2, 3)
a: List[Int] = List(1, 2, 3)

scala> val b = List(2, 3, 4)
b: List[Int] = List(2, 3, 4)

scala> a.union(b)
res130: List[Int] = List(1, 2, 3, 2, 3, 4)

scala> a.union(b).distinct
res131: List[Int] = List(1, 2, 3, 4)

scala> val a1 = a.diff(b)
a1: List[Int] = List(1)

scala> val b1 = b.diff(a)
b1: List[Int] = List(4)

scala> val ab = a.intersect(b)
ab: List[Int] = List(2, 3)

scala> a1 ++ ab
res132: List[Int] = List(1, 2, 3)

scala> b1 ++ ab
res133: List[Int] = List(4, 2, 3)
```

## Merging Two Sequential Collections into Pairs with zip

```scala
scala> val a = List(1, 2, 3)
a: List[Int] = List(1, 2, 3)

scala> val b = List("apple", "banana", "coconut")
b: List[String] = List(apple, banana, coconut)

scala> val ab = a zip b
ab: List[(Int, String)] = List((1,apple), (2,banana), (3,coconut))

scala> val c = a.zip(b).toMap
c: scala.collection.immutable.Map[Int,String] = Map(1 -> apple, 2 -> banana, 3 -> coconut)

scala> val d = Map(a.zip(b): _*)
d: scala.collection.immutable.Map[Int,String] = Map(1 -> apple, 2 -> banana, 3 -> coconut)
```
- Once you have a sequence of tuples like couples, you can convert it to a Map.

## Creating a Lazy View on a Collection

A *strict* version of the collection - This means that if you create a collection that contains one million elements, memory is allocated for all of those elements immediately.

In Scala you can optionally create a `view` on a collection. A view makes the result non‐strict, or *lazy*.

```scala
scala> (1 to 10)
res0: scala.collection.immutable.Range.Inclusive = Range(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

scala> (1 to 10).view
res1: scala.collection.SeqView[Int,scala.collection.immutable.IndexedSeq[Int]] = SeqView(...)

scala> (1 to 10).view.force
res2: scala.collection.immutable.IndexedSeq[Int] = Vector(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
```
- `Int` is the type of the view’s elements.
- The `scala.collection.immutable.IndexedSeq[Int]` portion of the output indicates the type you’ll get if you force the collection back to a “normal,” strict collection.
- You can see this when you `force` the view back to a normal collection.

`map` is a transformer method. Calling a map method with and without a view has dramatically different results:
```scala
scala> (1 to 10).map(_*2)
res3: scala.collection.immutable.IndexedSeq[Int] = Vector(2, 4, 6, 8, 10, 12, 14, 16, 18, 20)

scala> (1 to 10).view.map(_*2)
res4: scala.collection.SeqView[Int,Seq[_]] = SeqViewM(...)
```

- a view “constructs only a proxy for the result collection, and its elements get constructed only as one demands them ... A view is a special kind of collection that represents some base collection, but implements all transformers lazily.”
- A transformer is a method that constructs a new collection from an existing collection.
- This includes methods like `map`, `filter`, `reverse`, and many more.  When you use these methods, you’re transforming the input collection to a new output collection.

There are two primary use cases for using a view:
- Performance
- To treat a collection like a database view
```scala
scala> val arr = (1 to 10).toArray
arr: Array[Int] = Array(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

scala> val view = arr.view.slice(2, 5)
view: scala.collection.mutable.IndexedSeqView[Int,Array[Int]] = SeqViewS(...)

scala> arr(2) = 42

scala> view.foreach(println)
42
4
5

scala> view(0) = 10

scala> view(1) = 20

scala> view(2) = 30

scala> arr
res11: Array[Int] = Array(1, 2, 10, 20, 30, 6, 7, 8, 9, 10)
```
- Changing the elements in the array updates the view, and changing the elements referenced by the view changes the elements in the array.

## Populating a Collection with a Range

```scala
scala> Array.range(1, 10)
res0: Array[Int] = Array(1, 2, 3, 4, 5, 6, 7, 8, 9)

scala> List.range(1, 10)
res1: List[Int] = List(1, 2, 3, 4, 5, 6, 7, 8, 9)

scala> Vector.range(1, 10)
res2: scala.collection.immutable.Vector[Int] = Vector(1, 2, 3, 4, 5, 6, 7, 8, 9)

scala> (1 to 10).toArray
res3: Array[Int] = Array(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

scala> (1 to 10).toList
res4: List[Int] = List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

scala> (1 to 10).toVector
res5: Vector[Int] = Vector(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
```

## Creating and Using Enumerations

```scala
package foo.bar.app {
  object Color extends Enumeration {
    type Color = Value
    val RED, GREEN, BLUE, BLACK, WHITE = Value
  }
}

import foo.bar.app.Color._
val color = RED                                 //> color  : myTest.foo.bar.app.Color.Value = RED

foo.bar.app.Color.values foreach println        //> RED
                                                //| GREEN
                                                //| BLUE
                                                //| BLACK
                                                //| WHITE
```

[Understanding scala enumerations](http://stackoverflow.com/questions/11067396/understanding-scala-enumerations)
```scala
object WeekDay extends Enumeration {
  type WeekDay = Value
  val Mon, Tue, Wed, Thu, Fri, Sat, Sun = Value
}
import WeekDay._
def isWorkingDay(d: WeekDay) = ! (d == Sat || d == Sun)
```
- The line `type WeekDay = Value` is just a *type alias*. 
- The `Enumeration` trait has a type member `Value` representing the individual elements of the enumeration
- Thus `object WeekDay` inherits that type member.
- `val Mon, Tue, Wed, Thu, Fri, Sat, Sun = Value` = `val Mon = Value`, `val Tue = Value`, `val Wed = Value`...
- if the contents of object WeekDay is not imported, you would need to use type WeekDay.Value
```scala
def isWorkingDay(d: WeekDay.Value) = ! (d == WeekDay.Sat || d == WeekDay.Sun)
```

## Tuples, for When You Just Need a Bag of Things

## Sorting a Collection

## Converting a Collection to a String with mkString
