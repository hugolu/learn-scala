# Semigroup, Manoid, Foldable

## 參考連結
- [herding cats — Making our own typeclass with simulacrum](herding cats — Making our own typeclass with simulacrum)
- [herding cats — Semigroup](http://eed3si9n.com/herding-cats/Semigroup.html)
- [herding cats — Monoid](http://eed3si9n.com/herding-cats/Monoid.html)
- [herding cats — Using monoids to fold data structures](http://eed3si9n.com/herding-cats/using-monoids-to-fold.html)

## Semigroup
用一句話說明：Semigroup 提供合併的概念，合併要滿足[結合律](https://zh.wikipedia.org/wiki/%E7%BB%93%E5%90%88%E5%BE%8B)。

It doesn’t matter if we do `(3 * 4) * 5` or `3 * (4 * 5)`. Either way, the result is `60`. The same goes for `++`. … We call this property **associativity**. `*` is associative, and so is `++`, but `-`, for example, is not.

```scala
scala> assert { (3 * 2) * (8 * 5) == 3 * (2 * (8 * 5)) }
scala> assert { List("la") ++ (List("di") ++ List("da")) == (List("la") ++ List("di")) ++ List("da") }
```

### Semigroup Laws
- Associativity - `(x |+| y) |+| z = x |+| (y |+| z)`

full source: https://github.com/weihsiu/funpats/blob/master/src/main/scala/funpats/semigroups.scala
```scala
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
```
```scala
def associativity[A: Semigroup](x: A, y: A, z: A) = (x |+| (y |+| z)) == ((x |+| y) |+| z)
assert(associativity(1, 2, 3))
assert(associativity(List("a"), List("b"), List("c")))
```

## Monoid
用一句話說明：Monoid 除了提供合併的概念，還有 zero 的慨念。(一個 monoid 加上 zero，還是相同的值)

It seems that both `*` together with `1` and `++` along with `[]` share some common properties:

- The function takes two parameters.
- The parameters and the returned value have the same type.
- There exists such a value that doesn’t change other values when used with the binary function. (`zero`)

Type | Op | Zero | Examples
-----|----|------|---------
Int  | +  | 0    | `1 + 0` == `0 + 1` == `1`
Double  | *  | 1    | `4.3 * 1` == `1 * 4.3` == `4.3`
List | ++ | Nil  | `List(1,2,3) ++ Nil` == `Nil ++ List(1,2,3)` == `List(1,2,3)`

### Manoid Laws
- Associativity - `(x |+| y) |+| z = x |+| (y |+| z)`
- Left identity - `Monoid[A].empty |+| x = x`
- Right identity - `x |+| Monoid[A].empty = x`

full source: https://github.com/weihsiu/funpats/blob/master/src/main/scala/funpats/monoids.scala
```scala
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
```
- 定義 `listMonoid[A]` 時型別 `A` 尚未決定，使用 `def` (call by name)

```scala
def associativity[A: Monoid](x: A, y: A, z: A) = (x |+| (y |+| z)) == ((x |+| y) |+| z)
assert(associativity(1, 2, 3))
assert(associativity(List("a"), List("b"), List("c")))
```
```scala
def leftIdentity[A: Monoid](x: A) = (Monoid[A].zero |+| x) == x
assert(leftIdentity(123))
assert(leftIdentity(List("a")))
```
```scala
def rightIdentity[A: Monoid](x: A) = (x |+| Monoid[A].zero) == x
assert(rightIdentity(123))
assert(rightIdentity(List("a")))
```

## Foldable
用一句話說明：Foldable 提供加總資料結構內所有元素的概念。

Because there are so many data structures that work nicely with folds, the Foldable type class was introduced. Much like Functor is for things that can be mapped over, Foldable is for things that can be folded up!

full source: https://github.com/weihsiu/funpats/blob/master/src/main/scala/funpats/foldables.scala
```scala
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
```
- `foldLeft` 第二個參數列 `f: (B, A) => B` 遵守 `def /:[B](z: B)(op: (B, A) ⇒ B): B` 慣例
- `foldRigth` 第二個參數列 `f: (A, B) => B` 遵守 `def :\[B](z: B)(op: (A, B) ⇒ B): B` 慣例
- 定義 `listFoldable[_]` 時 `_` 尚未決定，使用 `def` (call by name)

```scala
assert(Foldable[List].foldLeft(List(1, 2, 3), 0)(_+_) == 6)
assert(Foldable[List].foldRight(List(1, 2, 3), 0)(_+_) == 6)
```
