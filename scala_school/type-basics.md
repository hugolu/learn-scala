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

### Function *parameters* are contravariant
```scala
scala> class Animal { val sound = "rustle" }
scala> class Bird extends Animal { override val sound = "call" }
scala> class Chicken extends Bird { override val sound = "cluck" }
```

```scala
// If you need a function that takes a Bird and you have a function that takes an Animal, that's OK.
scala> (a: Animal) => a.sound
res1: Animal => String = <function1>

scala> val getTweet: Bird => String = (a: Animal) => a.sound
getTweet: Bird => String = <function1>
```
- ```getTweet```接受```Bird```型別的參數
- 將```Bird```實例傳給接受```Animal```實例的匿名函式
- ```Bird```是```Animal```的subtype，型別轉換沒問題

```scala
// If you need a function that takes a Bird and you have a function that takes a Bird, that's OK.
scala> (b: Bird) => b.sound
res2: Bird => String = <function1>

scala> val getTweet: Bird => String = (b: Bird) => b.sound
getTweet: Bird => String = <function1>
```
- ```getTweet```接受```Bird```型別的參數
- 將```Bird```實例傳給接受```Bird```實例的匿名函式
- 不需型別轉換，沒問題

```scala
// If you need a function that takes a Bird and you have a function that takes a Chicken, that's NOT OK.
scala> (c: Chicken) => c.sound
res3: Chicken => String = <function1>

scala> val getTweet: Bird => String = (c: Chicken) => c.sound
<console>:14: error: type mismatch;
 found   : Chicken => String
 required: Bird => String
       val getTweet: Bird => String = (c: Chicken) => c.sound
                                                   ^
```
- ```getTweet```接受```Bird```型別的參數
- 將```Bird```實例傳給接受```Chicken```實例的匿名函式
- ```Bird```不是```Chicken```的subtype，型別轉換會出錯

### A function’s *return value* type is covariant.
```scala
scala> class Animal { val sound = "rustle" }
scala> class Bird extends Animal { override val sound = "call" }
scala> class Chicken extends Bird { override val sound = "cluck" }
```

```scala
// If you need a function that returns a Bird but have a function that returns an Animal, that’s NOT ok.
scala> () => new Animal
res0: () => Animal = <function0>

scala> val hatch: () => Bird = () => new Animal
<console>:12: error: type mismatch;
 found   : Animal
 required: Bird
       val hatch: () => Bird = () => new Animal
                                     ^
```
- ```hatch```回傳```Bird```型別的實例
- 匿名函式回傳```Animal```型別的實例
- ```Animal```不是```Bird```的subtype，匿名函式回傳值不可被```hatch```的呼叫者接受，型別轉換會出錯

```scala
// If you need a function that returns a Bird but have a function that returns a Bird, that’s OK.
scala> () => new Bird
res1: () => Bird = <function0>

scala> val hatch: () => Bird = () => new Bird
hatch: () => Bird = <function0>
```
- ```hatch```回傳```Bird```型別的實例
- 匿名函式回傳```Bird```型別的實例
- 不需型別轉換，匿名函式回傳值可被```hatch```的呼叫者接受，沒問題

```scala
// If you need a function that returns a Bird but have a function that returns a Chicken, that’s OK.
scala> () => new Chicken
res2: () => Chicken = <function0>

scala> val hatch: () => Bird = () => new Chicken
hatch: () => Bird = <function0>
```
- ```hatch```回傳```Bird```型別的實例
- 匿名函式回傳```Chicken```型別的實例
- ```Chicken```是```Bird```的subtype，匿名函式回傳值可被```hatch```的呼叫者接受，沒問題

## Bounds

### ```<:``` Upper Type Bounds
[A Tour of Scala: Upper Type Bounds](http://www.scala-lang.org/old/node/136)
- An upper type bound ```T <: A``` declares that type variable ```T``` refers to a subtype of type ```A```. 

```scala
class Animal { val sound = "rustle" }
class Bird extends Animal { override val sound = "call" }
class Chicken extends Bird { override val sound = "cluck" }

def cacophony[T](things: Seq[T]) = things map (_.sound)
// <console>:10: error: value sound is not a member of type parameter T
//       def cacophony[T](things: Seq[T]) = things map (_.sound)
//                                                        ^

def biophony[T <: Animal](things: Seq[T]) = things map (_.sound)
// biophony: [T <: Animal](things: Seq[T])Seq[String]

biophony(Seq(new Chicken, new Bird, new Animal))
// res3: Seq[String] = List(cluck, call, rustle)
```
- ```T``` is a subtype of ```Animal```, with the upper type bound annotation ```biophony()``` can access the variable ```sound```

### ```>:``` Lower Type Bounds
[A Tour of Scala: Lower Type Bounds](http://www.scala-lang.org/old/node/137)
- The term ```T >: A``` expresses that the type parameter ```T``` or the abstract type ```T``` refer to a supertype of type ```A```.

List defines ```::[B >: T](x: B)``` which returns a ```List[B]```. 
- Notice the ```B >: T```. That specifies type ```B``` as a superclass of ```T```. 
- That lets us do the right thing when prepending an Animal to a ```List[Bird]```:
```scala
val flock = List(new Bird, new Bird)
// flock: List[Bird] = List(Bird@3b7c306a, Bird@564e9da8)

new Chicken :: flock
// res5: List[Bird] = List(Chicken@3793258b, Bird@3b7c306a, Bird@564e9da8)

new Animal :: flock
res6: List[Animal] = List(Animal@6b8d773, Bird@3b7c306a, Bird@564e9da8)
```

