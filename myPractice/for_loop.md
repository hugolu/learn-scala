# for loop

Ref: [Scal for Loop](http://www.tutorialspoint.com/scala/scala_for_loop.htm)

## The for Loop with Ranges
```scala
for (x <- 1 to 3) println(x)                    //> 1
                                                //| 2
                                                //| 3

for (x <- 1 until 3) println(x)                 //> 1
                                                //| 2
```

## The for Loop with Collections
```scala
for (n <- Array(1, 2, 3)) println(n)            //> 1
                                                //| 2
                                                //| 3

for (n <- List(1, 2, 3)) println(n)             //> 1
                                                //| 2
                                                //| 3

for (n <- Map(1 -> "A", 2 -> "B", 3 -> "C")) println(n)
                                                //> (1,A)
                                                //| (2,B)
                                                //| (3,C)
```

## The for Loop with Filters
```scala
for (i <- 1 to 10 if i % 2 == 0) println(i)     //> 2
                                                //| 4
                                                //| 6
                                                //| 8
                                                //| 10
```

## The for Loop with yield
```scala
for (i <- 1 to 3) yield i                       //> res0: scala.collection.immutable.IndexedSeq[Int] = Vector(1, 2, 3)

(1 to 3) map (i => i)                           //> res1: scala.collection.immutable.IndexedSeq[Int] = Vector(1, 2, 3)

```

```scala
def mklist(n: Int): List[Int] = List.fill(n)(n) //> mklist: (n: Int)List[Int]
mklist(3)                                       //> res0: List[Int] = List(3, 3, 3)

for (i <- 1 to 3) yield (mklist(i))             //> res1: scala.collection.immutable.IndexedSeq[List[Int]] = Vector(List(1), Lis
                                                //| t(2, 2), List(3, 3, 3))

(1 to 3).map(mklist)                            //> res2: scala.collection.immutable.IndexedSeq[List[Int]] = Vector(List(1), Lis
                                                //| t(2, 2), List(3, 3, 3))
```
___
## What is the difference between `for()` and `for{}`?
```scala
for (
	i <- 1 to 2;
	j <- 1 to 2
) println(i, j)                                 //> (1,1)
                                                //| (1,2)
                                                //| (2,1)
                                                //| (2,2)

for {
  i <- 1 to 2
  j <- 1 to 2
} println(i, j)                                 //> (1,1)
                                                //| (1,2)
                                                //| (2,1)
                                                //| (2,2)
```

___
## Replace for-loop with ...

### simple for-loop
```scala
for (i <- 1 to 3) println (i * 2)               //> 2
                                                //| 4
                                                //| 6

(1 to 3).foreach(i => println(i * 2))           //> 2
                                                //| 4
                                                //| 6
```
- `for (x <- e1) e2` >>> `e1.foreach(x => e2)`

### for-loop to yield
```scala
for (i <- 1 to 3) yield (i * 2)                 //> res0: scala.collection.immutable.IndexedSeq[Int] = Vector(2, 4, 6)

(1 to 3).map(i => i * 2)                        //> res1: scala.collection.immutable.IndexedSeq[Int] = Vector(2, 4, 6)
```
- `for (x <- e1) yield e2` >>> `e1.map(x => e2)`

### for-loop with guard
```scala
for (i <- 1 to 3; if i % 2 != 0) yield i        //> res0: scala.collection.immutable.IndexedSeq[Int] = Vector(1, 3)

(1 to 3) filter (_ % 2 != 0)                    //> res1: scala.collection.immutable.IndexedSeq[Int] = Vector(1, 3)
```

```scala
for (i <- 1 to 3; if i % 2 != 0) yield i * 2    //> res0: scala.collection.immutable.IndexedSeq[Int] = Vector(2, 6)

(1 to 3) filter (_ % 2 != 0) map (i => i * 2)   //> res1: scala.collection.immutable.IndexedSeq[Int] = Vector(2, 6)

for (i <- (1 to 3).withFilter(_ % 2 != 0)) yield i * 2
                                                //> res2: scala.collection.immutable.IndexedSeq[Int] = Vector(2, 6)
```
- `for (x <- e1 if f) yield e2` >>> `e1.filter(f).map(x => e2)`
- `for (x <- e1 if f) yield e2` >>> `for (x <- e1.withFilter(x => f)) yield e2`

### multiple for-loop
```scala
for (i <- 1 to 2; j <- 1 to 2) yield (i, j)     //> res0: scala.collection.immutable.IndexedSeq[(Int, Int)] = Vector((1,1), (1,2
                                                //| ), (2,1), (2,2))

(1 to 2).flatMap(i => (1 to 2).map(j => (i, j)))//> res1: scala.collection.immutable.IndexedSeq[(Int, Int)] = Vector((1,1), (1,2
                                                //| ), (2,1), (2,2))
```
- `for (x <- e1; y <- e2) yield e3` >>> `e1.flatMap(x => for (y <- e2) yield e3)` >>> `e1.flatMap(x => e2.map (y => e3))`

```scala
for (
  i <- 1 to 2;
  j <- 1 to 2;
  k <- 1 to 2
) yield (i, j, k)                               //> res0: scala.collection.immutable.IndexedSeq[(Int, Int, Int)] = Vector((1,1,1
                                                //| ), (1,1,2), (1,2,1), (1,2,2), (2,1,1), (2,1,2), (2,2,1), (2,2,2))

(1 to 2).flatMap(i =>
  (1 to 2).flatMap(j =>
    (1 to 2).map(k => (i, j, k))))              //> res1: scala.collection.immutable.IndexedSeq[(Int, Int, Int)] = Vector((1,1,1
                                                //| ), (1,1,2), (1,2,1), (1,2,2), (2,1,1), (2,1,2), (2,2,1), (2,2,2))

```
- `for(;;)` 可讀性比較高

### multiple for-loop with guard
```scala
for (
  i <- 1 to 10;
  j <- 1 to 10;
  if (i + j) % 11 == 0
) yield (i, j)                                  //> res0: scala.collection.immutable.IndexedSeq[(Int, Int)] = Vector((1,10), (2,
                                                //| 9), (3,8), (4,7), (5,6), (6,5), (7,4), (8,3), (9,2), (10,1))

(1 to 10) flatMap (i =>
  (1 to 10) filter (j =>
    (i + j) % 11 == 0) map (j => (i, j)))       //> res1: scala.collection.immutable.IndexedSeq[(Int, Int)] = Vector((1,10), (2,
                                                //| 9), (3,8), (4,7), (5,6), (6,5), (7,4), (8,3), (9,2), (10,1))
```

```scala
def isPrime(i: Int) =
  if (i <= 1)
    false
  else if (i == 2)
    true
  else
    !(2 to i / 2).exists(x => i % x == 0)       //> isPrime: (i: Int)Boolean

for (
  i <- 1 until 10;
  j <- 1 until i;
  if isPrime(i + j) == true
) yield (i, j)                                  //> res0: scala.collection.immutable.IndexedSeq[(Int, Int)] = Vector((2,1), (3,2
                                                //| ), (4,1), (4,3), (5,2), (6,1), (6,5), (7,4), (7,6), (8,3), (8,5), (9,2), (9,
                                                //| 4), (9,8))

(1 until 10) flatMap (i =>
  (1 until i) filter (j =>
    isPrime(i + j)) map (j => (i, j)))          //> res1: scala.collection.immutable.IndexedSeq[(Int, Int)] = Vector((2,1), (3,2
                                                //| ), (4,1), (4,3), (5,2), (6,1), (6,5), (7,4), (7,6), (8,3), (8,5), (9,2), (9,
                                                //| 4), (9,8))
```
