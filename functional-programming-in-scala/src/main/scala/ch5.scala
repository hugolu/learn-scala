package fpinscala.Ch5
import Stream._

sealed trait Stream[+A] {
  def head: A
  def tail: Stream[A]

  def toList: List[A] = this match {
    case Empty => Nil: List[A]
    case Cons(h, t) => h() :: t().toList
  }

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

  def take(n: Int): Stream[A] = this match {
    case Cons(h, t) if n > 1 => cons(h(), t().take(n-1))
    case Cons(h, _) if n == 1 => cons(h(), empty)
    case _ => empty
  }

  def drop(n: Int): Stream[A] = this match {
    case Cons(_, t) if n > 0 => t().drop(n-1)
    case _ => this
  }
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
  def empty[A]: Stream[A] = Empty

  def cons[A](h: => A, t: => Stream[A]): Stream[A] = {
    lazy val head = h
    lazy val tail = t
    Cons(() => head, () => tail)
  }

  def apply[A](as: A*): Stream[A] =
    if (as.isEmpty) empty else cons(as.head, apply(as.tail: _*))
}
