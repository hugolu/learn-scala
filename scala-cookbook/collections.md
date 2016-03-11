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

## Looping over a Collection with a for Loop

## Using zipWithIndex or zip to Create Loop Counters

## Using Iterators

## Transforming One Collection to Another with for/ yield

##  Transforming One Collection to Another with map

## Flattening a List of Lists with flatten

## Combining map and flatten with flatMap

## Using filter to Filter a Collection

## Extracting a Sequence of Elements from a Collection

## Splitting Sequences into Subsets (groupBy, partition, etc.)

## Walking Through a Collection with the reduce and fold Methods

## Extracting Unique Elements from a Sequence

## Merging Sequential Collections

## Merging Two Sequential Collections into Pairs with zip

## Creating a Lazy View on a Collection

## Populating a Collection with a Range

## Creating and Using Enumerations

## Tuples, for When You Just Need a Bag of Things

## Sorting a Collection

## Converting a Collection to a String with mkString
