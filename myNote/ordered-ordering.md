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
[`Ordered`](http://www.scala-lang.org/api/current/index.html#scala.math.Ordered) 除了提供 `compare` 方法，還提供了 `<`, `>`, `<=`, `>=`

```scala
case class Point(x: Int, y: Int) extends Ordered[Point] {
  def compare(that: Point) = (this.x*this.x + this.y*this.y) - (that.x*that.x + that.y*that.y)
}

val p1 = Point(2,3)
val p2 = Point(1,4)

p1 compare p2   //> -4
p1 compareTo p2 //> -4
p1 < p2         //> true
p1 <= p2        //> true
p1 > p2         //> false
p1 >= p2        //> false
```

[`Ordered`](http://www.scala-lang.org/api/current/index.html#scala.math.Ordered$) 伴生物件提供了T到Ordered[T]的隱式轉換 (隱式參數為 `Ordering[T]`)
```scala
object Ordered {
  /* Lens from `Ordering[T]` to `Ordered[T]` */
  implicit def orderingToOrdered[T](x: T)(implicit ord: Ordering[T]): Ordered[T] = new Ordered[T] { defcompare(that: T): Int = ord.compare(x, that) }
}
```

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
val p1 = Person("rain",13)
val p2 = Person("rain",14)
import Ordered._
p1 < p2           //> True
```

### 排序
[List](http://www.scala-lang.org/api/current/index.html#scala.collection.immutable.List) 提供以下排序函數
- `def sortBy[B](f: (A) ⇒ B)(implicit ord: math.Ordering[B]): List[A]`
  - Sorts this Seq according to the Ordering which results from transforming an implicitly given Ordering with a transformation function.
- `def sortWith(lt: (A, A) ⇒ Boolean): List[A]`
  - Sorts this sequence according to a comparison function.
- `def sorted[B >: A](implicit ord: math.Ordering[B]): List[A]`
  - Sorts this sequence according to an Ordering.

```scala
val p1 =new Person("rain",24)
val p2 =new Person("rain",22)
val p3 =new Person("Lily",15)
val list = List(p1, p2, p3)   //> List(name: rain, age: 24, name: rain, age: 22, name: Lily, age: 15)
```

若调用`sorted`函数做排序，则需要指定`Ordering`隐式参数：
```scala
implicit object PersonOrdering extends Ordering[Person] { ... }
list.sorted                   //> List(name: rain, age: 22, name: rain, age: 24, name: Lily, age: 15)
```

若使用`sortWith`，则需要定义返回值为`Boolean`的比较函数：
```scala
list.sortWith { (p1: Person, p2: Person) =>
  p1.name == p2.name match {
    case false => -p1.name.compareTo(p2.name) < 0
    case _ => p1.age - p2.age < 0
  }
}                             //> List(name: rain, age: 22, name: rain, age: 24, name: Lily, age: 15)
```

若使用`sortBy`，需要指定`Ordering`隐式参数：
```scala
implicit object PersonOrdering extends Ordering[Person] { ... }
list.sortBy[Person](t => t)   //> List(name: rain, age: 22, name: rain, age: 24, name: Lily, age: 15)
```

```scala
val words = "The quick brown fox jumped over the lazy dog".split(' ')
words.sortBy(x => (x.length, x.head)) //> Array(The, dog, fox, the, lazy, over, brown, quick, jumped)
```
- 把 word 轉為 (word.length, word.head)
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

仅需定义key的隐式转换即可：
```scala
val rdd = sc.parallelize(Array(Person("rain",24), Person("rain",22), Person("Lily",15)))

implicit object PersonOrdering extends Ordering[Person] { ... }
rdd.sortBy[Person](t => t).collect()    //> Array(name: rain, age: 22, name: rain, age: 24, name: Lily, age: 15)
rdd.top(2)                              //> Array(name: Lily, age: 15, name: rain, age: 24)
rdd.takeOrdered(2)                      //> Array(name: rain, age: 22, name: rain, age: 24)
```
