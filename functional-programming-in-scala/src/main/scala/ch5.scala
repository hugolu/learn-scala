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
