# Scenario 0

```scala
def sum0(xs: List[Int]): Int = {
  var s = 0
  for (x <- xs)
    s += x
  s
}

scala> sum0(List(1,2,3))
res37: Int = 6
```
- `sum0` 加總 `List[Int]` 內的元素 (方法自己實作)

```scala
def sum1(xs: List[Int]): Int = xs.foldLeft(0)((a, x) => a + x)

scala> sum1(List(1,2,3))
res38: Int = 6
```
- `sum1` 加總 `List[Int]` 內的元素 (使用`List.foldLeft`)

```scala
def sum2[A : Monoid](xs: List[A]): A = xs.foldLeft(Monoid[A].zero)((a, x) => Semigroup[A].append(a, x))

scala> sum2(List(1,2,3))
res39: Int = 6
```
- 使用 `foldLeft` 對 Monoid 型別的值做加總
- `Monoid[A].zero` 給訂初值
- `(a, x) => Semigroup[A].append(a, x)` 提供匿名方法，合併初值與每個 list 元素
 
```scala
def sum3[F[_] : Foldable, A : Monoid](xs: F[A]): A = Foldable[F].foldMap(xs)(identity)

scala> sum3(List(1,2,3))
res40: Int = 6
```
- `def foldMap[A, B](fa: F[A])(f: A => B)(implicit MB: Monoid[B]): B = foldLeft(fa, MB.zero)((b, a) => MB.append(b, f(a)))`

> 最後一個還要想一想
