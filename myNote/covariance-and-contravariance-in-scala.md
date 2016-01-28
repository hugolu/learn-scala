# Covariance and Contravariance in Scala

參考連結 http://blogs.atlassian.com/2013/01/covariance-and-contravariance-in-scala/

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
