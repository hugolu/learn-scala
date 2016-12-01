import Stream._

sealed trait Stream[+A] {
  def head: A
  def tail: Stream[A]

  def toList: List[A] = this match {
    case Empty => Nil: List[A]
    case Cons(h, t) => h() :: t().toList
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
