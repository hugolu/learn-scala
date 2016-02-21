# Insert Sort

```scala
val list = 3 :: 1 :: 2 :: 4 :: Nil              //> list  : List[Int] = List(3, 1, 2, 4)

def isort[T <% Ordered[T]](xs: List[T]): List[T] = xs match {
  case List()  => List()
  case y :: ys => insert(y, isort(ys))
}                                               //> isort: [T](xs: List[T])(implicit evidence$2: T => Ordered[T])List[T]

def insert[T <% Ordered[T]](x: T, xs: List[T]): List[T] = xs match {
  case List()  => List(x)
  case y :: ys => if (x <= y) x :: xs else y :: insert(x, ys)
}                                               //> insert: [T](x: T, xs: List[T])(implicit evidence$3: T => Ordered[T])List[T]

isort(list)                                     //> res0: List[Int] = List(1, 2, 3, 4)
```
