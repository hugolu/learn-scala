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

