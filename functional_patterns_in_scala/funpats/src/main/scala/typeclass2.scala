package typeclass2

import simulacrum.typeclass

@typeclass
trait Appendable[A] {
    @op("|+|") def append(x: A, y: A): A
}

object Appendable {
    implicit val intAppendable = new Appendable[Int] {
        def append(x: Int, y: Int): Int = x + y
    }

    implicit val stringAppendable = new Appendable[String] {
        def append(x: String, y: String): String = x + y
    }
}

object Test extends App {
    import Appendable.ops._

    assert((1 |+| 2) == 3)
    assert(("foo" |+| "bar") == "foobar")
}
