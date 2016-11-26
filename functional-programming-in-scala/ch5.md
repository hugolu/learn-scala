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

外部函數：
```scala
def toList[A](s: Stream[A]): List[A] = s match {
  case Empty => Nil: List[A]
  case Cons(h, t) => h() :: toList(t())
}
```
 - 這個無法做到 tail-recursion 

作者作法 (寫成 `Stream` 的方法)
```scala
def toList: List[A] = {
  @annotation.tailrec
  def go(s: Stream[A], acc: List[A]): List[A] = s match {
    case Cons(h,t) => go(t(), h() :: acc)
    case _ => acc
  }
  go(this, List()).reverse
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
