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

```scala
(1 until n) flatMap (i =>
  (1 until i) filter (j => isPrime(i + j)) map
    (j => (i, j)))
```
等同於
```scala
for {
  i <- 1 until n
  j <- 1 until i
  if isPrime(i + j)
} yield (i, j)
```
"for" 表示式更易於理解

### 翻譯 for (1)
```scala
for (x <- e1) yield e2
```
被翻譯成
```scala
e1.map(x => e2)
```

### 翻譯 for (2)
```scala
for (x <- e1 if f; s) yield e2
```
`f` is a filter and `s` is a (potentially empty) sequence of generators and filters，"for" 被翻譯為
```scala
for (x <- e1.withFilter(x => f); s) yield e2
```
- `withFilter` 可以看成一種 `filter`，但不會產生 list 的中間產物

### 翻譯 for (3)
```scala
for (x <- e1; y <- e2; s) yield e3
```
被翻譯成
```scala
e1.flatMap(x => for(y <- e2; s) yield e3)
```

## For-expressions & Pattern Matching

```scala
abstract class JSON
case class JSeq(elems: List[JSON]) extends JSON
case class JObj(bindings: Map[String, JSON]) extends JSON
case class JNum(num: Double) extends JSON
case class JStr(str: String) extends JSON
case class JBool(b: Boolean) extends JSON
case object JNull extends JSON

val john = JObj(Map(
  "firstName" -> JStr("John"),
  "lastName" -> JStr("Smith"),
  "address" -> JObj(Map(
    "streetAddress" -> JStr("21 2nd Street"),
    "state" -> JStr("NY"),
    "postalCode" -> JNum(10021))),
  "phoneNumbers" -> JSeq(List(
    JObj(Map(
      "type" -> JStr("home"), "number" -> JStr("212 555-1234"))),
    JObj(Map(
      "type" -> JStr("fax"), "number" -> JStr("646 555-4567")))))))

val hugo = JObj(Map(
  "firstName" -> JStr("Hugo"),
  "lastName" -> JStr("Lu"),
  "address" -> JObj(Map(
    "streetAddress" -> JStr("12 1st Street"),
    "state" -> JStr("TP"),
    "postalCode" -> JNum(10101))),
  "phoneNumbers" -> JSeq(List(
    JObj(Map(
      "type" -> JStr("home"), "number" -> JStr("886 123-1234"))),
    JObj(Map(
      "type" -> JStr("fax"), "number" -> JStr("886 123-4567")))))))

val data = List(john, hugo)

```

找出電話開頭為"212"的人
```scala
for {
  JObj(bindings) <- data
  JSeq(phones) = bindings("phoneNumbers")
  JObj(phone) <- phones
  JStr(digits) = phone("number")
  if digits startsWith "212"
} yield (bindings("firstName"), bindings("lastName"))
                                                //> res0: List[(myTest.test20.JSON, myTest.test20.JSON)] = List((JStr(John),JSt
                                                //| r(Smith)))

```

For-expression generator `pat <- expr` 左手邊也可能是個 pattern，被翻譯成
```scala
x <- expr withFilter {
    case pat  => true
    case _    => false
  } map {
    case pat  => x
  }
```
- `withFilter` 作用在 `expr`，得到符合條件的元素
- `map` 在作用在符合條件的元素上

