# Chapter 2 在 Scala 中使用函數式編程

## 練習 2.1
寫一個遞歸函數，來獲取第n個 Fibonacci number，前兩個為 0 和 1，第 n 個數總是等於它前兩個的和 - 序列開始為 0, 1, 1, 2, 3, 5。應該定義為局部 (local) 尾遞歸函數。
```scala
def fib(n: Int): Int
```
```scala
def fib(n: Int): Int = {
  @annotation.tailrec
  def go(m: Int, a: Int, b: Int): Int = 
    if (m == n) a
    else go(m+1, b, a+b)
  go(0, 0, 1)
}

Range(0,10).map(fib(_)) //> Vector(0, 1, 1, 2, 3, 5, 8, 13, 21, 34)
```

## 練習 2.2
實現 isSorted 方法，檢測 Array[A] 是否按照給訂的比較函數排序：
```scala
def isSorted[A](as: Array[A], ordered: (A, A) => Boolean): Boolean
```

```scala
def isSorted[A](as: Array[A], ordered: (A, A) => Boolean): Boolean = {
  @annotation.tailrec
  def compare(n: Int): Boolean =
    if ((n+1) >= as.length) true
    else if (ordered(as(n), as(n+1)) == false) false
    else compare(n+1)

  compare(0)
}

isSorted(Array(1,2,3), (x:Int, y:Int) => x < y) //> true
isSorted(Array(1,3,2), (x:Int, y:Int) => x < y) //> false

isSorted(Array(3,2,1), (x:Int, y:Int) => x > y) //> true
isSorted(Array(3,1,2), (x:Int, y:Int) => x > y) //> false
```

## 練習 2.3
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

## 練習 2.4
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

## 練習 2.5
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
