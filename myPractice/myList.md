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

```scala
class A { override def toString = "A" }
class B extends A { override def toString = "B" }
class C extends A { override def toString = "C" }

val listA = List(new A, new A)                  //> listA  : week4.List[week4.test.A] = A->A->Nil
val listB = List(new B, new B)                  //> listB  : week4.List[week4.test.B] = B->B->Nil
val listC = List(new C, new C)                  //> listC  : week4.List[week4.test.C] = C->C->Nil

listA.prepend(new C)                            //> res0: week4.List[week4.test.A] = C->A->A->Nil
listB.prepend(new C)                            //> res1: week4.List[week4.test.A] = C->B->B->Nil
listC.prepend(new C)                            //> res2: week4.List[week4.test.C] = C->C->C->Nil
```
- ```listC.prepend(new C)```
  - ```T = C```
  - ```(elem: C)```
  - ```U = C```
  - ```def prepend[C](elem: C): List[C]```
- ```listA.prepend(new C)```
  - ```T = A```
  - ```(elem: C)```
  - ```U = A``` (∵ ```A >: C```)
  - ```def prepend[A](elem: A): List[A]```
- ```listB.prepend(new C)```
  - ```T = B```
  - ```(elem: C)```
  - ```U = A``` (∵ ```A >: B``` & ```A >: C```)
  - ```def prepend[A](elem: A): List[A]```

___
## add map()

```scala
abstract class List[+T] {
  def isEmpty: Boolean
  def head: T
  def tail: List[T]
  def ::[U >: T](x: U): List[U]
  def map[U >: T, R](f: U => R): List[R] = if (this.isEmpty == true) Nil else f(this.head) :: this.tail.map(f)
}

object Nil extends List[Nothing] {
  def isEmpty: Boolean = true
  def head: Nothing = throw new Error("Nil.head")
  def tail: Nothing = throw new Error("Nil.tail")
  override def toString = "Nil"
  def ::[T](x: T): List[T] = new Cons(x, this)
}

class Cons[T](val head: T, val tail: List[T]) extends List[T] {
  def isEmpty: Boolean = false
  override def toString = head + "->" + tail
  def ::[U >: T](x: U): List[U] = new Cons(x, this)
}

object List {
  def apply2[T](args: Seq[T]): List[T] = if (args.size == 0) Nil else new Cons(args.head, apply2(args.tail))
  def apply[T](args: T*): List[T] = apply2(args)
}

val list = List(1, 2, 3, 4)                     //> list  : myTest.test22.List[Int] = 1->2->3->4->Nil

list.map((x: Int) => x * 2)                     //> res0: myTest.test22.List[Int] = 2->4->6->8->Nil
```
