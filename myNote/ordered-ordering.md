# 比較集合內的元素

參考文章: [Scala 比较器：Ordered与Ordering](http://www.doc00.com/doc/10010048t)

集合排序需要對裡面的元素做比較，Scala 提供兩個特質 `Ordered` 與 `Ordering` 用於比較。`Ordered` 混入 Java `Comparable` 接口，`Ordering` 混入 Java `Comparator` 接口
。
- 实现`Comparable`接口的类，其对象具有了可比较性
- 实现`Comparator`接口的类，则提供一个外部比较器，用于比较两个对象

```scala
trait Ordered[A] extends Comparable[A]
trait Ordering[T] extends Comparator[T] with PartialOrdering[T] with Serializable
```

## `Ordered`
[`trait Ordered[A]`](http://www.scala-lang.org/api/current/index.html#scala.math.Ordered) 除了提供 `compare` 方法，還提供了 `<`, `>`, `<=`, `>=`。實現 `Ordered` 的類，對象間可以相互比較。

```scala
case class Person(name: String, age: Int) extends Ordered[Person] {
  def compare(that: Person): Int =
    this.name == that.name match {
      case false => -this.name.compareTo(that.name)
      case _ => this.age - that.age
    }
}

val p1 = Person("Rain",24)
val p2 = Person("Rain",22)
val p3 = Person("Lily",15)

p1 compareTo p2   //> 2
p1 < p2           //> false

p2 compareTo p3   //> -6
p2 < p3           //> true
```

[`Ordered`](http://www.scala-lang.org/api/current/index.html#scala.math.Ordered$) 伴生物件提供了`T`到`Ordered[T]`的隱式轉換 (隱式參數為 `Ordering[T]`)。使用時機：當 T 被 sealed 修飾，無法透過繼承擴充為 Orered。

```scala
object Ordered {
  /* Lens from `Ordering[T]` to `Ordered[T]` */
  implicit def orderingToOrdered[T](x: T)(implicit ord: Ordering[T]): Ordered[T] = new Ordered[T] { def compare(that: T): Int = ord.compare(x, that) }
}
```
- 事實上是利用 `Ordering[T]` 所提供 `compare(x: T, y: T): Int` 進行比較

## `Ordering`
Ordering 內建 `Ordering.by` 與 `Ordering.on` 可自行定義排序方式：

```scala
import scala.util.Sorting
val pairs = Array(("a", 5, 2), ("c", 3, 1), ("b", 1, 3))

// sort by 2nd element
Sorting.quickSort(pairs)(Ordering.by[(String, Int, Int), Int](_._2))

// sort by the 3rd element, then 1st
Sorting.quickSort(pairs)(Ordering[(Int, String)].on(x => (x._3, x._1)))
```
- `def by[T, S](f: (T) ⇒ S)(implicit ord: Ordering[S]): Ordering[T]`
- `def on[U](f: (U) ⇒ T): Ordering[U]`

## 實際應用

### 比較
```scala
case class Person(name: String, age: Int) {
  override def toString = "name: " + name + ", age: " + age
}
```

為了讓 `Person` 物件具有可比較性，使用 `Ordered` 伴生物件的 `orderingToOrdered` 做隱式轉換，額外提供 `PersonOrdering` 做為 `Ordering[Person]` 的隱式參數

```scala
implicit object PersonOrdering extends Ordering[Person] {
  override def compare(p1: Person, p2: Person): Int = {
    p1.name == p2.name match {
      case false => -p1.name.compareTo(p2.name)
      case _ => p1.age - p2.age
    }
  }
}
```

```scala
import Ordered._  // 提供了 T 到 Ordered[T] 的隱式轉換

val p1 = Person("rain",13)
val p2 = Person("rain",14)

p1 < p2           //> True
```

### 排序
[List](http://www.scala-lang.org/api/current/index.html#scala.collection.immutable.List) 提供以下排序函數

- `def sortBy[B](f: (A) ⇒ B)(implicit ord: math.Ordering[B]): List[A]`
  - Sorts this Seq according to **the Ordering** which results from transforming an implicitly given Ordering with a transformation function.
- `def sortWith(lt: (A, A) ⇒ Boolean): List[A]`
  - Sorts this sequence according to **a comparison function**.
- `def sorted[B >: A](implicit ord: math.Ordering[B]): List[A]`
  - Sorts this sequence according to **an Ordering**.

```scala
val p1 = Person("Rain",24)
val p2 = Person("Rain",22)
val p3 = Person("Lily",15)
val list = List(p1, p2, p3)   //> List(name: rain, age: 24, name: rain, age: 22, name: Lily, age: 15)
```

#### `sorted`
若调用`sorted`函数做排序，则需要指定`Ordering`隐式参数：

```scala
implicit object PersonOrdering extends Ordering[Person] { ... }
```
```scala
list.sorted                   //> List(name: rain, age: 22, name: rain, age: 24, name: Lily, age: 15)
```

#### `sortWith`
若使用`sortWith`，则需要定义返回值为`Boolean`的比较函数：

```scala
list.sortWith { (p1: Person, p2: Person) =>
  p1.name == p2.name match {
    case false => -p1.name.compareTo(p2.name) < 0
    case _ => p1.age - p2.age < 0
  }
}                             //> List(name: rain, age: 22, name: rain, age: 24, name: Lily, age: 15)
```

#### `sortBy`
若使用`sortBy`，需要指定`Ordering`隐式参数：
```scala
implicit object PersonOrdering extends Ordering[Person] { ... }
```
```scala
list.sortBy[Person](t => t)   //> List(name: rain, age: 22, name: rain, age: 24, name: Lily, age: 15)
```

另一個範例：
```scala
val words = "The quick brown fox jumped over the lazy dog".split(' ')

words.sortBy[(Int, Char)](x => (x.length, x.head))  //> Array(The, dog, fox, the, lazy, over, brown, quick, jumped)
words.sortBy(x => (x.length, x.head))
```

- 把 word 轉為 `(word.length, word.head) :(Int, Char)`
- scala.Ordering 提供 `Ordering[Tuple2[Int, Char]]` (先比較長度，在比較第一個字母)

## RDD sort
[RDD](http://spark.apache.org/docs/latest/api/scala/index.html#org.apache.spark.rdd.RDD)的sortBy函数，提供根据指定的key对RDD做全局的(升冪)排序。

```scala
def sortBy[K](f: (T) ⇒ K, ascending: Boolean = true, numPartitions: Int = this.partitions.length)(implicit ord: Ordering[K], ctag: ClassTag[K]): RDD[T]
def top(num: Int)(implicit ord: Ordering[T]): Array[T]
def takeOrdered(num: Int)(implicit ord: Ordering[T]): Array[T]
```
- `sortBy` returns this RDD sorted by the given key function.
- `top` returns the top k (largest) elements from this RDD as defined by the specified implicit Ordering[T] and maintains the ordering.
- `takeOrdered` returns the first k (smallest) elements from this RDD as defined by the specified implicit Ordering[T] and maintains the ordering.

```scala
val rdd = sc.parallelize(Array(Person("rain",24), Person("rain",22), Person("Lily",15)))
```

定義 `Person` 的隱式轉換：
```scala
implicit object PersonOrdering extends Ordering[Person] { ... }
```

```scala
rdd.sortBy[Person](t => t).collect()
//> Array(name: rain, age: 22, name: rain, age: 24, name: Lily, age: 15)
```
- 依照 `Person` 排序

```scala
rdd.sortBy[Int](p => p.age).collect()
//> Array(name: Lily, age: 15, name: rain, age: 22, name: rain, age: 24)
```
- 依照 `Person.age` 排序

```scala
rdd.sortBy[String](p => p.name).collect()
//> Array(name: Lily, age: 15, name: rain, age: 24, name: rain, age: 22)
```
- 依照 `Person.name` 排序

```scala
rdd.sortBy[(String, Int)](p => (p.name, p.age)).collect()
//> Array(name: Lily, age: 15, name: rain, age: 22, name: rain, age: 24)
```
- 依照 `(Person.name, Person.age)` 排序 (先 name, 後 age)

```scala
rdd.top(2)
//> Array(name: Lily, age: 15, name: rain, age: 24)
```
- `top` 取最高兩個 (降冪排序)

```scala
rdd.takeOrdered(2)
//> Array(name: rain, age: 22, name: rain, age: 24)
```
- `takeOrdered` 取最低兩個 (升冪排序)

### 使用 `Ordering.by` 提供隱式參數
```scala
abstract class RDD[T] {
  ...
  def top(num: Int)(implicit ord: Ordering[T]): Array[T]
  ...
}
```
- Returns the top k (largest) elements from this RDD as defined by the specified implicit Ordering[T] and maintains the ordering.

```scala
object Ordering {
  ...
  def by[T, S](f: (T) ⇒ S)(implicit ord: Ordering[S]): Ordering[T]
  ...
}
```
- Given f, a function from T into S, creates an Ordering[T] whose compare function is equivalent to: `def compare(x:T, y:T) = Ordering[S].compare(f(x), f(y))`

```scala
rdd.top(2)(Ordering.by[Person, (String, Int)]{ case Person(name, age) => (name, age) })
//> Array(name: rain, age: 24, name: rain, age: 22)
```
- 由 `Ordering.by` 決定排序的對象 `f: (T) ⇒ S`
- 藉由提供隱式參數 `Ordering[S]` 幫 `top` 進行排序
