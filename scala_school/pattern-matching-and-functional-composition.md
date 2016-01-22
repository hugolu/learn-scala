# Pattern matching & functional composition

## Function Composition
```scala
def f(s: String) = "f(" + s + ")"
def g(s: String) = "g(" + s + ")"

val fComposeG = f _ compose g _
fComsoseG("X")
// res1: String = f(g(X))

val fAndThenG = f _ andThen g _
fAndThenG("X")
// res2: String = g(f(X))
```

## PartialFunction
- A function defined as (Int) => String takes any Int and returns a String.
- A Partial Function (Int) => String might not accept every Int.

```scala
val one: PartialFunction[Int, String] = { case 1 => "one"}
// one: PartialFunction[Int,String] = <function1>

one.isDefinedAt(1)
// res5: Boolean = true

one.isDefinedAt(2)
// res6: Boolean = false

one(1)
// res7: String = one

one(2)
// scala.MatchError: 2 (of class java.lang.Integer)
```
