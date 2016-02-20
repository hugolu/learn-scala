# My List

```scala
trait List[+T] {
  def isEmpty: Boolean
  def head: T
  def tail: List[T]
  def prepend[U >: T](x: U): List[U]
}

object Nil extends List[Nothing] {
  def isEmpty = true
  def head: Nothing = throw new NoSuchElementException("Nil.head")
  def tail: Nothing = throw new NoSuchElementException("Nil.tail")
  override def toString = "Nil"
  def prepend[T](x: T): List[T] = new Cons(x, this)
}

class Cons[T](val head: T, val tail: List[T]) extends List[T] {
  def isEmpty = false
  override def toString = head + "->" + tail
  def prepend[U >: T](x: U): List[U] = new Cons(x, this)
}

object List {
  def apply2[T](args: Seq[T]): List[T] = if (args.size == 0) Nil else new Cons(args.head, apply2(args.tail))
  def apply[T](args: T*): List[T] = apply2(args)
}
```

```scala
val list0 = List()                              //> list0  : week4.List[Nothing] = Nil
val list1 = List(1)                             //> list1  : week4.List[Int] = 1->Nil
val list2 = List(1, 2)                          //> list2  : week4.List[Int] = 1->2->Nil
val list3 = List(1, 2, 3)                       //> list3  : week4.List[Int] = 1->2->3->Nil

val x = list0.prepend(1)                        //> x  : week4.List[Int] = 1->Nil
val y = list1.prepend(2)                        //> y  : week4.List[Int] = 2->1->Nil
```

## Lower Bounds
Prepend is a natural method to have on immutable lists.

Question: How can we make it covariance-correct?

We can use a *lower bound*:
```scala
def prepend[U >: T](elem: U): List[U] = new Cons(elem, this)
```

This pass variance checks, because:
- covariant type parameters ```T``` may appear in lower bounds of method type parameters ```[U >: T]```
- contravariant type parameters ```U``` may appear in uppper bounds of method ```(elem: U)```
