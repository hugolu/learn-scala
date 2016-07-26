# Monoid

延伸閱讀：[herding cats — Monoid](http://eed3si9n.com/herding-cats/Monoid.html)

## Monoid 法則

除了滿足 Semigroup 法則，Monoid 必須滿足兩個額外的法則：

| 定律 | 公式 |
|------|------|
| 結合律 (associativity) | `(x |+| y) |+| z = x |+| (y |+| z)` |
| 左恆等 (left identity) | `Monoid[A].empty |+| x = x` |
| 右恆等 (right identity)| `x |+| Monoid[A].empty = x` |

## 實作
```scala
@typeclass
trait Monoid[A] extends Semigroup[A] {
  def zero: A
}
```
- 擴充方法 `zero`

```scala
object Monoid {
  implicit val intMonoid = new Monoid[Int] { ... }
  implicit val stringMonoid = new Monoid[String] { ... }
  implicit def optionMonoid[A : Semigroup] = new Monoid[Option[A]] { ... }
  implicit def listMonoid[A] = new Monoid[List[A]] { ... }
  implicit def mapMonoid[A, B : Semigroup] = new Monoid[Map[A, B]] { ... }
}
```
- 針對 `Monoid[Int]`, `Monoid[String]`, `Monoid[Option[A]]`, `Monoid[List[A]]`, `Monoid[Map[A, B]]` 型別定義 `append` 與 `zero`

## 測試
```scala
import Monoid.ops._
def associativity[A : Monoid](x: A, y: A, z: A) = (x |+| (y |+| z)) == ((x |+| y) |+| z)
def leftIdentity[A : Monoid](x: A) = (Monoid[A].zero |+| x) == x
def rightIdentity[A : Monoid](x: A) = (x |+| Monoid[A].zero) == x
```

### 驗證結合律
```scala
assert(associativity(1, 2, 3))
assert(associativity("a", "b", "c"))
assert(associativity(Option(1), Option(2), Option(3)))
assert(associativity(List("a"), List("b"), List("c")))
assert(associativity(Map("a" -> 1), Map("a" -> 2), Map("a" -> 3)))
```

### 驗證左恆等
```scala
assert(leftIdentity(123))
assert(leftIdentity("abc"))
assert(leftIdentity(Option(1)))
assert(leftIdentity(List("a")))
assert(leftIdentity(Map("a" -> 1)))
```

### 驗證右恆等
```scala
assert(rightIdentity(123))
assert(rightIdentity("abc"))
assert(rightIdentity(Option(1)))
assert(rightIdentity(List("a")))
assert(rightIdentity(Map("a" -> 1)))
```
