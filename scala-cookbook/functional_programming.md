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

```scala
// 1. define the method
def exec(callback: (Int, Int) => Int, x: Int, y: Int) {
  println(callback(x, y))
}                                               //> exec: (callback#95918787: (Int#1103, Int#1103) => Int#1103, x#95918788: Int#1103, y#95918789: Int#1103)Unit#2630

// 2. define a function to pass in
def addBoth: (Int, Int) => Int = (a, b) => a + b//> addBoth: => (Int#1103, Int#1103) => Int#1103

// 3. pass the function and some parameters to the method
exec(addBoth, 1, 2)                             //> 3
```
## Using Closures
"a closure is a block of code which meets three criteria."

1. The block of code can be passed around as a value, and
2. It can be executed on demand by anyone who has that value, at which time
3. It can refer to variables from the context in which it was created (i.e., it is closed with respect to variable access, in the mathematical sense of the word “closed”).

```scala
def getMultiplier(n: Int): (Int) => Int = {
  def multiplier(v: Int): Int = n * v
  multiplier
}                                               //> getMultiplier: (n#96954462: Int#1103)Int#1103 => Int#1103

val x2 = getMultiplier(2)                       //> x2  : Int#1103 => Int#1103 = <function1>
x2(2)                                           //> res0: Int#1103 = 4
x2(3)                                           //> res1: Int#1103 = 6

val x3 = getMultiplier(3)                       //> x3  : Int#1103 => Int#1103 = <function1>
x3(2)                                           //> res2: Int#1103 = 6
x3(3)                                           //> res3: Int#1103 = 9
```

> a closure begins with a function and a variable defined in the same scope, which are then separated from each other. When the function is executed at some other point in space (scope) and time, it is magically still aware of the variable it referenced in their earlier time together, and even picks up any changes to that variable.

## Using Partially Applied Functions

```scala
val cat = (s1: String, s2: String) => s1 + s2   //> cat  : (String#98388767, String#98388767) => String#238 = <function2>
cat("Foo", "Bar")                               //> res0: String#238 = FooBar
cat("Foo", "Buz")                               //> res1: String#238 = FooBuz

val catFooWith = cat("Foo", _: String)          //> catFooWith  : String#98388767 => String#238 = <function1>
catFooWith("Bar")                               //> res2: String#238 = FooBar
catFooWith("Buz")                               //> res3: String#238 = FooBuz
```
- Because you haven’t provided a value for the 2nd parameter of `cat("Foo", _: String)`, the resulting variable `catFooWith` is a partially applied function.
- type of `catWithFoo` is `funtion1`, which takes a `String` and returns a `String`

In functional programming languages, when you call a function that has parameters, you are said to be applying the function to the parameters. 
- When all the parameters are passed to the function, you have **fully** applied the function to all of the parameters.
- But when you give only a subset of the parameters to the function, the result of the expression is a **partially** applied function.

This technique has many advantages, including the ability to make life easier for the consumers of a library you create. 

## Creating a Function That Returns a Function

```scala
def cat1(s1: String, s2: String) = s"$s1, $s2"    //> cat1: (s1#100832098: String#98388767, s2#100832099: String#98388767)String#98388767
cat1("hello", "world")                            //> res0: String#98388767 = hello, world
val cat1x = cat1("hello", _: String)              //> cat1x  : String#98388767 => String#98388767 = <function1>
cat1x("kitty")                                    //> res1: String#98388767 = hello, kitty

val cat2 = (s1: String, s2: String) => s"$s1, $s2"//> cat2  : (String#98388767, String#98388767) => String#98388767 = <function2>
cat2("hello", "world")                            //> res2: String#98388767 = hello, world
val cat2x = cat2("hello", _: String)              //> cat2x  : String#98388767 => String#98388767 = <function1>
cat2x("puppy")                                    //> res3: String#98388767 = hello, puppy

def cat3(s1: String) = (s2: String) => s"$s1, $s2"//> cat3: (s1#100832176: String#98388767)String#98388767 => String#98388767
val cat3x = cat3("hello")                         //> cat3x  : String#98388767 => String#98388767 = <function1>
cat3x("scala")                                    //> res4: String#98388767 = hello, scala
```

To return a function (algorithm) from a function or method.
```scala
def calculate(f: (Int, Int) => Int) = (a: Int, b: Int) => f(a, b)
                                                //> calculate: (f#101707466: (Int#1103, Int#1103) => Int#1103)(Int#1103, Int#1103) => Int#1103

val add = calculate(_ + _)                      //> add  : (Int#1103, Int#1103) => Int#1103 = <function2>
val sub = calculate(_ - _)                      //> sub  : (Int#1103, Int#1103) => Int#1103 = <function2>
val mul = calculate(_ * _)                      //> mul  : (Int#1103, Int#1103) => Int#1103 = <function2>
val div = calculate(_ / _)                      //> div  : (Int#1103, Int#1103) => Int#1103 = <function2>

add(7, 3)                                       //> res0: Int#1103 = 10
sub(7, 3)                                       //> res1: Int#1103 = 4
mul(7, 3)                                       //> res2: Int#1103 = 21
div(7, 3)                                       //> res3: Int#1103 = 2
```

## Creating Partial Functions

## A Real-World Example
