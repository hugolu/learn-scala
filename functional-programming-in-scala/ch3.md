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

## 練習 3.6
不是所有的實現都這麼令人滿意，實現一個 `init` 函數，返回一個列表，它包含原列表中刪除了最後一個元素之外的所有元素。比如，傳入 `List(1,2,3,4)` 給 `init` 函數會返回 `List(1,2,3)`，為什麼這個元素不能實現同 `tail` 一樣的常量級的時間開銷？
```scala
def init[A](l: List[A]): List[A]
```

```scala
def init[A](l: List[A]): List[A] = l match {
  case Nil => Nil
  case Cons(x, Nil) => Nil
  case Cons(x, xs) => Cons(x, init(xs))
}

init(Nil)           //> Nil
init(List(1,2,3,4)) //> Cons(1,Cons(2,Cons(3,Nil)))
```
- 因為必須遍歷整個列表才能完成 `init`，所以時間開銷與列表大小成正比。

## 練習 3.7
在入参是 0.0. 時用 `foldRight` 實現 `product` 是否可以立即停止的歸併返回 0.0? 為什麼可以或不可以？
```scala
def foldRight[A,B](l: List[A], z: B)(f: (A, B) => B): B =
  l match {
    case Nil => z
    case Cons(x, xs) => f(x, foldRight(xs, z)(f))
  }

def sum2(l: List[Int]) = foldRight(l, 0.0)(_ + _)
def product2(l: List[Double]) = foldRight(l, 1.0)(_ * _)
```

因為 `foldRight` 透過遞歸實現，如果沒有短路條件，就會遍歷整個列表。改成下面這樣，遇到 0.0 時會立即停止。
```scala
def foldRight2[A,B](l: List[A], z: B)(short: A => Boolean)(f: (A, B) => B): B =
  l match {
    case Nil => z
    case Cons(x, xs) => if (short(x)) f(x, z) else f(x, foldRight(xs, z)(short)(f))
  }

def sum3(l: List[Int]) = foldRight(l, 0.0)(_ => false)(_ + _)
def product3(l: List[Double]) = foldRight(l, 1.0)(_ == 0.0)(_ * _)
```

## 練習 3.8
當你對 `foldRight` 傳入 `Nil` 和 `Cons` 時，看看發生什麼？例如： `foldRight(List(1,2,3,4)), Nil:List[Int])(Cons(_,_))`。說到 `foldRight` 和 `List` 數據結構之間的關係，有什麼想法？

```scala
List(1,2,3,4)                                       //> Cons(1,Cons(2,Cons(3,Cons(4,Nil))))
foldRight(List(1,2,3,4), Nil: List[Int])(Cons(_,_)) //> Cons(1,Cons(2,Cons(3,Cons(4,Nil))))
```
- 很有趣，本來要做 reduce 的，但因為合併的函數又可以產生 `List`，所以對列表 foldRight 的結果又是列表自己。這個列表是透過 Cons 重新產生的。

## 練習 3.9
使用 `foldRight` 計算 `List` 的長度
```scala
def length[A](as: List[A]): Int
```

```scala
def length[A](as: List[A]): Int = foldRight(as, 0)((_,n)=>n+1)

length(Nil)         //> 0
length(List(1,2,3)) //> 3
```

## 練習 3.10
實現的 `foleRight` 不是尾遞迴，如果 List 很大可能發生 StackOverflowError。以尾遞迴方式改寫成
```scala
def foldLeft[A, B](as: List[A], z: B)(f: (A, B) => B): B
```

```scala
@annotation.tailrec
def foldLeft[A, B](l: List[A], z: B)(f: (B, A) => B): B = l match {
    case Nil => z
    case Cons(x, xs) => foldLeft(xs, f(z, x))(f)
  }
```

跟先前 `foldRight` 比較一下
```scala
def foldRight[A,B](l: List[A], z: B)(f: (A, B) => B): B = l match {
    case Nil => z
    case Cons(x, xs) => f(x, foldRight(xs, z)(f))
  }
```

## 練習 3.11
用 `foldLeft` 寫 `sum`, `product`, `length` 的函數。
```scala
def sum(ints: List[Int]): Int = foldLeft(ints, 0)(_+_)
sum(List(1,2,3,4))                //> 10

def product(ds: List[Double]): Double = foldLeft(ds, 1.0)(_*_)
product(List(1.0, 2.0, 3.0, 4.0)) //> 24

def length[A](as: List[A]): Int = foldLeft(as, 0)((n,_)=>n+1)
length(List("a", "b", "c", "d"))  //> 4
```

## 練習 3.12
寫一個對原列表元素顛倒的函數 (List(1,2,3) ⇒ List(3,2,1))，看看是否可用一種擇疊實現。

```scala
def reverse[A](list: List[A]): List[A] = {
  @annotation.tailrec
  def swap(as: List[A], bs: List[A]): List[A] = bs match {
    case Nil => as
    case Cons(x, xs) => swap(Cons(x, as), xs)
  }
  swap(Nil, list)
}

reverse(List(1,2,3))  //> Cons(3,Cons(2,Cons(1,Nil)))
```
```scala
def reverse2[A](list: List[A]): List[A] = foldLeft(list, Nil: List[A]){ (z, a) => Cons(a, z) }

reverse2(List(1,2,3)) //> Cons(3,Cons(2,Cons(1,Nil)))
```

## 練習 3.13
## 練習 3.14
## 練習 3.15
## 練習 3.16
## 練習 3.17
## 練習 3.18
## 練習 3.19
## 練習 3.20
## 練習 3.21
## 練習 3.22
## 練習 3.23
## 練習 3.24
## 練習 3.25
## 練習 3.26
## 練習 3.27
## 練習 3.28
## 練習 3.29
