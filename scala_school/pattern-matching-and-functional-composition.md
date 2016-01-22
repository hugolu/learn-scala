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

```scala
val one: PartialFunction[Int, String] = { case 1 => "one" }
val two: PartialFunction[Int, String] = { case 2 => "two" }
val three: PartialFunction[Int, String] = { case 3 => "three" }
val wildcard: PartialFunction[Int, String] = { case _ => "something else" }

val partial = one orElse two orElse three orElse wildcard
// partial: PartialFunction[Int,String] = <function1>

scala> partial(1)
// res17: String = one

scala> partial(2)
// res18: String = two

scala> partial(3)
// res19: String = three

scala> partial(0)
// res20: String = something else
```
- where to apply?

## The mystery of ```case```
```scala
case class PhoneExt(name: String, ext: Int)

val extensions = List(PhoneExt("steve", 100), PhoneExt("robey", 200))
// extensions: List[PhoneExt] = List(PhoneExt(steve,100), PhoneExt(robey,200))

extensions.filter { case PhoneExt(name, extension) => extension < 200 }
// res21: List[PhoneExt] = List(PhoneExt(steve,100))
```
- filter takes a function. In this case a **predicate** function of (PhoneExt) => Boolean.
