# Foldable

```scala
sealed trait Tree[A]
case class Branch[A](left: Tree[A], value: A, right: Tree[A]) extends Tree[A]
case class Leaf[A](value: A) extends Tree[A]
```
- `sealed` 密封类提供了一种约束:不能在类定义的文件之外定义任何新的子类

```scala
import simulacrum.typeclass

@typeclass
trait Foldable[F[_]] {
  def foldLeft[A, B](fa: F[A], zero: B)(f: (B, A) => B): B
  def foldRight[A, B](fa: F[A], zero: B)(f: (A, B) => B): B
}

object Foldable {
  implicit def listFoldable[_] = new Foldable[List] { ... }
  implicit def treeFoldable[_] = new Foldable[Tree] { ... }
}
```

## `Foldable[List]`

### foldLeft
```scala
def foldLeft[A, B](fa: List[A], zero: B)(f: (B, A) => B) = (zero /: fa)(f)
```
- 定義 fold from left to right 的實作
- List `def /:[B](z: B)(op: (B, A) ⇒ B): B`, B:zero, A:List由左至右的每個元素

```scala
scala> Foldable[List].foldLeft(List(1, 2, 3), 0)({(x,y) => println(x,y); x+y})
(0,1)
(1,2)
(3,3)
res26: Int = 6
```
- `x`: zero for each iteration
- `y`: elements in list from left to right

### foldRight
```scala
def foldRight[A, B](fa: List[A], zero: B)(f: (A, B) => B) = (fa :\ zero)(f)
```
- 定義 fold from right to left 的實作
- List `def :\[B](z: B)(op: (A, B) ⇒ B): B`, B:zero, A:List由左至右的每個元素

```scala
scala> Foldable[List].foldRight(List(1, 2, 3), 0)({(x,y) => println(x,y); x+y})
(3,0)
(2,3)
(1,5)
res25: Int = 6
```
- `x`: elements in list from left to right
- `y`: zero for each iteration

## `Foldable[Tree]`

### foldLeft
```scala
def foldLeft[A, B](fa: Tree[A], zero: B)(f: (B, A) => B) = fa match {
  case Branch(l, v, r) => foldLeft(r, f(foldLeft(l, zero)(f), v))(f)
  case Leaf(v) => f(zero, v)
}
```

### foldRight
```scala
def foldRight[A, B](fa: Tree[A], zero: B)(f: (A, B) => B): B = fa match {
  case Branch(l, v, r) => foldRight(l, f(v, foldRight(r, zero)(f)))(f)
  case Leaf(v) => f(v, zero)
}
```
