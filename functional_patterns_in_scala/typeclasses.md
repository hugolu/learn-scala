# Typeclasses

簡化的版本
```scala
trait Foo[A] {                                              // type class
    def times(x: A, time: Int): A
}

object Foo {                                                // companion object of Foo
    def apply[A: Foo]: Foo[A] = implicitly[Foo[A]]          // create Foo helper

    implicit val intFoo = new Foo[Int] {                    // implicit view for Foo[Int]
        def times(x: Int, time: Int): Int = x * time
    }

    implicit val DoubleFoo = new Foo[Double] {              // implicit view for Foo[Double]
        def times(x: Double, time: Int): Double = x * time
    }

    implicit val StringFoo = new Foo[String] {              // implicit view for Foo[String]
        def times(x: String, time: Int): String = x * time
    }

    implicit class Ops[A: Foo](x: A) {                      // implicit class
        def x(time: Int): A = Foo[A].times(x, time)
    }
}
```
- 希望不透過繼承，擴充 `Int`、`Double`、`String` 的功能
- 定義 type class trait，`times` 接收型別 `A` 的參數與代表倍數的參數 `time`
- implicit class 擴充型別 `A` 的方法，如果對型別 `A` 呼叫 `x` 方法，則呼叫 `Foo[A].times(x, time)` 

```scala
scala> import Foo._
import Foo._

scala>     println(1 x 3)
3

scala>     println(1.0 x 3)
3.0

scala>     println("hello" x 3)
hellohellohello
```
