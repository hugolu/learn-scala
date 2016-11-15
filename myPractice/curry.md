# Curry

## 練習一
實現 curry - 把帶有兩個參數的函數 `f` 轉換為只有一個參數的 curry function

```scala
def f(a: Int, b: Int): Int = a + b
f(1,2)  //> 3

def curry[A, B, C](f: (A, B) => C) = {
  def g(a: A) = (b: B) => f(a, b)
  g _
}

val g = curry(f)
g(1)(2) //> 3
```

比較正式的轉換函數如下：
```scala
def curry[A, B, C](f: (A, B) => C): (A => (B => C)) = new Function1[A, (B => C)] {
  def apply(a: A): (B => C) = (b: B) => f(a, b)
}
```

