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
