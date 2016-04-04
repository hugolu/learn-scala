# For Expression

內容來自 [Principles of Reactive Programming](https://class.coursera.org/reactive-002) week1 Recap: collections

## Translation of For

The Scala compiler translates for-expressions in terms of `map`, `flatMap` and a lazy variant of `filter`.

| for-expression | translated to |
|----------------|---------------|
| `for (x <- e1) yield e2` | `e1.map(x => e2)` |
| `for (x <- e1 if f; s) yield e2` | `for (x <- e1.withFilter(x => f); s) yield e2` |
| `for (x <- e1; y <- e2; s) yield e3` | `e1.flatMap(x => for (y <- e2; s) yield e3`) |

## Exercise
```scala
val N = 5                                       //> N  : Int = 5

for {
  x <- 2 to N
  y <- 2 to x
  if (x % y == 0)
} yield (x, y)                                  //> res0: scala.collection.immutable.IndexedSeq[(Int, Int)] = Vector((2,2), (3,3
                                                //| ), (4,2), (4,4), (5,5))
(2 to N).flatMap(x =>
  for {
    y <- 2 to x
    if (x % y == 0)
  } yield (x, y))                               //> res1: scala.collection.immutable.IndexedSeq[(Int, Int)] = Vector((2,2), (3,3
                                                //| ), (4,2), (4,4), (5,5))

(2 to N) flatMap (x =>
  (2 to x) withFilter (y =>
    x % y == 0) map (y => (x, y)))              //> res2: scala.collection.immutable.IndexedSeq[(Int, Int)] = Vector((2,2), (3,3
                                                //| ), (4,2), (4,4), (5,5))
(2 to N) flatMap (x =>
  (2 to x) filter (y =>
    x % y == 0) map (y => (x, y)))              //> res3: scala.collection.immutable.IndexedSeq[(Int, Int)] = Vector((2,2), (3,3
                                                //| ), (4,2), (4,4), (5,5))
```
