# Anonymous Function Syntax

## anonymous functions
```scala
// shorthand
(x: Int) => x + 1

// longhand
new Function1[Int, Int] {
  def apply(x: Int): Int = x + 1
}
```

## function types
```scala
// shorthand
() => String
Int => Int
(Int, Int) => String

// longhand
Function0[String]
Function1[Int, Int]
Function2[Int, Int, String]
```

## definitions
```scala
trait Function0[+R] extends AnyRef
trait Function1[-T1, +R] extends AnyRef
trait Function2[-T1, -T2, +R] extends AnyRef
```
