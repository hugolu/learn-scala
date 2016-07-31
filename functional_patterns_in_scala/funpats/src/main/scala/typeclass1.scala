package typeclass1

trait Appendable[A] {
    def append(x: A, y: A): A
}

object Appendable {
    def apply[A: Appendable]: Appendable[A] = implicitly[Appendable[A]]

    implicit val intAppendable = new Appendable[Int] {
        def append(x: Int, y: Int): Int = x + y
    }

    implicit val stringAppendable = new Appendable[String] {
        def append(x: String, y: String): String = x + y
    }

    implicit class Foo[A: Appendable](x: A) {
        def |+|(y: A): A = Appendable[A].append(x, y)
    }
}

object Test extends App {
    import Appendable._

    assert((1 |+| 2) == 3)
    assert(("foo" |+| "bar") == "foobar")
}
