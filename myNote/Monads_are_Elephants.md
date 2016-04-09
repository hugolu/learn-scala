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

### First Functor Law: Identity
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

### Second Functor Law: Composition
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

### The Functor/Monad Connection Law: The Zeroth Law
In the very first installment of this series I introduced a relationship. This law doesn't do much for us alone, but it does create a connection between three concepts: unit, map, and flatMap.

```
FM1     m map f ≡ m flatMap {x => unit(f(x))}
FM1a    for (x <- m) yield f(x) ≡ for (x <- m; y <- unit(f(x))) yield y
```

### Flatten Revisited
In the very first article I mentioned the concept of "flatten" or "join" as something that converts a monad of type `M[M[A]]` into `M[A]`, but didn't describe it formally. In that article I said that `flatMap` is a `map` followed by a `flatten`.

```
FL1     m flatMap f ≡ flatten(m map f)
```
```
        flatten(m map identity) ≡ m flatMap identity  // substitute identity for f
FL1a    flatten(m) ≡ m flatMap identity               // by F1
```

### The First Monad Law: Identity
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

### The Second Monad Law: Unit
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

## Part 4
