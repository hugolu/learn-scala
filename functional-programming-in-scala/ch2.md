# Curry

## 練習一
實現 Curry - 把帶有兩個參數的函數 `f` 轉換為只有一個參數的 curry function

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

正式的轉換函數寫法如下：
```scala
def curry[A, B, C](f: (A, B) => C): (A => (B => C)) = new Function1[A, (B => C)] {
  def apply(a: A): (B => C) = (b: B) => f(a, b)
}
```

## 練習二
實現 Uncurry - 把 curry function `f` 轉換為正常帶有兩個參數的函數

```scala
def f(x: Int)(y: Int): Int = x + y
f(1)(2) //> 3

def uncurry[A, B, C](f: A => B => C): (A, B) => C = {
  def g(a: A, b: B): C = f(a)(b)
  g _
}

val g = uncurry(f)
g(1, 2) //> 3
```

正式的轉換函數寫法如下：
```scala
def uncurry[A, B, C](f: A => B => C): (A, B) => C = new Function2[A, B, C] {
  def apply(a: A, b: B): C = f(a)(b)
}
```

## 練習三
實現高階函數 `compose` 與 `andThen`，組合兩個函數為一個

```scala
def f(str: String): Int = str.size
def g(num: Int): String = num.toString

def compose[A, B, C](f: B => C, g: A => B): A => C = {
  def h(a: A) = f(g(a))
  h _
}

def andThen[A, B, C](g: A => B, f: B => C): A => C = {
  def h(a: A) = f(g(a))
  h _
}

val h1 = compose(f, g)
h1(1234)  //> 4

val h2 = andThen(g, f)
h2(1234)  //> 4
```

正式的轉換函數寫法如下：
```scala
def compose[A, B, C](f: B => C, g: A => B): A => C = new Function1[A, C] {
  def apply(a: A): C = f(g(a))
}

def andThen[A, B, C](g: A => B, f: B => C): A => C = new Function1[A, C] {
  def apply(a: A): C = f(g(a))
}
```
