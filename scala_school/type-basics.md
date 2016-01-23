# Type & polymorphism basics

## Type inference
A traditional objection to static typing is that it has much syntactic overhead. Scala alleviates this by providing type inference.

In scala all type inference is local. Scala considers one expression at a time. For example:
```scala
def id[T](x:T):T = x
// id: [T](x: T)T

val x = id(322)
// x: Int = 322

val x = id("hey")
//x: String = hey
```

Types are now preserved, The Scala compiler infers the type parameter for us. Note also how we did not have to specify the return type explicitly.

## Variance
A central question that comes up when mixing OO with polymorphism is: if T’ is a subclass of T, is Container[T’] considered a subclass of Container[T]?

The subtype relationship really means: for a given type T, if T’ is a subtype, can you substitute it?
