import simulacrum.typeclass

@typeclass
trait Semigroup[A] {
    @op("|+|") def append(x: A, y: A): A
}

object Semigroup {
    implicit val intSemigroup = new Semigroup[Int] {
        def append(x: Int, y: Int): Int = x + y
    }

    implicit def listSemigroup[A] = new Semigroup[List[A]] {
        def append(x: List[A], y: List[A]) = x ++ y
    }
}

object SemigroupLaws extends App {
    import Semigroup.ops._

    def associativity[A: Semigroup](x: A, y: A, z: A) = (x |+| (y |+| z)) == ((x |+| y) |+| z)
    assert(associativity(1, 2, 3))
    assert(associativity(List("a"), List("b"), List("c")))
}
