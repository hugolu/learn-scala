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

### for-loop to yield
```scala
for (i <- 1 to 3) yield (i * 2)                 //> res0: scala.collection.immutable.IndexedSeq[Int] = Vector(2, 4, 6)

(1 to 3).map(i => i * 2)                        //> res1: scala.collection.immutable.IndexedSeq[Int] = Vector(2, 4, 6)
```

### for-loop with guard
```scala
for (i <- 1 to 3; if i % 2 != 0) yield i        //> res0: scala.collection.immutable.IndexedSeq[Int] = Vector(1, 3)

(1 to 3) filter (_ % 2 != 0)                    //> res1: scala.collection.immutable.IndexedSeq[Int] = Vector(1, 3)
```

### multiple for-loop
```scala
for (i <- 1 to 2; j <- 1 to 2) yield (i, j)     //> res0: scala.collection.immutable.IndexedSeq[(Int, Int)] = Vector((1,1), (1,2
                                                //| ), (2,1), (2,2))

(1 to 2).flatMap(i => (1 to 2).map(j => (i, j)))//> res1: scala.collection.immutable.IndexedSeq[(Int, Int)] = Vector((1,1), (1,2
                                                //| ), (2,1), (2,2))
```

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
(1 until 10) flatMap (i =>
  (1 until i) filter (j =>
    (i + j) % 5 == 0) map (j => (i, j)))        //> res0: scala.collection.immutable.IndexedSeq[(Int, Int)] = Vector((3,2), (4,1
                                                //| ), (6,4), (7,3), (8,2), (8,7), (9,1), (9,6))

for (
  i <- 1 until 10;
  j <- 1 until i;
  if (i + j) % 5 == 0
) yield (i, j)                                  //> res1: scala.collection.immutable.IndexedSeq[(Int, Int)] = Vector((3,2), (4,1
                                                //| ), (6,4), (7,3), (8,2), (8,7), (9,1), (9,6))
```
