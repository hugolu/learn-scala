# Functional Programming

## Using Function Literals (Anonymous Functions)

You want to use an anonymous function—also known as a function literal—so you can pass it into a method that takes a function, or to assign it to a variable.

```scala
scala> val list = (1 to 10).toList
list: List[Int] = List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

scala> val evens = list.filter(n => n % 2 == 0)
evens: List[Int] = List(2, 4, 6, 8, 10)
```

## Using Functions as Variables

```scala
scala> (i: Int) => i * 2
res4: Int => Int = <function1>

scala> val double = (i: Int) => i * 2
double: Int => Int = <function1>

scala> double(2)
res5: Int = 4
```
- assign that function literal to a variable

```scala
scala> val nums = List(1, 2, 3)
nums: List[Int] = List(1, 2, 3)

scala> nums.map(double)
res6: List[Int] = List(2, 4, 6)
```
- you can pass it to any method (or function) that takes a function parameter with its signature

### Assigning an existing function/method to a function variable

```scala
scala> val pi = scala.math.Pi
pi: Double = 3.141592653589793

scala> scala.math.sin(pi/2)
res9: Double = 1.0

scala> val sin = scala.math.sin _
sin: Double => Double = <function1>

scala> sin(pi/2)
res10: Double = 1.0
```

## Defining a Method That Accepts a Simple Function Parameter

```scala
def fun = println("hello")                      //> fun: => Unit#2630
def exec(f: => Unit) { f }                      //> exec: (f#94281529: => Unit#2630)Unit#2630

exec(fun)                                       //> hello
```

```scala
def fun() = println("hello")                    //> fun: ()Unit#2630
def exec(f: () => Unit) { f }                   //> exec: (f#94342698: () => Unit#2630)Unit#2630

exec(fun)
```

```scala
def fun(s: String) = println(s)                 //> fun: (s#94476829: String#92951931)Unit#2630
def exec(f: String => Unit) { f("hello") }      //> exec: (f#94476831: String#92951931 => Unit#2630)Unit#2630

exec(fun)                                       //> hello
```

```scala
def fun(s: String, i: Int) = println(s + i)     //> fun: (s#94812868: String#94644747, i#94812869: Int#1103)Unit#2630
def exec(f: (String, Int) => Unit) { f("hello", 123) }
                                                //> exec: (f#94812871: (String#94644747, Int#1103) => Unit#2630)Unit#2630

exec(fun)                                       //> hello123
```

## More Complex Functions

## Using Closures

## Using Partially Applied Functions

## Creating a Function That Returns a Function

## Creating Partial Functions

## A Real-World Example
