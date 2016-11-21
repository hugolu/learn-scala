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
