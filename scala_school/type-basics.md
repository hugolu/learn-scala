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

### ```[T]``` - invariant: C[T] and C[T’] are not related
```scala
class Invariant[T]

val iv: Invariant[String] = new Invariant[String]
// iv: Invariant[String] = Invariant@3fa76a13

val iv: Invariant[AnyRef] = new Invariant[String]
// <console>:11: error: type mismatch;

val iv: Invariant[String] = new Invariant[AnyRef]
// <console>:11: error: type mismatch;
```

### ```[+T]``` - covariant: C[T’] is a subclass of C[T]
```scala
class Covariant[+T]
// defined class Covariant

val cv: Covariant[String] = new Covariant[String]
// cv: Covariant[String] = Covariant@3f44dbd0

val cv: Covariant[AnyRef] = new Covariant[String]
// cv: Covariant[AnyRef] = Covariant@48f7b4f7

val cv: Covariant[String] = new Covariant[AnyRef]
// <console>:11: error: type mismatch;
```

### ```[-T]``` - contravariant: C[T] is a subclass of C[T’]
```scala
class Contravariant[-T]
// defined class Contravariant

val cv: Contravariant[String] = new Contravariant[String]
// cv: Contravariant[String] = Contravariant@121b121a

val cv: Contravariant[AnyRef] = new Contravariant[String]
// <console>:11: error: type mismatch;

val cv: Contravariant[String] = new Contravariant[AnyRef]
// cv: Contravariant[String] = Contravariant@35011e72
```

The subtype relationship really means: for a given type T, if T’ is a subtype, can you substitute T with T'?
```scala
class Base { val name = "base" }
class Sub extends Base { override val name = "sub" }

def showName(o: Base) = o.name
// showName: (o: Base)String

showName(new Sub)
//res1: String = sub
// subsitute Base with Sub
```

### Function parameters are contravariant
```scala
class Animal { val sound = "rustle" }
class Bird extends Animal { override val sound = "call" }
class Chicken extends Bird { override val sound = "cluck" }

val a = new Animal
val b = new Bird
val c = new Chicken

val getTweet: (Bird => String) = ((a: Animal) => a.sound )
// getTweet: Bird => String = <function1>
// "I need an Animal, I have a subclass of Bird." => it's ok
// If you need a function that takes a Bird and you have a function that takes an Animal, that's OK.

getTweet(a)
// <console>:15: error: type mismatch;
getTweet(b)
// res1: String = call
getTweet(c)
// res2: String = cluck
```

### A function’s return value type is covariant.
```scala
class Animal { val sound = "rustle" }
class Bird extends Animal { override val sound = "call" }
class Chicken extends Bird { override val sound = "cluck" }

def hatch: ()=>Bird = () => new Chicken
// hatch: () => Bird
// If you need a function that returns a Bird but have a function that returns a Chicken, that’s great.

val b: Bird = hatch()
// b: Bird = Chicken@7315e196
```

## Bounds

### ```<:``` Upper Type Bounds
```scala
def cacophony[T](things: Seq[T]) = things map (_.sound)
// <console>:10: error: value sound is not a member of type parameter T
//       def cacophony[T](things: Seq[T]) = things map (_.sound)
//                                                        ^

def biophony[T <: Animal](things: Seq[T]) = things map (_.sound)
// biophony: [T <: Animal](things: Seq[T])Seq[String]

class Animal { val sound = "rustle" }
class Bird extends Animal { override val sound = "call" }
class Chicken extends Bird { override val sound = "cluck" }

biophony(Seq(new Chicken, new Bird))
// res3: Seq[String] = List(cluck, call)
```
- [A Tour of Scala: Upper Type Bounds](http://www.scala-lang.org/old/node/136)

### ```>:``` Lower Type Bounds
List defines ```::[B >: T](x: B)``` which returns a ```List[B]```. Notice the ```B >: T```. That specifies type ```B``` as a superclass of ```T```. That lets us do the right thing when prepending an Animal to a ```List[Bird]```:
```scala
val flock = List(new Bird, new Bird)
// flock: List[Bird] = List(Bird@3b7c306a, Bird@564e9da8)

new Chicken :: flock
// res5: List[Bird] = List(Chicken@3793258b, Bird@3b7c306a, Bird@564e9da8)

new Animal :: flock
res6: List[Animal] = List(Animal@6b8d773, Bird@3b7c306a, Bird@564e9da8)
```

- [A Tour of Scala: Lower Type Bounds](http://www.scala-lang.org/old/node/137)
