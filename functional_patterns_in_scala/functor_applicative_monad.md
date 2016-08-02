# Functors, Applicatives, And Monads

## 參考連結
- [Functors, Applicatives, And Monads In Pictures](http://adit.io/posts/2013-04-17-functors,_applicatives,_and_monads_in_pictures.html)
- [herding cats — Functor](http://eed3si9n.com/herding-cats/Functor.html)
- [herding cats — Applicative](http://eed3si9n.com/herding-cats/Applicative.html)
- [herding cats — Monad](http://eed3si9n.com/herding-cats/Monad.html)

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

full source: https://github.com/weihsiu/funpats/blob/master/src/main/scala/funpats/functors.scala
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
    assert(associativity(Option("a"), (_: String).length, (_: Int) + 1))
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

### Applicative Laws
- Identity - `pure id <*> v = v`
- Homomorphism: `pure f <*> pure x = pure (f x)`
- Interchange: `u <*> pure y = pure ($ y) <*> u`

full source: https://github.com/weihsiu/funpats/blob/master/src/main/scala/funpats/applicatives.scala
```scala
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
```

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

### Monad Laws
- Left identity - `(Monad[F].pure(x) flatMap {f}) === f(x)`
- Right identity - `(m flatMap {Monad[F].pure(_)}) === m`
- Associativity - `(m flatMap f) flatMap g === m flatMap { x => f(x) flatMap {g} }`

```scala
import simulacrum.typeclass

@typeclass
trait Monad[F[_]] extends Applicative[F] {
    def flatMap[A, B](fa: F[A])(f: A => F[B]): F[B]
    override def ap[A, B](fa: F[A])(ff: F[A => B]) = flatMap(ff)(map(fa)(_))
}

object Monad {
    implicit val optionMonad = new Monad[Option] {
        def pure[A](a: A) = Some(a)
        override def map[A, B](fa: Option[A])(f: A => B) = fa.map(f)
        def flatMap[A, B](fa: Option[A])(f: A => Option[B]) = fa.flatMap(f)
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
}
```

## 小結
![](http://adit.io/imgs/functors/recap.png)

型別 | 實現 | 說明 
-----|------|------
`Functor` | `map` | 運行“函數”在“封裝的值”上
`Applicative` | `apply` | 運行“封裝的函數”在“封裝的值”上
`Monad` | `flatMap` | 運行“返回封裝的函數”在“封裝的值”上

## 優雅的呼叫鏈

`half` 將輸入的整數 `n` 剖半，如果整數可除以二則回傳 `n/2`，否則回傳空值 `null`。使用 haskell 連續呼叫 `half`：
```haskell
> Just 20 >>= half >>= half >>= half
Nothing
```

### nesting if
```scala
def half(n: Int): Int = if (n%2 == 0) n/2 else throw new Exception

var num = 20
val ans =
    if (num % 2 == 0) {
        num = half(num)
        if (num % 2 == 0) {
            num = half(num)
            if (num % 2 == 0) {
                half(num)
            } else {
                null
            }
        } else {
            null
        }
    } else {
        null
    }
```

### try-catch
```scala
def half(n: Int): Int = if (n%2 == 0) n/2 else throw new Exception

var num = 20
val ans =
    try {
        half(half(half(num)))
    } catch {
        case e: Exception => null
    }
```

### flatMap
```scala
def half(n: Int): Option[Int] = if (n%2 == 0) Some(n/2) else None

scala> Option(20) flatMap half flatMap half
res0: Option[Int] = Some(5)

scala> Option(20) flatMap half flatMap half flatMap half
res1: Option[Int] = None
```
