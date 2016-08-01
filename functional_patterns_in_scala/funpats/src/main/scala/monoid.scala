import simulacrum.typeclass

@typeclass
trait Monoid[A] extends Semigroup[A] {
    def zero: A
}

object Monoid {
    implicit val intMonoid = new Monoid[Int] {
        def append(x: Int, y: Int) = Semigroup[Int].append(x, y)
        def zero = 0
    }

    implicit def listMonoid[A] = new Monoid[List[A]] {
        def append(x: List[A], y: List[A]) = Semigroup[List[A]].append(x, y)
        def zero = List.empty
    }
}

object MonoidLaws extends App {
    import Monoid.ops._

    def associativity[A: Monoid](x: A, y: A, z: A) = (x |+| (y |+| z)) == ((x |+| y) |+| z)
    assert(associativity(1, 2, 3))
    assert(associativity(List("a"), List("b"), List("c")))

    def leftIdentity[A: Monoid](x: A) = (Monoid[A].zero |+| x) == x
    assert(leftIdentity(123))
    assert(leftIdentity(List("a")))

    def rightIdentity[A: Monoid](x: A) = (x |+| Monoid[A].zero) == x
    assert(rightIdentity(123))
    assert(rightIdentity(List("a")))
}
