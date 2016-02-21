# Insertion Sort

## Sorting List
Support we want to sort a list of numbers in ascending order:
- one way to sort the list ```List(7, 3, 9, 2)``` is to sort the tail ```List(3, 9, 3)``` to obtain ```List(2, 3, 9)```
- the next step is to insert the head ```7``` in the right place to obtain the result ```List(2, 3, 7, 9)``` 

```scala
val list = List(7, 3, 9, 2)                     //> list  : List[Int] = List(7, 3, 9, 2)

def isort[T <% Ordered[T]](xs: List[T]): List[T] = xs match {
  case List()  => List()
  case y :: ys => insert(y, isort(ys))
}                                               //> isort: [T](xs: List[T])(implicit evidence$2: T => Ordered[T])List[T]

def insert[T <% Ordered[T]](x: T, xs: List[T]): List[T] = xs match {
  case List()  => List(x)
  case y :: ys => if (x <= y) x :: xs else y :: insert(x, ys)
}                                               //> insert: [T](x: T, xs: List[T])(implicit evidence$3: T => Ordered[T])List[T]

isort(list)                                     //> res0: List[Int] = List(2, 3, 7, 9)
```
