# Semigroup, Manoid, Foldable

## 參考連結
- [herding cats — Making our own typeclass with simulacrum](herding cats — Making our own typeclass with simulacrum)
- [herding cats — Semigroup](http://eed3si9n.com/herding-cats/Semigroup.html)
- [herding cats — Monoid](http://eed3si9n.com/herding-cats/Monoid.html)
- [herding cats — Using monoids to fold data structures](http://eed3si9n.com/herding-cats/using-monoids-to-fold.html)

## Semigroup
It doesn’t matter if we do `(3 * 4) * 5` or `3 * (4 * 5)`. Either way, the result is `60`. The same goes for `++`. … We call this property **associativity**. `*` is associative, and so is `++`, but `-`, for example, is not.

```scala
trait Semigroup[@sp(Int, Long, Float, Double) A] extends Any with Serializable {
  def combine(x: A, y: A): A  // Associative operation taking which combines two values.
  ...
}

scala> assert { (3 * 2) * (8 * 5) == 3 * (2 * (8 * 5)) }
scala> assert { List("la") ++ (List("di") ++ List("da")) == (List("la") ++ List("di")) ++ List("da") }
```

### Semigroup Laws
- Associativity - `(x |+| y) |+| z = x |+| (y |+| z)`

## Manoid
It seems that both `*` together with `1` and `++` along with `[]` share some common properties:

- The function takes two parameters.
- The parameters and the returned value have the same type.
- There exists such a value that doesn’t change other values when used with the binary function.

```scala
trait Monoid[@sp(Int, Long, Float, Double) A] extends Any with Semigroup[A] {
  def empty: A  // Return the identity element for this monoid
  ...
}

scala> 4 * 1
res0: Int = 4

scala> 1 * 9
res1: Int = 9

scala> List(1, 2, 3) ++ Nil
res2: List[Int] = List(1, 2, 3)

scala> Nil ++ List(0.5, 2.5)
res3: List[Double] = List(0.5, 2.5)
```

### Manoid Laws
- Associativity - `(x |+| y) |+| z = x |+| (y |+| z)`
- Left identity - `Monoid[A].empty |+| x = x`
- Right identity - `x |+| Monoid[A].empty = x`

## Foldable
Because there are so many data structures that work nicely with folds, the Foldable type class was introduced. Much like Functor is for things that can be mapped over, Foldable is for things that can be folded up!

```scala
@typeclass trait Foldable[F[_]] extends Serializable { self =>
  def foldLeft[A, B](fa: F[A], b: B)(f: (B, A) => B): B // Left associative fold on 'F' using the function 'f'.
  ...
}

scala> Foldable[List].foldLeft(List(1, 2, 3), 1) {_ * _}
res0: Int = 6
```

