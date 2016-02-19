# My List

```scala
trait List[+T] {
  def isEmpty: Boolean
  def head: T
  def tail: List[T]
}

class Cons[T](val head: T, val tail: List[T]) extends List[T] {
  def isEmpty = false
  override def toString = head + "->" + tail
}

class Nil extends List[Nothing] {
  def isEmpty = true
  def head: Nothing = throw new NoSuchElementException("Nil.head")
  def tail: Nothing = throw new NoSuchElementException("Nil.tail")
  override def toString = "Nil"
}

object List {
  //def apply[T](x1: T, x2: T): List[T] = new Cons(x1, new Cons(x2, new Nil))
  //def apply[T]() = new Nil
  
  def apply2[T](args: Seq[T]): List[T] = if (args.size == 0) new Nil else new Cons(args.head, apply2(args.tail))
  def apply[T](args: T*): List[T] = apply2(args)
}
```

```scala
val list0 = List()                              //> list0  : week4.List[Nothing] = Nil
val list1 = List(1)                             //> list1  : week4.List[Int] = 1->Nil
val list2 = List(1, 2)                          //> list2  : week4.List[Int] = 1->2->Nil
val list3 = List(1, 2, 3)                       //> list3  : week4.List[Int] = 1->2->3->Nil
```
