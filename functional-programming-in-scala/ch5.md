# 嚴格求值與惰性求值

## `Stream` 小抄
```scala
sealed trait Stream[+A]
case object Empty extends Stream[Nothing]
case class Cons[+A](h: () => A, t: () => Stream[A]) extends Stream[A]

object Stream {
  def cons[A](hd: =>A, tl: Stream[A]): Stream[A] = {
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
def toList: List[A]
```
