import simulacrum.typeclass

@typeclass
trait Applicative[F[_]] extends Functor[F] {
    def pure[A](a: A): F[A]
    def ap[A, B](fa: F[A])(ff: F[A => B]): F[B]
    override def map[A, B](fa: F[A])(f: A => B) = ap(fa)(pure(f))
}

object Applicative {
    implicit val optionApplicative = new Applicative[Option] {
        def pure[A](a: A) = Some(a)
        def ap[A, B](fa: Option[A])(ff: Option[A => B]) = (fa, ff) match {
            case (Some(a), Some(f)) => Some(f(a))
            case _ => None
        }
    }
}

object ApplicativeLaws extends App {
    import Applicative.ops._

    def identity[F[_] : Applicative, A](fa: F[A]) = fa.ap(Applicative[F].pure((x: A) => x)) == fa
     def homomorphism[F[_] : Applicative, A, B](a: A, f: A => B) = Applicative[F].pure(a).ap(Applicative[F].pure(f)) == Applicative[F].pure(f(a))
       def interchange[F[_] : Applicative, A, B](a: A, ff: F[A => B]) = Applicative[F].pure(a).ap(ff) == ff.ap(Applicative[F].pure((f: A => B) => f(a)))

    assert(identity(Option("a")))
    assert(homomorphism[Option, String, Int]("abc", _.length))
    assert(interchange[Option, String, Int]("abc", Some(_.length)))
}
