# 嚴格求值與惰性求值

## `Stream` 小抄
```scala
sealed trait Stream[+A]
case object Empty extends Stream[Nothing]
case class Cons[+A](h: () => A, t: () => Stream[A]) extends Stream[A]

object Stream {
  def cons[A](hd: => A, tl: Stream[A]): Stream[A] = {
    lazy val head = hd
    lazy val tail = tl
    Cons(() => head, () => tail)
  }
  
  def empty[A]: Stream[A] = Empty
  
  def apply[A](as: A*): Stream[A] =
    if (as.isEmpty) empty else cons(as.head, apply(as.tail: _*))
}
```

## 練習 5.1
寫一個可以將 `Stream` 轉換成 `List` 的函數，它會被強制求值，可以在 REPL 下看到值得內容。
```scala
def toList[A](s: Stream[A]): List[A]
```

```scala
def toList[A](s: Stream[A]): List[A] = s match {
  case Empty => Nil: List[A]
  case Cons(h, t) => h() :: toList(t())
}

toList(Stream(1,2,3)) //> List(1, 2, 3)
```
- 這個無法做到 tail-recursion 

改寫作者作法：
```scala
def toList[A](s: Stream[A]): List[A] = {
  @annotation.tailrec
  def go(s: Stream[A], acc: List[A]): List[A] = s match {
    case Cons(h,t) => go(t(), h() :: acc)
    case _ => acc
  }
  go(s, List()).reverse
}
```

回憶 `List::foldRight` 的做法：
```scala
def foldRight[A, B](s: Stream[A], z: B)(f: (A, B) => B): B = s match {
  case Empty => z
  case Cons(h, t) => f(h(), foldRight(t(), z)(f))
}

def toListViaFoleRight[A](s: Stream[A]): List[A] =
  foldRight(s, List[A]())(_::_)

toListViaFoleRight(Stream(1,2,3)) //> List(1, 2, 3)
```
- 很可惜，這個也無法 tail-recursion

回憶 `List::foldLeft` 的做法：
```scala
@annotation.tailrec
def foldLeft[A, B](s: Stream[A], z: B)(f: (B, A) => B): B = s match {
  case Empty => z
  case Cons(h, t) => foldLeft(t(), f(z, h()))(f)
}

def foldRightViaFoldLeft[A, B](s: Stream[A], z: B)(f: (A, B) => B): B =
  foldLeft(s, (b: B) => b)((g, a) => (b: B) => g(f(a, b)))(z)

def toListViaFoldRightViaFoldLeft[A](s: Stream[A]): List[A] =
  foldRightViaFoldLeft(s, List[A]())(_::_)

toListViaFoldRightViaFoldLeft(Stream(1,2,3))  //> List(1, 2, 3)
```
- 這個可以做到 tail-recursion，不過拐了好幾個彎 XD

改寫作者加速版的做法：
```scala
def toListFast[A](s: Stream[A]): List[A] = {
  val buf = new collection.mutable.ListBuffer[A]
  @annotation.tailrec
  def go(s: Stream[A]): List[A] = s match {
    case Cons(h,t) =>
      buf += h()
      go(t())
    case _ => buf.toList
  }
  go(s)
}

toListFast(Stream(1,2,3)) //> List(1, 2, 3)
```
- 使用 mutable list 暫存運算中間結果，最後再轉成 immutable list。mutable list 的影響範圍沒有超過 `toListFast`，`toListFast` 仍可視為純函數。

## 練習 5.2
寫一個函數 `take(n)` 返回 `Stream` 中前 n 個元素，寫一個函數 `drop(n)` 返回 `Stream` 中前第 n 個元素之後的元素：
```scala
def take[A](s: Stream[A], n: Int): Stream[A]
def drop[A](s: Stream[A], n: Int): Stream[A]
```
```scala
def take[A](s: Stream[A], n: Int): Stream[A] = s match {
  case Cons(h, t) if n > 1 => cons(h(), take(t(), n-1))
  case Cons(h, _) if n == 1 => cons(h(), empty)
  case _ => empty
}

toList(take(Stream(1,2,3,4,5,6),3)) //> List(1, 2, 3)
```
```scala
def drop[A](s: Stream[A], n: Int): Stream[A] = s match {
  case Cons(_, t) if n > 0 => drop(t() , n - 1)
  case _ => s
}

toList(drop(Stream(1,2,3,4,5,6),3)) // List(4, 5, 6)
```

----
```scala
sealed trait Stream[+A] {
    def head: A
      def tail: Stream[A]
}

case object Empty extends Stream[Nothing] {
    def head = throw new java.util.NoSuchElementException("head of empty stream")
      def tail = throw new java.util.NoSuchElementException("tail of empty Stream")
        override def toString: String = "Stream()"
}

case class Cons[+A](h: () => A, t: () => Stream[A]) extends Stream[A] {
    def head = h()
      def tail = t()
        override def toString: String  = s"Stream(${h()}, ?)"
}

object Stream {
    def apply[A](as: A*): Stream[A] =
          if (as.isEmpty) Empty else Cons(() => as.head, () => apply(as.tail: _*))
}
```
