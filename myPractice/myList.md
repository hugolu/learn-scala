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
## 加入 `map`, `flatMap`, `filter`

```scala
abstract class List[+T] {
  def isEmpty: Boolean
  def head: T
  def tail: List[T]
  def ::[U >: T](x: U): List[U]
  def ++[U >: T](other: List[U]): List[U] =
    if (this.isEmpty == true) other else head :: (tail ++ other)
  def map[U >: T, R](f: U => R): List[R] =
    if (this.isEmpty == true) Nil else f(this.head) :: this.tail.map(f)
  def flatMap[U >: T, R](f: U => List[R]): List[R] =
    if (this.isEmpty == true) Nil else f(this.head) ++ this.tail.flatMap(f)
  def filter(f: T => Boolean): List[T] =
  	if (this.isEmpty == true) Nil else {
  		if (f(this.head) == true) this.head :: this.tail.filter(f) else this.tail.filter(f)
  	}
}

object Nil extends List[Nothing] {
  override def toString = "Nil"
  def isEmpty = true
  def head = throw new NoSuchElementException("Nil.head")
  def tail = throw new NoSuchElementException("Nil.tail")
  def ::[T](x: T): List[T] = new Cons(x, this)
}

class Cons[T](val head: T, val tail: List[T]) extends List[T] {
  override def toString = head + " :: " + tail
  def isEmpty: Boolean = false
  def ::[U >: T](x: U): List[U] = new Cons(x, this)
}

object List {
  def apply[T](args: T*): List[T] = if (args.size == 0) Nil else new Cons(args.head, apply(args.tail: _*))
}
```

測試結果
```scala
val list1 = List(1, 2, 3)                       //> list1  : myTest.List[Int] = 1 :: 2 :: 3 :: Nil
val list2 = List(4, 5, 6)                       //> list2  : myTest.List[Int] = 4 :: 5 :: 6 :: Nil
val list3 = list1 ++ list2                      //> list3  : myTest.List[Int] = 1 :: 2 :: 3 :: 4 :: 5 :: 6 :: Nil
list1.map { (x: Int) => x * 2 }                 //> res0: myTest.List[Int] = 2 :: 4 :: 6 :: Nil
list1.flatMap { (x: Int) => List(x - x, x + 1) }//> res1: myTest.List[Int] = 0 :: 2 :: 0 :: 3 :: 0 :: 4 :: Nil
list3.filter { (x: Int) => x % 2 == 0 }         //> res2: myTest.List[Int] = 2 :: 4 :: 6 :: Nil
```
