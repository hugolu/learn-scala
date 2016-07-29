# Functors, Applicatives, And Monads

## 參考連結
- [Functors, Applicatives, And Monads In Pictures](http://adit.io/posts/2013-04-17-functors,_applicatives,_and_monads_in_pictures.html)

## Functors
```scala
scala> Some(2).map(_+3)
res0: Option[Int] = Some(5)
```
![](http://adit.io/imgs/functors/fmap_just.png)

```scala
scala> (None: Option[Int]).map(_+3)
res1: Option[Int] = None
```
![](http://adit.io/imgs/functors/fmap_nothing.png)

## Applicatives
```scala
def ap[A,B](ff: Option[A => B])(fa: Option[A]): Option[B] = (fa, ff) match {
  case (Some(a), Some(f)) => Some(f(a))
  case _ => None
}

scala> ap(Some{x: Int => x + 3})(Some(2))
res2: Option[Int] = Some(5)
```
![](http://adit.io/imgs/functors/applicative_just.png)

## Monads
