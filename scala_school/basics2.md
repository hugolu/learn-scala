# Basics continued

## apply methods
```scala
class Foo {}
// defined class Foo

object FooMaker {
  def apply() = new Foo()
}
// defined object FooMaker

val foo = FooMaker()
// foo: Foo = Foo@736338c8
```

## Object as Singleton
```scala
object Timer {
  var count = 0

  def currentCount(): Long = {
    count += 1
    count
  }
}
// defined object Timer

Timer.currentCount()
// res0: Long = 1

Timer.currentCount()
// res1: Long = 2

Timer.currentCount()
// res2: Long = 3
```

## Functions are Objects
```scala
object addOne extends Function1[Int, Int] {
  def apply(m: Int): Int = m + 1
}
// defined object addOne

addOne(1)
// res3: Int = 2
```
- ```trait Function1[-T1, +R] extends AnyRef```: (T1) ⇒ R
- A nice short-hand for extends Function1[Int, Int] is extends (Int => Int)

```scala
trait MyFunction1[T, R] {
  def apply(m: T): R
}
// defined trait MyFunction1

object addOne extends MyFunction1[Int, Int] {
  def apply(m: Int): Int = m + 1
}
// defined object addOne

addOne(1)
// res7: Int = 2
```

## Packages

colorHolder.scala:
```scala
package com.twitter.example

object colorHolder {
  val BLUE = "Blue"
  val RED = "Red"
}
```

Main.scala:
```scala
object Main {
  def main(args: Array[String]) {
    println("the color is " + com.twitter.example.colorHolder.BLUE)
  }
}
```
Compile & execute:
```shell
$ ls
colorHolder.scala  Main.scala
$ scalac *.scala
$ scala Main
the color is Blue
```

## Pattern Matching
```scala
val times = 1

times match {
  case 1 => "one"
  case 2 => "two"
  case _ => "some other number"
}
// res0: String = one

times match {
  case i if i == 1 => println("catch " + i); "one"
  case i if i == 2 => println("catch " + i); "two"
  case _ => "some other number"
}
// catch 1
// res1: String = one
```
