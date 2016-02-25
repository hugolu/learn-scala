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
for (i <- 1 to 10 if i % 2 == 0) yield i        //> res0: scala.collection.immutable.IndexedSeq[Int] = Vector(2, 4, 6, 8, 10)

(1 to 10) filter (_ % 2 == 0)                    //> res1: scala.collection.immutable.IndexedSeq[Int] = Vector(2, 4, 6, 8, 10)
```
- for loop with guard = filter

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
