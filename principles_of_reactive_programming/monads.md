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
