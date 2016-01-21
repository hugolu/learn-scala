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

## Matching on type
```scala
def addOne(n: Any): Any = {
  n match {
    case i: Int => i + 1
    case d: Double => d + 1.0
    case _ => "??"
  }
}
// addOne: (n: Any)Any

addOne(2)
// res2: Any = 3
addOne(2.0)
// res3: Any = 3.0
addOne("two")
// res4: Any = ??
```

## Case Classes
```scala
cass class Calculator(brand: String, model: String)
// defined class Calculator

val hp20b = Calculator("hp", "20b")
val hp20B = Calculator("hp", "20b")
hp20b == hp20B
// res9: Boolean = true

def calcType(calc: Calculator) = calc match {
  case Calculator("hp", "20b") => "hp-20b"
  case Calculator("hp", "30b") => "hp-30b"
  case Calculator(brand, model) => brand + "-" + model
}
// calcType: (calc: Calculator)String

val ti40b = Calculator("ti", "40b")

calcType(hp20b)
// res14: String = hp-20b
calcType(ti40b)
// res15: String = ti-40b
```

## Exceptions
```shell
$ echo -n "hello world" > present.txt
$ rm -f absent.txt
```
```scala
val lines = try {
  scala.io.Source.fromFile("absent.txt").mkString
} catch {
  case e: java.io.FileNotFoundException => "??"
} finally {
  println("finally...")
}
// finally...
// lines: String = hello world

val lines = try {
  scala.io.Source.fromFile("present.txt").mkString
} catch {
  case e: java.io.FileNotFoundException => ""
} finally {
  println("finally...")
}
// finally...
// lines: String = ??
```
