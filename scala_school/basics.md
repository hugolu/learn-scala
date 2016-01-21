# Basics

## Values
```scala
val x = 1
// x: Int = 1

x = 2
// <console>:11: error: reassignment to val
```

## Variables
```scala
var y = 1
// y: Int = 1

y = 2
// y: Int = 2
```

## Functions
```scala
def addOne(n: Int) = { n + 1 }
// addOne: (n: Int)Int

def addOne(n: Int) = n + 1
// addOne: (n: Int)Int

addOne(2)
// res0: Int = 3
```

## Anonymous functions
```scala
(n: Int) => n + 1
// res1: Int => Int = <function1>

res1(2)
// res2: Int = 3

val addOne = (n: Int) => n + 1
// addOne: Int => Int = <function1>

addOne(2)
// res3: Int = 3

val addOne = { (n: Int) =>
  println(n + " + 1 =")
  n + 1
}
// addOne: Int => Int = <function1>

addOne(2)
// 2 + 1 =
// res4: Int = 3
```

## Partial application
```scala
def adder(x: Int, y: Int) = x + y
//adder: (x: Int, y: Int)Int

val add2 = adder(2, _:Int)
// add2: Int => Int = <function1>

add2(3)
// res5: Int = 5
```

## Curried functions
```scala
def multiply(x: Int)(y: Int) = x * y
// multiply: (x: Int)(y: Int)Int

multiply(2)(3)
// res6: Int = 6

val timesTwo = multiply(2)_
// timesTwo: Int => Int = <function1>

timesTwo(3)
// res7: Int = 6
```
```scala
def adder(x: Int, y: Int) = x + y
// adder: (x: Int, y: Int)Int

val curriedAdder = (adder _).curried
// curriedAdder: Int => (Int => Int) = <function1>

curriedAdder(2)(3)
// res9: Int = 5
```

## Variable length arguments
```scala
def capitalizeAll(args: String*) = args.map(arg => arg.capitalize)
// capitalizeAll: (args: String*)Seq[String]

capitalizeAll("hello", "world", "scala")
// res10: Seq[String] = ArrayBuffer(Hello, World, Scala)
```

## Classes
```scala
class Calculator {
  val brand: String = "HP"
  def add(x: Int, y: Int) = x + y
}
// defined class Calculator

val calc = new Calculator
// calc: Calculator = Calculator@2fce5037

calc.add(1, 2)
// res11: Int = 3

calc.brand
// res12: String = HP
```

## Constructor
```scala
class Calculator(brand: String) {
  val color: String = if (brand == "TI") { "blue" } else if (brand == "HP") { "black" } else { "white" }
  def add(x: Int, y: Int) = x + y
}
// defined class Calculator

val calc = new Calculator("HP")
// calc: Calculator = Calculator@6c089bd6

calc.color
// res13: String = black
```

## Inheritance
```scala
class Calculator(brand: String) {
  val color: String = if (brand == "TI") { "blue" } else if (brand == "HP") { "black" } else { "white" }
  def add(x: Int, y: Int) = x + y
}
// defined class Calculator

class ScientificCalculator(brand: String) extends Calculator(brand) {
  def log(m: Double, base: Double) = math.log(m) / math.log(base)
}
// defined class ScientificCalculator

val calc = new ScientificCalculator("TI")
// calc: ScientificCalculator = ScientificCalculator@263f94f3

calc.color
// res14: String = blue

calc.log(4, 2)
// res15: Double = 2.0
```

## Abstract Classes
```scala
abstract class Shape {
  def getArea(): Int
}
// defined class Shape

class Circle(r: Int) extends Shape {
  def getArea(): Int = r * r * 3
}
// defined class Circle

val s: Shape = new Circle(2)
// s: Shape = Circle@265e3ee4

s.getArea
// res18: Int = 12
```

## Traits
```scala
trait Car {
  val brand: String
}
// defined trait Car

trait Shiny {
  val shineRefraction: Int
}
// defined trait Shiny

class BMW extends Car with Shiny {
  val brand = "BMW"
  val shineRefraction= 12
}
// defined class BMW

val car = new BMW
// car: BMW = BMW@31c4b348

car.brand
// res0: String = BMW
car.shineRefraction
// res1: Int = 12
```

## Types
```scala
abstract class Shape[T] {
  def getArea():T
}
// defined class Shape

class Square(l: Int) extends Shape[Int] {
  def getArea(): Int = l * l
}
// defined class Square

class Circle(r: Double) extends Shape[Double] {
  def getArea(): Double = r * r * 3.14
}
// defined class Circle

val s = new Square(2)
// s: Square = Square@fc5f770
s.getArea
// res0: Int = 4

val c = new Circle(2)
// c: Circle = Circle@d4e25c3
c.getArea
// res1: Double = 12.56
```
