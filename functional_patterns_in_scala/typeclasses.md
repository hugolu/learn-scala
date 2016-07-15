# Typeclasses

簡化的版本
```scala
trait Foo[A] {
    def times(x: A, time: Int): A
}

object Foo {
    def apply[A: Foo]: Foo[A] = implicitly[Foo[A]]

    implicit val intFoo = new Foo[Int] {
        def times(x: Int, time: Int): Int = x * time
    }

    implicit val DoubleFoo = new Foo[Double] {
        def times(x: Double, time: Int): Double = x * time
    }

    implicit val StringFoo = new Foo[String] {
        def times(x: String, time: Int): String = x * time
    }

    implicit class Ops[A: Foo](x: A) {
        def x(time: Int): A = Foo[A].times(x, time)
    }
}

scala> import Foo._
import Foo._

scala>     println(1 x 3)
3

scala>     println(1.0 x 3)
3.0

scala>     println("hello" x 3)
hellohellohello
```
