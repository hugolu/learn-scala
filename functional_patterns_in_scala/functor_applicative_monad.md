# Functors, Applicatives, And Monads

## 參考連結
- [Functors, Applicatives, And Monads In Pictures](http://adit.io/posts/2013-04-17-functors,_applicatives,_and_monads_in_pictures.html)

## Functors
```scala
scala> Some(2).map(_+3)
res0: Option[Int] = Some(5)
```
![](http://adit.io/imgs/functors/fmap_just.png)

```scala
scala> (None: Option[Int]).map(_+3)
res1: Option[Int] = None
```
![](http://adit.io/imgs/functors/fmap_nothing.png)

### Functor Laws
- Identity - The first functor law states that if we map the `id` function over a functor, the functor that we get back should be the same as the original functor.
- Associativity - The second law says that composing two functions and then mapping the resulting function over a functor should be the same as first mapping one function over the functor and then mapping the other one.

```scala
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
```

## Applicatives
```scala
def ap[A,B](ff: Option[A => B])(fa: Option[A]): Option[B] = (fa, ff) match {
  case (Some(a), Some(f)) => Some(f(a))
  case _ => None
}

scala> ap(Some{x: Int => x + 3})(Some(2))
res2: Option[Int] = Some(5)
```
![](http://adit.io/imgs/functors/applicative_just.png)

## Monads
```scala
scala> def half(n: Int): Option[Int] = if (n % 2 == 0) Some(n/2) else None
half: (n: Int)Option[Int]

scala> Some(3).flatMap(half)
res3: Option[Int] = None

scala> Some(4).flatMap(half)
res4: Option[Int] = Some(2)
```
![](http://adit.io/imgs/functors/monad_just.png)

```scala
scala> None.flatMap(half)
res5: Option[Int] = None
```
![](http://adit.io/imgs/functors/monad_nothing.png)

## 小結
![](http://adit.io/imgs/functors/recap.png)

型別 | 實現 | 說明 
-----|------|------
`Functor` | `map` | 運行“函數”在“封裝的值”上
`Applicative` | `apply` | 運行“封裝的函數”在“封裝的值”上
`Monad` | `flatMap` | 運行“返回封裝的函數”在“封裝的值”上
