# 函數式數據結構

## `List` 小抄
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

```scala
foldRight(List(1,2,3), "0")((a,b) => s"(${a.toString}+$b)") //> (1+(2+(3+0)))
foldLeft(List(1,2,3), "0")((b,a) => s"($b+${a.toString})")  //> (((0+1)+2)+3)
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
寫一個對原列表元素顛倒的函數 (List(1,2,3) ⇒ List(3,2,1))，看看是否可用一種 fold 實現。

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

使用 fold 實現：
```scala
def reverse[A](list: List[A]): List[A] = foldLeft(list, Nil: List[A]){ (z, a) => Cons(a, z) }

reverse(List(1,2,3)) //> Cons(3,Cons(2,Cons(1,Nil)))
```

## 練習 3.13 (超級難)
根據 `foldLeft` 實現 `foldRight`，這會很又用，因為以尾遞迴方式實現不管列表多大都不會發生 stack overflow。能不能根據 `foldRight` 實現 `foldLeft`？還有其他變通方式嗎？
```scala
def foldRightViaFoldLeft[A,B](l: List[A], z: B)(f: (A,B) => B): B
def foldLeftViaFoldRight[A,B](l: List[A], z: B)(f: (B,A) => B): B
```

```scala
def foldRightViaFoldLeft[A,B](l: List[A], z: B)(f: (A,B) => B): B = 
  foldLeft(reverse(l), z)((b, a) = f(a, b))
```
- 這個其實有點詐砲，先使用 `foldLeft` 實作的 `reverse` 將數列翻轉，然後再用 `foldLeft` 由左而右遞歸數列，其中套用 `f()` 也對調 a, b 參數

用 `foldRight` 實作 `foldLeft` 或是 `foldLeft` 實作 `foldRight` 太抽象，直接看解答。
```scala
def foldRightViaFoldLeft_1[A,B](l: List[A], z: B)(f: (A,B) => B): B =
  foldLeft(l, (b:B) => b)((g,a) => b => g(f(a,b)))(z)

def foldLeftViaFoldRight[A,B](l: List[A], z: B)(f: (B,A) => B): B =
  foldRight(l, (b:B) => b)((a,g) => b => g(f(b,a)))(z)
```

光看解答都很難想出怎麼做到的，只能退而求其次用更簡單的範例思考。

先思考怎麼互換函數參數。怎麼用 `apply` 做出 `applyRev2`, 怎麼用 `applyRev` 做出 `apply2`：
```scala
def apply[A,B](a:A, z:B)(f: (A,B)=>B): B = f(a,z)
apply("x","y")(_+_)     // xy

def applyRev[A,B](a:A, z:B)(f: (B,A)=>B): B = f(z,a)
applyRev("x","y")(_+_)  //> yx

def apply2[A,B](a:A, z:B)(f: (A,B)=>B): B =
  applyRev(a, (b:B)=>b)((g,a) => b => g(f(a,b)))(z)
apply2("x","y")(_+_)    //> xy

def applyRev2[A,B](a:A, z:B)(f: (B,A)=>B): B =
  apply(a, (b:B)=>b)((a,g) => b => g(f(b,a)))(z)
