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
```scala
trait Ordered[A] extends Comparable[A]
```

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
