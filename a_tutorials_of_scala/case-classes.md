# Case Classes

Case classes are regular classes which export their constructor parameters and which provide a recursive decomposition mechanism via pattern matching.

To facilitate the construction of case class instances, Scala does not require that the new primitive is used. One can simply use the class name as a function.

The constructor parameters of case classes are treated as public values and can be accessed directly.

```scala
abstract class Term
case class Var(name: String) extends Term
case class Fun(arg: String, body: Term) extends Term
case class App(f: Term, v: Term) extends Term

val x = Var("x")
println(x.name)
//x

val x1 = Var("x")
val x2 = Var("x")
val y1 = Var("y")
println("" + x1 + " == " + x2 + " => " + (x1 == x2))
//Var(x) == Var(x) => true
println("" + x1 + " == " + y1 + " => " + (x1 == y1))
//Var(x) == Var(y) => false

def isIdentityFun(term: Term): Boolean = term match {
  case Fun(x, Var(y)) if x == y => true
  case _ => false
}

val id = Fun("x", Var("x"))
println(isIdentityFun(id))
//true
```

my practice:
```scala
abstract class Base
case class IS(x: Int, y: String) extends Base
case class SI(x: String, y: Int) extends Base

def strip(o: Base) = o match {
  case IS(a, b) => println(a, b)
  case SI(a, b) => println(b, a)
  case _ => println("??")
}

val is = IS(123, "hello")
val si = SI("hello", 456)

strip(is)
//(123,hello)
strip(si)
//(456,hello)
```
