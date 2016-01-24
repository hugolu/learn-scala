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
