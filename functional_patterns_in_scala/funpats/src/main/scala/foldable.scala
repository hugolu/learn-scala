import simulacrum.typeclass

@typeclass
trait Foldable[F[_]] {
    def foldLeft[A, B](fa: F[A], zero: B)(f: (B, A) => B): B
    def foldRight[A, B](fa: F[A], zero: B)(f: (A, B) => B): B
}

object Foldable {
    implicit def listFoldable[_] = new Foldable[List] {
        def foldLeft[A, B](fa: List[A], zero: B)(f: (B, A) => B): B = (zero /: fa)(f)
        def foldRight[A, B](fa: List[A], zero: B)(f: (A, B) => B): B = (fa :\ zero)(f)
    }
}

object FoldableExmaples extends App {
    import Foldable.ops._

    assert(Foldable[List].foldLeft(List(1, 2, 3), 0)(_+_) == 6)
    assert(Foldable[List].foldRight(List(1, 2, 3), 0)(_+_) == 6)
}
