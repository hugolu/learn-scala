# 不是用異常來處理錯誤

## `Option` 小抄
```scala
sealed trait Option[+A] {
  def map[B](f: A => B): Option[B]
  def flatMap[B](f: A => Option[B]): Option[B]
  def getOrElse[B >: A](default: => B): B
  def orElse[B >: A](ob: => Option[B]): Option[B]
  def filter(f: A => Boolean): Option[A]
}

case class Some[+A](get: A) extends Option[A]
case object None extends Option[Nothing]
```

## 練習 4.1
對 `Option` 實現之前所有函數，在實現每一個函數時試著考慮他有什麼意義，在什麼場景下使用。

```scala
sealed trait Option[+A] {
  def map[B](f: A => B): Option[B] = this match {
    case None => None
    case Some(a) => Some(f(a))
  }
  def flatMap[B](f: A => Option[B]): Option[B] = this match {
    case None => None
    case Some(a) => f(a)
  }
  def getOrElse[B >: A](default: => B): B = this match {
    case None => default
    case Some(a) => a
  }
  def orElse[B >: A](ob: => Option[B]): Option[B] = this match {
    case None => ob
    case _ => this
  }
  def filter(f: A => Boolean): Option[A] = this match {
    case None => None
    case Some(a) => if(f(a)) Some(a) else None
  }
}

case class Some[+A](get: A) extends Option[A]
case object None extends Option[Nothing]
```
```scala
val some: Option[Int] = Some(1)
val none: Option[Int] = None

some.map(_+1)                 //> Some(2)
none.map(_+1)                 //> None

some.flatMap(a => Some(a+1))  //> Some(2)
none.flatMap(a => Some(a+1))  //> None

some.getOrElse(2)             //> 1
none.getOrElse(2)             //> 2

some.orElse(Some(2))          //> Some(1)
none.orElse(Some(2))          //> Some(2)

some.filter(_%2==0)           //> None
none.filter(_%2==0)           //> None
some.filter(_%2!=0)           //> Some(1)
none.filter(_%2!=0)           //> None
```

參考作者的解答：
```scala
sealed trait Option[+A] {
  def map[B](f: A => B): Option[B] = this match {
    case None => None
    case Some(a) => Some(f(a))
  }
  def getOrElse[B >: A](default: => B): B = this match {
    case None => default
    case Some(a) => a
  }
  def flatMap[B](f: A => Option[B]): Option[B] =
    map(f) getOrElse None
  def orElse[B >: A](ob: => Option[B]): Option[B] = 
    map(Some(_)) getOrElse ob
  def filter(f: A => Boolean): Option[A] =
    flatMap(a => if (f(a)) Some(a) else None)
}

case class Some[+A](get: A) extends Option[A]
case object None extends Option[Nothing]
```

## 練習 4.2
根據 `flatMap` 實現一個 `variance` 方差函數。如果一個序列的平均值是 `m`，方差是對序列中每個元素 `x` 進行 `math.pow(x-m,2)`。
```scala
def variance(xs: Seq[Double]): Option[Double]
```

```scala
def mean(xs: Seq[Double]): Option[Double] = xs match {
  case Nil => None
  case _ => Some(xs.sum/xs.size)
}

def variance(xs: Seq[Double]): Option[Double] =
  mean(xs) flatMap (m => mean(xs.map(x => math.pow(x-m,2))))

variance(Seq(1,2,3,4,5): Seq[Double]) //> Some(2.0)
variance(Seq(3,3,3,3,3): Seq[Double]) //> Some(0.0)
variance(Seq(): Seq[Double])          //> None
```
1. 對元素 `x` 進行平均差平方 `math.pow(x-m,2)`
2. 針對每個元素... `xs map (x => math.pow(x-m,2))`
3. 將上面結果平均... `mean(xs map (x => math.pow(x-m,2)))`
4. 算出序列平均值... `mean(xs)`
5. 把序列平均值代入步驟三... `mean(xs) flatMap (m => ...)`

## 練習 4.3
寫一個泛型函數 map2，使用一個二元函數來組合兩個 `Option` 值。如果兩個 `Option` 都為 `None`，也返回 `None`。
```scala
def map2[A,B,C](a: Option[A], b: Option[B])(f: (A, B) => C): Option[C]
```

```scala
def map2[A,B,C](a: Option[A], b: Option[B])(f: (A, B) => C): Option[C] = (a,b) match {
  case (Some(aa),Some(bb)) => Some(f(aa,bb))
  case _ => None
}
```

作者的解答：
```scala
def map2[A,B,C](a: Option[A], b: Option[B])(f: (A, B) => C): Option[C] =
  a flatMap (aa => b map (bb => f(aa,bb)))
```

## 練習 4.4
寫一個 `Sequence` 函數，將一個 `Option` 列表結合為一個 `Option`。這個結果 `Option` 包含原 `Option` 列表所有元素值。如果原 `Option` 列表出現一個 `None`，函數結果也應該返回 `None`；否則結果應該是所有（使用 `Some` 包裝的）元素值的列表。
```scala
def sequence[A](a: List[Option[A]]): Option[List[A]]
```
```scala
def sequence[A](as: List[Option[A]]): Option[List[A]] = as match {
  case Nil => Some(Nil)
  case h::t => map2(h, sequence(t))(_ :: _)
}

sequence(List(Some(1), Some(2), Some(3))) //> Option[List[Int]] = Some(List(1, 2, 3))
sequence(List(Some(1), None, Some(3)))    //> Option[List[Int]] = None
```

