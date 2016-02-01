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
