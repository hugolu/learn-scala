# 函數式數據結構

## 小抄
```scala
sealed trait List[+A]
case object Nil extends List[Nothing]
case class Cons[+A](head: A, tail: List[A]) extends List[A]

object List {
  def sum(ints: List[Int]): Int = ints match {
    case Nil => 0
    case Cons(x, xs) => x + sum(xs)
  }
  
  def product(ds: List[Double]): Double = ds match {
    case Nil => 1.0
    case Cons(0.0, _) => 0.0
    case Cons(x, xs) => x * product(xs)
  }
  
  def apply[A](as: A*): List[A] =
    if (as.isEmpty) Nil
    else Cons(as.head, apply(as.tail: _*))
}

import List._
List(1, 2, 3)                 //> Cons(1,Cons(2,Cons(3,Nil)))
sum(List(1, 2, 3))            //> 6
product(List(1.0, 2.0, 3.0))  //> 6.0
```

## 練習 3.1
下面匹配表達式結果是什麼？
```scala
val x = List(1,2,3,4,5) match {
  case Cons(x, Cons(2, Cons(4, _))) => x
  case Nil => 42
  case Cons(x, Cons(y, Cons(3, Cons(4, _)))) => x + y
  case Cons(h, t) => h + sum(t)
  case _ => Nil
}
//> 3
```

## 練習 3.2
實現 `tail` 函數，刪除一個 `List` 的第一個元素。注意這個函數的時間開銷是常量級的。

```scala
def tail(list: List[Int]): List[Int] = list match {
  case Nil => Nil
  case Cons(x, xs) => xs
}

tail(Nil)           //> Nil
tail(List(1,2,3))   //> Cons(2,Cons(3,Nil))
```

## 練習 3.3
用相同的思路，實現函數 `setHead` 用一個不同的值替代列表中的第一個元素。
```scala
def setHead(list: List[Int], head: Int): List[Int] = list match {
  case Nil => Cons(head, Nil)
  case Cons(x, xs) => Cons(head, xs)
}

setHead(Nil, 0)           //> Cons(0,Nil)
setHead(List(1,2,3), 0)   //> Cons(0,Cons(2,Cons(3,Nil)))
```

## 練習 3.4
把 `tail` 泛化為 `drop` 函數，用於從列表中刪除前 n 個元素。注意，這個函數的時間開銷只需要與 `drop` 的元素個數成正比 - 不需要複製整個列表。
```scala
def drop[A](l: List[A], n: Int): List[A]
```

```scala
def drop[A](l: List[A], n: Int): List[A] = 
  if (n == 0) l
  else l match {
      case Nil => Nil
      case Cons(x, xs) => drop(xs, n-1)
    }
    
drop(List(1,2,3), 0)  //> Cons(1,Cons(2,Cons(3,Nil)))
drop(List(1,2,3), 2)  //> Cons(3,Nil)
drop(List(1,2,3), 4)  //> Nil
```

## 練習 3.5
實現 `dropWhile` 函數，刪除列表中前綴全部符合的元素。
```scala
def dropWhile[A](l: List[A], f: A => Boolean): List[A]
```

```scala
def dropWhile[A](l: List[A], f: A => Boolean): List[A] = l match {
  case Nil => Nil
  case Cons(x, xs) => if (f(x)) dropWhile(xs, f) else Cons(x, dropWhile(xs, f))
 }
 
dropWhile(List(1,2,3,4,5), (x: Int) => x % 2 == 0)  //> Cons(1,Cons(3,Cons(5,Nil)))
```
