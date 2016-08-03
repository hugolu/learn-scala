import simulacrum.typeclass

@typeclass
trait Monad[F[_]] extends Applicative[F] {
    def flatMap[A, B](fa: F[A])(f: A => F[B]): F[B]
    override def ap[A, B](fa: F[A])(ff: F[A => B]) = flatMap(ff)(map(fa)(_))
    @op(">>=") def bind[A, B](fa: F[A])(f: A => F[B]): F[B]
}

object Monad {
    implicit val optionMonad = new Monad[Option] {
        def pure[A](a: A) = Some(a)
        override def map[A, B](fa: Option[A])(f: A => B) = fa.map(f)
        def flatMap[A, B](fa: Option[A])(f: A => Option[B]) = fa.flatMap(f)
        def bind[A, B](fa: Option[A])(f: A => Option[B]) = fa.flatMap(f)
    }
}

object MonadLaws extends App {
    import Monad.ops._
    def leftIdentity[F[_] : Monad, A, B](a: A, f: A => F[B]) = Monad[F].pure(a).flatMap(f(_)) == f(a)
    def rightIdentity[F[_] : Monad, A](fa: F[A]) = fa.flatMap(Monad[F].pure(_)) == fa
    def associativity[F[_] : Monad, A, B, C](fa: F[A], f: A => F[B], g: B => F[C]) = fa.flatMap(f).flatMap(g) == fa.flatMap(f(_).flatMap(g))

    assert(leftIdentity[Option, String, Int]("abc", s => Some(s.length)))
    assert(rightIdentity[Option, String](Some("abc")))
    assert(associativity[Option, String, Int, Int](Some("abc"), s => Some(s.length), x => Some(x + 1)))

    def half(n: Int): Option[Int] = if (n%2 == 0) Some(n/2) else None
    assert((Option(20) >>= half >>= half) == Some(5))
    assert((Option(20) >>= half >>= half >>= half) == None)
}
