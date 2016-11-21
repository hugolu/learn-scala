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