applyRev2("x","y")(_+_) //> yx
```

另外，怎麼套用某個方向的 fold 得到另一個方向的 fold?

先觀察 `foldLeft` 的 zero 的型別是 `String` 
```scala
val f = (b:String, a:Int) => s"($b,$a)"
foldLeft(List(1,2,3), "0")((b:String,a:Int) => f(b,a))
//> (((0,1),2),3)
```

iteration | `(a,b)` | applied `a` | applied `b` 
----------|---------|-------------|------------
1         | `(1,"0")`           | `f(b,1)`  | `f("0",1)`
2         | `(2,f("0",1))`      | `f(b,2)`  | `f(f("0",1),2)`
3         | `(3,f(f("0",1),2)`  | `f(b,3)`  | `f(f(f("0",1),2),3)`
- 求值：`f(f(f("0",1),2),3)` = `"(((0,1),2),3)"`

再觀察 `foldLeft` 的 zero 的型別是 `String=>String`
```scala
val f = (a:Int, b:String) => s"($a,$b)"
foldLeft(List(1,2,3), (b:String)=>b)((g: String=>String, a:Int) => (b:String) => g(f(a,b)))("0")
//> (1,(2,(3,0)))
```

iteration | `(g,a)` | applied `a` | applied `g`
----------|---------|-------------|------------
1         | `((b:String)=>b,1)`           | `(b:String)=>g(f(1,b))` | `(b:String)=>f(1,b)`
2         | `((b:String)=>f(1,b),2)`      | `(b:String)=>g(f(2,b))` | `(b:String)=>f(1,f(2,b))`
3         | `((b:Sting)=>f(1,f(2,b)),3)`  | `(b:String)=>g(f(3,b))` | `(b:String)=>f(1,f(2,f(3,b)))`
- 求值：`((b:String)=>f(1,f(2,f(3,b))))("0")` = `f(1,f(2,f(3,"0")))` = `"(1,(2,(3,0)))"`

## 練習 3.14
根據 `foldLeft` 或 `foldRight` 實現 append 函數。根據答案，`append` 定義為
```scala
def append[A](l:List[A], r:List[A])
```

```scala
def appendViaFoldRight[A](l:List[A], r:List[A]) =
  foldRight(l, r)((a,b) => Cons(a,b))

appendViaFoldRight(List(1,2),List(3,4)) //> Cons(1,Cons(2,Cons(3,Cons(4,Nil))))
```
```scala
def appendViaFoldLeft[A](l:List[A], r:List[A]) = 
  foldLeft(l, (b:List[A])=>b)((g,a) => b => g(Cons(a,b)))(r)

appendViaFoldLeft(List(1,2),List(3,4))  //> Cons(1,Cons(2,Cons(3,Cons(4,Nil))))
```

## 練習 3.15
寫一個函數將一組列表連接成一個單個列表。他的運行效率應該隨所有列表的總長度線性增長。試著用已經定義過的函數。
```scala
def concate[A](l: List[List[A]]): List[A]
```
```scala
def concate[A](l: List[List[A]]): List[A] =
  foldLeft(l,Nil:List[A])(appendViaFoldLeft(_,_))
  
concate(List(List(1,2), List(3,4), List(5,6)))  //> Cons(1,Cons(2,Cons(3,Cons(4,Cons(5,Cons(6,Nil))))))
```

## 練習 3.16
寫一個函數，用來轉換一個整數列表，對每個元素 +1
```scala
def addOne(l: List[Int]): List[Int]
```
```scala
def addOne(l: List[Int]): List[Int] = l match {
  case Nil => Nil
  case Cons(x,xs) => Cons(x+1, addOne(xs))
}

addOne(List(1,2,3)) //> Cons(2,Cons(3,Cons(4,Nil)))
```

## 練習 3.17
寫一個函數，將 `List[Double]` 每個值轉成 `String`
```scala
def doubleToString(l: List[Doubld]): List[String]
```
```scala
def doubleToString(l: List[Double]): List[String] = l match {
  case Nil => Nil
  case Cons(x, xs) => Cons(x.toString, doubleToString(xs))
}

doubleToString(List(1.1, 2.2, 3.3)) //> Cons(1.1,Cons(2.2,Cons(3.3,Nil)))
```

## 練習 3.18
寫一個泛化的 `map` 函數，對列表中的每個元素進行修改，並維持列表結構。
```scala
def map[A,B](as: List[A])(f: A=>B): List[B]
```
```scala
def map[A,B](as: List[A])(f: A=>B): List[B] = as match {
  case Nil => Nil
  case Cons(x, xs) => Cons(f(x), map(xs)(f))
}

map(List(1,2,3))(_+1)               //> Cons(2,Cons(3,Cons(4,Nil)))
map(List(1.1,2.2,3.3))(_.toString)  //> Cons(1.1,Cons(2.2,Cons(3.3,Nil)))
```

## 練習 3.19
寫一個 `filter` 函數，從列表中刪除所有不滿足斷言的元素，用它來刪除一個 `List[Int]` 中所有奇數
```scala
def filter[A](as: List[A])(f: A=>Boolean): List[A]
```
```scala
def filter[A](as: List[A])(f: A=>Boolean): List[A] = as match {
  case Nil => Nil
  case Cons(x, xs) => if (f(x)) Cons(x, filter(xs)(f)) else filter(xs)(f)
}

filter(List(1,2,3,4,5))(_%2==0) //> Cons(2,Cons(4,Nil))
```

## 練習 3.20
寫一個 `flatMap` 函數，跟 `map` 類似，除了傳入的函數 `f` 返回列表而非單個結果。`f` 對每個元素返回的列表會被塞到 `flapMap` 最終所要返回的列表。
```scala
def flatMap[A,B](as: List[A], f: A=>List[B]): List[B]
```
```scala
def flatMap[A,B](as: List[A])(f: A=>List[B]): List[B] = as match {
  case Nil => Nil
  case Cons(x, xs) => append(f(x), flatMap(xs)(f))
}

flatMap(List(1,2,3))(x=>List(x,x+1))  //> Cons(1,Cons(2,Cons(2,Cons(3,Cons(3,Cons(4,Nil))))))
```

## 練習 3.21
用 `flatMap` 實現 `filter`
```scala
def filter2[A](as: List[A])(f: A=>Boolean): List[A] =
  flatMap(as){ a:A => if (f(a)) List(a) else Nil }

filter2(List(1,2,3,4,5))(_%2==0)  //> Cons(2,Cons(4,Nil))
```

## 練習 3.22
寫一個函數，接受兩個列表，通過對相對應的元素的相加構造出一個新的列表。例如 List(1,2,3) 與 List(4,5,6) 得到 List(5,7,9)
```scala
def zipInts(l: List[Int], r: List[Int]): List[Int] = (l,r) match {
  case (Nil, Nil) => List(0)
  case (Cons(x,xs), Nil) => Cons(x+0, zipInts(xs, Nil))
  case (Nil, Cons(y,ys)) => Cons(0+y, zipInts(Nil, ys))
  case (Cons(x,xs), Cons(y,ys)) => Cons(x+y, zipInts(xs, ys))
}

zipInts(List(1,2,3), List(4,5,6)) //> Cons(5,Cons(7,Cons(9,Cons(0,Nil))))
```

## 練習 3.23
針對剛剛的函數泛化，不只針對整數或相加操作。
```scala
def zipWith[A,B,C](a: List[A], b: List[B])(f: (A,B) => C): List[C]
```
```scala
def zipWith[A,B,C](a: List[A], b: List[B])(f: (A,B) => C): List[C] = (a,b) match {
  case (Nil, _) => Nil
  case (_, Nil) => Nil
  case (Cons(x,xs), Cons(y,ys)) => Cons(f(x,y), zipWith(xs, ys)(f))
}

zipWith(List(1,2,3), List(4,5,6))(_+_)  //> Cons(5,Cons(7,Cons(9,Nil)))
zipWith(List(1,2,3), List(4,5,6))(_*_)  //> Cons(4,Cons(10,Cons(18,Nil)))
```

## 練習 3.24
實現 `hasSubsequence` 方法，檢測一個 `List` 子序列是否包含另一個 `List`。例如 `List(1,2,3,4)` 的子序列有 `List(1,2)`, `List(2,3)`, `List(4)` 等。
```scala
def hasSubsequence[A](sup: List[A], sub: List[A]): Boolean
```
```scala
def hasSubsequence[A](sup: List[A], sub: List[A]): Boolean = {
  def startWith[A](sup: List[A], sub: List[A]): Boolean = (sup, sub) match {
    case (_, Nil) => true
    case (Nil, _) => false
    case (Cons(x, xs), Cons(y, ys)) => if (x == y) startWith(xs,ys) else false
  }
  if (startWith(sup, sub)) true else sup match {
    case Nil => false
    case Cons(x, xs) => hasSubsequence(xs, sub)
  }
}

hasSubsequence(List(1,2,3,4), List(2,3))  //> true
hasSubsequence(List(1,2,3,4), List(3,4))  //> true
hasSubsequence(List(1,2,3,4), List(4,5))  //> false
hasSubsequence(List(1,2,3,4), List(4))    //> true
hasSubsequence(List(1,2,3,4), List(1,3))  //> false
```

## `Tree` 小抄
```scala
sealed trait Tree[+A]
case class Leaf[A](value: A) extends Tree[A]
case class Branch[A](left: Tree[A], right: Tree[A]) extends Tree[A]

Branch(Branch(Leaf("A"),Leaf("B")), Branch(Leaf("C"),Leaf("D")))
```

## 練習 3.25
寫一個 size 函數，統計一棵樹中節點數 (Tree & Branch)
```scala
def size[A](tree: Tree[A]): Int = tree match {
  case Branch(a,b) => 1 + size(a) + size(b)
  case Leaf(_) => 1
}

val tree = Branch(Branch(Leaf("A"),Leaf("B")), Branch(Leaf("C"),Leaf("D")))
size(tree) //> 7
```

## 練習 3.26
寫一個 maximum 函數，返回 Tree[Int] 中最大的元素。
```scala
def maximum(tree: Tree[Int]) = {
  def first[Int](tree: Tree[Int]): Int = tree match {
    case Leaf(a) => a
    case Branch(a,b) => first(a)
  }
  def max(tree: Tree[Int], a: Int): Int = tree match {
    case Leaf(b) => b.max(a)
    case Branch(b,c) => max(b,(max(c,a)))
  }
  max(tree, first(tree))
}

val numTree = Branch(Branch(Leaf(1),Leaf(2)), Branch(Leaf(3),Leaf(4)))
maximum(numTree)  //> 4
```
## 練習 3.27
寫一個 depth 函數，返回一棵樹從跟節點到任何葉節點最大路徑長度。
```scala
def depth[A](tree: Tree[A]): Int = {
  def maxDepth(tree: Tree[A], n: Int): Int = tree match {
    case Leaf(_) => n
    case Branch(a,b) => maxDepth(a, n+1).max(maxDepth(b, n+1))
  }
  maxDepth(tree, 0)
}

val t1 = Branch(Leaf(1),Branch(Leaf(2),Branch(Leaf(3),Leaf(4))))
val t2 = Branch(Branch(Branch(Leaf(1),Leaf(2)),Leaf(3)),Leaf(4))
depth(t1) //> 3
depth(t2) //> 3
```

## 練習 3.28
寫一個 `map` 函數，類似於 `List` 中的同名函數，接收一個函數，對樹中每個元素進行修改。
```scala
def map[A,B](tree: Tree[A])(f: A=>B): Tree[B] = tree match {
  case Leaf(a) => Leaf(f(a))
  case Branch(a,b) => Branch(map(a)(f), map(b)(f))
}

val numTree = Branch(Branch(Leaf(1),Leaf(2)), Branch(Leaf(3),Leaf(4)))
map(numTree)(_+1) //> Branch(Branch(Leaf(2),Leaf(3)),Branch(Leaf(4),Leaf(5)))
```

## 練習 3.29
泛化 `size`, `maximun`, `depth`, `map`，寫一個 `fold` 函數，對它們的相似抽象。按照庚家通用的函數標準來重新實現它們。
```scala
def foldRight[A,B](tree: Tree[A], z: B)(f: (A,B)=>B): B = tree match {
  case Leaf(a) => f(a, z)
  case Branch(a, b) => foldRight(a, foldRight(b, z)(f))(f)
}

def foldLeft[A,B](tree: Tree[A], z: B)(f: (B,A)=>B): B = tree match {
  case Leaf(a) => f(z, a)
  case Branch(a, b) => foldLeft(b, foldLeft(a, z)(f))(f)
}

val tree = Branch(Branch(Leaf("A"),Leaf("B")), Branch(Leaf("C"),Leaf("D")))
foldRight(tree, "0")((a,b)=>s"($a,$b)") //> (A,(B,(C,(D,0))))
foldLeft(tree, "0")((b,a)=>s"($b,$a)")  //> ((((0,A),B),C),D)
```
