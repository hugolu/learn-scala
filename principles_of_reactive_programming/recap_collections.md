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
```

`List` 還有其他實作的方式，可參考 https://github.com/hugolu/learn-scala/blob/master/myPractice/myList.md

事實上，collection 這些方法實作上各有不同
- 要能套用到任意 collection type，不只是 list
- 要做到 tail-recursive (避免 stack overflow)

## For-Expressions

