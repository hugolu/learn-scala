# More collections

## The basics

### List
```scala
scala> List(1,2,3)
res0: List[Int] = List(1, 2, 3)

scala> 1::2::3::Nil
res1: List[Int] = List(1, 2, 3)
```

### Set
```scala
scala> Set(1,1,2)
res2: scala.collection.immutable.Set[Int] = Set(1, 2)
```
- no duplicates

### Seq
```scala
scala> Seq(1,1,2)
res3: Seq[Int] = List(1, 1, 2)
```
- In Java terms, Scala's ```Seq``` would be Java's ```List```, and Scala's ```List``` would be Java's ```LinkedList```.
- ```Seq``` is a trait, which is equivalent to Java's interface,
- ```List``` is an abstract class that is extended by ```Nil``` and ```::```

### Map
```scala
scala> Map('a'->1, 'b'->2)
res5: scala.collection.immutable.Map[Char,Int] = Map(a -> 1, b -> 2)

scala> Map(('a', 1), ('b', 2))
res6: scala.collection.immutable.Map[Char,Int] = Map(a -> 1, b -> 2)
```

## The Hierarchy

### Traversable
All collections can be traversed. This trait defines standard function combinators. These combinators are written in terms of ```foreach```, which collections must implement.
```scala
scala> List(1,2,3).foreach(print)
123
```

### Iterable
Has an iterator() method to give you an Iterator over the elements.
```scala
scala> val iter = List(1,2,3).iterator
iter: Iterator[Int] = non-empty iterator

scala> while(iter.hasNext) { print(iter.next) }
123
```

### Seq
Sequence of items with ordering.
```scala
scala> val iter = Seq(1,2,3).reverseIterator
iter: Iterator[Int] = non-empty iterator

scala> while(iter.hasNext) { print(iter.next) }
321
```

### Set
A collection of items with no duplicates.

### Map
Key Value Pairs.

## The methods
