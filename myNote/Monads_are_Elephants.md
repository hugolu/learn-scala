# Monads are Elephants

- [Monads are Elephants Part 1](http://james-iry.blogspot.tw/2007/09/monads-are-elephants-part-1.html)
- [Monads are Elephants Part 2](http://james-iry.blogspot.tw/2007/10/monads-are-elephants-part-2.html)
- [Monads are Elephants Part 3](http://james-iry.blogspot.tw/2007/10/monads-are-elephants-part-3.html)
- [Monads are Elephants Part 4](http://james-iry.blogspot.tw/2007_11_01_archive.html)

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
M2	    unit(x) flatMap f ≡ f(x)
M3  	  m flatMap g flatMap f ≡ m flatMap {x => g(x) flatMap f}
MZ1	    mzero flatMap f ≡ mzero
MZ2	    m flatMap {x => mzero} ≡ mzero
MZ3	    mzero plus m ≡ m
MZ4	    m plus mzero ≡ m
FIL1	  m filter p ≡ m flatMap {x => if(p(x)) unit(x) else mzero}
```
## Part 4
