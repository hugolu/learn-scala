# 嚴格求值與惰性求值

## Non-strictness functions
```scala
def if2[A](cond: Boolean, onTrue: () => A, onFalse: () => A): A = if(cond) onTrue() else onFalse()
//> if2: [A](cond: Boolean, onTrue: () => A, onFalse: () => A)A

if2(true, () => println("true"), () => println("false"))  //> true
if2(false, () => println("true"), () => println("false")) //> false
```
```scala
def if3[A](cond: Boolean, onTrue: Function0[A], onFalse: Function0[A]): A = if(cond) onTrue() else onFalse()
//> if3: [A](cond: Boolean, onTrue: () => A, onFalse: () => A)A

if3(true, () => println("true"), () => println("false"))  //> true
if3(false, () => println("true"), () => println("false")) //> false
```
```scala
def if4[A](cond: Boolean, onTrue: => A, onFalse: => A): A = if(cond) onTrue else onFalse
//> if4: [A](cond: Boolean, onTrue: => A, onFalse: => A)A

if4(true, println("true"), println("false"))  //> true
if4(false, println("true"), println("false")) //> false
```

## Non-stricness in Class parameters
```scala
class Foo(n: => Int) { def getN = n }

val foo = new Foo({ println("hello"); 100 })  //> foo: Foo = Foo@5b1efaaf
foo.getN  //> hello //> 100
```
```scala
case class Bar(n: => Int)
//> <console>:1: error: `val' parameters may not be call-by-name
//> case class Bar(n: => Int)
//>                   ^
```
> 因為技術限制，參數必須是明確強制求值的 thunk，而非傳名參數。

```scala
case class Buz(n: () => Int)

val buz = Buz( () => { println("hello"); 100 }) //> buz: Buz = Buz(<function0>)
buz.n() //> hello //> 100
```

將求過的值儲存在 closure 的 lazy 變數
```scala
case class Buz(n: () => Int)

def mkBuz(n: => Int) = {
  lazy val _n = n
  Buz(() => _n)
}

val buz = mkBuz({println("hello"); 1})

buz.n() //> hello //> 1
buz.n()           //> 1
```

## `Stream` 小抄
```scala
sealed trait Stream[+A] {
  // 物件的方法放這裡...
}
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
  def toList: List[A] = this match {
    case Empty => Nil: List[A]
    case Cons(h, t) => h() :: t().toList
  }
```
- 這個無法做到 tail-recursion 

作者作法：
```scala
  def toListTailrec: List[A] = {
    @annotation.tailrec
    def go(s: Stream[A], acc: List[A]): List[A] = s match {
      case Cons(h, t) => go(t(), h() :: acc)
      case _ => acc
    }
    go(this, List[A]()).reverse
  }

  def toListFast: List[A] = {
    val buf = new collection.mutable.ListBuffer[A]
    @annotation.tailrec
    def go(s: Stream[A]): List[A] = s match {
      case Cons(h, t) => buf += h(); go(t())
      case _ => buf.toList
    }
    go(this)
  }
```
- 使用 mutable list 暫存運算中間結果，最後再轉成 immutable list。mutable list 的影響範圍沒有超過 `toListFast`，`toListFast` 仍可視為純函數。

回憶 `List::foldRight` 與 `List::foldLeft` 的做法：
```scala
  def foldRight[B](z: B)(f: (A, B) => B): B = this match {
    case Empty => z
    case Cons(h, t) => f(h(), t().foldRight(z)(f))
  }

  def toListViaFoldRight: List[A] =
    foldRight(List[A]())(_::_)

  def foldLeft[B](z: B)(f: (B, A) => B): B = {
    @annotation.tailrec
    def fold[A,B](s: Stream[A], z: B)(f: (B, A) => B): B = s match {
      case Empty => z
      case Cons(h, t) => fold(t(), f(z, h()))(f)
    }
    fold(this, z)(f)
  }

  def foldRightViaFoldLeft[B](z: B)(f: (A, B) => B): B =
    foldLeft((b: B) => b)((g, a) => (b: B) => g(f(a, b)))(z)

  def toListViaFoldRightViaFoldLeft: List[A] =
    foldRightViaFoldLeft(List[A]())(_::_)
```
> foldRight, foldLeft, foldRightviaFoldLeft... 有點 overkill 了

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

## 練習 5.3
寫一個函數 `takeWhile` 返回 `Stream` 中從起始連續滿足給訂斷言的所有元素。
```scala
def takeWhile[A](s: Stream[A], p: A => Boolean): Stream[A]
```
```scala
import Stream._
def takeWhile[A](s: Stream[A])(p: A => Boolean): Stream[A] = s match {
  case Cons(h, t) if (p(h()) == true) => cons(h(), takeWhile(t())(p))
  case _ => empty
}

toList(takeWhile(Stream(1,2,3,4,5))(_ < 4)) //> List(1, 2, 3)
```
