# Trait Ordered

物件要能比較，必需繼承`Trait Ordered`並實作`compare()`與`equals()`

```scala
class Rectangle(val x: Int, val y: Int) extends Ordered[Rectangle] {
  def area = x * y
  def compare(that: Rectangle) = this.area - that.area
  override def equals(a: Any) = a match {
    case that: Rectangle => this.x == that.x && this.y == that.y
    case _               => false
  }
}

val a = new Rectangle(4, 5)                     //> a  : Rectangle = $Rectangle$1@8170146
val b = new Rectangle(3, 7)                     //> b  : Rectangle = $Rectangle$1@1e549ed0
val c = new Rectangle(4, 5)                     //> c  : Rectangle = $Rectangle$1@4586793e

a > b                                           //> res0: Boolean = false
a == b                                          //> res1: Boolean = false
a < b                                           //> res2: Boolean = true
```
