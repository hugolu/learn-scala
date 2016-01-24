# Classes

Classes in Scala are static templates that can be instantiated into many objects at runtime.

```scala
class Point(xc: Int, yc: Int) {
  var x: Int = xc
  var y: Int = yc
  def move(dx: Int, dy: Int) {
    x = x + dx
    y = y + dy
  }
  override def toString(): String = "(" + x + ", " + y + ")";
}

val pt = new Point(1, 2)
println(pt)
//(1, 2)
pt.move(10, 10)
println(pt)
//(11, 12)
```
