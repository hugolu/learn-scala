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

```scala
class M[A] {
  def map[B](f: A => B):M[B] = ...
}
```

### First Functor Law: Identity
Let's say I invent a function called identity like so

```scala
def identity[A](x:A) = x
```
This obviously has the property that for any `x`, 

```scala
identity(x) ≡ x
```

for any functor `m`,

| Rule | Equation |
|------|----------|
| F1 | `m map identity ≡ m` |
| F1b | `m map {x => identity(x)} ≡ m` |
| F1c | `m map {x => x} ≡ m` |
| F1d | `for (x <- m) yield x ≡ m` |

### Second Functor Law: Composition
The second functor law specifies the way several "maps" compose together.

| Ruls | Equation |
|------|----------|
| F2 | `m map g map f ≡ m map {x => f(g(x))}` |
| F2b | `for (y<- (for (x <-m) yield g(x)) yield f(y) ≡ for (x <- m) yield f(g(x))` |

### Functors and Monads, Alive, Alive Oh
As a reminder, a Scala monad has both map and flatMap methods with the following signatures

```scala
class M[A] {
  def map[B](f: A => B):M[B] = ...
  def flatMap[B](f: A=> M[B]): M[B] = ...
}
```

Additionally, the laws I present here will be based on `unit`. `unit` stands for a single argument constructor or factory with the following signature

```scala
def unit[A](x:A):M[A] = ...
```

Normally it's handy to create a monad `M` as a case class or with a companion object with an appropriate `apply(x:A):M[A]` method so that the expression `M(x)` behaves as `unit(x)`.

## Part 4
