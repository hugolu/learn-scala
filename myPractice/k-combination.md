# 排列組合

## Reference
- http://hmkcode.com/calculate-find-all-possible-combinations-of-an-array-using-java/

## 找出可能的排列組合
```scala
val array = Array(0, 1, 2, 3, 4)                //> array  : Array[Int] = Array(0, 1, 2, 3, 4)
val size = array.length                         //> size  : Int = 5
```
- 其中`(1,2)`與`(2,1)`屬於同一種可能

例如：C5取1，得到5組可能的組合
```scala
(0), (1), (2), (3), (4)
```

例如：C5取2，得到10組可能的組合
```scala
(0, 1), (0, 2), (0, 3), (0, 4)
(1, 2), (1, 3), (1, 4)
(2, 3), (2, 4)
(3, 4)
```

例如：C5取3，得到10組可能的組合
```
(0, 1, 2), (0, 1, 3), (0, 1, 4)
(0, 2, 3), (0, 2, 4)
(0, 3, 4)
(1, 2, 3), (1, 2, 4)
(2, 3, 4)
```

### Scala Code
```
val array = Array(0, 1, 2, 3, 4)                //> array  : Array[Int] = Array(0, 1, 2, 3, 4)
val size = array.length                         //> size  : Int = 5

val combination1 = for {
  i <- 0 until size
} yield (array(i))                              //> combination1  : scala.collection.immutable.IndexedSeq[Int] = Vector(0, 1, 2,
                                                //|  3, 4)
combination1.length                             //> res0: Int = 5

val combination2 = for {
  i <- 0 until size
  j <- i + 1 until size
} yield (array(i), array(j))                    //> combination2  : scala.collection.immutable.IndexedSeq[(Int, Int)] = Vector((
                                                //| 0,1), (0,2), (0,3), (0,4), (1,2), (1,3), (1,4), (2,3), (2,4), (3,4))
combination2.length                             //> res1: Int = 10

val combination3 = for {
  i <- 0 until size
  j <- i + 1 until size
  k <- j + 1 until size
} yield (array(i), array(j), array(k))          //> combination3  : scala.collection.immutable.IndexedSeq[(Int, Int, Int)] = Vec
                                                //| tor((0,1,2), (0,1,3), (0,1,4), (0,2,3), (0,2,4), (0,3,4), (1,2,3), (1,2,4), 
                                                //| (1,3,4), (2,3,4))
combination3.length                             //> res2: Int = 10
```

## 問題
- 有更一般化的方式嗎？
- 能做到 recursion 嗎？
- 能用 `map` 的方式嗎？
