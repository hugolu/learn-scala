# Call by Value vs Call by Name

```scala
scala> def one = {
     |   println("will return 1")
     |   1
     | }
one: Int

scala> one
will return 1
res0: Int = 1
```

## Call by Value
```scala
scala> def showNum(n: Int) = {
     |   println("show a number")
     |   println(n)
     | }
showNum: (n: Int)Unit

scala> showNum(one)
will return 1
show a number
1
```
- ```one```的值在呼叫```showNum```的時候就被 evaluate

## Call by Name
```scala
scala> def showNum(n: => Int) = {
     |   println("show a number")
     |   println(n)
     | }
showNum: (n: => Int)Unit

scala> showNum(one)
show a number
will return 1
1
```
- ```one```的值在```showNum```使用到的時候才去 evaluate

___
## What is the difference between “def” and “val” to define a value
```
val cbv = util.Random.nextInt                   //> cbv  : Int = -1419163692
cbv                                             //> res0: Int = -1419163692
cbv                                             //> res1: Int = -1419163692

def cbn = util.Random.nextInt                   //> cbn: => Int
cbn                                             //> res2: Int = -2019876673
cbn                                             //> res3: Int = 1024155007
```
- With ```def``` you can get new value on every evaluate

## What is the difference between “def” and “val” to define a function
```
val cbv = { val r = util.Random.nextInt; () => r }
                                             //> cbv  : () => Int = <function0>
cbv()                                           //> res0: Int = -1266642976
cbv()                                           //> res1: Int = -1266642976

def cbn = { val r = util.Random.nextInt; () => r }
                                             //> cbn: => () => Int
cbn()                                           //> res2: Int = -418853578
cbn()                                           //> res3: Int = 797816830
```
- With ```def``` you can get new function on every call
- ```val``` evaluates when defined, ```def``` evaluates when called

----
## Stream

補充[Functional Programming Principles in Scala](https://class.coursera.org/progfun-005)，Lecture 7.2 提到 `Stream` 的做法

```scala
trait Stream[+A] {
  def isEmpty: Boolean
  def head: A
  def tail: Stream[A]

	override def toString = if (isEmpty == true) "Empty" else s"Stream($head, ?)"
  println(s"$this is created")
}


object Stream {
  def cons[T](_head: T, _tail: => Stream[T]) = new Stream[T] {
    def isEmpty = false
    def head = _head
    def tail = _tail
  }

  lazy val empty = new Stream[Nothing] {
    def isEmpty = true
    def head = throw new NoSuchElementException("empty.head")
    def tail = throw new NoSuchElementException("empty.tail")
  }

  def range(lo: Int, hi: Int): Stream[Int] = {
    if (lo >= hi) Stream.empty
    else Stream.cons(lo, Stream.range(lo + 1, hi))
  }
}

val s1 = Stream.range(1, 3)                     //> Stream(1, ?) is created
                                                //| s1  : myTest.Stream[Int] = Stream(1, ?)
val s2 = s1.tail                                //> Stream(2, ?) is created
                                                //| s2  : myTest.Stream[Int] = Stream(2, ?)
val s3 = s2.tail                                //> Empty is created
                                                //| s3  : myTest.Stream[Int] = Empty
```
- `def cons[T](_head: T, _tail: => Stream[T])` 傳入的 `_tail` 是 call-by-value，只有真正透過 `.tail` 存取時才會去呼叫執行 `Stream.range(lo + 1, hi)` 產生新的 `Stream[T]`
