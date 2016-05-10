# Cheet Sheet

## For-Expressions

| For-expression | Translation |
|----------------|-------------|
| `for (x <- e1) yield e2` | `e1.map(x => e2)` |
| `for (x <- e1 if f; s) yield e2` | `for (x <- e1.withFilter(x => f); s) yield e2` |
| `for (x <- e1; y <- e2; s) yield e3` | `e1 flatMap(x => for (y <- e2; s) yield e3)` |

## Monad

### Definition

```scala
trait M[+T] {
  def flatMap(f: T => M[U]): M[U]
}

def unit[T](x: T): M[T]
```

`map` can be defined for every monad as a combination of `flatMap` and `unit`:
```scala
m map f == m flatMap(x => unit(f(x)))
```

### Laws (guidance)
| Law | Description |
|-----|-------------|
| Associativity | `(m flatMap f) flatMap g == m flatMap (x => f(x) flatMap g)` |
| Left Unit | `unit(x) flatMap f == f(x)` |
| Right Unit | `m flatMap unit == m` |
