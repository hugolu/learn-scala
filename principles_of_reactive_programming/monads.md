# Monads

擁有 `map` 與 `flatMap` 方法的資料結構相當常見。

事實上，有個名詞用來描述這類資料結構與一些他們該有的代數法則。他們叫做 Monad。

### 什麼是 Monad?

Monad `M` 是個擁有兩個操作 `flatMap` 與 `unit` 的參數型別 `M[T]`。他們滿足一些法則

```scala
trait M[T] {
  def flatMap[U](f: T => M[U]): M[U]
}

def unit[T](x: T): M[T]
```

學術上，`flatMap` 更常被稱作 `bind`。

### Monad 範例

- `List` 是 Monad, `unit(x) = List(x)`
- `Set` 是 Monad, `unit(x) = Set(x)`
- `Option` 是 Monad, `unit(x) = Some(x)`
- `Generator` 是 Monad，`unit(x) = single(x)`

`flatMap` 是每個型別都有的操作，在 Scala 每個 Monad 的 `unit` 各不同。

### Monad 與 `map`
`map` 可由 `flatMap` 與 `unit` 組合而成。


```scala
m map f == m flatMap (x => unit(f(x)))
        == m flatMap (f andThen unit)
```

## Monad 法則

要成為 Monad，必須滿足三個法則

### Associativity (結合性)
```scala
m flatMap f faltMap g == m flatMap (x => f(x) flatMap g)
```

### Left unit (左單元性)
```scala
unit(x) flatMap f == f(x)
```

### Right unit (右單元性)
```scala
m flatMap unit == m
```

### 透過 `Option`，檢驗 Monad 法則

`Option` 的 `flatMap` 定義為
```scala
abstract class Option[+T] {
  def flatMap[U](f: T => Option[U]): Option[U] = this match {
    case Some(x)  => f(x)
    case None     => None
  }
}
```

檢查是否符合 Left unit 法則: `Some(x) flatMap f == f(x)`
```scala
Some(x) flatMap f

==  Some(x) match {
      case Some(x)  => f(x)
      case None     => None
    }
    
==  f(x)
```

檢查是否符合 Right unit 法則: `opt flatMap Some == opt`
```scala
opt flatMap Some

==  opt match {
      case Some(x)  => Some(x)
      case None     => None
    }

== opt
```

檢查是否符合 Associative 法則: `opt flatMap f flatMap g == opt flatMap (x => f(x) flatMap g)`
```scala
opt flatMap f flatMap g

==  opt match { case Some(x) => f(x) case None => None }
        match { case Some(y) => g(y) case None => None }

==  opt match {
      case Some(x) =>
        f(x) match { case Some(y) => g(y) case None => None }
      case None =>
        None match { case Some(y) => g(y) case None => None }
    }

== opt match {
      case Some(x) => f(x) flatMap g
      case None => None
    }

== opt flatMap (x => f(x) flatMap g)
```

## Significance of the Law for for-expressions

### Associativity：巢狀結構的 for-expression
```scala
for (y <- (for (x <- m; y <- f(x)) yield y) z <- g(y)) yield z

== for {
    x <- m
    y <- f(x)
    z <- g(y)
  } yield z
```

根據 for-expression 翻譯規則，`for (x <- e1; y <- e2) yield e3` == `e1 flatMap(x => for (y <- e2) yield e3)`，所以 `for (x <- m; y <- y(x)) yield y` 被翻譯成 `m flatMap(x => for (y <- f(x)) yield y)`，等於 `m flatMap(x => f(x))`，等於`m flatMap f`。

把上面巢狀結構的 for-expression 轉換到 `flatMap` domain 驗證
```scala
(m flatMap f) flatMap g

== m flatMap(x => f(x) flatMap(y =>  g(y))) == m flatMap(x => f(x) flatMap g)
```

### Right unit

```scala
for (x <- m) yield x == m
```

### Left unit

沒有對應的 for-expression

## 另一種型別 `Try`

類似 `Option` 的 `Some` 與 `None`，各為有含值的 `Success(value)` 與有例外的 `Failuare(ex)`。

```scala
abstract class Try[+T]

case class Success[T](x: T)         extends Try[T]
case class Failure(ex: Exception)   extends Try[Nothing]
```

`Try(expr)` 裡面可以放任意計算式 `expr`，回傳值為 `Success(someValue)` 或 `Failure(someException)`。以下實作，注意 `apply` 傳入參數型別為 `=> T` (call by name)，呼叫時才會真正做計算。
```scala
object Try {
  def apply[T](expr: => T): Try[T] = {
    try Success(expr)
  } catch {
    case NonFatal(ex) => Failure(ex)
  }
}
```

### 複合的 Try
```scala
for {
  x <- computeX
  y <- computeY
} yield f(x, y)
```

- 如果 `computeX` 與 `computeY` 成功得到值 `Success(x)` 與 `Success(y)`，最後產生結果 `Success(f(x,y))`
- 如果其中一個因為例外 ex 導致失敗，最終結果為 `Failure(ex)`

### `flatMap` 與 `map`

```scala
abstract class Try[+T] {
  def flatMap[U](f: T => Try[U]): Try[U] = this match {
    case Success(x) => try f(x) catch { case NonFatal(ex) => Failure(ex) }
    case fail: Failure => fail
  }
  
  def map[U](f: T => U): Try[U] = this match {
    case Success(x) => Try(f(x))
    case fail: Failure => fail
  }
}
```

```scala
t map f == t flatMap (x => Try(f(x))) == t flatMap (f andThen Try)
```

### 練習: 如果 `Try` 的 `unit = Try`，它是 Monad 嗎？

不是，因為不滿足 left unit law

```scala
Try(expr) flatMap f != f(expr)
```

左邊不會發生 non-fatal exception，但右邊會。

## 結論

1. for-expression 不只對集合有用
2. 許多型別定義 `flatMap`, `map`, `withFilter`，與 for-expression 操作 (`Generator`, `Option`, `Try`)
3. 許多定義 `flatMap` 的型別是 Monad；如果也定義 `withFilter` 就稱做 Monad with Zero
4. 三條重要的 Monad 法則可用來檢查 library API 的設計
