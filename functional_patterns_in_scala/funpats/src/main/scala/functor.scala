import simulacrum.typeclass

@typeclass
trait Functor[F[_]] {
    def map[A, B](fa: F[A])(f: A => B): F[B]
}

object Functor {
    implicit val optionFunctor = new Functor[Option] {
        def map[A, B](fa: Option[A])(f: A => B): Option[B] = fa.map(f)
    }
}

object FunctorLaws extends App {
    import Functor.ops._

    def identity[F[_]: Functor, A](fa: F[A]) = fa.map(x => x) == fa
    def associativity[F[_]: Functor, A, B, C](fa: F[A], f: A => B, g: B => C) = fa.map(f).map(g) == fa.map(f andThen g)


    assert(identity(Option(1)))
    assert(identity(None))
    assert(associativity(Option("a"), (_: String).length, (_: Int) + 1))
    assert(associativity(None, (_: String).length, (_: Int) + 1))
}
