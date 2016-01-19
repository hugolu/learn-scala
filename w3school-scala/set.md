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

scala> set1 -- set2
res10: scala.collection.immutable.Set[Int] = Set(1, 2)

scala> set1.diff(set2)
res11: scala.collection.immutable.Set[Int] = Set(1, 2)
```
___
# 進階內容
 
參考連結
- [scala.collection.immutable Set](http://www.scala-lang.org/api/current/index.html#scala.collection.immutable.Set)

## 添加、移除
```scala
scala> val set = Set(1,2,3,4)

// +: 添加元素，結果產生新集合
scala> set + 5
res0: scala.collection.immutable.Set[Int] = Set(5, 1, 2, 3, 4)

// -: 移除元素，結果產生新集合
scala> set - 1
res1: scala.collection.immutable.Set[Int] = Set(2, 3, 4)
```

## reduce

- ```/:[B](z: B)(op: (B, A) ⇒ B): B```
       - Applies a binary operator to a start value and all elements of this traversable or iterator, going left to right.
       - ```op(...op(op(z, x_1), x_2), ..., x_n)```|
- ```:\[B](z: B)(op: (A, B) ⇒ B): B```
       - Applies a binary operator to all elements of this traversable or iterator and a start value, going right to left.
       - ```op(x_1, op(x_2, ... op(x_n, z)...))```|

```scala
scala> val set = List(1,2,3,4)
set: List[Int] = List(1, 2, 3, 4)

// 操作 number
scala> (5 /: set)(_+_)
res0: Int = 15

scala> (set :\ 5)(_+_)
res1: Int = 15

// 操作 string
scala> ("0" /: set)((a,b) => a+b.toString)
res2: String = 01234

scala> (set :\ "5")((a,b) => a+b.toString)
res3: String = 12345
```

## ```++:```
```scala
scala> val set1 = Set(1,2,3)
scala> val set2 = Set(3,4,5)
scala> val list1 = List(1,2,3)
scala> val list2 = List(3,4,5)

scala> set1 ++: list2
res0: List[Int] = List(1, 2, 3, 3, 4, 5)

scala> list1 ++: set2
res1: scala.collection.immutable.Set[Int] = Set(5, 1, 2, 3, 4)
```

## ```addString()```
```scala
scala> val a = Set(1,2,3)
a: scala.collection.immutable.Set[Int] = Set(1, 2, 3)

scala> val b = new StringBuilder()
b: StringBuilder =

scala> a.addString(b)
res2: StringBuilder = 123
```

## 測試是否包含
```scala
scala> val a = Set(1,2,3)

scala> a.apply(2)
res5: Boolean = true

scala> a.apply(5)
res6: Boolean = false

scala> a.contains(2)
res8: Boolean = true

scala> a.contains(5)
res9: Boolean = false
```

## 迭代器
```scala
scala> val a = Set(1,2,3)
scala> val iter = set.iterator

scala> while (iter.hasNext) { println(iter.next) }
1
2
3
```
