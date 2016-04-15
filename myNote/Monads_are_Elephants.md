# Monads are Elephants

- [Monads are Elephants Part 1](http://james-iry.blogspot.tw/2007/09/monads-are-elephants-part-1.html)
- [Monads are Elephants Part 2](http://james-iry.blogspot.tw/2007/10/monads-are-elephants-part-2.html)
- [Monads are Elephants Part 3](http://james-iry.blogspot.tw/2007/10/monads-are-elephants-part-3.html)
- [Monads are Elephants Part 4](http://james-iry.blogspot.tw/2007/11/monads-are-elephants-part-4.html)

## Part 1

## Part 2

## Part 3

### Equality for All
Using triple equals in `f(x) ≡ g(x)` as a mathematician uses `=` for equality.

### Breaking the Law
If `a`, `b`, and `c` are rational numbers then multiplication (`*`) obeys the following laws:

```
a * 1 ≡ a
a * b ≡ b * a
(a * b) * c ≡ a * (b * c)
```
Certainly it would be easy to create a class called "RationalNumber" and implement a `*` operator. But if it didn't follow these laws the result would be confusing to say the least.

### WTF - What The Functor?
For a functor of type `M[A]`, the map method takes a function from `A` to `B` and returns an `M[B]`. In other words, map converts an `M[A]` into an `M[B]` based on a function argument.

```
class M[A] {
  def map[B](f: A => B):M[B] = ...
}
```

#### First Functor Law: Identity
Let's say I invent a function called identity like so

```
def identity[A](x:A) = x
```
This obviously has the property that for any `x`, 

```
identity(x) ≡ x
```

for any functor `m`,
```
F1      m map identity ≡ m
F1b     m map {x => identity(x)} ≡ m
F1c     m map {x => x} ≡ m
F1d     for (x <- m) yield x ≡ m
```

#### Second Functor Law: Composition
The second functor law specifies the way several "maps" compose together.

```
F2      m map g map f ≡ m map {x => f(g(x))}
F2b     for (y<- (for (x <-m) yield g(x)) yield f(y) ≡ for (x <- m) yield f(g(x))
```

### Functors and Monads, Alive, Alive Oh
As a reminder, a Scala monad has both map and flatMap methods with the following signatures

```
class M[A] {
  def map[B](f: A => B):M[B] = ...
  def flatMap[B](f: A => M[B]): M[B] = ...
}
```

Additionally, the laws I present here will be based on `unit`. `unit` stands for a single argument constructor or factory with the following signature

```
def unit[A](x:A):M[A] = ...
```

Normally it's handy to create a monad `M` as a case class or with a companion object with an appropriate `apply(x:A):M[A]` method so that the expression `M(x)` behaves as `unit(x)`.

#### The Functor/Monad Connection Law: The Zeroth Law
In the very first installment of this series I introduced a relationship. This law doesn't do much for us alone, but it does create a connection between three concepts: unit, map, and flatMap.

```
FM1     m map f ≡ m flatMap {x => unit(f(x))}
FM1a    for (x <- m) yield f(x) ≡ for (x <- m; y <- unit(f(x))) yield y
```

#### Flatten Revisited
In the very first article I mentioned the concept of "flatten" or "join" as something that converts a monad of type `M[M[A]]` into `M[A]`, but didn't describe it formally. In that article I said that `flatMap` is a `map` followed by a `flatten`.

```
FL1     m flatMap f ≡ flatten(m map f)
```
```
        flatten(m map identity) ≡ m flatMap identity  // substitute identity for f
FL1a    flatten(m) ≡ m flatMap identity               // by F1
```

#### The First Monad Law: Identity
The first and simplest of the monad laws is the monad identity law

```
M1      m flatMap unit ≡ m                            // or equivalently
M1a     m flatMap {x => unit(x)} ≡ m
```

Where the connector law connected 3 concepts, this law focuses on the relationship between 2 of them. One way of reading this law is that, in a sense, flatMap undoes whatever unit does.

Using this and the connection law, we can derive the functor identity law

```
        m flatMap {x => unit(x)} ≡ m                // M1a
        m flatMap {x => unit(identity(x))}≡ m       // identity
        m map {x => identity(x)} ≡ m                // by FM1
        m map identity ≡ m                          // F1
```

The same derivation works in reverse, too. Expressed in "for" notation, the monad identity law is pretty straight forward
```
M1c     for (x <- m; y <- unit(x)) yield y ≡ m
```

#### The Second Monad Law: Unit
Monads have a sort of reverse to the monad identity law.

```
M2      unit(x) flatMap f ≡ f(x)                    // or equivalently
M2a     unit(x) flatMap {y => f(y)} ≡ f(x)
```
```
M2b     for (y <- unit(x); result <- f(y)) yield result ≡ f(x)
```

This law has another implication for unit and how it relates to map
```
        unit(x) map f ≡ unit(x) map f                       // no, really, it does!
        unit(x) map f ≡ unit(x) flatMap {y => unit(f(y))}   // by FM1
M2c     unit(x) map f ≡ unit(f(x))                          // by M2a
```
```
M2d     for (y <- unit(x)) yield f(y) ≡ unit(f(x))
```

####Flatten Revisited
In the very first article I mentioned the concept of "flatten" or "join" as something that converts a monad of type M[M[A]] into M[A], but didn't describe it formally. In that article I said that flatMap is a map followed by a flatten.

```
FL1     m flatMap f ≡ flatten(m map f)
        flatten(m map identity) ≡ m flatMap identity // substitute identity for f
```
```
FL1a    flatten(m) ≡ m flatMap identity // by F1
```

#### The Third Monad Law: Composition
The composition law for monads is a rule for how a series of flatMaps work together.
```
M3      m flatMap g flatMap f ≡ m flatMap {x => g(x) flatMap f} // or equivalently
M3a     m flatMap {x => g(x)} flatMap {y => f(y)} ≡ m flatMap {x => g(x) flatMap {y => f(y) }}
M3b     for (a <- m; b <- g(a); result <- f(b)) yield result ≡ for(a <- m; result <- for(b <- g(a); temp <- f(b)) yield temp) yield result
```

```
m map g map f ≡ m map g map f // I'm pretty sure
m map g map f ≡ m flatMap {x => unit(g(x))} flatMap {y => unit(f(y))} // by FM1, twice
m map g map f ≡ m flatMap {x => unit(g(x)) flatMap {y => unit(f(y))}} // by M3a
m map g map f ≡ m flatMap {x => unit(g(x)) map {y => f(y)}} // by FM1a
m map g map f ≡ m flatMap {x => unit(f(g(x))} // by M2c
F2. m map g map f ≡ m map {x => f(g(x))} // by FM1a
```

### Total Loser Zeros
List has Nil (the empty list) and Option has None. Nil and None seem to have a certain similarity: they both represent a kind of emptiness. Formally they're called monadic zeros.

A monad may have many zeros. For instance, imagine an Option-like monad called Result. A Result can either be a Success(value) or a Failure(msg). The Failure constructor takes a string indicating why the failure occurred. Every different failure object is a different zero for Result.

#### The First Zero Law: Identity

```
MZ1     mzero flatMap f ≡ mzero
```

```
        mzero map f ≡ mzero map f // identity
        mzero map f ≡ mzero flatMap {x => unit(f(x)) // by FM1
MZ1b    mzero map f ≡ mzero // by MZ1
```

#### The Second Zero Law: M to Zero in Nothing Flat
```
MZ2     m flatMap {x => mzero} ≡ mzero
```

#### The Third and Fourth Zero Laws: Plus
```
class M[A] {
  ...
  def plus(other: M[B >: A]): M[B] = ...
}
```

```
MZ3     mzero plus m ≡ m
MZ4     m plus mzero ≡ m
```

#### Filtering Revisited
```
class M[A] {
  def map[B](f: A => B):M[B] = ...
  def flatMap[B](f: A=> M[B]): M[B] = ...
  def filter(p: A=> Boolean): M[A] = ...
}
```

```
FIL1    m filter p ≡ m flatMap {x => if(p(x)) unit(x) else mzero}
```

```
        m filter {x => true} ≡ m filter {x => true} // identity
        m filter {x => true} ≡ m flatMap {x => if (true) unit(x) else mzero} // by FIL1
        m filter {x => true} ≡ m flatMap {x => unit(x)} // by definition of if
FIL1a   m filter {x => true} ≡ m // by M1
```

```
        m filter {x => false} ≡ m filter {x => false} // identity
        m filter {x => false} ≡ m flatMap {x => if (false) unit(x) else mzero} // by FIL1
        m filter {x => false} ≡ m flatMap {x => mzero} // by definition of if
FIL1b   m filter {x => false} ≡ mzero // by MZ1
```

### Side Effects
Throughout this article I've implicitly assumed no side effects. Let's revisit our second functor law
```
m map g map f ≡ m map {x => (f(g(x)) }
```

### Conclusion for Part 3
```
FM1     m map f ≡ m flatMap {x => unit(f(x))}
M1      m flatMap unit ≡ m
M2      unit(x) flatMap f ≡ f(x)
M3      m flatMap g flatMap f ≡ m flatMap {x => g(x) flatMap f}
MZ1     mzero flatMap f ≡ mzero
MZ2     m flatMap {x => mzero} ≡ mzero
MZ3     mzero plus m ≡ m
MZ4     m plus mzero ≡ m
FIL1    m filter p ≡ m flatMap {x => if(p(x)) unit(x) else mzero}
```
## Part 4

### Functional Programming and IO
In functional programming there's a concept called referential transparency. Referential transparency means you can call a particular function anywhere and any time and the same arguments will always give the same results.

There's one area where referential transparency would seem impossible to achieve: IO. But we can't get rid of IO just to accomplish referential transparency. 

You might guess that monads provide a solution for referentially transparent IO given the topic of this series but I'm going to work my way up from some simple principles. I'll solve the problem for reading and writing strings on the console but the same solution can be extended to arbitrary kinds of IO like file and network.

### The World In a Cup
The slight-of-hand I'll use is to model only a few aspects of the world and just pretend WorldState knows about the rest of the world. Here are some aspects that would be useful

1. The state of the world changes between IO functions.
2. The world's state is what it is. You can't just create new ones whenever you want (val coolWorldState = new WorldState(){def jamesIsBillionaire = true}).
3. The world is in exactly one state at any moment in time.

Here's a rough sketch for property 1
```scala
//file RTConsole.scala  
object RTConsole_v1 {  
  def getString(state: WorldState) = (state.nextState, Console.readLine)
  def putString(state: WorldState, s: String) = (state.nextState, Console.print(s))
}
```

Here's how I'll implement property 2
```scala
//file RTIO.scala
sealed trait WorldState{def nextState:WorldState}

abstract class IOApplication_v1 {
  private class WorldStateImpl(id:BigInt) extends WorldState {
    def nextState = new WorldStateImpl(id + 1)
  }
  final def main(args:Array[String]):Unit = {
    iomain(args, new WorldStateImpl(0))
  }
  def iomain(args:Array[String], startState:WorldState):(WorldState, _)
}
```

Here's what hello world looks like given all this
```scala
// file HelloWorld.scala
class HelloWorld_v1 extends IOApplication_v1 {
  import RTConsole_v1._
  def iomain(args:Array[String], startState:WorldState) = putString(startState, "Hello world")
}
```

### That Darn Property 3
```scala
class Evil_v1 extends IOApplication_v1 {
  import RTConsole_v1._
  def iomain(args:Array[String], startState:WorldState) = {
    val (stateA, a) = getString(startState)
    val (stateB, b) = getString(startState)
    assert(a == b)
    (startState, b)
  }
}
```

Here I've called getString twice with the same inputs. If the code was referentially transparent then the result, a and b, should be the same but of course they won't be unless the user types the same thing twice. The problem is that "startState" is visible at the same time as the other world states stateA and stateB.

### Inside Out
As a first step towards a solution, I'm going to turn everything inside out. Instead of iomain being a function from WorldState to WorldState, iomain will return such a function and the main driver will execute it. Here's the code

```scala
//file RTConsole.scala
object RTConsole_v2 {
  def getString = {state:WorldState => (state.nextState, Console.readLine)}
  def putString(s: String) = {state: WorldState => (state.nextState, Console.print(s))}
}
```

```scala
//file RTIO.scala
sealed trait WorldState{def nextState:WorldState}

abstract class IOApplication_v2 {
  private class WorldStateImpl(id:BigInt) extends WorldState {
    def nextState = new WorldStateImpl(id + 1)
  }
  final def main(args:Array[String]):Unit = {
    val ioAction = iomain(args)
    ioAction(new WorldStateImpl(0));
  }
  def iomain(args:Array[String]): WorldState => (WorldState, _)
}
```

```scala
//file HelloWorld.scala
class HelloWorld_v2 extends IOApplication_v2 {
  import RTConsole_v2._
  def iomain(args:Array[String]) = putString("Hello world")
}
```

### Oh That Darn Property 3
```scala
class Evil_v2 extends IOApplication_v2 {
  import RTConsole_v2._
  def iomain(args:Array[String]) = {
    {startState: WorldState =>
      val (statea, a) = getString(startState)
      val (stateb, b) = getString(startState)
      assert(a == b)
      (startState, b)
    }
  }
}
```

### Property 3 Squashed For Good
```scala
//file RTIO.scala
sealed trait IOAction_v3[+A] extends Function1[WorldState, (WorldState, A)]

object IOAction_v3 {
  def apply[A](expression: => A):IOAction_v3[A] = new SimpleAction(expression)

  private class SimpleAction [+A](expression: => A) extends IOAction_v3[A] {
    def apply(state:WorldState) = (state.nextState, expression)
  }
}

sealed trait WorldState{def nextState:WorldState}

abstract class IOApplication_v3 {
  private class WorldStateImpl(id:BigInt) extends WorldState {
    def nextState = new WorldStateImpl(id + 1)
  }
  final def main(args:Array[String]):Unit = {
    val ioAction = iomain(args)
    ioAction(new WorldStateImpl(0));
  }
  def iomain(args:Array[String]):IOAction_v3[_]
}
```

```scala
//file RTConsole.scala
object RTConsole_v3 {
  def getString = IOAction_v3(Console.readLine)
  def putString(s: String) = IOAction_v3(Console.print(s))
}
```

```scala
//file HelloWorld.scala
class HelloWorld_v3 extends IOApplication_v3 {
  import RTConsole_v3._
  def iomain(args:Array[String]) = putString("Hello world")
}
```

我的解讀版本：
```scala
sealed trait IOAction_v3[+A] extends Function1[WorldState, (WorldState, A)]

object IOAction_v3 {
  def apply[A](expression: => A): IOAction_v3[A] = new IOAction_v3[A] {
    def apply(state: WorldState) = (state.nextState, expression)
  }
}

sealed trait WorldState { def nextState:WorldState }

abstract class IOApplication_v3 {
  private class WorldStateImpl(id: BigInt) extends WorldState {
    def nextState = new WorldStateImpl(id + 1)
  }

  final def run = {
    val ioAction = iomain
    ioAction(new WorldStateImpl(0))
  }

  def iomain: IOAction_v3[_]
}

object RTConsole_v3 {
  def getString = IOAction_v3 (scala.io.StdIn.readLine)
  def putString(s: String) = IOAction_v3 (println(s))
}

object Test extends App {
  import RTConsole_v3._
  val ioApp = new IOApplication_v3 {
    def iomain = putString("Hello world")
  }
  ioApp.run
}
```
- `sealed trait WorldState { def nextState:WorldState }`: 模擬當下世界的狀態
- `sealed trait IOAction_v3[+A] extends Function1[WorldState, (WorldState, A)]`：包裝過的function，隱藏Function1的輸入(WorldState)
- `object IOAction_v3`：`new IOAction_v3`的factory method，產生的`IOAction_v3`類別中定義`apply`method
- `abstract class IOApplication_v3`：把世界的運作包裝起來，只把`iomain`留給繼承者定義
- `object RTConsole_v3`：定義IO方法，回傳 `IOAction_v3`，呼叫這些方法不會馬上執行`expression`內容
- `object Test`：test driver，產生 `IOApplication_v3`，並呼叫`run`讓世界開始動起來

### Ladies and Gentleman I Present the Mighty IO Monad
if we knew what flatMap was for this monad then the monad laws would tell us how to create map using it and unit. But what's flatMap going to be? The signature needs to look like `def flatMap[B](f: A=>IOAction[B]): IOAction[B]`. But what does it do?

What we want it to do is **chain an action** to a function that returns an action and when activated causes the two actions to occur in order. In other words, `getString.flatMap{y => putString(y)}` should result in a new IOAction monad that, when activated, first activates the getString action then does the action that putString returns. 

```scala
//file RTIO.scala
sealed abstract class IOAction_v4[+A] extends Function1[WorldState, (WorldState, A)] {
  def map[B](f:A => B):IOAction_v4[B] = flatMap {x => IOAction_v4(f(x))}
  def flatMap[B](f:A => IOAction_v4[B]):IOAction_v4[B]= new ChainedAction(this, f)

  private class ChainedAction[+A, B](action1: IOAction_v4[B], f: B => IOAction_v4[A]) extends IOAction_v4[A] {
    def apply(state1:WorldState) = {
      val (state2, intermediateResult) = action1(state1);
      val action2 = f(intermediateResult)
      action2(state2)
    }
  }
}

object IOAction_v4 {
  def apply[A](expression: => A):IOAction_v4[A] = new SimpleAction(expression)

  private class SimpleAction[+A](expression: => A) extends IOAction_v4[A] {
    def apply(state:WorldState) = (state.nextState, expression)
  }
}

sealed trait WorldState{def nextState:WorldState}

abstract class IOApplication_v4 {
  private class WorldStateImpl(id:BigInt) extends WorldState {
    def nextState = new WorldStateImpl(id + 1)
  }
  final def main(args:Array[String]):Unit = {
    val ioAction = iomain(args)
    ioAction(new WorldStateImpl(0));
  }
  def iomain(args:Array[String]):IOAction_v4[_]
}
```

```scala
//file RTConsole.scala
object RTConsole_v4 {
  def getString = IOAction_v4(Console.readLine)
  def putString(s: String) = IOAction_v4(Console.print(s))
}
```

```scala
//file HelloWorld.scala
class HelloWorld_v4 extends IOApplication_v4 {
  import RTConsole_v4._
  def iomain(args:Array[String]) = putString("Hello world")
}
```

我的解讀:
```scala
sealed abstract class IOAction_v4[+A] extends Function1[WorldState, (WorldState, A)] {
  def map[B](f: A => B): IOAction_v4[B] = flatMap {x => IOAction_v4(f(x))}
  def flatMap[B](f: A => IOAction_v4[B]): IOAction_v4[B]= new ChainedAction(this, f)

  private class ChainedAction[+A, B](action1: IOAction_v4[A], f: A => IOAction_v4[B]) extends IOAction_v4[B] {
    def apply(state1: WorldState) = {
      val (state2, intermediateResult) = action1(state1);
      val action2 = f(intermediateResult)
      action2(state2)
    }
  }
}

object IOAction_v4 {
  def apply[A](expression: => A): IOAction_v4[A] = new IOAction_v4[A] {
    def apply(state: WorldState) = (state.nextState, expression)
  }
}

sealed trait WorldState { def nextState: WorldState }

abstract class IOApplication_v4 {
  private class WorldStateImpl(id: BigInt) extends WorldState {
    def nextState = new WorldStateImpl(id + 1)
  }
  final def run = {
    val ioAction = createIOAction()
    ioAction(new WorldStateImpl(0));
  }
  def createIOAction(): IOAction_v4[_]
}

object RTConsole_v4 {
  def getString = IOAction_v4(scala.io.StdIn.readLine)
  def putString(s: String) = IOAction_v4(println(s))
}

object Test extends App {
  import RTConsole_v4._
  val ioApp = new IOApplication_v4 {
    def createIOAction() = for {
      _ <- putString("This is an example of the IO monad.")
      _ <- putString("What's your name?")
      name <- getString
      _ <- putString("Hello " + name)
    } yield ()
  }
  ioApp.run
}
```

### A Test Drive

### Take a Deep Breath

### IO Errors

### Conclusion for Part 4
