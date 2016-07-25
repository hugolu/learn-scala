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
- 定義 `Semigroup[Int]`，`append(x: Int, y: Int)` 回傳 x+y
- 定義 `Semigroup[String]`，`append(x: String, y: String)` 回傳 x+y
- 定義 `Semigroup[Option[A]]`，`append(x: Option[A], y: Option[A])` 回傳合併 Some 的值
- 定義 `Semigroup[List[A]]`，`append(x: List[A], y: List[A])` 回傳列表相加的結果
- 定義 `Semigroup[Map[A, B]]`，`append(x: Map[A, B], y: Map[A, B])` 回傳兩個 map 合併結果

## 拆解 `append(x: Map[A, B], y: Map[A, B])` 實作

### `def /:[B](z: B)(op: (B, (A, B)) ⇒ B): B` 
Applies a binary operator to a start value and all elements of this traversable or iterator, going left to right.

```scala
scala> (5 /: List(1,2,3,4))(_+_)
res6: Int = 15

scala> List(1,2,3,4).foldLeft(5)(_+_)
res7: Int = 15
```
- 初值給 `5`，使用 iterator 對 `List` 所有元素執行 `_+_`

### 拆解 `(x /: y)`
```scala
scala> val x = Map("a" -> 1, "b" -> 2)
x: scala.collection.immutable.Map[String,Int] = Map(a -> 1, b -> 2)

scala> val y = Map("b" -> 3, "c" -> 4)
y: scala.collection.immutable.Map[String,Int] = Map(b -> 3, c -> 4)
```

```scala
scala> (x /: y)({ (a, b) => println(a, b); a })
(Map(a -> 1, b -> 2),(b,3))
(Map(a -> 1, b -> 2),(c,4))
res10: scala.collection.immutable.Map[String,Int] = Map(a -> 1, b -> 2)
```
- 初值給 `x`，使用 iterator 對 `Map` 所有元素執行 println，回傳 `a`

```scala
scala> (x /: y)({ case(a, (k, v)) => println(a,k,v); a })
(Map(a -> 1, b -> 2),b,3)
(Map(a -> 1, b -> 2),c,4)
res11: scala.collection.immutable.Map[String,Int] = Map(a -> 1, b -> 2)
```
- 透過 pattern match 拆解 `y` 得到 `(k, v)`

### Map `updated`
```scala
scala> val x = Map("a" -> 1, "b" -> 2)

scala> x.updated("a", 2)
res15: scala.collection.immutable.Map[String,Int] = Map(a -> 2, b -> 2)
```

### Option `def fold[B](ifEmpty: ⇒ B)(f: (A) ⇒ B): B`
Returns the result of applying f to this scala.Option's value if the scala.Option is nonempty. Otherwise, evaluates expression ifEmpty.

```scala
scala> val a: Option[Int] = Some(1)
a: Option[Int] = Some(1)

scala> a.fold(2)(_+2)
res25: Int = 3
```
- `a = Some(1)`，fold 結果為 `1+2` (applying f to this scala.Option's value)

```scala
scala> val b: Option[Int] = None
b: Option[Int] = None

scala> b.fold(2)(_+2)
res26: Int = 2
```
- `b = None`，fold 結果為 `2` (evaluates expression ifEmpty)

### 合併 `Map[String, Int]`
```scala
scala> val x = Map("a" -> 1, "b" -> 2)
scala> val y = Map("b" -> 3, "c" -> 4)

scala> (x /: y)({ case(a, (k, v)) => a.updated(k, a.get(k).fold(v)(_ + v)) })
res28: scala.collection.immutable.Map[String,Int] = Map(a -> 1, b -> 5, c -> 4)
```
