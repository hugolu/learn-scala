# Covariance and Contravariance in Scala

參考連結 http://blogs.atlassian.com/2013/01/covariance-and-contravariance-in-scala/

## Subtyping

```scala
trait Function1[-T1, +R] {
  def apply(t : T1) : R
  ...
}
```

```scala
class GParent
class Parent extends GParent
class Child extends Parent
```

```Function1[GParent, Child] <: Function1[Parent, Parent]```
- the first parameter is contravariant, so can vary upwards
- the second parameter is covariant, so can vary downwards

If you have a function from A to B, what can you substitue for it? Anything you put in its place must make fewer requirements on it’s input type.
- the function can’t get away with calling a method that only exists on subtypes of A.
- it must return a type at least as specialised as B, since the caller of the function may be expecting all the methods on B to be available.
