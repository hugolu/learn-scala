# Recap: Collections

Scala 擁有豐富的 Collection 階層，參考文章 [Mutable and Immutable Collections](http://docs.scala-lang.org/overviews/collections/overview.html) 的圖片

![scala.collection](http://docs.scala-lang.org/resources/images/collections.png)

## Collection Methods

同時所有 Collection 共享以下非常有用的方法
- `map`
- `flatMap`
- `filter`
- `foldLeft`
- `foldRight`
 
上一堂課介紹 pattern matching 的做法，以下應用在 `map`, `flatMap`, `filter` 上

### List `map` 理想做法
```scala
abstract class List[+T] {
  def map[U](f: T => U): List[U] = this match {
    case x :: xs  => f(x) :: xs.map(f)
    case Nil      => Nil
    }
  }
}
```

### List `flatMap` 理想作法
```scala
abstract class List[+T] {
  def flatMap[U](f: T => List[U]): List[U] = this match {
    case x :: xs  => f(x) ++ xs.flatMap(f)
    case Nil      => Nil
  }
}
```

### List `filter` 理想作法
```scala
abstract class List[+T] {
  def filter(p: T => Boolean): List[T] = this match {
    case x :: xs  => if (p(x)) x :: xs.filter(p) else xs.filter(p)
    case Nil      => Nil
  }
}