作者的解答：
```scala
def sequence[A](as: List[Option[A]]): Option[List[A]] = as match {
  case Nil => Some(Nil)
  case h :: t => h flatMap (hh => sequence(t) map (tt => hh :: tt))
}
```

## `Try`
```scala
def Try[A](a: => A): Option[A] = 
  try Some(a)
  catch { case e: Exception => None }

Try("12345".toInt)  //> Some(12345)
Try("hello".toInt)  //> None
```

## `traverse`
```scala
def traverse[A, B](a: List[A])(f: A => Option[B]): Option[List[B]] = 
a match {
  case Nil => Some(Nil)
  case h :: t => f(h) flatMap (hh => traverse(t)(f) map (tt => hh :: tt))
}

traverse(List("123", "456", "789"))(str => Try(str.toInt))  //> Option[List[Int]] = Some(List(123, 456, 789))
traverse(List("123", "xxx", "789"))(str => Try(str.toInt))  //> Option[List[Int]] = None
```

作者的作法：
```scala
def traverse[A, B](a: List[A])(f: A => Option[B]): Option[List[B]] =
  a match {
    case Nil => Some(Nil)
    case h::t => map2(f(h), traverse(t)(f))(_ :: _)
  }

def traverse_1[A, B](a: List[A])(f: A => Option[B]): Option[List[B]] =
  a.foldRight[Option[List[B]]](Some(Nil))((h,t) => map2(f(h),t)(_ :: _))
```

## 練習 4.5
實現一個函數，它直接使用 `map` 和 `seqence`，但效率更好，只遍歷一次列表。事實上，按照 `traverse` 來實現 `sequence`。
```scala
def sequence[A](as: List[Option[A]]): Option[List[A]] =
  traverse(as)(a=>a)
```

## `Either` 小抄
```scala
sealed trait Either[+E, +A]
case class Left[+E](value: E) extends Either[E, Nothing]
case class Right[+A](value: A) extends Either[Nohting, A]
```

## 練習 4.6
實現 `Either` 版本的 `map`, `flatMap`, `orElse` 和 `map2` 函數。
```scala
trait Either[+E, +A] {
  def map[B](f: A => B): Either[E, B]
  def flatMap[EE >: E, B](f: A => Either[EE, B]): Either[EE, B]
  def orElse[EE >: E, B >: A](b: => Either[EE, B]): Either[EE, B]
  def map2[EE >: E, B, C](b: Either[EE, B])(f: (A, B) => C): Either[EE, C]
}
```

```scala
sealed trait Either[+E, +A] {
  def map[B](f: A => B): Either[E, B] =
    this match {
      case Right(a) => Right(f(a))
      case Left(e) => Left(e)
    }

  def flatMap[EE >: E, B](f: A => Either[EE, B]): Either[EE, B] =
    this match {
      case Right(a) => f(a)
      case Left(e) => Left(e)
    }

  def orElse[EE >: E, AA >: A](b: => Either[EE,AA]): Either[EE, AA] =
    this match {
      case Left(_) => b
      case Right(a) => Right(a)
    }
  def map2[EE >: E, B, C](b: Either[EE, B])(f: (A,B) => C): Either[EE, C] =
    for {
      a <- this
      bb <- b
    } yield f(a, bb)
}
case class Left[+E](value: E) extends Either[E, Nothing]
case class Right[+A](value: A) extends Either[Nothing, A]
```
```scala
def safeDiv(x: Int, y: Int): Either[Exception, Int] =
  try Right(x/y)
  catch { case e: Exception => Left(e) }

val right = safeDiv(2, 1) //> Right(2)
val left = safeDiv(2, 0)  //> Left(java.lang.ArithmeticException: / by zero)
```
```scala
right.map(_+1)                      //> Right(3)
left.map(_+1)                       //> Left(java.lang.ArithmeticException: / by zero)

right.flatMap(n => safeDiv(10, n))  //> Right(5)
left.flatMap(n => safeDiv(10, n))   //> Left(java.lang.ArithmeticException: / by zero)

right.orElse(Right(3))              //> Right(2)
left.orElse(Right(3))               //> Right(3)

right.map2(Right(3))((a,b)=>a+b)    //> Right(5)
left.map2(Right(3))((a,b)=>a+b)     //> Left(java.lang.ArithmeticException: / by zero)
```

## 練習 4.7
對 `Either` 實現 `sequence` 和 `traverse`，如果遇到錯誤返回第一個錯誤。
```scala
def sequence[E, A](es: List[Either[E, A]]): Either[E, List[A]]
def traverse[E, A, B](as: List[A])(f: A => Either[E, B]): Either[E, List[B])
```

## 練習 4.8
在這個實現裡，即使 `name` 和 `age` 都無效，`map2` 也只能報出一個錯誤。為了讓兩個錯誤都能報出來，你需要做些什麼改變？會改變 `map2` 或 `mkPerson` 的簽名嗎？或者會通過一些輔助結構創建一種新的數據結構比 `Either` 更好地滿足這一個需求嗎？這種更好的數據結構類型的 `orElse`, `traverse`, `sequence` 行為與 `Either` 有何不同？
