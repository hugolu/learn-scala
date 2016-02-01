# Type & polymorphism basics

## Type inference
A traditional objection to static typing is that it has much syntactic overhead. Scala alleviates this by providing type inference.

In scala all type inference is local. Scala considers one expression at a time. For example:
```scala
scala> def id[T](x:T):T = x
id: [T](x: T)T

scala> val x = id(322)
x: Int = 322

scala> val x = id("hey")
x: String = hey
```

Types are now preserved, The Scala compiler infers the type parameter for us. Note also how we did not have to specify the return type explicitly.

## Variance
A central question that comes up when mixing OO with polymorphism is: if T’ is a subclass of T, is Container[T’] considered a subclass of Container[T]?

### ```[T]``` - invariant: C[T] and C[T’] are not related
```scala
scala> class Invariant[T]
defined class Invariant

scala> val x: Invariant[String] = new Invariant[String]
x: Invariant[String] = Invariant@e5bcd5a

scala> val y: Invariant[AnyRef] = new Invariant[String]
<console>:11: error: type mismatch;
 found   : Invariant[String]
 required: Invariant[AnyRef]
Note: String <: AnyRef, but class Invariant is invariant in type T.
You may wish to define T as +T instead. (SLS 4.5)
       val y: Invariant[AnyRef] = new Invariant[String]
                                  ^

scala> val z: Invariant[String] = new Invariant[AnyRef]
<console>:11: error: type mismatch;
 found   : Invariant[AnyRef]
 required: Invariant[String]
Note: AnyRef >: String, but class Invariant is invariant in type T.
You may wish to define T as -T instead. (SLS 4.5)
       val z: Invariant[String] = new Invariant[AnyRef]
                                  ^
```

### ```[+T]``` - covariant: if ```T'``` is a subtype of ```T```, ```C[T']``` is a subclass of ```C[T]```
```scala
scala> class Covariant[+T]
defined class Covariant

scala> val x: Covariant[String] = new Covariant[String]
x: Covariant[String] = Covariant@46d3ddd8

scala> val y: Covariant[AnyRef] = new Covariant[String]
y: Covariant[AnyRef] = Covariant@3e4b7374

scala> val z: Covariant[String] = new Covariant[AnyRef]
<console>:11: error: type mismatch;
 found   : Covariant[AnyRef]
 required: Covariant[String]
       val z: Covariant[String] = new Covariant[AnyRef]
                                  ^
```

### ```[-T]``` - contravariant: if ```T'``` is a subtype of ```T```, ```C[T]``` is a subclass of ```C[T']```
```scala
scala> class Contravariant[-T]
defined class Contravariant

scala> val x: Contravariant[String] = new Contravariant[String]
x: Contravariant[String] = Contravariant@299c282e

scala> val y: Contravariant[AnyRef] = new Contravariant[String]
<console>:11: error: type mismatch;
 found   : Contravariant[String]
 required: Contravariant[AnyRef]
       val y: Contravariant[AnyRef] = new Contravariant[String]
                                      ^

scala> val z: Contravariant[String] = new Contravariant[AnyRef]
z: Contravariant[String] = Contravariant@44009cf2
```

### Subtype
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
scala> def cacophony[T](things: Seq[T]) = things map (_.sound)
<console>:10: error: value sound is not a member of type parameter T
       def cacophony[T](things: Seq[T]) = things map (_.sound)
                                                        ^
```
- 無法判斷```T```是否具備```sound```方法

```scala
scala> def biophony[T <: Animal](things: Seq[T]) = things map (_.sound)
biophony: [T <: Animal](things: Seq[T])Seq[String]

scala> biophony(Seq(new Chicken, new Bird, new Animal))
res3: Seq[String] = List(cluck, call, rustle)
```
- 透過```T <: Animal```，宣告```T``` is a subtype of ```Animal```
- with the upper type bound annotation ```biophony()``` can access the variable ```sound```

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

