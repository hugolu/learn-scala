# Set (集合)

參考連結
- [Scala Set(集合)](https://wizardforcel.gitbooks.io/w3school-scala/content/16.html)

## immutableSet & mutableSet
- 对不可变Set进行操作，会产生一个新的set，原来的set并没有改变，这与List一样。
- 对可变Set进行操作，改变的是该Set本身，与ListBuffer类似。

```scala
scala> val set = Set(1,2,3)
set: scala.collection.immutable.Set[Int] = Set(1, 2, 3)

scala> set += 4
<console>:12: error: value += is not a member of scala.collection.immutable.Set[Int]
       set += 4
           ^

scala> val set2 = set + 4
set2: scala.collection.immutable.Set[Int] = Set(1, 2, 3, 4)
```
```scala
scala> import scala.collection.mutable.Set
import scala.collection.mutable.Set

scala> val mutableSet = Set(1,2,3)
mutableSet: scala.collection.mutable.Set[Int] = Set(1, 2, 3)

scala> mutableSet += 4
res1: mutableSet.type = Set(1, 2, 3, 4)
```

## 基本操作

### 最大、最小值
```scala
scala> val set = Set(1,2,3,4,5)
set: scala.collection.immutable.Set[Int] = Set(5, 1, 2, 3, 4)

scala> set.max
res0: Int = 5

scala> set.min
res1: Int = 1
```

### 集合操作
```scala
scala>val set1 = Set(1,2,3)
scala>val set2 = Set(3,4,5)

// 聯集：如果元素有重复的就会移除重复的元素。
scala> set1 ++ set2
res3: scala.collection.immutable.Set[Int] = Set(5, 1, 2, 3, 4)

scala> set1.++(set2)
res4: scala.collection.immutable.Set[Int] = Set(5, 1, 2, 3, 4)

// 交集：取兩集合重疊的元素
scala> set1 & set2
res5: scala.collection.immutable.Set[Int] = Set(3)

scala> set1.&(set2)
res6: scala.collection.immutable.Set[Int] = Set(3)

// 差集：取前者set扣除兩者重複的元素
scala> set1 &~ set2
res7: scala.collection.immutable.Set[Int] = Set(1, 2)

scala> set2 &~ set1
res9: scala.collection.immutable.Set[Int] = Set(4, 5)
```
___
# 進階內容
 
參考連結
- [scala.collection.immutable Set](http://www.scala-lang.org/api/current/index.html#scala.collection.immutable.Set)
