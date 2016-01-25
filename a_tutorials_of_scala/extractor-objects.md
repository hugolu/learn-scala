# Extractor Objects

```scala
object Twice {
  def apply(x: Int): Int = x * 2
  def unapply(z: Int): Option[Int] = if (z%2 == 0) Some(z/2) else None
}

42 match {
  case Twice(n) => println(n)
  case _ => println("??")
}
//21

41 match {
  case Twice(n) => println(n)
  case _ => println("??")
}
//??
```
- The pattern ```case Twice(n)``` will cause an invocation of ```Twice.unapply```, which is used to match any even number
- The return type of an ```unapply``` should be chosen
  - If it returns a single sub-value of type T, return an Option[T]
