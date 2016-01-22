# Collections

## List
```scala
val list = List(1,2,3)
// list: List[Int] = List(1, 2, 3)
```

## Set
```scala
val set = Set(1,2,2,3)
// set: scala.collection.immutable.Set[Int] = Set(1, 2, 3)
```

## Tuple
```scala
val tuple = ("localhost", 80)
// tuple: (String, Int) = (localhost,80)
tuple._1
// res0: String = localhost
tupel._2
// res2: Int = 80

tuple match {
  case ("localhost", 80) => "localhost:80"
  case _ => "others"
}
// res3: String = localhost:80
```

## Map
```scala
1 -> 2
// res4: (Int, Int) = (1,2)
(1, 2)
// res5: (Int, Int) = (1,2)
Map((1,2), (3,4))
// res6: scala.collection.immutable.Map[Int,Int] = Map(1 -> 2, 3 -> 4)
```

## Option
```scala
val some = Option("hello")
// some: Option[String] = Some(hello)

val none = Option(null)
// none: Option[Null] = None

some.get
// res0: String = hello

none.get
// java.util.NoSuchElementException: None.get

none.getOrElse("nothing")
// res2: String = nothing


def getValue(o: Option[String]) = o match {
  case Some(n) => n
  case None => "nothing"
}
// getValue: (o: Option[String])String

getValue(some)
// res5: String = hello
getValue(none)
// res6: String = nothing
```

## Functional Combinators
```List(1, 2, 3) map squared``` applies the function ```squared``` to the elements of the list, returning a new list, perhaps ```List(1, 4, 9)```. We call operations like ```map``` combinators.

## ```map```
```map``` evaluates a function over each element in the list, returning a list with the same number of elements.

```scala
val numbers = List(1,2,3)

def timesTwo(i: Int) = i * 2
// timesTwo: (i: Int)Int
numbers.map(timesTwo)
// res9: List[Int] = List(2, 4, 6)

numbers.map(_*2)
// res10: List[Int] = List(2, 4, 6)
```

## ```foreach```
```foreach``` is like map but returns nothing. ```foreach``` is intended for side-effects only.

```scala
val numbers = List(1,2,3)

val ret = numbers.foreach(_*2)
// ret: Unit = ()
```

## ```filter```
```filter``` removes any elements where the function you pass in evaluates to false. Functions that return a Boolean are often called predicate functions.

```scala
val numbers = (1 to 9).toList
// numbers: List[Int] = List(1, 2, 3, 4, 5, 6, 7, 8, 9)

val odds = numbers.filter(_%2!=0)
// odds: List[Int] = List(1, 3, 5, 7, 9)
```

## ```zip```
```zip``` aggregates the contents of two lists into a single list of pairs.

```scala
val list1 = List(1,2,3)
val list2 = List("a", "b", "c")

list1.zip(list2)
// res12: List[(Int, String)] = List((1,a), (2,b), (3,c))
```

## ```partition```
```partition``` splits a list based on where it falls with respect to a predicate function.

```scala
val numbers = (1 to 10).toList

numbers.partition(_%2==0)
// res13: (List[Int], List[Int]) = (List(2, 4, 6, 8, 10),List(1, 3, 5, 7, 9))
```

## ```find```
```find``` returns the first element of a collection that matches a predicate function.

```scala
val numbers = (1 to 10).toList

numbers.find(_>3)
res14: Option[Int] = Some(4)
```

## ```drop```
```drop``` drops the first i elements.

```scala
val numbers = (1 to 10).toList

numbers.drop(5)
// res16: List[Int] = List(6, 7, 8, 9, 10)
```

## ```dropWhile```
```dropWhile``` removes the first elements that match a predicate function. 

```scala
val numbers = (1 to 10).toList

numbers.drop(5)
res16: List[Int] = List(6, 7, 8, 9, 10)
```

## ```foldLeft```
folding the elements of a list from left to right.

```
val numbers = (1 to 10).toList

numbers.foldLeft(0) {(acc, n) => print(acc, n); acc + n}
// (0,1)(1,2)(3,3)(6,4)(10,5)(15,6)(21,7)(28,8)(36,9)(45,10)res21: Int = 55
// = ((((((((((0, 1), 2), 3), 4), 5), 6), 7), 8), 9), 10)
```

## ```foldRight```
folding the elements of a list from rigth to left.

```scala
val numbers = (1 to 10).toList

scala> numbers.foldRight(0) {(n, acc) => print(n, acc); acc + n}
// (10,0)(9,10)(8,19)(7,27)(6,34)(5,40)(4,45)(3,49)(2,52)(1,54)res22: Int = 55
// = (1, (2, (3, (4, (5, (6, (7, (8, (9, (10, 0))))))))))
```

## ```flatten```
```flatten``` collapses one level of nested structure.


```scala
List(List(1,2,3), List(4,5,6)).flatten
// res23: List[Int] = List(1, 2, 3, 4, 5, 6)

List(Set(1,2,3), Set(4,5,6)).flatten
// res28: List[Int] = List(1, 2, 3, 4, 5, 6)

scala> Set(List(1,2,3), List(4,5,6)).flatten
// res29: scala.collection.immutable.Set[Int] = Set(5, 1, 6, 2, 3, 4)
```

## ```flatMap```
```flatMap``` is a frequently used combinator that combines mapping and flattening. flatMap takes a function that works on the nested lists and then concatenates the results back together.
- do ```map``` then ```flatten```

```scala
val nestedNumbers = List(List(1, 2), List(3, 4))

nestedNumbers.flatMap(_.map(_*2))
// res30: List[Int] = List(2, 4, 6, 8)

nestedNumbers.map(_.map(_*2)).flatten
// res31: List[Int] = List(2, 4, 6, 8)
```

## Generalized functional combinators
Interestingly, every functional combinator shown above can be written on top of **fold**. Let’s see some examples.

```scala
val numbers = (1 to 10).toList

def myMap[T](list: List[T], fn: T => T): List[T] = {
  list.foldRight(List[T]()) {(n: T, acc: List[T]) => fn(n) :: acc}
}

myMap[Int](numbers, n => n*2)
// res40: List[Int] = List(2, 4, 6, 8, 10, 12, 14, 16, 18, 20)

val aMap = myMap[Int]_
//aMap: (List[Int], Int => Int) => List[Int] = <function2>
aMap(numbers, _*2)
// res41: List[Int] = List(2, 4, 6, 8, 10, 12, 14, 16, 18, 20)
```
