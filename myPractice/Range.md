# Range

```scala
class Range extends AbstractSeq[Int] with IndexedSeq[Int] with CustomParallelizable[Int, ParRange] with Serializable
```

## 表示法
```scala
1 to 3              //> res0: scala.collection.immutable.Range.Inclusive = Range(1, 2, 3)
(1 to 3)            //> res1: scala.collection.immutable.Range.Inclusive = Range(1, 2, 3)

1 until 3           //> res2: scala.collection.immutable.Range = Range(1, 2)
(1 until 3)         //> res3: scala.collection.immutable.Range = Range(1, 2)

Range(1, 10)        //> res4: scala.collection.immutable.Range = Range(1, 2, 3, 4, 5, 6, 7, 8, 9)
Range(1, 10, 2)     //> res5: scala.collection.immutable.Range = Range(1, 3, 5, 7, 9)
Range(10, 1, -2)    //> res6: scala.collection.immutable.Range = Range(10, 8, 6, 4, 2)
```

## for-loop
```scala
(1 to 3).foreach(println)                        //> 1
                                                //| 2
                                                //| 3
                                                
for (i <- 1 to 3) println(i)                     //> 1
                                                //| 2
                                                //| 3
```

## test
```scala
(1 to 3) forall { n => println(n); n > 0 }      //> 1
                                                //| 2
                                                //| 3
                                                //| res0: Boolean = true

(1 to 3) exists { n => println(n); n > 0 }      //> 1
                                                //| res1: Boolean = true
```
- `forall`: Tests whether a predicate holds for all elements of this iterable collection.
- `exists`: Tests whether a predicate holds for some of the elements of this iterable collection.
