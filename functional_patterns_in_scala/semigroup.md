# Semigroup

```scala
@typeclass
trait Semigroup[A] {
  @op("|+|") def append(x: A, y: A): A
}
```
- 定義 type class trait `Semigroup`

```scala
object Semigroup {
  import Semigroup.ops._
  implicit val intSemigroup = new Semigroup[Int] { ... }
  implicit val stringSemigroup = new Semigroup[String] { ... }
  implicit def optionSemigroup[A : Semigroup] = new Semigroup[Option[A]] { ... }
  implicit def listSemigroup[A] = new Semigroup[List[A]] { ... }
  implicit def mapSemigroup[A, B : Semigroup] = new Semigroup[Map[A, B]] { ... }
}
```
- 定義 `Semigroup[Int]`, `Semigroup[String]`, `Semigroup[Option[A]]`, `Semigroup[List[A]]`, `Semigroup[Map[A, B]]`
